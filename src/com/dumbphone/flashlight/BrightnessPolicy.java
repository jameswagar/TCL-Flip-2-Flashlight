package com.dumbphone.flashlight;

public final class BrightnessPolicy {
    public static final int MIN_PERCENT = 10;
    public static final int MAX_PERCENT = 100;
    public static final int STEP_PERCENT = 10;
    private static final int KEYCODE_DPAD_UP = 19;
    private static final int KEYCODE_DPAD_DOWN = 20;

    private BrightnessPolicy() {}

    public static int initialPercent() {
        return MAX_PERCENT;
    }

    public static int restorePercent(int storedPercent) {
        return clamp(storedPercent);
    }

    public static boolean isAdjustmentKey(int keyCode) {
        return keyCode == KEYCODE_DPAD_UP || keyCode == KEYCODE_DPAD_DOWN;
    }

    public static int adjust(int currentPercent, int direction) {
        int current = clamp(currentPercent);
        if (direction > 0) {
            int next = current % STEP_PERCENT == 0
                    ? current + STEP_PERCENT
                    : ((current + STEP_PERCENT - 1) / STEP_PERCENT) * STEP_PERCENT;
            return clamp(next);
        }
        if (direction < 0) {
            int next = current % STEP_PERCENT == 0
                    ? current - STEP_PERCENT
                    : (current / STEP_PERCENT) * STEP_PERCENT;
            return clamp(next);
        }
        return current;
    }

    public static float windowBrightness(int percent) {
        return clamp(percent) / 100.0f;
    }

    private static int clamp(int percent) {
        return Math.max(MIN_PERCENT, Math.min(MAX_PERCENT, percent));
    }
}
