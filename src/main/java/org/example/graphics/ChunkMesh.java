package org.example.graphics;

import org.example.block.Block;
import org.example.system.JUtils;
import org.example.world.Chunk;
import org.example.world.World;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkMesh {
    public static final int renderDistance = 8;
    public static long lastRendered = -1;

    public static boolean isBlockTransparent(World world, int x, int y, int z) {
        Block block = world.GetBlock(x, y, z);
        return block != null && block.BlockType < 1;
    }

    // TODO: pre-load neighbor chunks and call isBlockAir only in them instead of whole world
    public static JMesh renderMeshOpaque(World world, Chunk chunk) {
        long startTime = System.nanoTime();

        List<Float> positionList = new ArrayList<>();
        List<Float> uvList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        // TODO: use parallel for-loop
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 256; ++y) {
                for (int z = 0; z < 16; ++z) {
                    Vector3i pos = (new Vector3i(x, y, z)).add(chunk.Position);
                    if (isBlockTransparent(world, pos.x, pos.y, pos.z)) continue;
                    if (isBlockTransparent(world, pos.x + 1, pos.y, pos.z))
                        getFace(positionList, uvList, normalList, BLOCK_RIGHT_FACE, pos);
                    if (isBlockTransparent(world, pos.x - 1, pos.y, pos.z))
                        getFace(positionList, uvList, normalList, BLOCK_LEFT_FACE, pos);
                    if (isBlockTransparent(world, pos.x, pos.y + 1, pos.z))
                        getFace(positionList, uvList, normalList, BLOCK_TOP_FACE, pos);
                    if (isBlockTransparent(world, pos.x, pos.y - 1, pos.z))
                        getFace(positionList, uvList, normalList, BLOCK_BOTTOM_FACE, pos);
                    if (isBlockTransparent(world, pos.x, pos.y, pos.z + 1))
                        getFace(positionList, uvList, normalList, BLOCK_FRONT_FACE, pos);
                    if (isBlockTransparent(world, pos.x, pos.y, pos.z - 1))
                        getFace(positionList, uvList, normalList, BLOCK_BACK_FACE, pos);
                }
            }
        }

        float[] positionArray = JUtils.toPrimitive(positionList.toArray(new Float[0]));
        float[] uvArray = JUtils.toPrimitive(uvList.toArray(new Float[0]));
        float[] normalArray = JUtils.toPrimitive(normalList.toArray(new Float[0]));
        //System.out.println("Chunk mesh rendered in: " + (System.nanoTime() - startTime) + " ns");
        // TODO: remove to more smart method
        chunk.isRendered = ++lastRendered;
        JMesh result = new JMesh(positionArray, uvArray, normalArray);

        System.out.println("Chunk mesh at " + chunk.Position.x / 16 + ", " + chunk.Position.z / 16 +
                " rendered in: " + (System.nanoTime() - startTime) + " ns");
        return result;
    }

    public static JMesh renderMeshTransparent(World world, Chunk chunk) {
        long startTime = System.nanoTime();

        List<Float> positionList = new ArrayList<>();
        List<Float> uvList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        // TODO: use parallel for-loop
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 256; ++y) {
                for (int z = 0; z < 16; ++z) {
                    Vector3i pos = (new Vector3i(x, y, z)).add(chunk.Position);
                    Block block = world.GetBlock(pos.x, pos.y, pos.z);
                    if (block == null || block.BlockType > -1) continue;
                    positionList.addAll(Arrays.asList(getFacePositions(BLOCK_TOP_FACE, pos)));
                    uvList.addAll(Arrays.asList(
                            0.5f, 0.5f, // 8
                            0.5f, 1.0f, // 10
                            1.0f, 1.0f, // 11

                            1.0f, 0.5f, // 9
                            0.5f, 0.5f, // 8
                            1.0f, 1.0f // 11
                    ));
                    normalList.addAll(Arrays.asList(getFaceNormals(BLOCK_TOP_FACE)));
                }
            }
        }

        float[] positionArray = JUtils.toPrimitive(positionList.toArray(new Float[0]));
        float[] uvArray = JUtils.toPrimitive(uvList.toArray(new Float[0]));
        float[] normalArray = JUtils.toPrimitive(normalList.toArray(new Float[0]));
        //System.out.println("Chunk mesh rendered in: " + (System.nanoTime() - startTime) + " ns");
        // TODO: remove to more smart method
        chunk.isRendered = ++lastRendered;
        JMesh result = new JMesh(positionArray, uvArray, normalArray);

        System.out.println("Chunk mesh at " + chunk.Position.x / 16 + ", " + chunk.Position.z / 16 +
                " rendered in: " + (System.nanoTime() - startTime) + " ns");
        return result;
    }

    //public static JavaMesh

    public static void getFace(List<Float> p, List<Float> u, List<Float> n, int face, Vector3i pos) {
        p.addAll(Arrays.asList(getFacePositions(face, pos)));
        u.addAll(Arrays.asList(getFaceUVs(face)));
        n.addAll(Arrays.asList(getFaceNormals(face)));
    }

    private static Float[] getFacePositions(int face, Vector3i pos) {
        int offset = face * 3 * 6;
        Float[] array = new Float[3 * 6];
        for (int i = 0; i < 3 * 6;) {
            array[i++] = positions[offset++] + pos.x;
            array[i++] = positions[offset++] + pos.y;
            array[i++] = positions[offset++] + pos.z;
        }
        return array;
    }

    private static Float[] getFaceUVs(int face) {
        int offset = face * 2 * 6;
        Float[] array = new Float[2 * 6];
        for (int i = 0; i < 2 * 6;) {
            array[i++] = textCoords[offset++];
            array[i++] = textCoords[offset++];
        }
        return array;
    }

    private static Float[] getFaceNormals(int face) {
        int offset = face * 3 * 6;
        Float[] array = new Float[3 * 6];
        for (int i = 0; i < 3 * 6;) {
            array[i++] = normals[offset++];
            array[i++] = normals[offset++];
            array[i++] = normals[offset++];
        }
        return array;
    }

    private static final int BLOCK_FRONT_FACE = 0;
    private static final int BLOCK_TOP_FACE = 1;
    private static final int BLOCK_RIGHT_FACE = 2;
    private static final int BLOCK_LEFT_FACE = 3;
    private static final int BLOCK_BOTTOM_FACE = 4;
    private static final int BLOCK_BACK_FACE = 5;

    private static final float[] positions = {
            // Front face
            -0.5f, 0.5f, 0.5f, // V0
            -0.5f, -0.5f, 0.5f, // V1
            0.5f, 0.5f, 0.5f, // V3

            0.5f, 0.5f, 0.5f, // V3
            -0.5f, -0.5f, 0.5f, // V1
            0.5f, -0.5f, 0.5f, // V2
            // Top face
            -0.5f, 0.5f, -0.5f, // V8: V4 repeated
            -0.5f, 0.5f, 0.5f,  // V10: V0 repeated
            0.5f, 0.5f, 0.5f, // V11: V3 repeated

            0.5f, 0.5f, -0.5f, // V9: V5 repeated
            -0.5f, 0.5f, -0.5f, // V8: V4 repeated
            0.5f, 0.5f, 0.5f, // V11: V3 repeated
            // Right face
            0.5f, 0.5f, 0.5f, // V12: V3 repeated
            0.5f, -0.5f, 0.5f, // V13: V2 repeated
            0.5f, -0.5f, -0.5f, // V7

            0.5f, 0.5f, -0.5f, // V5
            0.5f, 0.5f, 0.5f, // V12: V3 repeated
            0.5f, -0.5f, -0.5f, // V7
            // Left face
            -0.5f, 0.5f, 0.5f, // V14: V0 repeated

            -0.5f, -0.5f, -0.5f, // V6
            -0.5f, -0.5f, 0.5f, // V15: V1 repeated

            -0.5f, 0.5f, 0.5f, // V14: V0 repeated
            -0.5f, 0.5f, -0.5f, // V4

            -0.5f, -0.5f, -0.5f, // V6
            // Bottom face
            -0.5f, -0.5f, -0.5f, // V16: V6 repeated
            -0.5f, -0.5f, 0.5f, // V18: V1 repeated
            0.5f, -0.5f, 0.5f, // V19: V2 repeated

            0.5f, -0.5f, -0.5f, // V17: V7 repeated
            -0.5f, -0.5f, -0.5f, // V16: V6 repeated
            0.5f, -0.5f, 0.5f, // V19: V2 repeated
            // Back face
            -0.5f, 0.5f, -0.5f, // V4

            0.5f, -0.5f, -0.5f, // V7
            -0.5f, -0.5f, -0.5f, // V6

            -0.5f, 0.5f, -0.5f, // V4
            0.5f, 0.5f, -0.5f, // V5

            0.5f, -0.5f, -0.5f, // V7
    };

    private static final float[] textCoords = new float[]{
            // front 013-312
            0.0f, 0.0f, // 0
            0.0f, 0.5f, // 1
            0.5f, 0.0f, // 3

            0.5f, 0.0f, // 3
            0.0f, 0.5f, // 1
            0.5f, 0.5f, // 2
            // top 8-10-11 9-8-11
            0.0f, 0.5f, // 8
            0.0f, 1.0f, // 10
            0.5f, 1.0f, // 11

            0.5f, 0.5f, // 9
            0.0f, 0.5f, // 8
            0.5f, 1.0f, // 11
            // right 12-13-7 5-12-7
            0.0f, 0.0f, // 12
            0.0f, 0.5f, // 13
            0.5f, 0.5f, // 7

            0.5f, 0.0f, // 5
            0.0f, 0.0f, // 12
            0.5f, 0.5f, // 7
            // left 14-15-6 4-14-6
            0.5f, 0.0f, // 14

            0.0f, 0.5f, // 6
            0.5f, 0.5f, // 15

            0.5f, 0.0f, // 14
            0.0f, 0.0f, // 4

            0.0f, 0.5f, // 6
            // bottom 16-18-19 17-16-19
            0.5f, 0.0f, // 16
            0.5f, 0.5f, // 18
            1.0f, 0.5f, // 19

            1.0f, 0.0f, // 17
            0.5f, 0.0f, // 16
            1.0f, 0.5f, // 19
            // back 4-6-7 5-4-7
            0.0f, 0.0f, // 4

            0.5f, 0.5f, // 7
            0.0f, 0.5f, // 6

            0.0f, 0.0f, // 4
            0.5f, 0.0f, // 5

            0.5f, 0.5f, // 7
    };

    private static final float[] normals = new float[] {
            // front
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            // top
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            // right
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            // left
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            // bottom
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            // back
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
    };
}
