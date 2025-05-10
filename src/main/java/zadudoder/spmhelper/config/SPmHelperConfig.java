package zadudoder.spmhelper.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SPmHelperConfig {
    private static final Path CONFIG_PATH = Paths.get("config/spmhelper.json");

    public static void setToken(String id, String token) {
        try {
            JsonObject config = new JsonObject();
            config.addProperty("id", id);
            config.addProperty("token", token);
            Files.writeString(CONFIG_PATH, config.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getId() {
        try {
            if (!Files.exists(CONFIG_PATH)) return null;
            JsonObject config = JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
            return config.get("id").getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getToken() {
        try {
            if (!Files.exists(CONFIG_PATH)) return null;
            JsonObject config = JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
            return config.get("token").getAsString();
        } catch (Exception e) {
            return null;
        }
    }
}