package flafkus.ubikscubetimer.keybinding;

import flafkus.ubikscubetimer.gui.ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybindings {
    private static KeyBinding openConfigKey;
    
    public static void register() {
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ubiks-cube-timer.open_config",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_U, // Default key: U
            "category.ubiks-cube-timer"
        ));
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new ConfigScreen(null));
            }
        });
    }
}
