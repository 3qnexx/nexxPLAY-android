1) in flutter Wiget override where you want to use player 

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
    }

  add to init 
    WidgetsBinding.instance.addObserver(this);

  and use WidgetsBindingObserver 

  use   
    NexxPlayerViewController _controller;

  add it to a Widget
    final NexxPlayer nexxPlayer = NexxPlayer(
      customerId: "<customerId>",
      mediaId: "<mediaId>",
      domainSecret: "<domainSecret>",
      sessionId: "<sessionId>",
      autoplay: true,
      onPlayerCreated: (controller) {
        _controller = controller;
        _controller.callOnActivityResume();
      },
    );  

2) NexxPlayerViewController (Dart) add functions 

  Future<void> callOnActivityResume() {
    print("Futter --- call on activity resume---");
    return _channel.invokeMethod('onActivityResume', {});
  }

  Future<void> callOnActivityPause() {
    print("Futter ---call on activity pause---");
    return _channel.invokeMethod('onActivityPause', {});
  }

3) NexxPlayer (java) replace constructor to

  public NexxPlayer(Context context, int id, BinaryMessenger messenger, Activity activity) {

        this.androidContext = activity;
        this.channel = new MethodChannel(messenger, "de.vonaffenfels.nexxtv/player/" + id);

        Log.d(this.TAG, "init NexxPlayerView id: " + id);

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

  add to onMethodCall 

    case "onActivityResume":
        Log.d(this.TAG, "Native ---RESUME--- ");
        nexxPlayer.onActivityResume();
        return;
    case "onActivityPause":
        Log.d(this.TAG, "Native ---PAUSE--- ");
        nexxPlayer.onActivityPause();
        return;
