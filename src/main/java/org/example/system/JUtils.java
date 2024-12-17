package org.example.system;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JUtils {
    public static String loadResource(String path) {
        String line;
        StringBuilder str = new StringBuilder();
        try (FileReader file = new FileReader("./src/main/resources/" + path)) {
            BufferedReader reader = new BufferedReader(file);
            while ((line = reader.readLine()) != null) {
                str.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public static float[] toPrimitive(@NotNull Float[] array) {
        float[] result = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
}
