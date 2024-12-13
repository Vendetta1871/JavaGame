package org.example.math;

import org.joml.Vector3i;

public class JavaMath {
    public static double distance(int ax, int ay, int az, int bx, int by, int bz) {
        double dx = ax - bx;
        double dy = ay - by;
        double dz = az - bz;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double distance(int ax, int ay, int bx, int by) {
        double dx = ax - bx;
        double dy = ay - by;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double distanceFast(Vector3i a, Vector3i b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return dx * dx + dy * dy + dz * dz;
    }
}
