package flafkus.ubikscubetimer.render;

import flafkus.ubikscubetimer.TimerManager;
import flafkus.ubikscubetimer.config.ModConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class TimerOverlay {
    private static final TimerOverlay INSTANCE = new TimerOverlay();
    
    public static TimerOverlay getInstance() {
        return INSTANCE;
    }
    
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        ModConfig config = ModConfig.getInstance();
        if (!config.enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        TimerManager timer = TimerManager.getInstance();
        String labelText = "Ubik's Cube: ";
        String timeText = timer.getFormattedTime();
        String fullText = labelText + timeText;
        
        TextRenderer textRenderer = client.textRenderer;
        int labelWidth = textRenderer.getWidth(labelText);
        int fullTextWidth = textRenderer.getWidth(fullText);
        int textHeight = textRenderer.fontHeight;
        
        // Calculate scaled dimensions
        int scaledWidth = (int) (fullTextWidth * config.scale);
        int scaledHeight = (int) (textHeight * config.scale);
        
        // Add padding
        int padding = (int) (4 * config.scale);
        int boxWidth = scaledWidth + (padding * 2);
        int boxHeight = scaledHeight + (padding * 2);
        
        // Draw background box
        drawContext.fill(
            config.overlayX, 
            config.overlayY, 
            config.overlayX + boxWidth, 
            config.overlayY + boxHeight, 
            config.backgroundColor
        );
        
        // Draw border
        drawContext.drawBorder(
            config.overlayX, 
            config.overlayY, 
            boxWidth, 
            boxHeight, 
            0xFF555555
        );
        
        // Draw text with color coding
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(config.overlayX + padding, config.overlayY + padding, 0);
        drawContext.getMatrices().scale(config.scale, config.scale, 1.0f);
        
        // Draw "Ubik's Cube: " in white
        drawContext.drawText(
            textRenderer, 
            labelText, 
            0, 
            0, 
            0xFFFFFF, // White color
            true
        );
        
        // Determine timer color: green if ready, red if counting down
        int timerColor;
        if (timer.isFinished() || timeText.equals("READY!")) {
            timerColor = 0x00FF00; // Green
        } else {
            timerColor = 0xFF0000; // Red
        }
        
        // Draw the timer text in the appropriate color
        drawContext.drawText(
            textRenderer, 
            timeText, 
            (int) (labelWidth / config.scale), // Position after the label
            0, 
            timerColor, 
            true
        );
        
        drawContext.getMatrices().pop();
    }
    
    @SuppressWarnings("deprecation") // TODO: Update to HudLayerRegistrationCallback in future versions
    public static void register() {
        HudRenderCallback.EVENT.register(INSTANCE::onHudRender);
    }
}
