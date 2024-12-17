package org.example.scene;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class JCamera {
    private Vector3f Up;
    private Vector3f Front;
    private Vector3f Right;

    public Vector3f Position;
    public Matrix4f Rotation;

    public float fov;

    public float width;
    public float height;

    public void UpdateVectors() {
        Vector4f up = (new Vector4f(0.0f, 1.0f, 0.0f, 1.0f)).mul(Rotation);
        Vector4f front = (new Vector4f(0.0f, 0.0f, -1.0f, 1.0f)).mul(Rotation);
        Vector4f right = (new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)).mul(Rotation);

        Up = new Vector3f(up.x, up.y, up.z);
        Front = new Vector3f(front.x, front.y, front.z);
        Right = new Vector3f(right.x, right.y, right.z);
    }

    public Vector3f eye() {
        return new Vector3f(Position);
    }

    public Vector3f center() {
        return (new Vector3f(Position)).add(Front);
    }

    public Vector3f up() {
        return new Vector3f(Up);
    }

    private static JCamera _camera;

    public JCamera(Vector3f pos, float fov, float width, float height) {
        this.Position = pos;
        this.fov = fov;
        this.width = width;
        this.height = height;
        this.Rotation = new Matrix4f();
        UpdateVectors();

        _camera = this;
    }

    public static JCamera Get() {
        if (_camera == null) {
            _camera = new JCamera(new Vector3f(0.0f, 150.0f, 0.0f),
                    (float)Math.toRadians(70), 1920, 1080);
        }
        return _camera;
    }

    public void Rotate(float x, float y, float z) {
        Matrix4f tmp = new Matrix4f();
        Rotation = Rotation.rotate(z, new Vector3f(0.0f, 0.0f, 1.0f), tmp);
        Rotation = Rotation.rotate(y, new Vector3f(0.0f, 1.0f, 0.0f), tmp);
        Rotation = Rotation.rotate(x, new Vector3f(1.0f, 0.0f, 0.0f), tmp);
        UpdateVectors();
    }

    public float camX, camY;

    public void Rotate(float mouseDeltaX, float mouseDeltaY) {
        camX += mouseDeltaX;
        camY += mouseDeltaY;
        camY = (float) Math.max(camY, -Math.toRadians(89.0f));
        camY = (float) Math.min(camY, Math.toRadians(89.0f));
        Rotation = new Matrix4f();
        Rotate(camY, camX, 0);
    }

    public void Move(float speedX, float speedY, float speedZ) {
        Position = Position.add((new Vector3f(Front)).mul(speedX));
        Position = Position.add((new Vector3f(Up)).mul(speedY));
        Position = Position.add((new Vector3f(Right)).mul(speedZ));
    }

    public Matrix4f getView() { // lookAt
        Vector3f f = center().sub(eye()).normalize();
        Vector3f s = (new Vector3f(f)).cross(up()).normalize();
        Vector3f u = (new Vector3f(s)).cross(f);

        Matrix4f result = new Matrix4f();
        result.set(0, 0,  s.x);
        result.set(1, 0,  s.y);
        result.set(2, 0,  s.z);
        result.set(0, 1,  u.x);
        result.set(1, 1,  u.y);
        result.set(2, 1,  u.z);
        result.set(0, 2, -f.x);
        result.set(1, 2, -f.y);
        result.set(2, 2, -f.z);
        result.set(3, 0, -eye().dot(s));
        result.set(3, 1, -eye().dot(u));
        result.set(3, 2,  eye().dot(f));
        return result;
    }

    public Matrix4f getProjection() {
        float aspect = width / height;
        float zNear = 0.01f;
        float zFar = 1000f;

        return new Matrix4f().perspective(fov, aspect, zNear, zFar);
    }
}
