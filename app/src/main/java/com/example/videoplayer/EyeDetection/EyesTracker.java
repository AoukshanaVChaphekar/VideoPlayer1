package com.example.videoplayer.EyeDetection;

import android.content.Context;
import android.util.Log;

import com.example.videoplayer.EyeDetection.Condition;
import com.example.videoplayer.MainActivity;
import com.example.videoplayer.player;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

public class EyesTracker extends Tracker<Face> {
    private final float THRESHOLD = 0.5f;
    private Context context;

    public EyesTracker(Context context) {
        this.context = context;
    }
    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
         //   Log.i("EyesTracker", "onUpdate: Open Eyes Detected");
        //    ((player)context).updateMainView(Condition.USER_EYES_OPEN);

        }else {
          //  Log.i("EyesTracker", "onUpdate: Close Eyes Detected");
          //  ((player)context).updateMainView(Condition.USER_EYES_CLOSED);
        }
    }
    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        super.onMissing(detections);
     //   Log.i("EyesTracker", "onUpdate: Face Not Detected yet!");
   //   ((player)context).updateMainView(Condition.FACE_NOT_FOUND);
    }
    @Override
    public void onDone() {
        super.onDone();
    }
}
