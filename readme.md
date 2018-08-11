# nexxPLAY Android (v.3.x.x)
We would like to enable you a smooth integration of NexxPlay into your existing project. The following guide will explain everything you need to know. There is a demo project "demo", where you can find a running example of the player.

`Please read the installation guide again if you used v.1 or v.2 of nexxPlay for Android.`

The PlayerEvents and MediaData have changed since 3.1.0 so the naming is consistently between all platforms. Please check the Event section for this. 

All releases are listed [in the SDK's feed](https://www.myget.org/feed/nexxtv/package/maven/tv.nexx/nexxplay-android).

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
    implementation 'tv.nexx:nexxplay-android:3.+'
    ...
}
```
That way the build script will look for the newest version of nexxPlay everytime a build of your app is being initiated. However if you want to stick to a specific version, you can always find a list of the releases
[on the nexxPlay release site](https://www.myget.org/feed/nexxtv/package/maven/tv.nexx/nexxplay-android)


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

The NexxPlay Android SDK implements two interfaces: NexxPlayer which is the interface for all media functions and NexxPlayerAndroid which is the Android specific interface.

#### NexxPlayerAndroid
The SDK needs to be triggered by some of the Android lifecycle methods. There are the following methods in NexxPlayerAndroid that need to be called in the Activit's particular method:
```
void onActivityResume();
void onActivityPause();
void onActivityDestroyed();
```

#### NexxPlayer
Let us get to the exciting part! With the NexxPlayer interface, you can control all the media related functionality. Here is a list of all methods:

##### void startPlay(String cid, String client, PlayMode playMode, String param, int startPosition, int startItem);
Start media

##### void swapToPos(int newPosition);
Directly swap to position in seconds in current media

##### void swap(String param);
Use current player instace to load another media

##### MediaData getMediaData();
Obtain an object containing meta-data of the current media item.

##### EnhancedMediaData getEnhancedMediaData();
Obtain an object containing meta-data of the current media item.

##### void setSSL(boolean ssl);
Set if ssl should be used or not

##### void enableCordova();
Enable Cordova mode.

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

##### void setUserIsTrackingOptOuted();
If you call this method, the VAST calls will be marked with a flag to not track the user.

##### float getCurrentTime();
get the current played time

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
Define if titels should be showen. Possible values are 0 = don't show, 1 = always show, 2 = show in fullscreen only


### Events
Your application may subscribe for receiving notifications from the player calling:

```
NexxPlayerNotification.Listener listener = this.nexxPlayer.addEventListener(listener);
```
There are multiple events, which may or may not have additional data as a secondary paramenter:

    ERROR,
    METADATA,
    PLAYERREADY,
    SHOWUI,
    HIDEUI,
    ENTERFULLSCREEN,
    EXITFULLSCREEN,
    STARTPLAYBACK,
    STARTPLAY,
    PLAY,
    PAUSE,
    ENDED,
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


