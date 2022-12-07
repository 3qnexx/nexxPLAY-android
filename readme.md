# nexxPLAY for android

## Player documentation

Please find the complete documentation of the nexxPLAY android SDK following this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-android).

nexxPLAY for android also supports Flutter. Find more Info about this following this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-flutter).

## Widget documentation

Please find the complete documentation of the nexxPLAY Widget following this [link](https://play.docs.nexx.cloud/widgets/widgets-for-native-apps/android-widget).

## TV Recommendation Tiles documentation

Please find the complete documentation of the nexxPLAY TV Recommendation Tiles following this [link](https://play.docs.nexx.cloud/widgets/widgets-for-native-apps/androidtv-channel).


## Changelog

### v6.3.01
* fixing minor Issues with muted Start

### v6.3.00
* android 13 is now officially supported and default Target
* enable Method to use a global MediaSession instead of temporary ones
* Ads will be controlled only by IMA from now on
* adding Support for Realtime Premiere Control
* adding Support for external TextTracks
* adding Support for "Hero" Audio UI
* adding Support for muted Start and muted Ad Control
* **Breakings Changes**
  - the getCaptions() SDK Method is now called getTextTracks()
  - the getCaptions(String Language) SDK Method has been removed
  - the Response Type of the SDK Method getCurrentPlaybackState() has been changed to CurrentPlaybackState and its Methods have been slightly changed
  - the Signature of the "onPlayerStateChanged" Method from NexxPLAYNotification.Listener has been changed. The initial "playWhenReady" (boolean) Parameter was always true and therefore removed
  - the Ad Functionality has been removed completely from the SDK in order to not include References to Google IMA in the Code Base / Manifest
    - if your Implementation needs (Frontend) Ads, you need to include the new tv.nexx.android.admanager SDK and use an Instance of this AdManager in the Environment Configuration 

### v6.2.25
- fixing an Issue with remote Media

### v6.2.23
- fixing an Issue with Audio without Captions on some Devices
- adding Support for Audio Mini / Micro UI
- adding Support for new "Thin" Icon UI

### v6.2.22
- fixing an Issue with local Media
- fixing an Issue with Bumpers

### v6.2.20
- fixing an Issue with Realtime Control
- adding Support for Scene HotSpot
- adding Support for Audio Chapters
- internal Refactoring for optimized StartUp Time

### v6.2.11
- fixing an Issue with Progress Events
- fixing an Issue with AVIF Support Detection

### v6.2.10
* finishing Support for Android Accessibility
* adding Support for RTL Orientation
* adding Support for Realtime Captions of LiveStreams
* adding Support for "artwork" StartScreen

### v6.2.01
- fixed an Issue with SeekButton Overrides

### v6.2.00
* Chromecast is now officially supported
* android 12L is now officially supported and default Target
* added Support for "Coming-Up-Next Overlays"
* added Support for Attachment Downloads
* added Support for Awards in Info Overlay
* added Support for Info Overlay on TV
* added Support for Premiere Events (Download and Join)
* added Support for "made for Kids" Rules
* added Support for new Streamtype "Rack"
* added getConnectedFiles() SDK Method            
* added getCurrentMediaParent() SDK Method     
* fixed various UI Issues
* **Breakings Changes**
  - the getCaptionData() SDK Method is now called getCaptions()
  - the getCaptionLanguages() SDK Method has been removed
  - the getAudioLanguages() SDK Method is now called getAudioTracks()