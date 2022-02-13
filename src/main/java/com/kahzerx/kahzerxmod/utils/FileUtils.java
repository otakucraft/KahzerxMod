package com.kahzerx.kahzerxmod.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kahzerx.kahzerxmod.config.KSettings;

import java.io.*;

public class FileUtils {
    private static final String configName = "KConfig.json";

    public static String loadConfig(String path) {
        File file = new File(path + "/" + configName);
        if (!file.exists()) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void createConfig(String path, KSettings settings) {
        File file = new File(path + "/" + configName);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(settings, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
