# nexxPLAY Android
We would like to enable you a smooth integration of NexxPlay into your existing project. The following guide will explain everything you need to know. There is a demo project "demo", where you can find a running example of the player.

All releases are listed [in the SDK's feed](https://www.myget.org/feed/nexxtv/package/maven/tv.nexx/nexxplay-android).

## Changelog

#### v. 4.4.10
- Fixes for Downloading Handling of localMedia Audio
- Version Update for ExoPlayer

#### v. 4.4.9
- bugfixes for Offline Engine and localMedia Audio

#### v. 4.4.6
- bugfixes

#### v. 4.4.5
- upgraded google ima
- upgraded exoplayer to v.2.12.0
- internal change of representations
- notifcation changes
- reporting changes


## Installation guide

This section shows how to integrate the media player into an Android app.

### Gradle

Let us start by adding the nexx.tv Maven repository in your root Gradle file. Under the section allprojects you can find the repositories section. Please add the following line:

```
...
maven { url "https://www.myget.org/F/nexxtv/maven" }
...
```

We are almost done for the Gradle files. The last step is to include the following line to your app Gradle file (see app/build.gradle). 

```
dependencies { 
    ...
    implementation 'tv.nexx:nexxplay-android:4.+'
    ...
}
```
That way the build script will look for the newest version of nexxPlay everytime a build of your app is being initiated. However if you want to stick to a specific version, you can always find a list of the releases
[on the nexxPlay release site](https://www.myget.org/feed/nexxtv/package/maven/tv.nexx/nexxplay-android)

### Support for older Android versions
If you need to support Android versions prior to SDK version 21 (but min v17), please also add the uses-sdk entry to your AndroidManifest.xml. It should look something like this:

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="tv.nexx.nexxtvtesting.app">

    <uses-sdk tools:overrideLibrary="androidx.tvprovider,androidx.recommendation,android.support.media.tv,android.support.recommendation,android.support.v17.leanback,com.google.android.exoplayer2.ext.leanback"/>
    
    ...YOUR MANIFEST CONTENT...
    
</manifest>
```
This will not cause issues because the support classes will only be used by devices which are on Android SDK version 21 or higher. 

### User Interface
NexxPlay needs a root anchor view which should be a FrameLayout. Please add something likes this to your layout, depending on your needs:

```
    <FrameLayout
        android:id="@+id/root"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

#### Layout for Audio player
If you would like to add the player as an audio player, you should set the height and the width to 0dp and create your own UI for now (v.4.3.0)


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

The NexxPlay Android SDK implements two interfaces: NexxPlayer which is the interface for all media functions and NexxPlayerAndroid which is the Android specific interface.

#### NexxPlayerAndroid
The SDK needs to be triggered by some of the Android lifecycle methods. There are the following methods in NexxPlayerAndroid that need to be called in the Activit's particular method:
```
void onActivityResume();
void onActivityPause();
void onActivityDestroyed();
```

##### PiP Mode
In addition to these lifecycle methods, please also add the following methods if you want to use Picture in Pictore mode:

```
void onUserLeaveHint();
void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig);
```

Please also add the following to your app's Manifest corresponding to the Player Activitiy:

```
android:configChanges="orientation|keyboardHidden|screenSize|smallestScreenSize|screenLayout"
android:supportsPictureInPicture="true"
android:resizeableActivity="true"
```

#### NexxPlayer
Let us get to the exciting part! With the NexxPlayer interface, you can control all the media related functionality. Here is a list of all methods:

##### void startPlay(String cid, String client, PlayMode playMode, String param, int startPosition, int startItem);
Start media

##### void startPlayWithGlobalID(String sessionID, String domainID, int globalID);
Start media by global id

##### void startPlayWithRemoteMedia(String sessionID, String domainID, PlayMode playMode, String provider, String reference);
Start to play media from sources outside Omnia

##### void play();
Starts to play media if it is paused

##### void pause();
Pauses media if there is one playing

##### void swapToPos(int newPosition);
Directly swap to position in current playlist

##### void swap(String param);
Use current player instace to load another media

##### void seekToPos(int pos);
Seek to position in video (in seconds)

##### MediaData getMediaData();
Obtain an object containing meta-data of the current media item.

##### EnhancedMediaData getEnhancedMediaData();
Obtain an object containing meta-data of the current media item.

##### void setSSL(boolean ssl);
Set if ssl should be used or not

##### void enableCordova();
Enable Cordova mode.

##### void setAlwaysFullscreen();
Sets the player to always be in fullscreen mode and remove the fullscreen button.

##### void setLanguage(String language);
Set explicit language.

##### void setDataMode(DataMode dataMode);
Set static or usual method to receive data. by default, API is set.

##### void clearCaches();
clear all caches

##### void setPlayLicense(int playLicense);
Adjust domain settings

##### void setWebURLRepresentation(String url);
set web url for given media to add in vast url

##### void setLoaderSkin(LoaderSkin skin);
skin of loading indicator

##### void setStreamingFilter(String streamingFilter);
Streaming filter for bit-rates

##### void setViewParentID(String viewParentID);
e.g. playlist-PLAYLIST-ID

##### void setUserHash(String hash);
set hash for current user

#### void setConsentString(String ConsentString);
set ConsentString from TCF/CMP Environment for VAST Settings

##### void setUserIsTrackingOptOuted();
If you call this method, the VAST calls will be marked with a flag to not track the user.

##### void setLanguage(String language);
Set explicit language

##### float getCurrentTime();
get the current played time

##### Caption[] getCaptionData();
Get all captions for media that is currently playing

##### Caption[] getCaptionData(String forLanguage);
Get all captions for media that is currently playing depending on selected language


#### Override methods

##### void overrideMenu(MenuVisibilityMode mode);
Specifies how the menu should be shown

##### void overrideAutoPlay(AutoPlayMode mode);
Specifies how the auto-play should behave

##### void overrideStartMuted(MutedMode mode);
Specifies whether or not to start muted.

##### void overrideExitPlayMode(ExitPlayMode mode);
Specifies what happen at exit.

##### void overrideAutoPlayNext(AutoPlayNextMode mode);
Specifies how auto-next should behave.

##### void overrideCaptionMode(String captionMode);
Set the caption mode ('none', 'select', 'selectandstart' or 'always')

##### void overridePreroll(String url);
Manually set the VAST PreRoll URL

##### void overrideMidroll(String url, int interval);
Manually set the VAST MidRoll URL AND the Interval in Minutes.

##### void overridePostroll(String url);
Manually set the VAST PostRoll URL

##### void overrideAdProvider(String providerCode);
Manually set the VAST Provider Code (as defined within nexxOMNIA)

##### void overrideAdType(String adType);
Define ad consuming type. Possible values are ima / vast

##### void overrideTitles(int allowTitles);
Define if titles should be visible. Possible values are 0 = don't show, 1 = always show, 2 = show in fullscreen only


### Local media
The player also supports the download and playback of local audio files.

To use this Feature, the App has to make use of the OfflineEngine:

`OfflineEngine offlineEngine = OfflineEngine.getInstance(Context context);`

Configure it for the current Domain (and optionally, a loggedin User):

`offlineEngine.initForDomainAndUser(Class offlineActivity, int domain, int userID, String userHash, String session, OfflineCallback callback)`

For explicit Download of a specific Item:

`offlineEngine.startDownloadLocalMedia(String mediaID, StreamType streamType, @Nullable String provider, @Nullable SingleDownloadListener listener)`


The complete List of Methods is listed here:

##### void startDownloadLocalMedia(String mediaID, playMode streamType, String provider (optional), SingleDownloadListener listener (optional))
Initiates the download of the media file, meta data and the cover.

##### Array listLocalMedia(playMode streamType (optional))
Lists all downloaded media files for the given streamtype.

##### boolean hasDownloadOfLocalMedia(String mediaID, playMode streamType, String provider (optional))
Indicates whether the media with the given ID and streamtype has been successfully downloaded.

##### void removeLocalMedia(String mediaID, playMode streamType, String provider (optional))
Removes the media file, meta data and cover for the given ID and streamtype.

##### void clearLocalMedia(playMode streamType (optional))
Removes all files for a given streamtype. If no streamtype is set, all downloaded files will be removed.

##### long diskSpaceUsedForLocalMedia()
Returns the disk space used by the offline content (covers, media files) in bytes.


### Events
Your application may subscribe for receiving notifications from the player calling:

```
NexxPlayerNotification.Listener listener = this.nexxPlayer.addEventListener(listener);
```
There are multiple events, which may or may not have additional data as a secondary paramenter:

    ERROR,
    METADATA,
    PLAYERREADY,
    ENTERFULLSCREEN,
    EXITFULLSCREEN,
    STARTPLAYBACK,
    STARTPLAY,
    PLAY,
    PAUSE,
    ENDED,
    ENDEDALL,
    ADSTARTED,
    ADENDED,
    ADERROR,
    ADCLICKED,
    ADRESUMED,
    ADCALLED,
    MUTE,
    UNMUTE,
    PROGRESS25,
    PROGRESS50,
    PROGRESS75,
    PROGRESS95,
    SECOND,
    QUARTER,
    
### Troubleshooting

```
AGPBI: {"kind":"error","text":"error: resource android:attr/ttcIndex not found.","sources":[{"file":"/path/.gradle/caches/transforms-1/files-1.1/appcompat-v7-28.0.0.aar/XXX/res/values/values.xml","position":{"startLine":1303,"startColumn":4,"startOffset":70911,"endColumn":68,"endOffset":70975}}],"original":"","tool":"AAPT"}
> Task :app:processDebugResources FAILED
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':app:processDebugResources'.
> Failed to process resources, see aapt output above for details.

```

This is an issue related to the used SDK version to compile the project. It seems you are using older packages (e.g. v.27) of the support libraries. In this case please add the following to your root build.gradle:

```
subprojects {
    project.configurations.all {
        resolutionStrategy.eachDependency { details ->
            if (details.requested.group == 'com.android.support'
                    && !details.requested.name.contains('multidex')
                    && !details.requested.name.contains('support-tv')) {
                details.useVersion "27.0.2" // <-- YOUR SUPPORT LIBS VERSION
            }
        }
    }
}
```

### Flutter Support

To make the native SDK work in a Flutter App, follow these Steps:

in flutter Wiget override where you want to use player

```
@override
void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);    
    if (state == AppLifecycleState.paused) {
        _controller.callOnActivityPause();
    } else if (state == AppLifecycleState.resumed) {
        setState(() {
        _controller.callOnActivityResume();
    });
}
```

add to init

`WidgetsBinding.instance.addObserver(this);`

and use _WidgetsBindingObserver_

then use `NexxPlayerViewController _controller;`

add it to a Widget

```
final NexxPlayer nexxPlayer = NexxPlayer(...,onPlayerCreated: (controller) {
    _controller = controller;
    _controller.callOnActivityResume();
    },
);
```


Add Functions to the (Dart) Contoller:

```
Future callOnActivityResume() {
    return _channel.invokeMethod('onActivityResume', {});
}
Future callOnActivityPause() {
    return _channel.invokeMethod('onActivityPause', {});
}
```

and finally, replace the Java Constructor like outlined here:

```
public NexxPlayer(Context context, int id, BinaryMessenger messenger, Activity activity) {
    this.androidContext = activity;
    this.channel = new MethodChannel(messenger, "de.vonaffenfels.nexxtv/player/" + id);

    RelativeLayout rlayout = new RelativeLayout(this.androidContext);

    this.channel.setMethodCallHandler((MethodChannel.MethodCallHandler) this);
    FrameLayout layout = new FrameLayout(this.androidContext);
    layout.setLayoutParams((new android.widget.RelativeLayout.LayoutParams(-1, -2)));
    layout.setDescendantFocusability(393216);
    this.container = rlayout; 

    rlayout.addView(layout);

    NexxPlayerAndroid player = NexxFactory.createNexxPlayer(this.androidContext);
    this.nexxPlayer = player;
    this.nexxPlayer.setViewRoot(layout);
    this.nexxPlayer.setWindow(activity.getWindow());
}
```

and finally, add the onMessage Call:

```
case "onActivityResume":
    Log.d(this.TAG, "Native ---RESUME--- ");
    nexxPlayer.onActivityResume();
    return;
case "onActivityPause":
    Log.d(this.TAG, "Native ---PAUSE--- ");
    nexxPlayer.onActivityPause();
    return;
```




### Android TV
The nexxPlay SDK will work on Android TV, too. However there will be another GUI which is based on Leanback. If you want to use the SDK on Android TV, do not forget to add the Leanback Theme to your app.

In order to have the SDK work properly on Android TV, please also add the following to your Player Activity:

```
@Override
public boolean dispatchKeyEvent(KeyEvent event) {

    if(this.player != null) {
        this.player.dispatchKeyEvent(event);
    }

    return super.dispatchKeyEvent(event);

}
```
