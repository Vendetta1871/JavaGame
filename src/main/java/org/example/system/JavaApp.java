package org.example.system;

public interface JavaApp {
    void cleanup();

    void init(JavaWindow window, JavaScene scene, JavaRender render);

    void input(JavaWindow window, JavaScene scene, long diffTimeMillis);

    void update(JavaWindow window, JavaScene scene, long diffTimeMillis);
}
