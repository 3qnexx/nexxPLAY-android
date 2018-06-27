# nexxPLAY Android

## Installation guide

This section shows how to integrate the media player into an Android app. The App NexxPlayerTestSdk delivered also as source code with this SDK is an example of integration. The App contains a launcher activity MainActivity used to start PlayerActivity. The later uses NexxPlayer SDK to create a player, providing the rendering surface and a notification listener.

Copy "sdkrelease.aar" into "libs" directory of the application. Android Studio must be instructed to search "libs" folder for repositories (in build.gradle):

```
repositories {
  ...
  flatDir {
    dirs 'libs'
  }
  ...
}
```

The library must be then compiled into the app (see app/build.gradle)

```
dependencies { 
    ...
    compile(name:'sdkrelease', ext:'aar')
    ...
}
```

Since the NexxPlayer SDK library uses thirdparty components which could not be included with sdkrelease.aar, they must be also included into the build. Be sure that your build.gradle contains also these lines:

```
repositories { 
  mavenCentral()
   ...
  maven { url "https://repo.spring.io/release" } 
  maven { url "https://repo.spring.io/ milestone" } 
  maven { url "https://repo.spring.io/snapshot" }
  ...
}
```

```
dependencies { 
  ...
  compile 'com.google.code.gson:gson:2.3.1'
  compile 'org.springframework.android:springandroidresttemplate:2.0.0.M1'
  compile('org.simpleframework:simplexml:2.7.1') { exclude group: 'stax', module: 'staxapi' exclude group: 'xpp3', module: 'xpp3'
  }
  compile 'com.noveogroup.android:androidlogger:1.3.1' compile ‚org.apache.mina:mina-
  statemachine:2.0.9'
  // if using ima:
  compile 'com.google.ads.interactivemedia.v3:interactivemedia:3.2.1' compile 'com.google.android.gms:play-services-ads:8.4.0'
}
```

### Android TV
There is an additional AAR file for Android TV which uses the newest implementation of ExoPlayer as well as the Leanback library for controls.  Please add this AAR to your libs folder and add the following lines to your gradlew file:
```
implementation 'com.google.android.exoplayer:exoplayer:2.8.1'
implementation 'com.google.android.exoplayer:extension-mediasession:2.8.1'
implementation 'com.google.android.exoplayer:extension-leanback:2.8.1'
implementation 'com.android.support:support-v13:27.0.2'
```


> If possible add this repository as a git submodule into your project to ensure you always have the latest version of the sdk.
