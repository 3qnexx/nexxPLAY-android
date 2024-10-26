# nexxPLAY for android

## Player documentation

Please find the complete documentation of the nexxPLAY android SDK following this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-android).

If you need Ad Support in the Player, please notice, that starting with Version 6.3.00 this Functionality needs the AdManager Plugin. For more Information, please follow this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-android#ad-support)

nexxPLAY for android also supports Flutter. Find more Info about this following this [link](https://play.docs.nexx.cloud/native-players/nexxplay-for-flutter).

## Widget documentation

Please find the complete documentation of the nexxPLAY Widget following this [link](https://play.docs.nexx.cloud/widgets/widgets-for-native-apps/android-widget).

## TV Recommendation Tiles documentation

Please find the complete documentation of the nexxPLAY TV Recommendation Tiles following this [link](https://play.docs.nexx.cloud/widgets/widgets-for-native-apps/androidtv-channel).


## Changelog

### v6.3.22
* targetSdk and compileSdk is now 35, finishing Support for android 15
* fixed a Seek Issues on DASH with HEVC
* updated to latest API Versions for playback of Collections with static Images

### v6.3.21
* adding Support for AI TextTrack UI
* adding Support for Samaritan 2.0 Analytics
* fixed an Issue with Foldables

### v6.3.20
* This Version of the SDK is the first Version to support the updated nexxOMNIA Analytics Backend.
* Updated Chromecast Integration to support Output Switcher UI (Customers App Manifest must be updated too, if Chromecast is supported)
* added additional Override to provider an App Icon for Media Notification

### v6.3.11
* Fixes an Issue with LiveStreams with Realtime TextTracks

### v6.3.10
* enhancing Accessibility Support for Mouse/Keyboard Control
* enhancing Support for Foldables and Tablets
* adding Support for android 14 (Breaking Changes!)
  - targetSdk and compileSdk is now 34
  - minSdk is now 25
  - sourceCompatibility is now Java 17
  - due to android 14 "Predictive Back Gesture", the SDK will NOT handle the Back Button anymore. Instead, the App must handle this by listening to the new "closerequest" Event.
    - this is only relevant, if the SDK is in a WebView Environment, which chose to add a permanent "Back" Button on Top of the Player
