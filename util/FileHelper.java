package util;

import java.io.*;
import java.util.*;

public class FileHelper {
    public static List<String> readFile(String path) {
        List<String> list = new ArrayList<>();
        try {
            File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create directory: " + file.getParentFile());
                }
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + path);
                }
                return list;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(path))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        list.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + path);
            e.printStackTrace();
        }
        return list;
    }

    public static void appendToFile(String path, String content) {
        if (content == null || content.trim().isEmpty()) {
            System.err.println("Warning: Attempted to write empty content to file: " + path);
            return;
        }

        try {
            File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create directory: " + file.getParentFile());
                }
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + path);
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
                bw.write(content);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + path);
            e.printStackTrace();
        }
    }

    public static void overwriteFile(String path, List<String> contents) {
        if (contents == null) {
            System.err.println("Error: Null content list provided for file: " + path);
            return;
        }

        try {
            File file = new File(path);
            if (!file.exists()) {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create directory: " + file.getParentFile());
                }
                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + path);
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
                for (String line : contents) {
                    if (line != null) {
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error overwriting file: " + path);
            e.printStackTrace();
        }
    }

    // Additional helper method for file existence check
    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    // Additional helper method for backup
    public static boolean backupFile(String sourcePath, String backupPath) {
        try {
            File source = new File(sourcePath);
            File backup = new File(backupPath);
            
            if (!source.exists()) return false;
            
            if (!backup.getParentFile().exists() && !backup.getParentFile().mkdirs()) {
                return false;
            }
            
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(backup)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }
}