package org.example.scene;

import org.example.graphics.JavaMesh;
import org.example.graphics.JavaMeshChunk;
import org.example.graphics.JavaShader;
import org.example.graphics.JavaTexture;
import org.example.math.JavaMath;
import org.example.system.JavaUtils;
import org.example.world.Chunk;
import org.example.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class JavaScene {
    private JavaShader shader;
    private JavaTexture texture;
    private final Map<Chunk, JavaMesh> mesh;

    private World world;

    public JavaScene() {
        world = World.Get();

        mesh = new HashMap<>();

        world.chunks.forEach((Vector3i _, Chunk c) -> {
            mesh.put(c, JavaMeshChunk.renderMesh(world, c));
        });
        texture = new JavaTexture("cube.png");
    }

    public void init() throws Exception {
        shader = new JavaShader();
        shader.createVertexShader(JavaUtils.loadResource("vertex.vs"));
        shader.createFragmentShader(JavaUtils.loadResource("fragment.fs"));
        shader.link();
    }

    private void update(Vector3f camPos) {
        // TODO: save unloaded chunks to file
        world.AddChunks((int)camPos.x, (int)camPos.z);
        world.PopChunks((int)camPos.x, (int)camPos.z);
        // TODO: make this async
        world.chunks.forEach((Vector3i v, Chunk c) -> {
            int dx = v.x / 16 - (int)Math.floor((int)camPos.x / 16f);
            int dz = v.z / 16 - (int)Math.floor((int)camPos.z / 16f);
            if (Math.abs(dx) > JavaMeshChunk.renderDistance ||
                    Math.abs(dz) > JavaMeshChunk.renderDistance) {
                JavaMesh m = mesh.get(c);
                if (m != null) m.cleanup();
                mesh.remove(c);
                c.isRendered = -1;
            } else if (c.isRendered < 0) {
                JavaMesh m = mesh.get(c);
                if (m != null) m.cleanup();
                mesh.remove(c);
                mesh.put(c, JavaMeshChunk.renderMesh(world, c));
            }
        });
    }

    public void render() {
        update(JavaCamera.Get().Position);

        shader.bind();
        shader.setUniformMatrix4f("modelViewMatrix", (new Matrix4f()).mul(JavaCamera.Get().getView()));
        shader.setUniformMatrix4f("projectionMatrix", JavaCamera.Get().getProjection());
        shader.setUniformVector3f("lightPos", new Vector3f(200f, 200f, 200f));
        shader.setUniformInt("textureSampler", 0);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        mesh.forEach((Chunk _, JavaMesh m) -> { m.draw(GL_TRIANGLES); });

        shader.unbind();
    }

    public void cleanup() {
        // Nothing to be done here yet
    }
}
