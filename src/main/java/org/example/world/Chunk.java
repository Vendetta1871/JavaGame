package org.example.world;

import org.example.block.Block;
import org.example.math.PerlinNoise;
import org.joml.Vector3i;

public class Chunk {
    public Vector3i Position;
    public Block[] Blocks;

    public long isRendered;

    private static final PerlinNoise noise = new PerlinNoise();

    private Chunk(Vector3i pos) {
        isRendered = -1;
        Position = pos;
        Blocks = new Block[256*256];
        for (int i = 0; i < 256*256; ++i) {
            Blocks[i] = new Block((short)0);
        }
    }

    public static int Pos2Index(int x, int y, int z) {
        return x + z * 16 + y * 256;
    }

    public static Vector3i Index2Pos(int i) {
        int y = i / 256;
        int z = (i - y * 256) / 16;
        int x = (i - y * 256 - z * 16);

        return new Vector3i(x, y, z);
    }

    private static float getHeight(float fx, float fy) {
        fx /= 100f;
        fy /= 100f;
        float h = noise.noise(fx, fy, 8, 0.5f);
        h += noise.noise(fx, fy - 0.01f, 8, 0.5f);
        h += noise.noise(fx, fy + 0.01f, 8, 0.5f);
        h += noise.noise(fx- 0.01f, fy, 8, 0.5f);
        h += noise.noise(fx + 0.01f, fy, 8, 0.5f);
        h += noise.noise(fx - 0.01f, fy + 0.01f, 8, 0.5f);
        h += noise.noise(fx + 0.01f, fy - 0.01f, 8, 0.5f);
        h += noise.noise(fx - 0.01f, fy - 0.01f, 8, 0.5f);
        h += noise.noise(fx + 0.01f, fy + 0.01f, 8, 0.5f);
        return (h / 9f * 128f + 128f) / (float)Math.sqrt(2d);
    }

    public static Chunk Generate(Vector3i pos) {
        // TODO: fix bug with chunk edges
        long startTime = System.nanoTime();

        Chunk chunk = new Chunk(pos);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                float h = getHeight(i + pos.x, j + pos.z);
                for (int k = 0; k < h; ++k) {
                    chunk.Blocks[Chunk.Pos2Index(i, k, j)] = new Block((short) 2);
                }
                for (int k = (int)h; k < 64; ++k) {
                    chunk.Blocks[Chunk.Pos2Index(i, k, j)] = new Block((short) -1);
                }
            }
        }

        System.out.println("Chunk at " + chunk.Position.x / 16 + ", " + chunk.Position.z / 16 +
                " generated in: " + (System.nanoTime() - startTime) + " ns");
        return chunk;
    }
}

