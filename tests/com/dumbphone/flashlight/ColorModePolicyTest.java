package com.dumbphone.flashlight;

public final class ColorModePolicyTest {
    public static void main(String[] args) {
        expect(ColorModePolicy.Mode.WHITE, ColorModePolicy.restore(null), "null defaults white");
        expect(ColorModePolicy.Mode.WHITE, ColorModePolicy.restore("invalid"), "invalid defaults white");
        expect(ColorModePolicy.Mode.AMBER, ColorModePolicy.restore("amber"), "restore amber");
        expect(ColorModePolicy.Mode.RED, ColorModePolicy.restore("red"), "restore red");

        expect(ColorModePolicy.Mode.AMBER,
                ColorModePolicy.adjust(ColorModePolicy.Mode.WHITE, 1), "right enters amber");
        expect(ColorModePolicy.Mode.RED,
                ColorModePolicy.adjust(ColorModePolicy.Mode.AMBER, 1), "right enters red");
        expect(ColorModePolicy.Mode.RED,
                ColorModePolicy.adjust(ColorModePolicy.Mode.RED, 1), "right clamps at red");
        expect(ColorModePolicy.Mode.AMBER,
                ColorModePolicy.adjust(ColorModePolicy.Mode.RED, -1), "left enters amber");
        expect(ColorModePolicy.Mode.WHITE,
                ColorModePolicy.adjust(ColorModePolicy.Mode.AMBER, -1), "left enters white");
        expect(ColorModePolicy.Mode.WHITE,
                ColorModePolicy.adjust(ColorModePolicy.Mode.WHITE, -1), "left clamps at white");

        expect(true, ColorModePolicy.isAdjustmentKey(21), "dpad left");
        expect(true, ColorModePolicy.isAdjustmentKey(22), "dpad right");
        expect(false, ColorModePolicy.isAdjustmentKey(19), "dpad up is brightness only");
        expect("White", ColorModePolicy.Mode.WHITE.label(), "white label");
        expect("Amber", ColorModePolicy.Mode.AMBER.label(), "amber label");
        expect("Red", ColorModePolicy.Mode.RED.label(), "red label");
        expect("amber", ColorModePolicy.Mode.AMBER.preferenceValue(), "stable preference value");
        expect(0xffff6600, ColorModePolicy.Mode.AMBER.argb(), "warm orange amber");
        expect(0xffff0000, ColorModePolicy.Mode.RED.argb(), "deep red");
        System.out.println("ColorModePolicyTest PASS");
    }

    private static void expect(Object expected, Object actual, String label) {
        if (!expected.equals(actual)) throw new AssertionError(label + ": expected " + expected + " got " + actual);
    }

    private static void expect(boolean expected, boolean actual, String label) {
        if (expected != actual) throw new AssertionError(label + ": expected " + expected + " got " + actual);
    }

    private static void expect(int expected, int actual, String label) {
        if (expected != actual) throw new AssertionError(label + ": expected 0x" + Integer.toHexString(expected)
                + " got 0x" + Integer.toHexString(actual));
    }
}
