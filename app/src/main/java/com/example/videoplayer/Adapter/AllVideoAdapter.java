package com.example.videoplayer.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videoplayer.Fragments.AllFragment;
import com.example.videoplayer.MainActivity;
import com.example.videoplayer.R;
import com.example.videoplayer.Model.VideoFiles;
import com.example.videoplayer.Services.FloatingWidgetService;
import com.example.videoplayer.callback;
import com.example.videoplayer.player;
import com.google.android.exoplayer2.PlaybackParameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class AllVideoAdapter extends RecyclerView.Adapter<AllVideoAdapter.MyViewHolder> {

    private Context context;
    public static ArrayList<VideoFiles> videoFiles;
    public callback listener;
    String timeString;
    public AllVideoAdapter(Context context, ArrayList<VideoFiles> videoFiles,callback listener) {
        this.context = context;
        this.videoFiles = videoFiles;
        this.listener=listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fileName.setText(videoFiles.get(position).getTitle());
        String sec=videoFiles.get(position).getDuration();
        if(sec==null)
            timeString="00:00";
        else {
        Long totalSecs=Long.parseLong(sec)/1000;
        Long hours = (totalSecs) / 3600;
        Long minutes = (totalSecs)%3600 / 60;
        Long seconds = (totalSecs )% 60+1;
        Log.d("sec", String.valueOf(totalSecs));
        if(hours==0)
            timeString = String.format("%02d:%02d",minutes, seconds);
        else
            timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        holder.duration.setText(timeString);
        Glide.with(context).load(new File(videoFiles.get(position).getPath())).into(holder.thumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FloatingWidgetService.floatingWidgetPlaying ==false) {
                    Intent intent = new Intent(context, player.class);
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    listener.onIconMoreClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView thumbnail;
        TextView duration;
        TextView fileName;
        ImageView menu;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=itemView.findViewById(R.id.thumbnail);
            duration=itemView.findViewById(R.id.videoDuration);
            fileName=itemView.findViewById(R.id.videoFileName);
            menu=itemView.findViewById(R.id.menu);
        }
    }
}
