package com.example.videoplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.videoplayer.Adapter.AllVideoAdapter;
import com.example.videoplayer.Adapter.InternalVideoFilesAdapter;
import com.example.videoplayer.Fragments.InternalVideoFilesFragment;
import com.example.videoplayer.Fragments.internalStorageFragment;
import com.example.videoplayer.Model.VideoFiles;
import com.google.android.gms.vision.text.Line;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.videoplayer.Fragments.InternalVideoFilesFragment.flag;

public class BottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    private TextView videoTitle;
    private  LinearLayout delete_layout,details_layout,share_layout,rename_layout;
    private int position;
    private AllVideoAdapter adapter;
    private InternalVideoFilesAdapter internalVideoFilesAdapter;
    private ArrayList<VideoFiles> videoFiles;
    private BottomSheetDialog bottomSheetDialog;
    String fragment;
    @Nullable   
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.bottom_sheet_layout,container,false);
        videoTitle=view.findViewById(R.id.videoTile);
        delete_layout=view.findViewById(R.id.delete_layout);
        details_layout=view.findViewById(R.id.detials_layout);
        share_layout=view.findViewById(R.id.share_layout);
       // rename_layout=view.findViewById(R.id.rename_layout);
            fragment = (String) getArguments().get("internalVideoFiles");

            if (fragment.equals("1")) {
                internalVideoFilesAdapter = InternalVideoFilesFragment.internalVideoFilesAdapter;
                videoFiles = InternalVideoFilesFragment.videoFiles;
                bottomSheetDialog = InternalVideoFilesFragment.bottomSheetDialog;
            }
            else {
                internalVideoFilesAdapter=InternalVideoFilesFragment.internalVideoFilesAdapter;
                videoFiles=MainActivity.videoFilesArrayList;

                bottomSheetDialog= internalStorageFragment.bottomSheetDialog;
            }


        setBottomSheetTitle();
        delete_layout.setOnClickListener(this);
        details_layout.setOnClickListener(this);
        share_layout.setOnClickListener(this);
     //   rename_layout.setOnClickListener(this);

        return view;
    }

    private void setBottomSheetTitle() {
        videoTitle.setText(videoFiles.get(position).getTitle());
    }
    public void setVideoPosition(int position)
    {
        this.position=position;
    }
    private void delete() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        //  alert.setTitle("Delete entry");
        alert.setMessage("Are you sure you want to delete?");

        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                File file=new File(videoFiles.get(position).getPath());
                file.delete();
                videoFiles.remove(position);
                if(flag) {
                    if (fragment.equals("1"))
                        internalVideoFilesAdapter.notifyItemRemoved(position);
                }
                else
                     adapter.notifyItemRemoved(position);
                dialog.cancel();

            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        AlertDialog dialog=alert.create();
        dialog.show();
        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.detials_layout:
                details();
                bottomSheetDialog.dismiss();
                break;
//            case R.id.rename_layout:
//                rename(videoFiles.get(position),position);
//                bottomSheetDialog.dismiss();
//
//                break;
            case R.id.share_layout:
                share(videoFiles.get(position).getPath());
                bottomSheetDialog.dismiss();
                break;
            case R.id.delete_layout:
                delete();
                bottomSheetDialog.dismiss();
                break;
        }
    }
    private void details() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.details_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView file = dialogView.findViewById(R.id.fileTv);
        TextView location = dialogView.findViewById(R.id.locationTv);
        TextView date = dialogView.findViewById(R.id.dateTv);
        TextView size = dialogView.findViewById(R.id.sizeTv);

        float length= (float) Math.ceil(Float.parseFloat(videoFiles.get(position).getSize())/(1024*1024));

        long seconds= Long.parseLong(videoFiles.get(position).getDateAdded());
        String formattedDate=new SimpleDateFormat("MMM d, yyyy").format(new Date(seconds*1000));

        file.setText(videoFiles.get(position).getFileName());
        location.setText(videoFiles.get(position).getPath());
        date.setText(formattedDate);
        size.setText(length+"MB");
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    public void share(String path) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(path);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));

    }
    public void rename(VideoFiles videoFiles,int position) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View dialogView= LayoutInflater.from(getContext()).inflate(R.layout.rename_alert_dialog,null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.editTextView);
        Button ok=dialogView.findViewById(R.id.ok);
        Button cancel=dialogView.findViewById(R.id.cancel);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(editText.getText().toString().trim()))
                {
                    String oldName=videoFiles.getPath();
                    int i=oldName.lastIndexOf("/");
                    String name=editText.getText().toString().trim();
                    String newName=oldName.substring(0,i)+"/"+name+".mp4";

                    File from=new File(oldName);
                    File to=new File(newName);
                    FileChannel outChannel = null;
                    FileChannel inChannel=null;
                    try {
                        outChannel = new FileOutputStream(to).getChannel();
                        inChannel = new FileInputStream(from).getChannel();
                        inChannel.transferTo(0, inChannel.size(), outChannel);

                        from.delete();

                        MainActivity.internalVideoFilesArrayList.remove(position);
                        adapter.notifyItemRemoved(position);
                        videoFiles.setFileName(name+".mp4");
                        videoFiles.setPath(newName);
                        videoFiles.setTitle(name);
                        MainActivity.internalVideoFilesArrayList.add(position,videoFiles);
                        adapter.notifyItemInserted(position);
                //      Toast.makeText(getContext(),"renamed",Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (inChannel != null) {
                            try {
                                inChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (outChannel != null) {
                            try {
                                outChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else
                    Toast.makeText(getContext(),"Empty fields not allowed",Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }


}
