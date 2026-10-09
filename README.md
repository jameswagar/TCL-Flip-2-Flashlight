# Flashlight

A standalone, keypad-first main-screen flashlight for TCL flip phones.

## Behavior

- Installs as **Flashlight** and appears directly in All Apps.
- Uses a straight-up yellow flashlight on the same solid black square treatment as Smart Tools, keeping it legible on both normal and highlighted launcher backgrounds.
- D-pad **Up/Down** adjusts window-only brightness in absolute 10% steps from 10% through 100%.
- D-pad **Left/Right** selects **White**, **Amber**, or **Red**, stopping at the ends rather than unexpectedly wrapping to white.
- The last color mode and brightness level are both saved and restored on the next launch.
- Disables Android's static launch preview so reopening goes directly to the restored color without a white flash.
- Brightness changes briefly display only the selected percentage; color changes need no label.
- System-bar areas match the selected hue so the display remains edge-to-edge without a white strip.
- Keeps the screen on only while the activity remains in the foreground.
- Exits when it loses the foreground; it does not wake the phone, bypass the keyguard, or change global system brightness.
- Has no dependency on Smart Tools. Smart Tools can hide it through its ordinary Hidden Apps feature like any other installed application.

For night vision, select **Red** and reduce brightness to 10–20%; hue alone does not compensate for excessive brightness on an LCD backlight.

## Build

```sh
bash test.sh
bash contract-test.sh
bash build.sh
```

Output: `build/Flashlight-v0.3.2-no-white-preview-dev.apk`
