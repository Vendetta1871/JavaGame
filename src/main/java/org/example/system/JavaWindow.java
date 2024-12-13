package org.example.system;

import org.example.scene.JavaCamera;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.concurrent.Callable;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class JavaWindow {

    private final long windowHandle;
    private int height;
    private Callable<Void> resizeFunc;
    private int width;

    public JavaWindow(String title, WindowOptions opts, Callable<Void> resizeFunc) {
        this.resizeFunc = resizeFunc;
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        if (opts.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        if (opts.width > 0 && opts.height > 0) {
            this.width = opts.width;
            this.height = opts.height;
        } else {
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            width = vidMode.width();
            height = vidMode.height();
        }

        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> resized(w, h));

        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
            keyCallBack(key, action);
        });

        glfwSetCursorPos(windowHandle, width / 2f, height / 2f);
        glfwSetCursorPosCallback(windowHandle, (window, x, y) -> {
            cursorPositionCallback((float)x, (float)y);
        });


        glfwMakeContextCurrent(windowHandle);

        if (opts.fps > 0) {
            glfwSwapInterval(0);
        } else {
            glfwSwapInterval(1);
        }

        glfwShowWindow(windowHandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];

        JavaCamera.Get().width = width;
        JavaCamera.Get().height = height;
    }

    public void cleanup() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    private static float _speed(boolean shift) {
        return shift ? 0.5f : 0.1f;
    }

    public void keyCallBack(int key, int action) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(windowHandle, true); // We will detect this in the rendering loop
        }

        //TODO: keyboard input handle
        float s = _speed(isKeyPressed(GLFW_KEY_LEFT_SHIFT));
        if (isKeyPressed(GLFW_KEY_W)) {
            JavaCamera.Get().Move(s, 0, 0);
        }
        if (isKeyPressed(GLFW_KEY_S)) {
            JavaCamera.Get().Move(-s, 0, 0);
        }
        if (isKeyPressed(GLFW_KEY_A)) {
            JavaCamera.Get().Move(0f, 0, -s);
        }
        if (isKeyPressed(GLFW_KEY_D)) {
            JavaCamera.Get().Move(0f, 0, s);
        }
        if (isKeyPressed(GLFW_KEY_SPACE)) {
            JavaCamera.Get().Move(0, s, 0);
        }
        if (isKeyPressed(GLFW_KEY_LEFT_CONTROL)) {
            JavaCamera.Get().Move(0, -s, 0);
        }
    }

    public float X, Y, DeltaX, DeltaY;
    public boolean IsCursorStarted = false;
    public void cursorPositionCallback(float xpos, float ypos)
    {
        if (IsCursorStarted)
        {
            DeltaX = xpos - width / 2f;
            DeltaY = ypos - height / 2f;
            JavaCamera.Get().Rotate(-DeltaX / width * 2, -DeltaY / height * 2);
        }
        else
        {
            IsCursorStarted = true;
        }
        X = xpos;
        Y = ypos;
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    protected void resized(int width, int height) {
        this.width = width;
        this.height = height;
        try {
            resizeFunc.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        glfwSetCursorPos(windowHandle, width / 2f, height / 2f);
        glfwSwapBuffers(windowHandle);
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public static class WindowOptions {
        public boolean compatibleProfile;
        public int fps;
        public int height;
        public int ups = JavaEngine.TARGET_UPS;
        public int width;
    }
}
