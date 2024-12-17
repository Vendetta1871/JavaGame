package org.example;

import org.example.graphics.JRender;
import org.example.scene.JScene;
import org.example.system.*;

public class Main implements IJApp {

    public static void main(String[] args) {
        Main main = new Main();
        JEngine gameEng = new JEngine("Tianlong ", new JWindow.WindowOptions(), main);
        gameEng.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(JWindow window, JScene scene, JRender render) {
        try {
            render.init();
            scene.init(window.getWidth(), window.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to init render");
        }
    }

    @Override
    public void input(JWindow window, JScene scene, long diffTimeMillis) {
        // Nothing to be done yet
    }

    @Override
    public void update(JWindow window, JScene scene, long diffTimeMillis) {
        // Nothing to be done yet
    }
}