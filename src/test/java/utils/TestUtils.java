package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestUtils {
    public static String readFile(String fileName) throws IOException {
        String initFile = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test/resources/states/" + fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                initFile += line + "\n";
            }
            initFile = initFile.substring(0, initFile.length() - 1);
        }

        return initFile;
    }
}
