package org.example.math;

import org.joml.Random;
import org.joml.Vector2f;

/*

public class PerlinNoise {
    public static int Seed;

    private static float interpolation(float a, float b, float t)
    {
        t = t * t * t * (t * (t * 6 - 15) + 10);
        return a + (b - a) * t;
    }

    private static int next(long seed, int bits) {
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        return (int)(seed >>> (48 - bits));
    }

    // TODO: modify random for trigonometric functions instead of linear
    private static double rand(long seed) {
        return (((long) next(seed, 26) << 27) + next(seed, 27)) / (double)(1L << 53);
    }

    private static Vector2f getPseudoRandomGradientVector(int x, int y) {
        //Random random = new Random(Seed).nextInt(4);
        int a = new Random(x + y + Seed).nextInt(4);
        if (a == 0) return new Vector2f(1f, 0f);
        if (a == 1) return new Vector2f(-1f, 0f);
        if (a == 2) return new Vector2f(0f, 1f);
        return new Vector2f(0f, -1f);
    }

    public static float Noise(float fx, float fy)
    {
        int left = (int)Math.floor(fx);
        int top = (int)Math.floor(fy);

        // Local coordinates inside the square
        float pointInQuadX = fx - left;
        float pointInQuadY = fy - top;

        // Gradient vectors for square vertexes
        Vector2f topLeftGradient = getPseudoRandomGradientVector(left, top);
        Vector2f topRightGradient = getPseudoRandomGradientVector(left + 1, top);
        Vector2f bottomLeftGradient = getPseudoRandomGradientVector(left, top + 1);
        Vector2f bottomRightGradient = getPseudoRandomGradientVector(left + 1, top + 1);

        // From vertex to the point inside square
        Vector2f distanceToTopLeft = new Vector2f(pointInQuadX, pointInQuadY);
        Vector2f distanceToTopRight = new Vector2f(pointInQuadX - 1, pointInQuadY);
        Vector2f distanceToBottomLeft = new Vector2f(pointInQuadX, pointInQuadY - 1);
        Vector2f distanceToBottomRight = new Vector2f(pointInQuadX - 1, pointInQuadY - 1);

        // Compute dot products we will interpolate among
        float tx1 = distanceToTopLeft.dot(topLeftGradient);
        float tx2 = distanceToTopRight.dot(topRightGradient);
        float bx1 = distanceToBottomLeft.dot(bottomLeftGradient);
        float bx2 = distanceToBottomRight.dot(bottomRightGradient);

        // Interpolation itself
        float tx = interpolation(tx1, tx2, pointInQuadX);
        float bx = interpolation(bx1, bx2, pointInQuadX);
        return interpolation(tx, bx, pointInQuadY);
    }

    public static float Noise(float fx, float fy, int octaves, float persistence)
    {
        float amplitude = 1;
        float max = 0;
        float result = 0;

        while (octaves-- > 0)
        {
            max += amplitude;
            result += Noise(fx, fy) * amplitude;
            amplitude *= persistence;
            fx *= 2;
            fy *= 2;
        }

        return result/max;
    }
}
*/

public class PerlinNoise {

    private final int[] permutation;

    public PerlinNoise() {
        this(new Random());
    }

    public PerlinNoise(Random random) {
        permutation = new int[512];
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
        }
        System.arraycopy(p, 0, permutation, 0, 256);
        System.arraycopy(p, 0, permutation, 256, 256);
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int aa = permutation[X] + Y;
        int ab = permutation[X] + Y + 1;
        int ba = permutation[X + 1] + Y;
        int bb = permutation[X + 1] + Y + 1;

        return lerp(v,
                lerp(u, grad(permutation[aa], x, y), grad(permutation[ba], x - 1, y)),
                lerp(u, grad(permutation[ab], x, y - 1), grad(permutation[bb], x - 1, y - 1))
        );
    }

    public float noise(float x, float y, int octaves, float persistence)
    {
        float amplitude = 1;
        float max = 0;
        float result = 0;

        while (octaves-- > 0)
        {
            max += amplitude;
            result += (float) (noise(x, y) * amplitude);
            amplitude *= persistence;
            x *= 2;
            y *= 2;
        }

        return result/max;
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public static void main(String[] args) {
        PerlinNoise perlinNoise = new PerlinNoise();
        int width = 100;
        int height = 100;
        double scale = 0.1;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double value = perlinNoise.noise(x * scale, y * scale);
                System.out.printf("%6.3f ", value);
            }
            System.out.println();
        }
    }
}

