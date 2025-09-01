package flafkus.ubikscubetimer;

import flafkus.ubikscubetimer.keybinding.ModKeybindings;
import flafkus.ubikscubetimer.render.TimerOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class UbiksCubeTimerClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Register the HUD overlay
		TimerOverlay.register();
		
		// Register keybindings
		ModKeybindings.register();
		
		// Register client tick event to update timer
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			TimerManager.getInstance().tick();
		});
		
		// Add shutdown hook to save timer state when game closes
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			TimerManager.getInstance().saveToConfig();
			UbiksCubeTimer.LOGGER.info("Timer state saved on shutdown");
		}));
		
		UbiksCubeTimer.LOGGER.info("Ubik's Cube Timer Client initialized!");
	}
}