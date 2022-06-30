# nexxPLAY for android

## Player documentation

Please find the complete documentation of the nexxPLAY android SDK following this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-android).

nexxPLAY for android also supports Flutter. Find more Info about this following this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-flutter).

## Widget documentation

Please find the complete documentation of the nexxPLAY Widget following this [link](https://play.docs.nexx.cloud/widgets/widgets-for-native-apps/android-widget).

## TV Recommendation Tiles documentation

Please find the complete documentation of the nexxPLAY TV Recommendation Tiles following this [link](https://play.docs.nexx.cloud/widgets/widgets-for-native-apps/androidtv-channel).


## Changelog

### v6.2.21
- fixing an Issue with local Media

### v6.2.20
- fixing an Issue with Realtime Control
- adding Support for Chapter HotSpot
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


### v6.1.05
- fixed an Issue with NetworkConnectivity Changes

### v6.1.04
- added Option to close MediaSession Notification during Playback
- added Support for Quality Reporting
- fixed an Issue with PiP UI
- fixed an Issue with Environment.showCloseButtonOnFullscreen
- fixed an Issue with DataSaver Mode

### v6.1.03
- fixed an Issue with Video DRM
- fixed UI on ExitScreen
- fixed an Issue with swap* Methods on TV
- fixed an Issue with PiP on TV
- fixed an Issue with TextMessage Generation
- fixed an Issue with Audio in Variable Bitrate
- fixed an Issue with custom Attributes

### v6.1.02
- fixed an Issue with Seeking in Remote Audio
- fixed an Issue with MultiLine Title UI
- fixed an Issue with seekTo Method

### v6.1.00
- added Support for localMedia and Offline Playback
- added Support for Podcast Overlay and Linking
- added Support for Media with OPUS Audio Codec
- added Support for Media with HEVC Video Codec
- added Support for LiveStreams with DVR
- added Support for WatchNext System on TV
- fixed an Issue with Premiere Mode
- fixed an Issue with AutoPlay in DataSaver Mode
- fixed an Issue with Handling of List-PlayModes
- **Breakings Changes**
  - the getMediaData() Method has been replaced by the new getCurrentMedia() and getCurrentPlaybackState() Methods.  
