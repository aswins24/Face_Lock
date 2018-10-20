package com.example.ashwin.facelock;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by ashwin on 7/17/2017.
 */
public class face_open extends face_lock implements LockscreenUtils.OnLockStatusChangedListener {

    CameraPreview mPreview;
    LockscreenUtils mLockscreenUtils=new LockscreenUtils();
    static Camera mCamera=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_open);
//        face_open.this.startLockTask();

        if (getIntent() != null && getIntent().hasExtra("kill") && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            unlockHomeButton();
        } else {

            mCamera=cameraInstance(face_open.this);

        }



    }


    public Camera cameraInstance(Context context){
        Camera c=null;
        try {

            c = Camera.open(1);
            // attempt to get a Camera instance

            Log.v("LockActivity","Camera is opened");

         CameraPreview  mPreview = new CameraPreview(context, c);
//            Camera.Parameters param = c.getParameters();
//            param.setRotation(90);
//            c.setParameters(param);

            c.setFaceDetectionListener(new CheckinFaceListener(context,mPreview,mLockscreenUtils));

//            c.setDisplayOrientation(90);

            FrameLayout preview = (FrameLayout) (this).findViewById(R.id.cam_preview);

            preview.addView(mPreview);

        } catch (Exception e){
            // Camera is not available (in use or does not exist)

            Log.v("MainActivity","Camera error");

        }

        return c; // returns null if camera is unavailable
    }


    // Handle button clicks
//    @Override
//    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
//
//        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
//                || (keyCode == KeyEvent.KEYCODE_POWER)
//                || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
//                || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
//            return true;
//        }
//        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
//
//            Log.i("Home Button","Clicked");
//        }
//
//        return false;
//
//    }
//
//    // handle the key press events here itself
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
//                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
//                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
//            return false;
//        }
//        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
//
//            Log.i("Home Button","Clicked");
//        }
//        return false;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("Back button", "It is registered");
        finish();
        disableKeyguard();
        lockHomeButton();
    }


    // Unlock home button and wait for its callback
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();

    }

    // Simply unlock device when home button is successfully unlocked
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unlockHomeButton();


    }

    @SuppressWarnings("deprecation")
    private void disableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }

    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }

    //Simply unlock device by finishing the activity
    private void unlockDevice()
    {
        finish();
    }

    // Lock home button
    public void lockHomeButton() {
        mLockscreenUtils.lock(face_open.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
       finish();
    }

}

