package com.example.alberto.pvideop;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alberto.pvideop.ImagenVideo.MiIntentService;
import com.example.alberto.pvideop.ImagenVideo.MyGLSurfaceView;

import java.io.IOException;
import java.io.Serializable;

import static com.example.alberto.pvideop.ImagenVideo.MyRenderer.RESULTADO;
import static com.example.alberto.pvideop.ImagenVideo.MyRenderer.getTam_Img;
import static com.example.alberto.pvideop.ImagenVideo.MyRenderer.isPausa;
import static com.example.alberto.pvideop.ImagenVideo.MyRenderer.setPausa;
import static com.example.alberto.pvideop.ImagenVideo.MyRenderer.setTam_Img;


public class Reproductor extends Activity implements TextureView.SurfaceTextureListener,MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener ,
        MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener
{
    static final String TAG = "NativeMedia";
    

    // member variables for Java media player
    public static MediaPlayer mMediaPlayer;
    boolean mMediaPlayerIsPrepared = false;
    private MyGLSurfaceView mGLView1;
    // member variables for native media player
    boolean mIsPlayingStreaming = false;
    private String clave;


    VideoSink mSelectedVideoSink;
    VideoSink mJavaMediaPlayerVideoSink;
    GLViewVideoSink mGLView1VideoSink;

    Intent msgIntent;
    String message;
    boolean habilitar= true;
    TextView genero;
    private FloatingActionButton playPause,reWind;
   // private ImageButton playPause,reWind;

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    SeekBar  seekBar;
    TextView tiempoTotal, tiempoTemp;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(icicle);
        setContentView(R.layout.activity_reproductor);
        //setContentView(R.layout.reproductor_feo);
        View videoLayout = (View) findViewById(R.id.videoLayout);
        videoLayout.setBackgroundColor(Color.BLACK);

        // set up the Surface 1 video sink
        mGLView1 = (MyGLSurfaceView) findViewById(R.id.glsurfaceview1);
        playPause = (FloatingActionButton) findViewById(R.id.start_java);
        reWind = (FloatingActionButton) findViewById(R.id.rewind_java);
        genero = (TextView) findViewById(R.id.genero);

        //  playPause = (ImageButton) findViewById(R.id.start_java);
      //  reWind = (ImageButton) findViewById(R.id.rewind_java);



        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int videoWidth = size.x;
        int videoHeight = size.y;
        GLES20 gl = mGLView1.getGl();


        seekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        tiempoTotal = (TextView) findViewById(R.id.tiempoTotal);
        tiempoTemp = (TextView) findViewById(R.id.tiempoTemp);
        Intent intent = getIntent();
        message = intent.getStringExtra(MenuVideos.PATH);
        clave = intent.getStringExtra("clave");
       // toast(clave);

//        musicSeekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);


        // create Java media player
        mMediaPlayer = new MediaPlayer();

        seekBar.setOnSeekBarChangeListener(this); // Important
        mMediaPlayer.setOnCompletionListener(this); // Important
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  mMediaPlayer.start();

        int tiempo = mMediaPlayer.getDuration();
      //  Log.d("tiempo", "" + tiempo);
        int min = tiempo / 60000;
        int segundos = (tiempo-min*60000)/1000;
        String seg = ""+segundos;
        if(seg.length()>=2)
            seg =seg.substring(0,2);
        else
            seg= "0"+seg;
        tiempoTotal.setText(+min + ":" + seg);


        // set up Java media player listeners
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                int width = mediaPlayer.getVideoWidth();
                int height = mediaPlayer.getVideoHeight();
            //    Log.v(TAG, "onPrepared width=" + width + ", height=" + height);
                if (width != 0 && height != 0 && mJavaMediaPlayerVideoSink != null) {
                    mJavaMediaPlayerVideoSink.setFixedSize(width, height);
                }
                mMediaPlayerIsPrepared = true;
                mGLView1VideoSink = new GLViewVideoSink(mGLView1);
              //  Log.d("tiempo",""+mMediaPlayer.getCurrentPosition());
                mSelectedVideoSink = mGLView1VideoSink;


                //   mediaPlayer.start();
            }

        });

        mMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {

            //    Log.v(TAG, "onVideoSizeChanged width=" + width + ", height=" + height);
                if (width != 0 && height != 0 && mJavaMediaPlayerVideoSink != null) {
                    mJavaMediaPlayerVideoSink.setFixedSize(width, height);
                }
            }

        });

    }

    // Java MediaPlayer start/pause


    public void playStop(View view) throws IOException {
        int id=0;
        seekBar.setProgress(0);
        seekBar.setMax(100);
        if (mJavaMediaPlayerVideoSink == null) {
            mSelectedVideoSink.useAsSinkForJava(mMediaPlayer);
            mJavaMediaPlayerVideoSink = mSelectedVideoSink;
        }
        if (!mMediaPlayerIsPrepared) {
        } else if (mMediaPlayer.isPlaying()) {
            id = getResources().getIdentifier("com.example.alberto.pvideop:drawable/ic_play_arrow_black_24dp", null, null);
            playPause.setImageResource(id);
            //   stopService(msgIntent);
            mMediaPlayer.pause();
        } else {
            id = getResources().getIdentifier("com.example.alberto.pvideop:drawable/ic_pause_black_24dp", null, null);
            playPause.setImageResource(id);
            //  startService(msgIntent);
            mMediaPlayer.start();
        }
    }

    // Java MediaPlayer rewind


    public void rewind (View view) throws IOException {

        if (mMediaPlayerIsPrepared) {
            mMediaPlayer.seekTo(0);
        }
    }



    Handler mHandler = new Handler();

    /** Called when the activity is about to be paused. */
    @Override
    protected void onPause()
    {
        mIsPlayingStreaming = false;
        mGLView1.onPause();
        super.onPause();
        mMediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        mGLView1.onResume();
        super.onResume();
       // mMediaPlayer.start();
    }

    /** Called when the activity is about to be destroyed. */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mMediaPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mMediaPlayer.seekTo(currentPosition);
        // update timer progress again
        updateProgressBar();
    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }
    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public void dialogo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Reproductor.this, R.style.MyDialogTheme);
        builder.setTitle("Vídeo inapropiado");
        builder.setIcon(R.drawable.icono);

         View viewInflated = getLayoutInflater().inflate(R.layout.dialogo_inapropiado, null);
         final EditText respuesta = (EditText) viewInflated.findViewById(R.id.respuesta);
         builder.setView(viewInflated);
         builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String  resp = respuesta.getText().toString();
                      //  toast(clave);

                            if(resp.equals(clave))
                            {
                                playPause.setEnabled(true);
                                playPause.setVisibility(View.VISIBLE);
                                reWind.setEnabled(true);
                                reWind.setVisibility(View.VISIBLE);
                                habilitar = true;
                                mMediaPlayer.start();
                                setPausa(false);


                            }
                        else{
                                stopService(new Intent(getApplicationContext(), MiIntentService.class));
                                finish();
                            }

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        stopService(new Intent(getApplicationContext(), MiIntentService.class));
                        finish();
                    }
                });

                             builder.show();

    }

    public void toast(String  text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }



    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            genero.setText(RESULTADO);

            if(isPausa() && habilitar) {
                dialogo();
                playPause.setEnabled(false);
                playPause.setVisibility(View.INVISIBLE);
                reWind.setEnabled(false);
                reWind.setVisibility(View.INVISIBLE);

                habilitar = false;
            }
            long totalDuration = mMediaPlayer.getDuration();
            long currentDuration = mMediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            long currentSeconds = (int) (currentDuration / 1000);
            long totalSeconds = (int) (totalDuration / 1000);
            Double percentage = (double) 0;
            tiempoTemp.setText(""+milliSecondsToTimer(currentDuration));

            // calculating percentage
            percentage =(((double)currentSeconds)/totalSeconds)*100;

            // return percentage
            // Updating progress bar
            int progress = (int)(percentage.intValue());
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    // VideoSink abstracts out the difference between Surface and SurfaceTexture
    // aka SurfaceHolder and GLSurfaceView
    static abstract class VideoSink {

        abstract void setFixedSize(int width, int height);
        abstract void useAsSinkForJava(MediaPlayer mediaPlayer);
        abstract void useAsSinkForNative();

    }

    static class SurfaceHolderVideoSink extends VideoSink {

        private final SurfaceHolder mSurfaceHolder;

        SurfaceHolderVideoSink(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        void setFixedSize(int width, int height) {
            mSurfaceHolder.setFixedSize(width, height);
        }

        void useAsSinkForJava(MediaPlayer mediaPlayer) {
            // Use the newer MediaPlayer.setSurface(Surface) since API level 14
            // instead of MediaPlayer.setDisplay(mSurfaceHolder) since API level 1,
            // because setSurface also works with a Surface derived from a SurfaceTexture.
            Surface s = mSurfaceHolder.getSurface();
            mediaPlayer.setSurface(s);
            s.release();

        }

        void useAsSinkForNative() {
            Surface s = mSurfaceHolder.getSurface();
            s.release();
        }

    }

    static class GLViewVideoSink extends VideoSink {

        private final MyGLSurfaceView mMyGLSurfaceView;

        GLViewVideoSink(MyGLSurfaceView myGLSurfaceView) {
            mMyGLSurfaceView = myGLSurfaceView;
        }

        void setFixedSize(int width, int height) {
        }

        void useAsSinkForJava(MediaPlayer mediaPlayer) {
            SurfaceTexture st = mMyGLSurfaceView.getSurfaceTexture();
            Surface s = new Surface(st);
            mediaPlayer.setSurface(s);
            s.release();

        }

        void useAsSinkForNative() {
            SurfaceTexture st = mMyGLSurfaceView.getSurfaceTexture();
            Surface s = new Surface(st);
            s.release();
        }

    }

}