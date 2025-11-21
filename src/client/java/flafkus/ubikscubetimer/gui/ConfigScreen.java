package flafkus.ubikscubetimer.gui;

import flafkus.ubikscubetimer.TimerManager;
import flafkus.ubikscubetimer.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private ModConfig config;
    
    private CheckboxWidget enabledCheckbox;
    private SliderWidget xSlider;
    private SliderWidget ySlider;
    private SliderWidget scaleSlider;
    private ButtonWidget resetTimerButton;
    private ButtonWidget toggleButton;
    private ButtonWidget endTimerButton;
    
    public ConfigScreen(Screen parent) {
        super(Text.literal("Ubik's Cube Timer Config"));
        this.parent = parent;
        this.config = ModConfig.getInstance();
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50;
        int spacing = 25;
        
        // Enable/Disable checkbox
        this.enabledCheckbox = CheckboxWidget.builder(Text.literal("Enable Timer Display"), this.textRenderer)
            .pos(centerX - 100, startY)
            .checked(config.enabled)
            .callback((checkbox, checked) -> {
                config.enabled = checked;
                config.save();
            })
            .build();
        this.addDrawableChild(this.enabledCheckbox);
        
        // X Position slider
        this.xSlider = new SliderWidget(centerX - 100, startY + spacing, 200, 20, 
            Text.literal("X Position: " + config.overlayX), 
            (double) config.overlayX / this.width) {
            
            @Override
            protected void updateMessage() {
                config.overlayX = (int) (this.value * ConfigScreen.this.width);
                this.setMessage(Text.literal("X Position: " + config.overlayX));
            }
            
            @Override
            protected void applyValue() {
                config.overlayX = (int) (this.value * ConfigScreen.this.width);
                config.save();
            }
        };
        this.addDrawableChild(this.xSlider);
        
        // Y Position slider
        this.ySlider = new SliderWidget(centerX - 100, startY + spacing * 2, 200, 20, 
            Text.literal("Y Position: " + config.overlayY), 
            (double) config.overlayY / this.height) {
            
            @Override
            protected void updateMessage() {
                config.overlayY = (int) (this.value * ConfigScreen.this.height);
                this.setMessage(Text.literal("Y Position: " + config.overlayY));
            }
            
            @Override
            protected void applyValue() {
                config.overlayY = (int) (this.value * ConfigScreen.this.height);
                config.save();
            }
        };
        this.addDrawableChild(this.ySlider);
        
        // Scale slider
        this.scaleSlider = new SliderWidget(centerX - 100, startY + spacing * 3, 200, 20, 
            Text.literal("Scale: " + String.format("%.1f", config.scale)), 
            (config.scale - 0.5) / 1.5) {
            
            @Override
            protected void updateMessage() {
                config.scale = (float) (0.5 + this.value * 1.5);
                this.setMessage(Text.literal("Scale: " + String.format("%.1f", config.scale)));
            }
            
            @Override
            protected void applyValue() {
                config.scale = (float) (0.5 + this.value * 1.5);
                config.save();
            }
        };
        this.addDrawableChild(this.scaleSlider);
        
        // Reset Timer button
        this.resetTimerButton = ButtonWidget.builder(Text.literal("Reset Timer"), button -> {
            TimerManager.getInstance().reset();
        })
        .dimensions(centerX - 100, startY + spacing * 4, 200, 20)
        .build();
        this.addDrawableChild(this.resetTimerButton);
        
        // Toggle Timer button
        this.toggleButton = ButtonWidget.builder(
            Text.literal(TimerManager.getInstance().isActive() ? "Stop Timer" : "Start Timer"), 
            button -> {
                TimerManager timer = TimerManager.getInstance();
                if (timer.isActive()) {
                    timer.stop();
                    button.setMessage(Text.literal("Start Timer"));
                } else {
                    timer.startTimer();
                    button.setMessage(Text.literal("Stop Timer"));
                }
            })
        .dimensions(centerX - 100, startY + spacing * 5, 200, 20)
        .build();
        this.addDrawableChild(this.toggleButton);
        
        // End Timer button (set to READY)
        this.endTimerButton = ButtonWidget.builder(Text.literal("End Timer (Set to READY)"), button -> {
            TimerManager.getInstance().endTimer();
        })
        .dimensions(centerX - 100, startY + spacing * 6, 200, 20)
        .build();
        this.addDrawableChild(this.endTimerButton);
        
        // Done button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> {
            this.client.setScreen(this.parent);
        })
        .dimensions(centerX - 50, startY + spacing * 8, 100, 20)
        .build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Update toggle button text
        TimerManager timer = TimerManager.getInstance();
        this.toggleButton.setMessage(Text.literal(timer.isActive() ? "Stop Timer" : "Start Timer"));
        
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        
        // Show current timer status
        String timerStatus = "Timer: " + timer.getFormattedTime();
        context.drawCenteredTextWithShadow(this.textRenderer, timerStatus, this.width / 2, this.height - 40, 0xFFFFFF);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
