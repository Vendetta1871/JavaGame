package org.example.graphics;

import org.example.scene.JScene;
import org.example.system.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.glViewport;

public class JRender {
    public JRender() {
        GL.createCapabilities();
    }

    public void init() throws Exception {
    }

    public void cleanup() {
        // Nothing to be done here yet
    }

    public void render(JWindow window, JScene scene) {
        glViewport(0, 0, window.getWidth(), window.getHeight());
        scene.render(window);
    }
}
