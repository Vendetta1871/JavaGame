package org.example.system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JavaUtils {
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
}
