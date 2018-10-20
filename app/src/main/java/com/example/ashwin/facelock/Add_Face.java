package com.example.ashwin.facelock;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Created by ashwin on 7/18/2017.
 */
public class Add_Face extends MainActivity {

    static Camera mCamera=null;
    private static CameraPreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_face);

//        final  FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        mCamera=getCameraInstance(Add_Face.this);

        ImageButton back2cam=(ImageButton) findViewById(R.id.img_cam);

        back2cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FrameLayout pre = (FrameLayout) findViewById(R.id.camera_preview);

                pre.removeAllViews();


                ImageButton back2cam=(ImageButton) findViewById(R.id.img_cam);

                back2cam.setVisibility(View.INVISIBLE);

                mCamera.release();

                mCamera=getCameraInstance(Add_Face.this);

            }
        });


    }



    public Camera getCameraInstance(Context context){


        Camera c=null;

        try {
            c = Camera.open(1);
            // attempt to get a Camera instance

            Log.v("MainActivity","Camera is opened");

            mPreview = new CameraPreview(context, c);
//            Camera.Parameters param = c.getParameters();
//            param.setRotation(90);
//            c.setParameters(param);

            c.setFaceDetectionListener(new MyFaceDetectionListener(context,mPreview));

//            c.setDisplayOrientation(90);

            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

            preview.addView(mPreview);

        } catch (Exception e){
            // Camera is not available (in use or does not exist)

            Log.v("MainActivity","Camera error");

        }

        return c; // returns null if camera is unavailable
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Add_Face.this);
        SharedPreferences.Editor editor = prefs.edit();
        if(prefs.getInt("data",1) > 10){
            if(prefs.getInt("Checker",0)==0){
                editor.putInt("Checker",1);
            }else{
                editor.putInt("Checker",0);
            }
            editor.apply();
        }

        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
