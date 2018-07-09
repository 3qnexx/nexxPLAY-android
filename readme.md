# nexxPLAY Android

## Installation guide

> If possible add this repository as a git submodule into your project to ensure you always have the latest version of the sdk.

This section shows how to integrate the media player into an Android app.

### Gradle

Copy the AAR file into "libs" directory of the application. Android Studio must be instructed to search "libs" folder for repositories (in build.gradle):

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
### User Interface
NexxPlay needs a root anchor view which should be a FrameLayout. Please add something likes this to your layout, depending on your needs:

```
    <FrameLayout
        android:id="@+id/root"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

### Activity

Create the NexxPlayer object providing API by calling the factory method within your
Activity: 

```
this.nexxPlayerAndroid = NexxFactory.createNexxPlayer(this);
```

The NexxPlayer needs a single view. The root view is the view group where the media player control layout (the player
skin) is shown. Also the player needs the Acitivity's window
```
ViewGroup root = (ViewGroup) findViewById(R.id.root);
this.nexxPlayerAndroid.setViewRoot(root);
this.nexxPlayerAndroid.setWindow(getWindow());
```

### Player methods

### Events
Your application may subscribe for receiving notifications from the player calling:

```
NexxPlayerNotification.Listener listener = this.nexxPlayer.addEventListener(listener);
```
There are multiple events, which may or may not have additional data as a secondary paramenter:

    ERROR,
    META_DATA_RECEIVED,
    READY,
    SHOW_UI,
    HIDE_UI,
    ENTER_FULLSCREEN,
    EXIT_FULLSCREEN,
    START_PLAYBACK,
    START_PLAY,
    PLAY,
    PAUSE,
    ENDED,
    PLAY_POS,
    AD_STARTED,
    AD_ENDED,
    AD_ERROR,
    AD_CLICKED,
    AD_RESUMED,
    AD_CALLED,
    MUTE,
    UNMUTE,
    PLAYED_25_PERCENT,
    PLAYED_50_PERCENT,
    PLAYED_75_PERCENT,
    PLAYED_95_PERCENT

### Android TV
There is an additional AAR file for Android TV which uses the newest implementation of ExoPlayer as well as the Leanback library for controls.  Please add this AAR to your libs folder and add the following lines to your gradlew file:
```
implementation 'com.google.android.exoplayer:exoplayer:2.8.1'
implementation 'com.google.android.exoplayer:extension-mediasession:2.8.1'
implementation 'com.google.android.exoplayer:extension-leanback:2.8.1'
implementation 'com.android.support:support-v13:27.0.2'
```



