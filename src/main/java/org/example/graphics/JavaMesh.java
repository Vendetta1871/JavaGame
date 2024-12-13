package org.example.graphics;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class JavaMesh {
    private int numVertices;
    private int vaoId;
    private List<Integer> vboIdList;

    public JavaMesh(float[] positions, float[] textCoords, float[] normals) {
        int size = Float.BYTES * (positions.length + textCoords.length + normals.length);
        try (MemoryStack stack = MemoryStack.create(size)) {
            stack.push();

            numVertices = positions.length / 3;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            bindJavaMeshArray(stack, 0, 3, positions);
            bindJavaMeshArray(stack, 1, 2, textCoords);
            bindJavaMeshArray(stack, 2, 3, normals);

            /*
            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            IntBuffer indicesBuffer = stack.callocInt(indices.length);
            indicesBuffer.put(0, indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
             */

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    private void bindJavaMeshArray(MemoryStack stack, int index, int size, float[] array) {
        int vboId = glGenBuffers();
        vboIdList.add(vboId);
        FloatBuffer buffer = stack.mallocFloat(array.length); // malloc or calloc ?
        buffer.put(0, array);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0);
    }

    public void drawInd(int primitives) {
        glBindVertexArray(getVaoId());
        glDrawElements(primitives, getNumVertices(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void draw(int primitives) {
        glBindVertexArray(getVaoId());
        glDrawArrays(primitives, 0, getNumVertices());
        glBindVertexArray(0);
    }

    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoId);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public final int getVaoId() {
        return vaoId;
    }

}
