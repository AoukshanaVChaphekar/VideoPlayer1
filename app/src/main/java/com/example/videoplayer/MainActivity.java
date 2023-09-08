package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.videoplayer.Adapter.AllVideoAdapter;
import com.example.videoplayer.Fragments.AllFragment;
import com.example.videoplayer.Fragments.InternalVideoFilesFragment;
import com.example.videoplayer.Fragments.internalStorageFragment;
import com.example.videoplayer.Fragments.sdCardFragment;
import com.example.videoplayer.Model.VideoFiles;
import com.example.videoplayer.Services.FloatingWidgetService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements callback {

    private static final int REQUEST_CODE = 10;
    private  BottomNavigationView bottomNavigationView;
    public static ArrayList<VideoFiles> internalVideoFilesArrayList=new ArrayList<>();
    public static ArrayList<VideoFiles> externalVideoFilesArrayList=new ArrayList<>();
    public static ArrayList<VideoFiles> videoFilesArrayList=new ArrayList<>();
    public  static  ArrayList<String> folderList=new ArrayList<>();
    public static AllVideoAdapter videoAdapter;
    public static BottomSheetDialog bottomSheetDialog;
    private FloatingWidgetService floatingWidgetService;
    boolean doubleBackToExitPressedOnce = false;
    private AdView adView;
    public boolean overLays;
    public boolean readPerm;
   public boolean writePerm;
    public boolean canWritePerm;
    public Snackbar snackbar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this);
        getSupportActionBar().show();
        getSupportActionBar().setTitle("Folder");

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.bg));
        snackbar= Snackbar.make(this.findViewById(android.R.id.content),
                "Please Grant Required Permissions",
                Snackbar.LENGTH_INDEFINITE);


//            bottomNavigationView = (BottomNavigationView)
//                    findViewById(R.id.bottom_navigation);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission();
        }
        Collections.reverse(internalVideoFilesArrayList);
        videoAdapter=new AllVideoAdapter(this,internalVideoFilesArrayList,this);

//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                    new BottomNavigationView.OnNavigationItemSelectedListener() {
//                        @Override
//                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                            switch (item.getItemId()) {
//                                case R.id.all:
//                                    FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
//                                    fragmentTransaction.replace(R.id.mainFragment,new AllFragment());
//                                    fragmentTransaction.commit();
//                                    item.setChecked(true);
//                                    break;
//                                case R.id.internalStorage:
//                                    FragmentTransaction fragmentTransaction2=getSupportFragmentManager().beginTransaction();
//                                    fragmentTransaction2.replace(R.id.mainFragment,new internalStorageFragment());
//                                    fragmentTransaction2.commit();
//                                    item.setChecked(true);
//                                    break;
//                                case R.id.sdCard:
//                                    FragmentTransaction fragmentTransaction3=getSupportFragmentManager().beginTransaction();
//                                    fragmentTransaction3.replace(R.id.mainFragment,new sdCardFragment());
//                                    fragmentTransaction3.commit();
//                                    item.setChecked(true);
//                                    break;
//                            }
//                            return false ;
//                        }
//                    });
        }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !Settings.canDrawOverlays(this))
        {
            Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent,2084);
        }
       else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
//      else  if (!Settings.System.canWrite(getApplicationContext())) {
//            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//            intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivityForResult(intent, 2);
//            }
        else{
            readPerm=true;
            writePerm=true;
            overLays=true;
        checkPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
//        Log.e("readPerm", String.valueOf(readPerm));
//        Log.e("writePerm", String.valueOf(writePerm));
//        Log.e("overlays", String.valueOf(overLays));
        if(readPerm && writePerm && overLays)
        {
            internalVideoFilesArrayList=getInternalVideoFiles(this);
            //externalVideoFilesArrayList=getExternalVideoFiles(this);
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFragment,new internalStorageFragment());
            fragmentTransaction.commit();
        }
        if (!Settings.System.canWrite(getApplicationContext())) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean read=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                boolean write=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (read && write) {
                    readPerm=true;
                    writePerm=true;
                //    Log.e("=","read/write");
                    checkPermissions();
                }
                else
                {
                    snackbar.setAction("GRANT",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    snackbar.dismiss();
                                    requestPermissions(
                                            new String[]{Manifest.permission
                                                    .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            1);
                                }
                            });
                    snackbar.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }
    public ArrayList<VideoFiles> getInternalVideoFiles(Context context)
    {
        ArrayList<VideoFiles> tempFiles=new ArrayList<>();
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection={
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME
              };
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                String id=cursor.getString(0);
                String path=cursor.getString(1);
                String title=cursor.getString(2);
                String size=cursor.getString(3);
                String date_added=cursor.getString(4);
                String duration=cursor.getString(5);
                String fileName=cursor.getString(6);
                VideoFiles videoFiles=new VideoFiles(id,path,title,fileName,size,date_added,duration);


                int slashFirstIndex=path.lastIndexOf("/");
                String subString=path.substring(0,slashFirstIndex);
                int index=subString.lastIndexOf("/");
                String folderName=subString.substring(index+1,slashFirstIndex);
               // Log.e("foldername",folderName);
                if(folderName.equals("0"))
                {
                    videoFilesArrayList.add(videoFiles);
                //    Log.e("video",videoFiles.getFileName());
                }
                else {
                    if (!folderList.contains(folderName))
                        folderList.add(folderName);

                    tempFiles.add(videoFiles);
                }

            }
            cursor.close();
        }

        return tempFiles;
    }
    @Override
    public void onIconMoreClick(int position) {
         bottomSheetDialog=new BottomSheetDialog();
        bottomSheetDialog.setVideoPosition(position);
        bottomSheetDialog.show(getSupportFragmentManager(),bottomSheetDialog.getTag());

    }
//    public void nextButtonPress(View view) {
//        floatingWidgetService=new FloatingWidgetService();
//        simpleExoPlayer.release();
//        FloatingWidgetService.position++;
//        Toast.makeText(getApplicationContext(),"c",Toast.LENGTH_SHORT).show();
//        floatingWidgetService.play(FloatingWidgetService.position);
//    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if(InternalVideoFilesFragment.flag==true)
        {
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFragment,new internalStorageFragment());
            fragmentTransaction.commit();

        }
        else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2084)
        {
            if(Settings.canDrawOverlays(this))
            {
                overLays=true;
             //   Log.e("=","can draw overlays");

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            else
            {
                snackbar.setAction("GRANT",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                                Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));
                                startActivityForResult(intent,2084);

                            }
                        });
                snackbar.show();
            }
        }
        if(requestCode==2)
        {
            if(Settings.System.canWrite(getApplicationContext()))
            {
               // Log.e("=","canWrite");
                canWritePerm=true;
               // checkPermissions();
            }
            else
            {
              //  Log.e("in","denied");
                snackbar.setAction("GRANT",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                snackbar.dismiss();

                                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + getApplication().getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent,2);
                             }
                        });
                snackbar.show();
            }
        }

     //   checkPermissions();
    }

}
