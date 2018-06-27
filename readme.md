# nexxPLAY Android

## Installation guide

### Android TV
There is an additional AAR file for Android TV which uses the newest implementation of ExoPlayer as well as the Leanback library for controls.  Please add this AAR to your libs folder and add the following lines to your gradlew file:
```
implementation 'com.google.android.exoplayer:exoplayer:2.8.1'
implementation 'com.google.android.exoplayer:extension-mediasession:2.8.1'
implementation 'com.google.android.exoplayer:extension-leanback:2.8.1'
implementation 'com.android.support:support-v13:27.0.2'
```

> If possible add this repository as a git submodule into your project to ensure you always have the latest version of the sdk.
