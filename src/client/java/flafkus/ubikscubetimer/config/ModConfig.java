package flafkus.ubikscubetimer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ubiks-cube-timer.json");
    
    private static ModConfig instance;
    
    // Config fields
    public boolean enabled = true;
    public int overlayX = 10;
    public int overlayY = 10;
    public float scale = 1.0f;
    public int backgroundColor = 0x80000000; // Semi-transparent black
    public int textColor = 0xFFFFFF; // White text
    
    // Timer persistence fields
    public long timerEndTime = 0; // When the timer should end (0 = not set)
    public boolean timerActive = false; // Whether timer is currently running
    public boolean timerFinished = true; // Whether timer is in finished state
    
    public static ModConfig getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }
    
    public static ModConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, ModConfig.class);
            }
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
        
        ModConfig config = new ModConfig();
        config.save();
        return config;
    }
    
    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(this);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}
