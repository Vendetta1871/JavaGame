package org.example.graphics;

import org.example.scene.JavaScene;
import org.example.system.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;

public class JavaRender {
    public JavaRender() {
        GL.createCapabilities();
    }

    public void init() throws Exception {
        glEnable(GL_DEPTH_TEST);
    }

    public void cleanup() {
        // Nothing to be done here yet
    }

    public void render(JavaWindow window, JavaScene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        scene.render();
    }
}
