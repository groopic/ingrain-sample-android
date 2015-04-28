package io.ingrain.sdkDemo;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.exoplayer.demo.IngrainSurfaceView;
import com.google.android.exoplayer.demo.R;

import java.io.IOException;
import java.util.HashMap;

import io.ingrain.sdk.IngrainAdView;
import io.ingrain.sdk.views.IngrainPlayerController;

/**
 * Created by wingoku on 3/30/15.
 */
public class MediaPlayerDemo extends Activity implements SurfaceHolder.Callback, OnPreparedListener, IngrainAdView.IngrainViewControls, MediaPlayer.OnBufferingUpdateListener {
    Context mContext;

    String videoURL;
    String vidID;
    String readFrom;
    String adFrom;

    MediaPlayer mMediaPlayer;
    IngrainSurfaceView surfView;
    FrameLayout rootView;

    IngrainAdView ingrainView;
    IngrainPlayerController.MediaPlayerControl mMediaPlayerControl;
    IngrainPlayerController mediaController;

    int bufferredPercent = 0;

    /* stuff related to restoring Frame on surfaceView on activity resume*/
    private boolean ACTIVITY_WENT_IN_PAUSE_STATE = false;
    private int PLAYER_TIME_TO_SEEK_TO = 0;
    private boolean PLAYER_RUNNING_ON_ACTIVITY_PAUSE = false;
    boolean isMediaPlayerPrepared = false;
    boolean isPlayerReady = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_activity);
        mContext = this;

        videoURL = "https://s3-us-west-2.amazonaws.com/geo.ingrain/GeoNews_39271_Khabarnaak_201516042300.mp4";
        vidID = "39271";
        adFrom="DFP";
        readFrom = "INTERNET";

        surfView = (IngrainSurfaceView) findViewById(R.id.videoView4);
        SurfaceHolder holder = surfView.getHolder();
        holder.addCallback(this);

        rootView = (FrameLayout) findViewById(R.id.rootView);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(ingrainView.isAdClicked(event)) {
                    Log.e("MediaPlayerDemo", "isAdClicked = true");
                    mediaController.hide();
                    return true;
                }
                Log.e("MediaPlayerDemo", "isAdClicked = false");
                return false;
            }
        });

        /** MediaPlayer initialization **/
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);

        /** IngrainAdView **/
        ingrainView = (IngrainAdView) findViewById(R.id.ingrainView);
        ingrainView.setIngrainViewControlListener(this);
        ingrainView.setUp(vidID, "9c5de27yj9b2nxfy8i1ong3d80cbkao3qbgzmej7", IngrainAdView.INTERNET_DATA, IngrainAdView.DFP_SERVER);

//        ingrainView.setUp(vidID, "9c5de27yj9b2nxfy8i1ong3d80cbkao3qbgzmej7", false);

        /**
         * pass your objects/Posters/Tickers tag in the method provided {@link ImaPlayer#setObjectTag(String tag)}, {@link ImaPlayer#setPosterTag(String tag)}, {@link ImaPlayer#setTickerTag(String tag)} respectively in ImaPlayer
         */
        String objectTag = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/7708063/ingrain_object&ciu_szs&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=www.groopic.com&description_url=www.groopic.com";
        ingrainView.setObjectsTag(objectTag);
        String tickerTag = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/7708063/ingrain_ticker_2&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1";
        ingrainView.setTickersTag(tickerTag);
        String posterTag = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/7708063/ingrain_poster&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]";
        ingrainView.setPostersTag(posterTag);
        /**
         * For custom targeting, pass your params as shown below.
         */
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("age", 14);
        ingrainView.setCustomTargetingParams(params);

        /** IngrainPlayerController **/
        mediaController = new IngrainPlayerController(this);
        //mediaController.setCustomControllerLayout(R.layout.custom_media_controller_file); // uncomment if a custom designed MediaController is to be used
        overrideIngrainPlayerControllerMethods();
        mediaController.setMediaPlayer(mMediaPlayerControl);

        // rootView is the top most/root view in layout's heirarchy of the playerActivity xml file
        mediaController.setAnchorView((ViewGroup) findViewById(R.id.anchorViewForControls));
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


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        ingrainView.surfaceViewSizeChanged(surfView.getWidth(), surfView.getHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        mMediaPlayer.setDisplay(holder);

        try {
            if(readFrom.equals("LOCAL"))
            {
                videoURL = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ingrain/" + vidID+".mp4";
            }

            mMediaPlayer.setDataSource(videoURL);
            mMediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            Log.e("MediaPlayerDemo", e.toString());
            e.printStackTrace();
        } catch (SecurityException e) {
            Log.e("MediaPlayerDemo", e.toString());
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e("MediaPlayerDemo", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("MediaPlayerDemo", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isMediaPlayerPrepared = true;

        surfView.mVideoHeight = mMediaPlayer.getVideoHeight();
        surfView.mVideoWidth = mMediaPlayer.getVideoWidth();
        surfView.requestLayout();
        surfView.invalidate();

        ingrainView.onMediaPlayerPrepared();

        mMediaPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // calling start/pause/stop before the player is prepared, causes player go in error state
        if(PLAYER_RUNNING_ON_ACTIVITY_PAUSE && isMediaPlayerPrepared)
            mMediaPlayer.start();
        else
        if(ACTIVITY_WENT_IN_PAUSE_STATE && isMediaPlayerPrepared && isPlayerReady) /** isPlayerReady if this flag isn't true and we execute the following code, PLayer Activity will go in ANR state if we seekd the player when it was paused and then press share Button before the spinner hide callback arrives **/
        {
            // the same method is used by Google Plus Photo app's player on pre-lollipop devices. Check it out on Samsung Note 3. This is to avoid black surfaceView when the player activity comes back from Pause state on pre-loolipop devices
            mMediaPlayer.setVolume(0, 0);
            mMediaPlayer.seekTo(PLAYER_TIME_TO_SEEK_TO - 500); // going back 500 msec and playing for 500msec so that SurfaceView does remain black.
            mMediaPlayer.start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    mMediaPlayer.pause();
                    mMediaPlayer.setVolume(1, 1);
                }
            }, 350); // shouldn't go less then 350 msec. It is a safe spot
        }

        ACTIVITY_WENT_IN_PAUSE_STATE = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mMediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        PLAYER_TIME_TO_SEEK_TO = mMediaPlayer.getCurrentPosition();
        ACTIVITY_WENT_IN_PAUSE_STATE = true;

        PLAYER_RUNNING_ON_ACTIVITY_PAUSE = mMediaPlayer.isPlaying();

        if(isMediaPlayerPrepared)
            mMediaPlayer.pause(); // calling this when the player isn't prepared causes player go in ERROR STATE meaning, activity will have to be destroyed & recreated in order toplay video

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    /** IngrainViewControls callbacks **/
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
        isPlayerReady = status;
    }


    /** IngrainPlayerController interface callbacks **/
    private void overrideIngrainPlayerControllerMethods()
    {
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
    }

    /** MediaPlayer's buffering update values that must be passed to the IngrainPlayerController callbacks **/
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferredPercent = percent;
    }

}
