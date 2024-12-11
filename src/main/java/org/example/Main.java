package org.example;

import org.example.system.*;

public class Main implements JavaApp {

    public static void main(String[] args) {
        Main main = new Main();
        JavaEngine gameEng = new JavaEngine("Tianlong ", new JavaWindow.WindowOptions(), main);
        gameEng.start();
    }

    @Override
    public void cleanup() {
        // Nothing to be done yet
    }

    @Override
    public void init(JavaWindow window, JavaScene scene, JavaRender render) {
        try {
            render.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to init render");
        }
    }

    @Override
    public void input(JavaWindow window, JavaScene scene, long diffTimeMillis) {
        // Nothing to be done yet
    }

    @Override
    public void update(JavaWindow window, JavaScene scene, long diffTimeMillis) {
        // Nothing to be done yet
    }
}