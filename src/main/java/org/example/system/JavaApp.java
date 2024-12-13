package org.example.system;

import org.example.graphics.JavaRender;
import org.example.scene.JavaScene;

public interface JavaApp {
    void cleanup();

    void init(JavaWindow window, JavaScene scene, JavaRender render);

    void input(JavaWindow window, JavaScene scene, long diffTimeMillis);

    void update(JavaWindow window, JavaScene scene, long diffTimeMillis);
}
