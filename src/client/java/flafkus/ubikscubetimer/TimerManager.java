package flafkus.ubikscubetimer;

import flafkus.ubikscubetimer.config.ModConfig;

public class TimerManager {
    private static TimerManager instance;
    
    private long timerEndTime;
    private boolean timerActive;
    private boolean timerFinished;
    private final long TIMER_DURATION = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
    
    private TimerManager() {
        loadFromConfig();
    }
    
    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }
    
    public void startTimer() {
        timerEndTime = System.currentTimeMillis() + TIMER_DURATION;
        timerActive = true;
        timerFinished = false;
        saveToConfig();
        UbiksCubeTimer.LOGGER.info("Timer started for 2 hours");
    }
    
    public void reset() {
        timerEndTime = System.currentTimeMillis() + TIMER_DURATION;
        timerActive = true;
        timerFinished = false;
        saveToConfig();
        UbiksCubeTimer.LOGGER.info("Timer reset to 2 hours");
    }
    
    public void stop() {
        timerActive = false;
        timerFinished = false;
        saveToConfig();
    }
    
    public void endTimer() {
        timerActive = false;
        timerFinished = true;
        saveToConfig();
        UbiksCubeTimer.LOGGER.info("Timer ended manually, set to READY state");
    }
    
    public long getRemainingTime() {
        if (!timerActive) return 0;
        
        long remaining = timerEndTime - System.currentTimeMillis();
        if (remaining <= 0) {
            timerFinished = true;
            timerActive = false;
            saveToConfig();
            return 0;
        }
        return remaining;
    }
    
    public boolean isActive() {
        return timerActive;
    }
    
    public boolean isFinished() {
        return timerFinished;
    }
    
    public String getFormattedTime() {
        if (isFinished()) {
            return "READY!";
        }
        
        long remaining = getRemainingTime();
        if (remaining <= 0) {
            return "READY!";
        }
        
        long hours = remaining / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (remaining % (60 * 1000)) / 1000;
        
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
    
    public void tick() {
        if (timerActive) {
            getRemainingTime(); // This will update the finished state
        }
    }
    
    private void loadFromConfig() {
        ModConfig config = ModConfig.getInstance();
        this.timerEndTime = config.timerEndTime;
        this.timerActive = config.timerActive;
        this.timerFinished = config.timerFinished;
        
        // If we have a saved timer that should be active, check if it's expired
        if (timerActive && timerEndTime > 0) {
            long remaining = timerEndTime - System.currentTimeMillis();
            if (remaining <= 0) {
                timerFinished = true;
                timerActive = false;
                saveToConfig();
                UbiksCubeTimer.LOGGER.info("Loaded expired timer, setting to finished state");
            } else {
                UbiksCubeTimer.LOGGER.info("Loaded active timer with " + (remaining / 1000) + " seconds remaining");
            }
        } else if (timerEndTime == 0) {
            // First time running, initialize in ready state
            timerActive = false;
            timerFinished = true;
            UbiksCubeTimer.LOGGER.info("First time running, timer set to READY state");
        }
    }
    
    public void saveToConfig() {
        ModConfig config = ModConfig.getInstance();
        config.timerEndTime = this.timerEndTime;
        config.timerActive = this.timerActive;
        config.timerFinished = this.timerFinished;
        config.save();
    }
}
