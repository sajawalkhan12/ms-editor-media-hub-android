# MS Editor Android Project

- App display name: MS Editor
- Application ID: `com.mseditor.mediahub` (preserved)
- Launcher icon: supplied MS Editor logo
- Website backend: https://sajawalkhan12.github.io/ms-editor-media-hub-/

## Build APK
Run `gradle assembleDebug` in a Java 17 environment, or upload this project to GitHub and run the included GitHub Actions workflow (`Build Android APK`).

Note: this project currently displays the existing website inside Android WebView. The app label, launcher icon, and Android system-bar styling are updated; this is not a full native rewrite of all website screens. Installing over an existing app also requires a compatible signing certificate.


## Android WebView compatibility update
The Android host now includes a more complete WebView lifecycle/file-picker handler and injects a small Android-only CSS compatibility override after the page loads. This disables the fixed-height nested feed layout that can prevent post rendering/scrolling in some WebView versions. The live website source is not modified by this project ZIP.
