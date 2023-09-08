package com.example.videoplayer.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;

import com.example.videoplayer.MainActivity;
import com.example.videoplayer.Model.VideoFiles;
import com.example.videoplayer.R;
import com.example.videoplayer.player;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.example.videoplayer.Adapter.AllVideoAdapter.videoFiles;


public class FloatingWidgetService extends Service  {
    public FloatingWidgetService(){}
    WindowManager windowManager;
    View floatingWidget;
    PlayerView playerView;
    Uri uri;
    public  int position;
    public long currentVideoPosition;
    public View constraintLayout;
    private Handler handler;
    private ImageButton nextButton;
    private Runnable hideControls;
    RelativeLayout rootCustomLayout;
    int videoWidth;
    int videoHeight;
    int deviceHeight;
    private View view;
    int vh=0,vw=0;
    public static boolean floatingWidgetPlaying=false;
    {
        hideControls = new Runnable() {
            @Override
            public void run() {

                hideAllControl();
            }
        };
    }
    public SimpleExoPlayer simpleExoPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent!=null)
        {
            floatingWidgetPlaying=true;
            final WindowManager.LayoutParams params;
            handler = new Handler();

            floatingWidget= LayoutInflater.from(this).inflate(R.layout.custom_popup_window,null);
            view=floatingWidget.findViewById(R.id.view);
            playerView=(PlayerView)floatingWidget.findViewById(R.id.playerView);
            ImageView imageViewDismiss=floatingWidget.findViewById(R.id.ImageViewDismiss);
            ImageView imageViewMaximize=floatingWidget.findViewById(R.id.ImageViewMaximize);

            view.setVisibility(GONE);
            playerView.setControllerShowTimeoutMs(1);

            position=intent.getIntExtra("position",0);
            currentVideoPosition=intent.getLongExtra("lastPosition",0);
            deviceHeight=intent.getIntExtra("deviceHeight",0);

            constraintLayout=LayoutInflater.from(this).inflate(R.layout.custom_popup_controller,null);
            rootCustomLayout=constraintLayout.findViewById(R.id.rootCustom);

            if(handler.hasCallbacks(hideControls))
                handler.removeCallbacks(hideControls);
            handler.postDelayed(hideControls, 3000);

            if(windowManager!=null && floatingWidget.isShown() && simpleExoPlayer!=null)
            {
                windowManager.removeView(floatingWidget);
                floatingWidget=null;
                windowManager=null;
                simpleExoPlayer.setPlayWhenReady(false);
                simpleExoPlayer.release();
                simpleExoPlayer=null;
            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                params=new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

            }
            else
            {
                params=new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
            }

            params.gravity= Gravity.RIGHT| Gravity.BOTTOM;
            params.x=200;
            params.y=200;


            windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);
            windowManager.addView(floatingWidget,params);

            imageViewMaximize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(windowManager!=null && floatingWidget.isShown() && simpleExoPlayer!=null)
                    {
                        Intent intent=new Intent(FloatingWidgetService.this,player.class);
                        intent.putExtra("position",position);
                        intent.putExtra("lastposition",simpleExoPlayer.getCurrentPosition());
                        windowManager.removeView(floatingWidget);
                        windowManager=null;
                        floatingWidget=null;
                        simpleExoPlayer.setPlayWhenReady(false);
                        if(simpleExoPlayer!=null)
                        simpleExoPlayer.stop();
                        simpleExoPlayer=null;
                        stopSelf();

                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                       floatingWidgetPlaying=false;
                       startActivity(intent);
                    }
                }
            });
            imageViewDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  player.backPressed=true;

                    if(windowManager!=null && floatingWidget.isShown() && simpleExoPlayer!=null)
                    {
                        windowManager.removeView(floatingWidget);
                        windowManager=null;
                        floatingWidget=null;
                        simpleExoPlayer.setPlayWhenReady(false);
                        simpleExoPlayer.release();
                        simpleExoPlayer=null;
                        FloatingWidgetService.floatingWidgetPlaying=false;
                        stopSelf();

                    }
                }
            });

            playerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//
