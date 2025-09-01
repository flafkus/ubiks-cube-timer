package flafkus.ubikscubetimer.mixin.client;

import flafkus.ubikscubetimer.TimerManager;
import flafkus.ubikscubetimer.UbiksCubeTimer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class ChatMessageMixin {
    // Pattern to match "You earned X Motes in this match!" with flexible surrounding text
    // Updated to handle numbers with commas like "10,000"
    private static final Pattern MOTES_PATTERN = Pattern.compile(".*You earned [\\d,]+ Motes in this match!.*", Pattern.CASE_INSENSITIVE);
    
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void onChatMessage(Text message, CallbackInfo ci) {
        String messageText = message.getString();
        // Also get the plain string version in case there's formatting
        String plainText = stripFormatting(messageText);
        
        // Debug: Log all chat messages to help troubleshoot
        UbiksCubeTimer.LOGGER.info("Chat message received: '" + messageText + "'");
        if (!messageText.equals(plainText)) {
            UbiksCubeTimer.LOGGER.info("Plain text version: '" + plainText + "'");
        }
        
        // Try both formatted and plain text versions
        if (MOTES_PATTERN.matcher(messageText).matches() || MOTES_PATTERN.matcher(plainText).matches()) {
            UbiksCubeTimer.LOGGER.info("MATCHED! Detected motes message, starting timer!");
            TimerManager.getInstance().startTimer();
        } else if (messageText.toLowerCase().contains("motes") && messageText.toLowerCase().contains("match")) {
            UbiksCubeTimer.LOGGER.info("Contains motes and match but didn't match pattern");
            UbiksCubeTimer.LOGGER.info("Testing pattern against: '" + messageText + "'");
        }
    }
    
    private String stripFormatting(String text) {
        // Remove Minecraft color codes (§ followed by any character)
        return text.replaceAll("§.", "");
    }
}
