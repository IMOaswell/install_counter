package imo.install_counter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileTools
{
    static String readTextFile (File file) {
        String filePath = file.getAbsolutePath();
        return readTextFile(filePath);
    }

    static String readTextFile (String filePath) {
        StringBuilder content = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {}
        return content.toString().trim();
    }

    static void writeTextFile (File file, String input) {
        try {
            if (!file.exists()) file.createNewFile();
            if (file.isFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write(input);
                fw.close();
            }
        } catch (IOException e) {}
    }
}
