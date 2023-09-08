package com.example.videoplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoplayer.EyeDetection.Condition;
import com.example.videoplayer.EyeDetection.FaceTrackerDaemon;
import com.example.videoplayer.Model.VideoFiles;
import com.example.videoplayer.Services.FloatingWidgetService;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.media.MediaMetadataRetriever.METADATA_KEY_IMAGE_HEIGHT;
import static android.media.MediaMetadataRetriever.METADATA_KEY_IMAGE_WIDTH;
import static android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION;
import static android.view.View.GONE;
import static com.example.videoplayer.Adapter.AllVideoAdapter.videoFiles;

public class player<RendererBuilder> extends AppCompatActivity implements MediaPlayer.OnVideoSizeChangedListener {
    public PlayerView playerView;
    public SimpleExoPlayer simpleExoPlayer;
    int position=-1;
    public  MediaSource mediaSource;
    private LinearLayout vol_center_text,volume_slider_container,brightness_slider_container,brightness_center_text,seekbar_center_text,lock_unlock_panel,widgets,root;
    private ImageView vol_image,volIcon,brightnessIcon,brightness_image;
    private TextView vol_perc_center_text,brigtness_perc_center_text,txt_seek_currTime,txt_seek_secs;
    private ProgressBar volume_slider,brightness_slider;
    private Display display;
    private Point size;
    private int sWidth,sHeight;
    private float baseX, baseY;
    private long diffX, diffY;
    private int calculatedTime;
    private String seekDur;
    private Boolean tested_ok = false;
    private Boolean screen_swipe_move = false;
    private boolean immersiveMode, intLeft, intRight, intTop, intBottom, finLeft, finRight, finTop, finBottom;
    private static final int MIN_DISTANCE = 150;
    private int brightness, mediavolume,device_height,device_width;
    public  static AudioManager audioManager;
    private double seekSpeed=0;
    private ControlsMode controlsState;
    private Window window;
    private ContentResolver cResolver;
    private DefaultTimeBar timeBar;
    private ImageButton exo_subtitles,exo_lock,screen_rotate,btn_unlock,ic_next,video_capture,loop,fit_to_screen,fullscreen,nextButton,floating_widget;
    private RendererBuilder rendererBuilder;
    Uri uri;
    boolean flag = false;
    CameraSource cameraSource;
    private TextView speed;
    private Handler mainHandler;
    private Runnable hideControls;
    View addView;
    public  static ArrayList<VideoFiles> folderVideoFiles;
    private AdView adView;
    boolean repeat;
    private ConstraintLayout constraintLayout;
    int width;
    int height;
    //public static boolean floatingWidgetClicked=false;
    boolean goneFlag = false;
    int temp;
    long lastVideoPosition;
    public static boolean backPressed=false;
    @SuppressLint("ResourceAsColor")
    public void loop(Boolean repeat) {
        if (repeat) {
           // Log.d("Loop", "on");
            simpleExoPlayer.setRepeatMode(simpleExoPlayer.REPEAT_MODE_ONE);
            loop.setBackgroundColor(R.color.grey);


        }
        else {
           // Log.d("Loop","off");
            simpleExoPlayer.setRepeatMode(simpleExoPlayer.REPEAT_MODE_OFF);
            loop.setBackground(null);
        }
    }
    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int w, int h) {
     //   Log.d("hello0","videoSize chnaged");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
    }
    public enum ControlsMode {
        LOCK, FULLCONTORLS
    }
    {
        hideControls = new Runnable() {
            @Override
            public void run() {
                hideAllControls();
            }
        };
    }
    {
        mLongPressed  = new Runnable() {
            public void run() {
                goneFlag = true;
                //Code for long click
               // Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();
              //  Log.e("click","long");
            }
        };
    }
    Handler  handler;
    Runnable mLongPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
