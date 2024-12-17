package org.example.system;

import org.example.graphics.JRender;
import org.example.scene.JScene;

public interface IJApp {
    void cleanup();

    void init(JWindow window, JScene scene, JRender render);

    void input(JWindow window, JScene scene, long diffTimeMillis);

    void update(JWindow window, JScene scene, long diffTimeMillis);
}
