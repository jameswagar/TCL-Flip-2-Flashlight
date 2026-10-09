package com.dumbphone.flashlight;

public final class BrightnessPolicyTest {
    public static void main(String[] args) {
        expect(10, BrightnessPolicy.MIN_PERCENT, "minimum");
        expect(100, BrightnessPolicy.MAX_PERCENT, "maximum");
        expect(100, BrightnessPolicy.initialPercent(), "starts as a flashlight");
        expect(10, BrightnessPolicy.restorePercent(-5), "stored value clamps to minimum");
        expect(50, BrightnessPolicy.restorePercent(50), "stored value restores exactly");
        expect(100, BrightnessPolicy.restorePercent(250), "stored value clamps to maximum");
        expect(100, BrightnessPolicy.adjust(100, 1), "up saturates");
        expect(90, BrightnessPolicy.adjust(100, -1), "down steps");
        expect(10, BrightnessPolicy.adjust(10, -1), "down saturates");
        expect(20, BrightnessPolicy.adjust(15, 1), "normalizes before stepping up");
        expect(10, BrightnessPolicy.adjust(15, -1), "normalizes before stepping down");
        expect(true, BrightnessPolicy.isAdjustmentKey(19), "dpad up");
        expect(true, BrightnessPolicy.isAdjustmentKey(20), "dpad down");
        expect(false, BrightnessPolicy.isAdjustmentKey(23), "dpad center is not brightness");
        expect(1.0f, BrightnessPolicy.windowBrightness(100), "100 percent float");
        expect(0.1f, BrightnessPolicy.windowBrightness(10), "10 percent float");
        System.out.println("BrightnessPolicyTest PASS");
    }

    private static void expect(int expected, int actual, String label) {
        if (expected != actual) throw new AssertionError(label + ": expected " + expected + " got " + actual);
    }

    private static void expect(boolean expected, boolean actual, String label) {
        if (expected != actual) throw new AssertionError(label + ": expected " + expected + " got " + actual);
    }

    private static void expect(float expected, float actual, String label) {
        if (Math.abs(expected - actual) > 0.0001f) {
            throw new AssertionError(label + ": expected " + expected + " got " + actual);
        }
    }
}