//        }else{
//            init();
//        }
        repeat=false;
        backPressed=false;
        AdRequest adRequest=new AdRequest.Builder().build();
        adView=findViewById(R.id.adView);
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener()
        {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });

        ViewGroup container=findViewById(R.id.player_activity);
        addView = getLayoutInflater().inflate(R.layout.controller_view,null);
        speed=findViewById(R.id.exo_speed);
        timeBar=findViewById(R.id.exo_progress);
        floating_widget=findViewById(R.id.floating_widget);
        //exo_subtitles=findViewById(R.id.exo_subtitle);
        exo_lock=findViewById(R.id.exo_lock);
        screen_rotate=findViewById(R.id.screen_rotate);
        btn_unlock=findViewById(R.id.btn_unlock);
        ic_next=findViewById(R.id.ic_next);
        lock_unlock_panel=findViewById(R.id.lock_unlock_panel);
        root=findViewById(R.id.rootLayout);
       // video_capture=findViewById(R.id.video_capture);
        loop=findViewById(R.id.ic_loop);
        fit_to_screen=findViewById(R.id.fit_to_screen);
        fullscreen=findViewById(R.id.full_screen);
        constraintLayout=findViewById(R.id.constLayout);
        widgets=findViewById(R.id.widgets);

        fit_to_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullscreen.setVisibility(View.VISIBLE);
                fit_to_screen.setVisibility(GONE);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            }
        });
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullscreen.setVisibility(GONE);
                fit_to_screen.setVisibility(View.VISIBLE);
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }
        });

        loop.setBackground(null);
       loop.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(repeat==false)
                   repeat=true;
               else
                   repeat=false;
               loop(repeat);
           }
       });

        playerView=findViewById(R.id.exoplayer_video);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vol_center_text=findViewById(R.id.vol_center_text);
        volume_slider_container=findViewById(R.id.volume_slider_container);
        brightness_slider_container=findViewById(R.id.brightness_slider_container);
        brightness_center_text=findViewById(R.id.brightness_center_text);
        vol_image=findViewById(R.id.vol_image);
        volIcon=findViewById(R.id.volIcon);
        brightnessIcon=findViewById(R.id.brightnessIcon);
        brightness_image=findViewById(R.id.brightness_image);
        vol_perc_center_text=findViewById(R.id.vol_perc_center_text);
        brigtness_perc_center_text=findViewById(R.id.brigtness_perc_center_text);
        volume_slider=findViewById(R.id.volume_slider);
        brightness_slider=findViewById(R.id.brightness_slider);
        txt_seek_secs=findViewById(R.id.txt_seek_secs);
        txt_seek_currTime=findViewById(R.id.txt_seek_currTime);
        seekbar_center_text=findViewById(R.id.seekbar_center_text);
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        device_height = displaymetrics.heightPixels;
        device_width = displaymetrics.widthPixels;

        position=getIntent().getIntExtra("position",-1);
        lastVideoPosition=getIntent().getLongExtra("lastposition",0);

        int po=getIntent().getIntExtra("p",-1);
       mainHandler = new Handler();
       if(position!=-1) {
           playVideo(position);

       }
       if(po!=-1) {
           folderVideoFiles= (ArrayList<VideoFiles>) getIntent().getSerializableExtra("files");
           videoFiles=folderVideoFiles;
           playVideo(po);
           position=po;
       }
        handler = new Handler();
    }
    public void nextVideo(View view) {
        simpleExoPlayer.release();
        position++;
        PlaybackParameters param3 = new PlaybackParameters(0.5f);
        speed.setText("1x");
        simpleExoPlayer.setPlaybackParameters(param3);
        lastVideoPosition=0;
        playVideo(position);
    }
