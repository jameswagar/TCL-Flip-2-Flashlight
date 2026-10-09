package com.dumbphone.flashlight;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public final class FlashlightActivity extends Activity {
    private static final long LABEL_DURATION_MS = 800L;
    private final Handler handler = new Handler();
    private final Runnable hideLabel = new Runnable() {
        @Override public void run() {
            if (brightnessLabel != null) brightnessLabel.setVisibility(View.GONE);
        }
    };
    private FrameLayout whiteScreen;
    private TextView brightnessLabel;
    private int brightnessPercent = BrightnessPolicy.initialPercent();
    private ColorModePolicy.Mode colorMode;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        colorMode = ColorModePolicy.restore(getSharedPreferences("flashlight", MODE_PRIVATE)
                .getString("color_mode", null));
        brightnessPercent = BrightnessPolicy.restorePercent(
                getSharedPreferences("flashlight", MODE_PRIVATE)
                        .getInt("brightness_percent", BrightnessPolicy.initialPercent()));
        whiteScreen = new FrameLayout(this);
        whiteScreen.setBackgroundColor(colorMode.argb());
        whiteScreen.setFocusable(true);
        whiteScreen.setFocusableInTouchMode(true);

        brightnessLabel = new TextView(this);
        brightnessLabel.setTextColor(Color.BLACK);
        brightnessLabel.setTextSize(24f);
        brightnessLabel.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams labelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        whiteScreen.addView(brightnessLabel, labelParams);
        setContentView(whiteScreen);
        configureSystemBars();
        applyColorMode(false);
        whiteScreen.requestFocus();
        applyBrightness(true);
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (BrightnessPolicy.isAdjustmentKey(keyCode)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int direction = keyCode == KeyEvent.KEYCODE_DPAD_UP ? 1 : -1;
                brightnessPercent = BrightnessPolicy.adjust(brightnessPercent, direction);
                getSharedPreferences("flashlight", MODE_PRIVATE).edit()
                        .putInt("brightness_percent", brightnessPercent).commit();
                applyBrightness(true);
            }
            return keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN;
        }
        if (ColorModePolicy.isAdjustmentKey(keyCode)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int direction = keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1 : -1;
                colorMode = ColorModePolicy.adjust(colorMode, direction);
                getSharedPreferences("flashlight", MODE_PRIVATE).edit()
                        .putString("color_mode", colorMode.preferenceValue()).commit();
                applyColorMode(false);
            }
            return keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        return super.dispatchKeyEvent(event);
    }

    private void applyColorMode(boolean showLabel) {
        whiteScreen.setBackgroundColor(colorMode.argb());
        getWindow().setStatusBarColor(colorMode.argb());
        int menuBarId = getResources().getIdentifier("decor_menu_bar", "id", "android");
        View menuBar = menuBarId == 0 ? null : getWindow().getDecorView().findViewById(menuBarId);
        if (menuBar != null) colorDecorMenuBar(menuBar, colorMode.argb());
        if (showLabel) showStatus();
    }

    private void colorDecorMenuBar(View view, int color) {
        view.setBackgroundColor(color);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                colorDecorMenuBar(group.getChildAt(i), color);
            }
        }
    }

    private void configureSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setStatusBarContrastEnforced(false);
            getWindow().setNavigationBarContrastEnforced(false);
        }
    }

    private void applyBrightness(boolean showLabel) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = BrightnessPolicy.windowBrightness(brightnessPercent);
        getWindow().setAttributes(params);
        if (showLabel) showStatus();
    }

    private void showStatus() {
        brightnessLabel.setText(brightnessPercent + "%");
        brightnessLabel.setVisibility(View.VISIBLE);
        handler.removeCallbacks(hideLabel);
        handler.postDelayed(hideLabel, LABEL_DURATION_MS);
    }

    @Override protected void onPause() {
        super.onPause();
        if (!isChangingConfigurations()) finish();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }
}