//                    int x = (int) event.getX();
//                    int y = (int) event.getY();
//                    int width = v.getLayoutParams().width;
//                    int height = v.getLayoutParams().height;
//                    DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
//                    int screenwidth = displayMetrics.widthPixels;
//                    int screenheight = displayMetrics.heightPixels;
//                    int maxWidth=screenwidth;
//                    int minWidth=600;
//                    int maxHeight=screenheight/2;
//                    int minHeight=Integer.parseInt(String.valueOf(Math.round((double)(videoHeight*((double)(100*300)/videoWidth))/100)));
//                //     Log.e(">>", "layoutwidth:" + width + " layoutheight:" + height + " x:" + x + " y:" + y);
//                    //   Log.e(">>", "MAXwidth:" + maxWidth + " minwidth:" + minWidth + " minheight:" + minHeight + " max height:" + maxHeight);
//                    Log.d("=minheight", String.valueOf(minHeight));
//                    Log.d("=Videoheight", String.valueOf(videoHeight));
//                    Log.d("=VideoWidth", String.valueOf(videoWidth));
//
//                    if(x>=minWidth && x<=maxWidth && y>=minHeight && y<=maxHeight) {
//                        if ((x - width <= 100 && x - width >= 0) || (width - x <= 100 && width - x >= 0) ){
//                            switch (event.getAction()) {
//                                case MotionEvent.ACTION_DOWN:
//                                    break;
//                                case MotionEvent.ACTION_MOVE:
//                                    v.getLayoutParams().width = x;
//                                    v.getLayoutParams().height = y;
//                                    v.requestLayout();
//                                    view.getLayoutParams().width=x;
//
//                                    break;
//                                case MotionEvent.ACTION_UP:
//                                    break;
//                            }
//                        }
//                   }
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_UP:
                            showControl();
                    }
                    return false;
                   }
            });

            play(position);

            view.setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            initialX=params.x;
                            initialY=params.y;
                            initialTouchX=motionEvent.getRawX();
                            initialTouchY=motionEvent.getRawY();
                            Log.e("initialX", String.valueOf(initialTouchX));
                            Log.e("initialY", String.valueOf(initialTouchY));

                            return true;
                        case MotionEvent.ACTION_UP :
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            params.x= (int) (initialX -(int)motionEvent.getRawX()+initialTouchX);
                            params.y= (int) (initialY-(int)motionEvent.getRawY()+initialTouchY);
                            Log.e("paramsX", String.valueOf(params.x));
                            Log.e("paramsY", String.valueOf(params.y));
                            windowManager.updateViewLayout(floatingWidget,params);
                            return true;
                       }
                    return false;
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);

    }
    public void play(int position) {
        if(position>=videoFiles.size())
            position=videoFiles.size()-1;
        else if(position<0)
            position=0;

        String path = videoFiles.get(position).getPath();
            if (path != null) {
                uri = Uri.parse(path);
                simpleExoPlayer = new SimpleExoPlayer.Builder(getApplicationContext()).build();

                DataSource.Factory factory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "VideoPlayer"));
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(uri);

                if (simpleExoPlayer.isPlaying()) {
                    playerView.setVisibility(VISIBLE);

                }
                File file = new File(path);
                if (file.exists()) {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(file.getAbsolutePath());

                    MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
                    Bitmap bmp;
                    retriever2.setDataSource(this, uri);
                    bmp = retriever2.getFrameAtTime();


                    int videoWidth2 = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                    int videoHeight2 = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                    if (videoHeight2 > videoWidth2) {
                        vh = 400;
                        vw = (((vh * 100) / videoHeight2) * videoWidth2) / 100 + 400;
                       } else if (videoWidth2 > videoHeight2) {
                        vw=600;
                        vh = (((vw * 100) / videoWidth2) * videoHeight2) / 100;
                    } else if (videoHeight2 == videoWidth2) {
                        vw = 600;
                        vh = 600;

                    }

                    ViewGroup.LayoutParams params1 = playerView.getLayoutParams();
                    params1.width = vw;
                    params1.height = vh;
                    playerView.setLayoutParams(params1);
                    view.getLayoutParams().width = vw;

                    fitToScreen();
                }
                playerView.setPlayer(simpleExoPlayer);
                playerView.setKeepScreenOn(true);

                simpleExoPlayer.setPlayWhenReady(true);

                simpleExoPlayer.prepare(mediaSource, false, false);
                Log.e("current", String.valueOf(currentVideoPosition));
                simpleExoPlayer.seekTo(currentVideoPosition);
            }


    }
    private void fitToScreen() {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bmp;
            retriever.setDataSource(this, uri);
            bmp = retriever.getFrameAtTime();

            int videoWidth = bmp.getWidth();
            int videoHeight = bmp.getHeight();
            if (videoWidth > videoHeight) {
              playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            }
            if (videoWidth < videoHeight) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            }
        } catch (RuntimeException ex) {
            Log.e("MediaMetadataRetriever", "- Failed to rotate the video");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatingWidget!=null)
        {
            windowManager.removeView(floatingWidget);
        }
    }
    private void hideAllControl(){
        if(rootCustomLayout.getVisibility()==View.VISIBLE)
                rootCustomLayout.setVisibility(GONE);
           if(view.getVisibility()==View.VISIBLE)
               view.setVisibility(GONE);
        }
        private void showControl(){
            if(rootCustomLayout.getVisibility()==GONE)
                rootCustomLayout.setVisibility(View.VISIBLE);
            if(view.getVisibility()==GONE)
                view.setVisibility(View.VISIBLE);
            playerView.setControllerShowTimeoutMs(3000);

            handler.removeCallbacks(hideControls);
            handler.postDelayed(hideControls, 3000);

        }
    public void nextButtonPress(View view) {
        currentVideoPosition=0;
        simpleExoPlayer.release();
        position++;
        play(position);
    }
}

