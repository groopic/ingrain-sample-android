package io.ingrain.sdkDemo;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.google.android.exoplayer.demo.IngrainSurfaceView;
import com.google.android.exoplayer.demo.R;

import java.io.IOException;

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

    IngrainAdView ingrainView;
    IngrainPlayerController.MediaPlayerControl mMediaPlayerControl;
    IngrainPlayerController mediaController;

    int bufferredPercent = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player_activity);
        mContext = this;

        videoURL = "https://s3-us-west-2.amazonaws.com/ingrain/capitaltalk.geo/CapitalTalk20150105.mp4";
        vidID = "37";
        adFrom="DFP";
        readFrom = "INTERNET";

        surfView = (IngrainSurfaceView) findViewById(R.id.videoView4);
        SurfaceHolder holder = surfView.getHolder();
        holder.addCallback(this);

        /** MediaPlayer initialization **/
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);

        /** IngrainAdView **/
        ingrainView = (IngrainAdView) findViewById(R.id.ingrainView);
        ingrainView.setIngrainViewControlListener(this);
        ingrainView.setUp(vidID, "ingrainSDKKey", IngrainAdView.INTERNET_DATA, IngrainAdView.DFP_SERVER);

        /** IngrainPlayerController **/
        mediaController = new IngrainPlayerController(this);
        //mediaController.setCustomControllerLayout(R.layout.custom_media_controller_file); // uncomment if a custom designed MediaController is to be used
        overrideIngrainPlayerControllerMethods(); 
        mediaController.setMediaPlayer(mMediaPlayerControl);
        
        // rootView is the top most/root view in layout's heirarchy of the playerActivity xml file
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
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /** Pass touch events to the SDK from here (or anyother method that recieves the touchEvents) for ad clicking **/
        ingrainView.isAdClicked(event);
        return super.onTouchEvent(event);
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        mMediaPlayer.stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
