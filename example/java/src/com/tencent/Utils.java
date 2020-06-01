package com.tencent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public byte[] getFile(String filePath) {
        File f;
        try {
            f = new File(filePath);
        } catch (Exception e) {
            return null;
        }  

        Path path = Paths.get(f.getAbsolutePath());
        byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            return null;
        }

        return data;
    }

}
