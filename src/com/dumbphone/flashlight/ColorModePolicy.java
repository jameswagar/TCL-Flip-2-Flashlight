package com.dumbphone.flashlight;

public final class ColorModePolicy {
    private static final int KEYCODE_DPAD_LEFT = 21;
    private static final int KEYCODE_DPAD_RIGHT = 22;

    public enum Mode {
        WHITE("white", "White", 0xffffffff),
        AMBER("amber", "Amber", 0xffff6600),
        RED("red", "Red", 0xffff0000);

        private final String preferenceValue;
        private final String label;
        private final int argb;

        Mode(String preferenceValue, String label, int argb) {
            this.preferenceValue = preferenceValue;
            this.label = label;
            this.argb = argb;
        }

        public String preferenceValue() { return preferenceValue; }
        public String label() { return label; }
        public int argb() { return argb; }
    }

    private ColorModePolicy() {}

    public static Mode restore(String stored) {
        if (stored != null) {
            for (Mode mode : Mode.values()) {
                if (mode.preferenceValue().equals(stored)) return mode;
            }
        }
        return Mode.WHITE;
    }

    public static boolean isAdjustmentKey(int keyCode) {
        return keyCode == KEYCODE_DPAD_LEFT || keyCode == KEYCODE_DPAD_RIGHT;
    }

    public static Mode adjust(Mode current, int direction) {
        Mode safe = current == null ? Mode.WHITE : current;
        int index = safe.ordinal();
        if (direction > 0 && index < Mode.values().length - 1) index++;
        else if (direction < 0 && index > 0) index--;
        return Mode.values()[index];
    }
}