//    }
//    private void initCameraSource() {
//        FaceDetector detector = new FaceDetector.Builder(this)
//                .setTrackingEnabled(true)
//                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//                .setMode(FaceDetector.FAST_MODE)
//                .build();
//        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemon(player.this)).build());
//
//        cameraSource = new CameraSource.Builder(this, detector)
//                .setRequestedPreviewSize(1024, 768)
//                .setFacing(CameraSource.CAMERA_FACING_FRONT)
//                .setRequestedFps(30.0f)
//                .build();
//
//        try {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            cameraSource.start();
//        }
//        catch (IOException e) {
//            Toast.makeText(player.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//    public void updateMainView(Condition condition){
//        switch (condition){
//            case USER_EYES_OPEN:
//                simpleExoPlayer.setPlayWhenReady(true);
//
//                Log.d("eyes","open");
//                break;
//            case USER_EYES_CLOSED:
//                simpleExoPlayer.setPlayWhenReady(false);
//                Log.d("eyes","closed");
//                break;
//            case FACE_NOT_FOUND:
//                simpleExoPlayer.setPlayWhenReady(false);
//
//                Log.d("eyes","no");
//                break;
//            default:
//                Log.d("eyes","default");
//
//        }
//    }
    public void playVideo(int position)
    {
        if(position>=videoFiles.size())
            position=videoFiles.size()-1;
        else if(position<0)
            position=0;
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        device_height = displaymetrics.heightPixels;
        device_width = displaymetrics.widthPixels;

        String path=videoFiles.get(position).getPath();
        if(path!=null)
        {
            uri= Uri.parse(path);
            simpleExoPlayer =new SimpleExoPlayer.Builder(this).build();
            DataSource.Factory factory=new DefaultDataSourceFactory(this, Util.getUserAgent(this,"VideoPlayer"));
            ExtractorsFactory extractorsFactory=new DefaultExtractorsFactory();
            mediaSource=new ProgressiveMediaSource.Factory(factory,extractorsFactory).createMediaSource(uri);
            if(simpleExoPlayer.isPlaying())
            {
                playerView.setVisibility(View.VISIBLE);
            }
            File file = new File(path);
            if (file.exists()) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(file.getAbsolutePath());

                 width=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                 height=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                fitToScreen();

            }
            playerView.setPlayer(simpleExoPlayer);
            playerView.setKeepScreenOn(true);

            simpleExoPlayer.prepare(mediaSource);

            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.seekTo(lastVideoPosition);

            controlsState = ControlsMode.FULLCONTORLS;
            mainHandler.removeCallbacks(hideControls);
            mainHandler.postDelayed(hideControls, 3000);
            loop(repeat);
            setOnGestureListeners();

        }
    }
    public void fitToScreen() {
       // if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                //Create a new instance of MediaMetadataRetriever
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                //Declare the Bitmap
                Bitmap bmp;
                //Set the video Uri as data source for MediaMetadataRetriever
                retriever.setDataSource(this, uri);
                //Get one "frame"/bitmap - * NOTE - no time was set, so the first available frame will be used
                bmp = retriever.getFrameAtTime();
                //Get the bitmap width and height
                int videoWidth = bmp.getWidth();
                int videoHeight = bmp.getHeight();

                //If the width is bigger then the height then it means that the video was taken in landscape mode and we should set the orientation to landscape
                if (videoWidth > videoHeight) {
                    //Set orientation to landscape
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    // this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                //If the width is smaller then the height then it means that the video was taken in portrait mode and we should set the orientation to portrait
                if (videoWidth < videoHeight) {
                    //Set orientation to portrait
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    //  this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }

            } catch (RuntimeException ex) {
                //error occurred
                Log.e("MediaMetadataRetriever", "- Failed to rotate the video");

            }
      //  }
    }
    public void floating_widget_click(View view) {

            Intent serviceIntent = new Intent(player.this, FloatingWidgetService.class);

            serviceIntent.putExtra("position", position);
            serviceIntent.putExtra("videoWidth", width);
            serviceIntent.putExtra("videoHeight", height);
            serviceIntent.putExtra("lastPosition",simpleExoPlayer.getCurrentPosition());
            serviceIntent.putExtra("deviceHeight",device_height);
             simpleExoPlayer.setPlayWhenReady(false);
             simpleExoPlayer.release();

        startService(serviceIntent);
            finish();

    }
    public void rotate_screen(View view) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);


        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        }


    }
    public void button_lock_pressed(View view) {
        controlsState=ControlsMode.LOCK;
        root.setVisibility(GONE);
        widgets.setVisibility(GONE);
        lock_unlock_panel.setVisibility(View.VISIBLE);
    }
    public void button_unlock_pressed(View view) {
        controlsState=ControlsMode.FULLCONTORLS;
        root.setVisibility(View.VISIBLE);
        lock_unlock_panel.setVisibility(GONE);
        widgets.setVisibility(View.VISIBLE);
    }
    private void hideAllControls(){
        if(controlsState==ControlsMode.FULLCONTORLS){
            if(root.getVisibility()==View.VISIBLE){
                root.setVisibility(GONE);
                if(widgets.getVisibility()==View.VISIBLE)
                    widgets.setVisibility(GONE);
            }
        }else if(controlsState==ControlsMode.LOCK){
            if(lock_unlock_panel.getVisibility()==View.VISIBLE) {
                lock_unlock_panel.setVisibility(GONE);
            }
        }
    }
    private void showControls(){
        if(controlsState==ControlsMode.FULLCONTORLS){
            if(root.getVisibility()== GONE){
                root.setVisibility(View.VISIBLE);
                if(widgets.getVisibility()== GONE)
                    widgets.setVisibility(View.VISIBLE);
            }
        }else if(controlsState==ControlsMode.LOCK){
            if(lock_unlock_panel.getVisibility()== GONE){
                lock_unlock_panel.setVisibility(View.VISIBLE);
            }
        }
        mainHandler.removeCallbacks(hideControls);
        mainHandler.postDelayed(hideControls, 3000);
    }
    class OnSwipeTouchListener implements View.OnTouchListener {
        private static final String TAG = "OnSwipeTouchListener";
        private final GestureDetectorCompat mDetector;

        public OnSwipeTouchListener(Context context) {
            mDetector = new GestureDetectorCompat(context, new GestureListener());
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Boolean flag=true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(getApplicationContext())) {
                        // Do stuff here
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                handler.removeCallbacks(mLongPressed);
                                if(controlsState==ControlsMode.FULLCONTORLS) {
                                    tested_ok = false;
//                                    Log.d("=WIDTH", String.valueOf(sWidth));
//                                    Log.d("=diffX", String.valueOf(event.getX()));
                                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                        if (event.getX() < (sHeight/2)) {
                                            intLeft = true;
                                            intRight = false;
                                        }
                                        else if (event.getX() > (sHeight/2)) {
                                            intLeft = false;
                                            intRight = true;
                                        }
                                    }
                                    else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                                        if (event.getX() < (sWidth / 2)) {
                                        intLeft = true;
                                        intRight = false;
                                        }
                                        else if (event.getX() > (sWidth / 2)) {
                                        intLeft = false;
                                        intRight = true;
                                        }
                                    }
                                    int upperLimit = (sHeight / 4) + 100;
                                    int lowerLimit = ((sHeight / 4) * 3) - 150;
                                    if (event.getY() < upperLimit) {
                                        intBottom = false;
                                        intTop = true;
                                    } else if (event.getY() > lowerLimit) {
                                        intBottom = true;
                                        intTop = false;
                                    } else {
                                        intBottom = false;
                                        intTop = false;
                                    }
                                    seekSpeed = (TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getDuration()) * 0.1);
                                    diffX = 0;
                                    diffY=0;
                                    calculatedTime = 0;
                                    seekDur = String.format("%02d:%02d",
                                            TimeUnit.MILLISECONDS.toMinutes(diffX) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffX)),
                                            TimeUnit.MILLISECONDS.toSeconds(diffX) -
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diffX)));

                                    //TOUCH STARTED
                                    baseX = event.getX();
                                    baseY = event.getY();
                                }
                                return true;
                            case MotionEvent.ACTION_MOVE:
                               handler.postDelayed(mLongPressed, 200);
                                if(goneFlag) {
                                    if (controlsState == ControlsMode.FULLCONTORLS) {
                                        screen_swipe_move = true;
                                        diffX = (long) (Math.ceil(event.getX() - baseX));
                                        diffY = (long) Math.ceil(event.getY() - baseY);
                                        double brightnessSpeed = 0.007;
                                        if (Math.abs(diffY) > MIN_DISTANCE) {
                                            tested_ok = true;
                                        }
                                        if (Math.abs(diffY) > Math.abs(diffX)) {
                                            if (intLeft) {
                                                cResolver = getContentResolver();
                                                window = getWindow();
                                                try {
                                                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                                    brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
                                                } catch (Settings.SettingNotFoundException e) {
                                                    e.printStackTrace();
                                                }
                                                int new_brightness = (int) (brightness - (diffY * brightnessSpeed));
                                                if (new_brightness > 250) {
                                                    new_brightness = 250;
                                                } else if (new_brightness < 1) {
                                                    new_brightness = 1;
                                                }
                                                double brightPerc = Math.ceil((((double) new_brightness / (double) 250) * (double) 100));
                                                brightness_slider_container.setVisibility(View.VISIBLE);
                                                brightness_center_text.setVisibility(View.VISIBLE);
                                                brightness_slider.setProgress((int) brightPerc);
                                                if (brightPerc < 30) {
                                                    brightnessIcon.setImageResource(R.drawable.hplib_brightness_minimum);
                                                    brightness_image.setImageResource(R.drawable.hplib_brightness_minimum);
                                                } else if (brightPerc > 30 && brightPerc < 80) {
                                                    brightnessIcon.setImageResource(R.drawable.hplib_brightness_medium);
                                                    brightness_image.setImageResource(R.drawable.hplib_brightness_medium);
                                                } else if (brightPerc > 80) {
                                                    brightnessIcon.setImageResource(R.drawable.hplib_brightness_maximum);
                                                    brightness_image.setImageResource(R.drawable.hplib_brightness_maximum);
                                                }
                                                brigtness_perc_center_text.setText(" " + (int) brightPerc);
                                                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, (new_brightness));
                                                WindowManager.LayoutParams layoutpars = window.getAttributes();
                                                layoutpars.screenBrightness = brightness / (float) 255;
                                                window.setAttributes(layoutpars);

                                            }
                                            else if (intRight) {
                                                vol_center_text.setVisibility(View.VISIBLE);
                                                mediavolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                                int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                             //  double cal = (double) diffY * ((double) maxVol / ((double)device_height *2));

                                             // Log.d("=cal", String.valueOf(cal));
                                               // Log.d("=diffY", String.valueOf(diffY));
                                              //  Log.d("=device height", String.valueOf((double)maxVol/device_height));
                                             //   Log.d("=media Volume", String.valueOf(mediavolume));
                                                double volumeSpeed=0.002;
                                                temp= (int) (diffY-250);
                                              //  diffY=diffY-250;
                                                int newMediaVolume = (int) (mediavolume - (temp*volumeSpeed));
                                              // int newMediaVolume=(int)(mediavolume-cal);


                                                if (newMediaVolume > maxVol) {
                                                    newMediaVolume = maxVol;
                                                } else if (newMediaVolume < 1) {
                                                    newMediaVolume = 0;
                                                }
                                              //  Log.d("=newVolume", String.valueOf(newMediaVolume));
                                              //  Log.d("=maxVolume", String.valueOf(maxVol));
                                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                                                double volPerc = Math.ceil((((double) newMediaVolume *(double)100)/ (double) maxVol));
                                             //   Log.d("=volPerc", String.valueOf(volPerc));

                                                vol_perc_center_text.setText(" " + (int) newMediaVolume);
                                                if (volPerc < 1) {
                                                    volIcon.setImageResource(R.drawable.hplib_volume_mute);
                                                    vol_image.setImageResource(R.drawable.hplib_volume_mute);
                                                    vol_perc_center_text.setVisibility(GONE);
                                                } else if (volPerc >= 1) {
                                                    volIcon.setImageResource(R.drawable.hplib_volume);
                                                    vol_image.setImageResource(R.drawable.hplib_volume);
                                                    vol_perc_center_text.setVisibility(View.VISIBLE);
                                                }
                                                volume_slider_container.setVisibility(View.VISIBLE);
                                                volume_slider.setProgress((int) newMediaVolume);
                                            }
                                        }
                                        else if (Math.abs(diffX) > Math.abs(diffY)) {
                                            if (Math.abs(diffX) > (MIN_DISTANCE + 100)) {
                                                tested_ok = true;
                                                seekbar_center_text.setVisibility(View.VISIBLE);
                                                timeBar.setVisibility(View.VISIBLE);
                                                String totime = "";
                                                calculatedTime = (int) ((diffX) * seekSpeed);
                                                if (calculatedTime > 0) {
                                                    seekDur = String.format("[ +%02d:%02d ]",
                                                            TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
                                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime)),
                                                            TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
                                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime)));
                                                } else if (calculatedTime < 0) {
                                                    seekDur = String.format("[ -%02d:%02d ]",
                                                            Math.abs(TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
                                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime))),
                                                            Math.abs(TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
                                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime))));
                                                }
                                                totime = String.format("%02d:%02d",
                                                        TimeUnit.MILLISECONDS.toMinutes(simpleExoPlayer.getCurrentPosition() + (calculatedTime)) -
                                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(simpleExoPlayer.getCurrentPosition() + (calculatedTime))), // The change is in this line
                                                        TimeUnit.MILLISECONDS.toSeconds(simpleExoPlayer.getCurrentPosition() + (calculatedTime)) -
                                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(simpleExoPlayer.getCurrentPosition() + (calculatedTime))));
                                                txt_seek_secs.setText(seekDur);
                                                txt_seek_currTime.setText(totime);
                                             //   Log.d("timeBar", "h");
                                                timeBar.setPosition((int) (simpleExoPlayer.getCurrentPosition() + (calculatedTime)));
                                            }
                                        }
                                    }
                                }
                                goneFlag=false;
                                return true;
                            case MotionEvent.ACTION_CANCEL:
                            case MotionEvent.ACTION_UP:
                                showControls();
                               handler.removeCallbacks(mLongPressed);
                                if(controlsState==ControlsMode.FULLCONTORLS) {

                                    screen_swipe_move = false;
                                    tested_ok = false;
                                    seekbar_center_text.setVisibility(GONE);
                                    brightness_center_text.setVisibility(GONE);
                                    vol_center_text.setVisibility(GONE);
                                    brightness_slider_container.setVisibility(GONE);
                                    volume_slider_container.setVisibility(GONE);
                                    timeBar.setVisibility(View.VISIBLE);
                                    calculatedTime = (int) (simpleExoPlayer.getCurrentPosition() + (calculatedTime));
                                    simpleExoPlayer.seekTo(calculatedTime);
                                }
                                return true;
                        }
                    } else {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            return true;
        }

        public void onSwipeRight() {
        //    Log.e(TAG, "onSwipeRight: Swiped to the RIGHT");
        }

        public void onSwipeLeft() {
        //    Log.e(TAG, "onSwipeLeft: Swiped to the LEFT");
        }

        public void onSwipeTop() {
        //    Log.e(TAG, "onSwipeTop: Swiped to the TOP");
        }

        public void onSwipeBottom() {
          //  Log.e(TAG, "onSwipeBottom: Swiped to the BOTTOM");
        }

        public void onClick() {
         //   Log.e(TAG, "onClick: Clicking in the screen");
        }

        public void onDoubleClick() {
         //   Log.e(TAG, "onClick: Clicking TWO TIMES in the screen");
        }

        public void onLongClick() {
         //   Log.e("click", "onLongClick: LONG click in the screen");
        }


        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onClick();
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                onDoubleClick();
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
             //   Log.e("click", "onLongClick: LONG click in the screen");
              //  onLongClick();
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }
    }
    public void setOnGestureListeners() {
        playerView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                // Swipe to the right
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                // Swipe to the left
            }

            @Override
            public void onClick() {
                super.onClick();
                // User tapped once (This is what you want)
            }

            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                // User tapped twice
            }

            @Override
            public void onLongClick() {
                super.onLongClick();
            }
        });

    }
    public void speedChange(View view) {
        PopupMenu pm = new PopupMenu(player.this, view);
        final PlaybackParameters[] parameters = new PlaybackParameters[1];
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())   {
                    case R.id.pointFiveX:
                        parameters[0] = new PlaybackParameters(0.5f);
                        Toast.makeText(getApplicationContext(), String.valueOf(item.getTitle()), Toast.LENGTH_SHORT).show();
                        speed.setText("0.5x");
                        break;
                        case R.id.oneX:
                            parameters[0]= new PlaybackParameters(1f);
                        speed.setText("1x");
                        break;

                    case R.id.onePfive5:
                        parameters[0] = new PlaybackParameters(1.5f);
                        speed.setText("1.5x");
                        Toast.makeText(getApplicationContext(),speed.getText()+"clicked!",Toast.LENGTH_SHORT);

                        break;

                    case R.id.twoX:
                        parameters[0]= new PlaybackParameters(2f);
                        speed.setText("2x");
                        break;

                    default:
                        break;
                }
                simpleExoPlayer.setPlaybackParameters(parameters[0]);
                return true;
            }
        });
        pm.inflate(R.menu.speed_menu);
        pm.show();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
        }
        if (cameraSource!=null) {
            cameraSource.release();
        }
        backPressed=false;
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Pause playback

        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
        }
        if (cameraSource!=null) {
            cameraSource.stop();
        }
        boolean temp=!backPressed;
        if(!FloatingWidgetService.floatingWidgetPlaying) {
            if (!backPressed) {

                Intent serviceIntent = new Intent(player.this, FloatingWidgetService.class);
                serviceIntent.putExtra("position", position);
                serviceIntent.putExtra("videoWidth", width);
                serviceIntent.putExtra("videoHeight", height);
                serviceIntent.putExtra("lastPosition", simpleExoPlayer.getCurrentPosition());
                serviceIntent.putExtra("deviceHeight", device_height);

                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.release();

                startService(serviceIntent);
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // continue playing
        if (simpleExoPlayer != null) {
            simpleExoPlayer.prepare(mediaSource, false, false);
        }
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed=true;
        finish();
    }
}