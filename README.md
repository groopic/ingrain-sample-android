# IngrainSDK sample project Readme #

## Description ##
This is an example project on how to integrate Ingrain SDK in your apps.

* Referencing IngrainSDK in **build.gradle** of your app.

```
apply plugin: 'com.android.application'

repositories {
    maven { url "https://raw.github.com/groopic/ingrain-sdk-android/master" }
}

dependencies {
    compile 'io.ingrain.sdk:ingrain-io:0.1.0'
    compile 'com.github.nkzawa:socket.io-client:0.4.2'
}
```

* Head over to your player’s Activity class & include the following methods for using the SDK.

1. Include **IngrainAdView** on top of the surfaceView (or anyother view that is being used rendering video) in your layout's xml file

```
<io.ingrain.sdk.IngrainAdView
		android:id="@+id/ingrainView"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
        android:layout_centerInParent="true"
/>
```
2. Reference **IngrainAdView** in your player's activity.
        
        ingrainView = (IngrainAdView) findViewById(R.id.ingrainView);

3. For initializing the SDK, call **setUp()**

    a. For fetching ads from Ingrain server

         ingrainView.setUp(videoID, "ingrainSDKKey")

    b. For specifying which server to use for fetching ads 

        ingrainView.setUp(videoID, "ingrainSDKKey", IngrainAdView.DFP_SERVER)

    c. For specifying the server and fetching location i-e; internet

        ingrainView.setUp(videoID, "ingrainSDKKey", IngrainAdView.INTERNET_DATA, IngrainAdView.DFP_SERVER);

4. **Fetch Locations**:

    a. IngrainAdView.INTERNET_DATA // for reading data from internet

5. **Server types**:

    a. IngrainAdView.INGRAIN_SERVER // for ads from Ingrain Server

    b. IngrainAdView.DFP_SERVER     // for ads from DFP server  

6. If your player provides **Ready** state after seek/buffering occurs For Example: _ExoPlayer_, pass **true** in 
```
                   ingrainView.isReadyStateAvailable(true);
```       
7. Implement **IngrainViewControls** and override its callbacks as shown below. Note that implementing this interface and overriding its methods as shown below is **CRUCIAL** for the workings of the SDK

```                  
                    ingrainView.setIngrainViewControlListener(this);

                    @Override
                    public void playPlayer() {
                        mMediaPlayer.start();
                    }
                
                    @Override
                    public void pausePlayer() {
                        mMediaPlayer.pause();
                    }
                
                    @Override
                    public int getPlayerCurrentPosition() {
                        return mMediaPlayer.getCurrentPosition();
                    }
                
                    @Override
                    public long getPlayerDuration() {
                        return mMediaPlayer.getDuration();
                    }
                
                    @Override
                    public boolean isPlayerPlaying() {
                        return mMediaPlayer.isPlaying();
                    }
                
                    @Override
                    public void isIngrainReady(boolean status) {
                        mediaController.isIngrainReady(status);
                    }

```
8. Create **IngrainPlayerController** object. It will provide _Play, Pause, Forward, Rewind & Seek_ controls.

```
        mediaController = new IngrainPlayerController(this);
```
9. If you want to provide your own custom layout for IngrainPlayerController, you must call this method after creating the IngrainPlayerControllerObject.

```
        mediaController.setCustomControllerLayout(R.layout.mycustomui);
        
```
10. Implement the MediaPlayerControl interface and pass its instance to IngrainPlayerController as shown below.

```
        mMediaPlayerControl = new IngrainPlayerController.MediaPlayerControl() {

            @Override
            public void start() {
                if (mMediaPlayer != null)
                    mMediaPlayer.start();

                ingrainView.playerEventOccured(IngrainAdView.PLAY);
            }

            @Override
            public void seekTo(int pos) {
                if (mMediaPlayer != null)
                    mMediaPlayer.seekTo(pos);
            }

            @Override
            public void pause() {
                if (mMediaPlayer != null)
                    mMediaPlayer.pause();

                ingrainView.playerEventOccured(IngrainAdView.PAUSE);

            }

            @Override
            public boolean isPlaying() {
                if (mMediaPlayer != null)
                    return mMediaPlayer.isPlaying();
                else
                    return false;
            }

            @Override
            public int getBufferPercentage() {
                return bufferredPercent;
            }

            @Override
            public int getDuration() {
                if (mMediaPlayer != null)
                    return mMediaPlayer.getDuration();
                else
                    return 0;
            }

            @Override
            public int getCurrentPosition() {
                if (mMediaPlayer != null)
                    return mMediaPlayer.getCurrentPosition();
                else
                    return 1;
            }

            @Override
            public boolean canSeekForward() {
                return true;
            }

            @Override
            public int getAudioSessionId() {
                return 0;
            }

            @Override
            public boolean canSeekBackward() {
                return true;
            }

            @Override
            public boolean canPause() {
                return true;
            }

        };
        mediaController.setMediaPlayer(mMediaPlayerControl);
        mediaController.setAnchorView((ViewGroup) findViewById(R.id.rootView));
        mediaController.setEnabled(true);
        mediaController.show();
        mediaController.setSeekEventListener(new IngrainPlayerController.SeekEventListener() {

            @Override
            public void onSeekStart() {
                ingrainView.playerEventOccured(IngrainAdView.SEEK_START);
            }

            @Override
            public void onSeekEnd() {
                ingrainView.playerEventOccured(IngrainAdView.SEEK_END);
            }
        });
```

9. Call **isAdClicked()** inside the method where your app delivers the _touchEvents_.
```
   @Override
    public boolean onTouchEvent(MotionEvent event) {
        ingrainView.isAdClicked(event);
        return super.onTouchEvent(event);
    }
```
