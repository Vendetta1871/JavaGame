package org.example.world;

import org.example.block.Block;
import org.joml.Vector3i;

import java.util.*;

public class World {
    public Map<Vector3i, Chunk> chunks;

    public static World _world = null;

    private World() {
        chunks = new HashMap<>();
    }

    public static World Get() {
        if (_world == null) {
            _world = new World();
        }
        return _world;
    }

    public void AddChunks(int x, int z) {
        Chunk c;
        x = (int)Math.floor(x / 16f);
        z = (int)Math.floor(z / 16f);
        for (int i = x - 12; i < x + 12; ++i) {
            for (int j = z - 12; j < z + 12; ++j) {
                Vector3i pos = new Vector3i(i * 16, 0, j * 16);
                if (chunks.get(pos) != null) continue;
                chunks.put(pos, Chunk.Generate(pos));
                if ((c = chunks.get(new Vector3i(pos.x + 16,0, pos.z))) != null)
                    c.isRendered = -1;
                if ((c = chunks.get(new Vector3i(pos.x - 16,0, pos.z))) != null)
                    c.isRendered = -1;
                if ((c = chunks.get(new Vector3i(pos.x,0, pos.z + 16))) != null)
                    c.isRendered = -1;
                if ((c = chunks.get(new Vector3i(pos.x,0, pos.z - 16))) != null)
                    c.isRendered = -1;
            }
        }
    }

    private static final int loadDistance = 12;

    public void PopChunks(int x, int z) {
        // TODO: unload chunks to memory
        chunks.entrySet().removeIf(entry -> {
            Vector3i v = entry.getKey();
            int dx = v.x / 16 - (int) Math.floor(x / 16f);
            int dz = v.z / 16 - (int) Math.floor(z / 16f);
            return Math.abs(dx) > loadDistance || Math.abs(dz) > loadDistance;
        });

    }

    public static Vector3i getChunkCoord(int x, int y, int z) {
        int chunkX = (int)Math.floor(x / 16f) * 16;
        int chunkZ = (int)Math.floor(z / 16f) * 16;
        return new Vector3i(chunkX, 0, chunkZ);
    }
    //public void

    public Block GetBlock(int x, int y, int z) {
        if (y < 0) return null;
        int blockX = x % 16;
        int blockZ = z % 16;
        if (blockX < 0) blockX = 15 + blockX;
        if (blockZ < 0) blockZ = 15 + blockZ;

        try {
            return chunks.get(getChunkCoord(x, y, z)).Blocks[Chunk.Pos2Index(blockX, y, blockZ)];
        } catch (Exception e) {
            return null;
        }
    }

    public boolean IsBlockAir(int x, int y, int z) {
        Block block = GetBlock(x, y, z);
        return block != null && block.BlockType == 0;
    }
}
