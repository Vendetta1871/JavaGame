package org.example.block;

import org.example.world.Chunk;
import org.joml.Vector3i;

public class Block {
    public char BlockState = 0;

    public short BlockType;

    public Block(short i) {
        BlockType = i;
    }

    public Vector3i GetPosition(Chunk chunk) {
        for (int i = 0; i < 256 * 256; ++i) {
            if (chunk.Blocks[i] == this)
                return Chunk.Index2Pos(i).add(chunk.Position);
        }
        return null;
    }

    public enum BLOCK {
        SOLID,
        CUSTOM,
        TRANSPARENT
    }

    public BLOCK getTransparency() {
        return switch (this.BlockType) {
            case 0 -> BLOCK.TRANSPARENT;
            case -1 -> BLOCK.CUSTOM;
            default -> BLOCK.SOLID;
        };
    }
}
