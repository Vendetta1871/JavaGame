package org.example.system;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class JavaRender {
    private JavaShader shader;

    private JavaMesh mesh;
    private JavaTexture texture;
    public JavaRender() {
        GL.createCapabilities();

        float[] positions = {
                // Front face
                -0.5f, 0.5f, 0.5f, // V0
                -0.5f, -0.5f, 0.5f, // V1
                0.5f, 0.5f, 0.5f, // V3

                0.5f, 0.5f, 0.5f, // V3
                -0.5f, -0.5f, 0.5f, // V1
                0.5f, -0.5f, 0.5f, // V2
                // Top face
                -0.5f, 0.5f, -0.5f, // V8: V4 repeated
                -0.5f, 0.5f, 0.5f,  // V10: V0 repeated
                0.5f, 0.5f, 0.5f, // V11: V3 repeated

                0.5f, 0.5f, -0.5f, // V9: V5 repeated
                -0.5f, 0.5f, -0.5f, // V8: V4 repeated
                0.5f, 0.5f, 0.5f, // V11: V3 repeated
                // Right face
                0.5f, 0.5f, 0.5f, // V12: V3 repeated
                0.5f, -0.5f, 0.5f, // V13: V2 repeated
                0.5f, -0.5f, -0.5f, // V7

                0.5f, 0.5f, -0.5f, // V5
                0.5f, 0.5f, 0.5f, // V12: V3 repeated
                0.5f, -0.5f, -0.5f, // V7
                // Left face
                -0.5f, 0.5f, 0.5f, // V14: V0 repeated
                -0.5f, -0.5f, 0.5f, // V15: V1 repeated
                -0.5f, -0.5f, -0.5f, // V6

                -0.5f, 0.5f, -0.5f, // V4
                -0.5f, 0.5f, 0.5f, // V14: V0 repeated
                -0.5f, -0.5f, -0.5f, // V6
                // Bottom face
                -0.5f, -0.5f, -0.5f, // V16: V6 repeated
                -0.5f, -0.5f, 0.5f, // V18: V1 repeated
                0.5f, -0.5f, 0.5f, // V19: V2 repeated

                0.5f, -0.5f, -0.5f, // V17: V7 repeated
                -0.5f, -0.5f, -0.5f, // V16: V6 repeated
                0.5f, -0.5f, 0.5f, // V19: V2 repeated
                // Back face
                -0.5f, 0.5f, -0.5f, // V4
                -0.5f, -0.5f, -0.5f, // V6
                0.5f, -0.5f, -0.5f, // V7

                0.5f, 0.5f, -0.5f, // V5
                -0.5f, 0.5f, -0.5f, // V4
                0.5f, -0.5f, -0.5f, // V7
        };
        float[] textCoords = new float[]{
                // front 013-312
                0.0f, 0.0f, // 0
                0.0f, 0.5f, // 1
                0.5f, 0.0f, // 3

                0.5f, 0.0f, // 3
                0.0f, 0.5f, // 1
                0.5f, 0.5f, // 2
                // top 8-10-11 9-8-11
                0.0f, 0.5f, // 8
                0.0f, 1.0f, // 10
                0.5f, 1.0f, // 11

                0.5f, 0.5f, // 9
                0.0f, 0.5f, // 8
                0.5f, 1.0f, // 11
                // right 12-13-7 5-12-7
                0.0f, 0.0f, // 12
                0.0f, 0.5f, // 13
                0.5f, 0.5f, // 7

                0.5f, 0.0f, // 5
                0.0f, 0.0f, // 12
                0.5f, 0.5f, // 7
                // left 14-15-6 4-14-6
                0.5f, 0.0f, // 14
                0.5f, 0.5f, // 15
                0.0f, 0.5f, // 6

                0.0f, 0.0f, // 4
                0.5f, 0.0f, // 14
                0.0f, 0.5f, // 6
                // bottom 16-18-19 17-16-19
                0.5f, 0.0f, // 16
                0.5f, 0.5f, // 18
                1.0f, 0.5f, // 19

                1.0f, 0.0f, // 17
                0.5f, 0.0f, // 16
                1.0f, 0.5f, // 19
                // back 4-6-7 5-4-7
                0.0f, 0.0f, // 4
                0.0f, 0.5f, // 6
                0.5f, 0.5f, // 7

                0.5f, 0.0f, // 5
                0.0f, 0.0f, // 4
                0.5f, 0.5f, // 7
        };
        float[] normals = new float[] {
                // front
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f,
                // top
                0f, 1f, 0f,
                0f, 1f, 0f,
                0f, 1f, 0f,
                0f, 1f, 0f,
                0f, 1f, 0f,
                0f, 1f, 0f,
                // right
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f,
                // left
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                -1f, 0f, 0f,
                // bottom
                0f, -1f, 0f,
                0f, -1f, 0f,
                0f, -1f, 0f,
                0f, -1f, 0f,
                0f, -1f, 0f,
                0f, -1f, 0f,
                // back
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
                0f, 0f, -1f,
        };

        mesh = new JavaMesh(positions, textCoords, normals);
        texture = new JavaTexture("cube.png");
    }

    public void init() throws Exception {
        shader = new JavaShader();
        shader.createVertexShader(JavaUtils.loadResource("vertex.vs"));
        shader.createFragmentShader(JavaUtils.loadResource("fragment.fs"));
        shader.link();

        glEnable(GL_DEPTH_TEST);
    }

    public void cleanup() {
        // Nothing to be done here yet
    }

    public void render(JavaWindow window, JavaScene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        shader.bind();
        shader.setUniformMatrix4f("modelViewMatrix", (new Matrix4f()).mul(JavaCamera.Get().getView()));
        shader.setUniformMatrix4f("projectionMatrix", JavaCamera.Get().getProjection());
        shader.setUniformVector3f("lightPos", new Vector3f(2f, 2f, 2f));
        shader.setUniformInt("texture_sampler", 0);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        mesh.draw(GL_TRIANGLES);

        shader.unbind();
    }
}
