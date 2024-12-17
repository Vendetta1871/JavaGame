package org.example.scene;

import org.example.graphics.JMesh;
import org.example.graphics.ChunkMesh;
import org.example.graphics.JShader;
import org.example.graphics.JTexture;
import org.example.system.JUtils;
import org.example.system.JWindow;
import org.example.world.Chunk;
import org.example.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class JScene {
    private JShader shaderOpaque;
    private JShader shaderOIT;
    private JShader shaderOIT2;
    private JShader shaderComposite;

    private JTexture texture;
    private final Map<Chunk, JMesh> meshOpaque;
    private final Map<Chunk, JMesh> meshTransparent;
    private final World world;

    public JScene() {
        world = World.Get();

        meshOpaque = new HashMap<>();
        meshTransparent = new HashMap<>();

        world.chunks.forEach((Vector3i _, Chunk c) -> {
            meshOpaque.put(c, ChunkMesh.renderMeshOpaque(world, c));
            meshTransparent.put(c, ChunkMesh.renderMeshTransparent(world, c));
        });
    }

    private int accumulationTexture;
    private int revealageTexture;
    private int opaqueTexture;
    private int depthRenderbuffer;

    private int frameBuffer;
    private int frameBufferOpaque;

    private void genGLResources(int w, int h) {
        frameBufferOpaque = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferOpaque);

        opaqueTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, opaqueTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, w, h, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, opaqueTexture, 0);

        depthRenderbuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderbuffer);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, w, h);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderbuffer);

        glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_DEPTH_ATTACHMENT});

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete");
        }

        frameBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);

        accumulationTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, accumulationTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, w, h, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, accumulationTexture, 0);

        revealageTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, revealageTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R16F, w, h, 0, GL_RED, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, revealageTexture, 0);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderbuffer);

        glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1});

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Framebuffer is not complete");
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void init(int w, int h) throws Exception {
        GL.createCapabilities();
        genGLResources(w, h);

        shaderOIT = new JShader(
                JUtils.loadResource("vertexOIT.glsl"),
                JUtils.loadResource("fragmentOIT.glsl"));
        shaderOIT.link();

        shaderOIT2 = new JShader(
                JUtils.loadResource("vertexOIT.glsl"),
                JUtils.loadResource("fragmentOIT2.glsl"));
        shaderOIT2.link();

        shaderComposite = new JShader(
                JUtils.loadResource("vertexComposite.glsl"),
                JUtils.loadResource("fragmentComposite.glsl"));
        shaderComposite.link();

        shaderOpaque = new JShader(
                JUtils.loadResource("vertexOpaque.glsl"),
                JUtils.loadResource("fragmentOpaque.glsl"));
        shaderOpaque.link();

        texture = new JTexture("cube.png");
    }

    private void update(Vector3f camPos) {
        // TODO: save unloaded chunks to file
        world.AddChunks((int)camPos.x, (int)camPos.z);
        world.PopChunks((int)camPos.x, (int)camPos.z);
        // TODO: make this async
        world.chunks.forEach((Vector3i v, Chunk c) -> {
            int dx = v.x / 16 - (int)Math.floor((int)camPos.x / 16f);
            int dz = v.z / 16 - (int)Math.floor((int)camPos.z / 16f);
            if (Math.abs(dx) > ChunkMesh.renderDistance ||
                    Math.abs(dz) > ChunkMesh.renderDistance) {
                JMesh mo = meshOpaque.get(c);
                JMesh mt = meshTransparent.get(c);
                if (mo != null) mo.cleanup();
                if (mt != null) mt.cleanup();
                meshOpaque.remove(c);
                meshTransparent.remove(c);
                c.isRendered = -1;
            } else if (c.isRendered < 0) {
                JMesh mo = meshOpaque.get(c);
                JMesh mt = meshTransparent.get(c);
                if (mo != null) mo.cleanup();
                if (mt != null) mt.cleanup();
                meshOpaque.remove(c);
                meshTransparent.remove(c);
                meshOpaque.put(c, ChunkMesh.renderMeshOpaque(world, c));
                meshTransparent.put(c, ChunkMesh.renderMeshTransparent(world, c));
            }
        });
    }

    public void render(JWindow window) {
        // 1. Add new chunks and unload old ones
        update(new Vector3f(JCamera.Get().Position));

        // 2. Draw opaque objects (including custom geometry)
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferOpaque);
        glClearBufferfv(GL_COLOR, 0,  new float[]{ 135 / 255f, 206 / 255f, 235 / 255f, 1.0f });
        glClearBufferfv(GL_DEPTH, 0, new float[]{ 1.0f });

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glDepthMask(true);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);

        shaderOpaque.bind();
        shaderOpaque.setUniformMatrix4f("modelViewMatrix", (new Matrix4f()).mul(JCamera.Get().getView()));
        shaderOpaque.setUniformMatrix4f("projectionMatrix", JCamera.Get().getProjection());
        shaderOpaque.setUniformVector3f("lightPos", new Vector3f(200f, 200f, 200f));
        glActiveTexture(GL_TEXTURE0);
        shaderOpaque.setUniformInt("textureSampler", 0);

        texture.bind();

        meshOpaque.forEach((_, m) -> {m.draw(GL_TRIANGLES);});

        // 3. Accumulate weighted color for transparent objects
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
        glClearBufferfv(GL_COLOR, 0, new float[]{0.0f, 0.0f, 0.0f, 0.0f});
        glClearBufferfv(GL_COLOR, 1, new float[]{1.0f});

        glDepthMask(false);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        glBlendEquation(GL_FUNC_ADD);

        shaderOIT.bind();
        shaderOIT.setUniformMatrix4f("modelViewMatrix", (new Matrix4f()).mul(JCamera.Get().getView()));
        shaderOIT.setUniformMatrix4f("projectionMatrix", JCamera.Get().getProjection());
        shaderOIT.setUniformVector3f("lightPos", new Vector3f(200f, 200f, 200f));
        glActiveTexture(GL_TEXTURE0);
        shaderOIT.setUniformInt("textureSampler", 0);

        texture.bind();

        meshTransparent.forEach((_, m) -> {m.draw(GL_TRIANGLES);});

        // 4. Calculate revealge for transparent objects
        glDepthMask(false);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_COLOR);
        glBlendEquation(GL_FUNC_ADD);

        shaderOIT2.bind();
        shaderOIT2.setUniformMatrix4f("modelViewMatrix", (new Matrix4f()).mul(JCamera.Get().getView()));
        shaderOIT2.setUniformMatrix4f("projectionMatrix", JCamera.Get().getProjection());
        shaderOIT2.setUniformVector3f("lightPos", new Vector3f(200f, 200f, 200f));
        glActiveTexture(GL_TEXTURE0);
        shaderOIT2.setUniformInt("textureSampler", 0);

        texture.bind();

        meshTransparent.forEach((_, m) -> {m.draw(GL_TRIANGLES);});

        // 5. Composite final image
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(true);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT);
        compositeFinalImage();
    }

    private void compositeFinalImage() {
        shaderComposite.bind();

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, accumulationTexture);
        shaderComposite.setUniformInt("AccumTexture", 1);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, revealageTexture);
        shaderComposite.setUniformInt("RevealageTexture", 2);

        glActiveTexture(GL_TEXTURE3);
        glBindTexture(GL_TEXTURE_2D, opaqueTexture);
        shaderComposite.setUniformInt("OpaqueTexture", 3);

        // bind texture of non-transparent

        float[] quadVertices = {
                // Positions   // Texture Coords
                -1.0f, -1.0f, 0.0f, 0.0f,
                1.0f, -1.0f, 1.0f, 0.0f,
                1.0f,  1.0f, 1.0f, 1.0f,
                -1.0f,  1.0f, 0.0f, 1.0f
        };

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(quadVertices.length);
        vertexBuffer.put(quadVertices).flip();

        int quadVAO = glGenVertexArrays();
        int quadVBO = glGenBuffers();
        glBindVertexArray(quadVAO);

        glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(quadVAO);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);

        glBindVertexArray(0);
        glDeleteBuffers(quadVBO);
        glDeleteVertexArrays(quadVAO);
    }

    public void cleanup() {
        glDeleteFramebuffers(frameBufferOpaque);
        glDeleteTextures(opaqueTexture);
        glDeleteRenderbuffers(depthRenderbuffer);

        glDeleteFramebuffers(frameBuffer);
        glDeleteTextures(accumulationTexture);
        glDeleteTextures(revealageTexture);
    }
}
