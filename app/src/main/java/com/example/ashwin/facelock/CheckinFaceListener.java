package com.example.ashwin.facelock;

/**
 * Created by ashwin on 7/18/2017.
 */
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by ashwin on 6/22/2017.
 */



public class CheckinFaceListener extends View implements Camera.FaceDetectionListener {

    Context context;
    CameraPreview cameraPreview;

    Camera.Face face;
    private LockscreenUtils mLockscreenUtils;
    Camera mCamera;


    public CheckinFaceListener(Context c, CameraPreview mPreview,LockscreenUtils lockscreenUtils) {
        super(c);
        context = c;
        cameraPreview = mPreview;

        mLockscreenUtils =lockscreenUtils;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        Log.d("Inside detector", "face detected is " + faces.length);

        if (faces.length == 1) {
            final Camera.Face face = faces[0];


            if (faces[0].score >= 55 && (Math.abs(faces[0].rightEye.y-faces[0].leftEye.y))<=8) { // if (detectFace(face)) {
                this.face = face;

//                int Y2= face.mouth.y-face.rightEye.y;
//                int X2=face.mouth.x-face.rightEye.x;


//                int Y1=face.mouth.y-face.leftEye.y;
//                int X1=face.mouth.x-face.leftEye.x;

//                Log.d("Face spec", "Mouth "+face.mouth +" Left Eye "+ face.leftEye + " Right Eye "+ face.rightEye);

//                double dist2=Math.sqrt(Math.pow(Y2,2) + Math.pow(X2,2));

//                Log.d("Distance","Distance is "+ (dist2));


//                double dist1=Math.sqrt(Math.pow(Y1,2) + Math.pow(X1,2));

//                Log.d("Distance","Distance is "+ (dist1));

//                Log.d("Distance","Distance is "+ (dist1-dist2));
//                if(Math.abs((int)(dist1-dist2)) > 5 && Math.abs((int)(dist1-dist2)) < 15 ) {

                String msg = "Distance is " + (distanceBetweenEyes(face));
                Log.d("Eye Distance", msg);
//                Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
//                toast.show();
                camera.stopFaceDetection();
                camera.takePicture(null, null, mPicture);

//                camera.stopPreview();
                // DrawRect(bmp);
//                }
//                else{
//                    camera.stopFaceDetection();
//                    camera.setFaceDetectionListener(new CheckinFaceListener(context, cameraPreview, cam_no, disp));
//                    camera.startFaceDetection();
//                }
            } else{
                camera.stopFaceDetection();
                camera.setFaceDetectionListener(new CheckinFaceListener(context, cameraPreview,mLockscreenUtils));
                camera.startFaceDetection();
            }
        } else {
            Log.d("Inside detector", "Can work on only one face.");
            camera.stopFaceDetection();
            camera.setFaceDetectionListener(new CheckinFaceListener(context, cameraPreview,mLockscreenUtils));
            camera.startFaceDetection();
        }

    }

    private boolean detectFace(Camera.Face face) {
        if (isAFace(face)) {
            int distanceBetweenEyes = distanceBetweenEyes(face);
            if (distanceBetweenEyes >= 345 && distanceBetweenEyes <= 350) {
                return true;
            } else {
                Log.d("Inside detector", "Distance between eyes: " + distanceBetweenEyes);
                return false;
            }
        } else {
            Log.d("Inside detector", "Could not recognize face");
            return false;
        }
    }

    private int distanceBetweenEyes(Camera.Face face) {
        return face.rightEye.x - face.leftEye.x;
    }

    private boolean isAFace(Camera.Face face) {
        return face.score > 30;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inMutable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap picture = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            Log.d("Cam preview", "Actual width " + picture.getWidth() + " Actual Height " + picture.getHeight());
//        int nh = (int) ( picture.getHeight() * (1024.0 / picture.getWidth()) );
//            Bitmap scaled_pic = Bitmap.createScaledBitmap(picture, 1800, 1000, true);

            camera.stopPreview();
            int[][] cropimage = crop_image(picture);

            picture.recycle();
            ImageView previewImage = new ImageView(context);

            FrameLayout pre = (FrameLayout) ((Activity) context).findViewById(R.id.cam_preview);

            pre.removeView(cameraPreview);
            // pre.addView(previewImage);




            Bitmap blur_img = DOG(cropimage);



            int new_row = blur_img.getWidth() / 2;
            int new_col = blur_img.getHeight() / 2;


            ExecutorService executor_1 = Executors.newFixedThreadPool(100);
//
            Callable<int[]> histo1 = new HistogramChecker(blur_img, new_row, new_col, 0, 0, "histo_1_", context);
            Callable<int[]> histo2 = new HistogramChecker(blur_img, new_row, new_col, 0, new_col, "histo_2_", context);
//            Callable<int[]> histo3 = new HistogramChecker(blur_img, new_row, new_col, 0, (2 * new_col), "hist0_3_", context);
//            Callable<int[]> histo4 = new HistogramChecker(blur_img, new_row, new_col, 0, (3 * new_col), "histo_4_", context);

            Callable<int[]> histo3 = new HistogramChecker(blur_img, new_row, new_col, new_row, 0, "histo_3_", context);
            Callable<int[]> histo4 = new HistogramChecker(blur_img, new_row, new_col,  new_row , new_col, "histo_4_", context);
//

            Future<int[]> future_1 = executor_1.submit(histo1);
            Future<int[]> future_2 = executor_1.submit(histo2);
            Future<int[]> future_3 = executor_1.submit(histo3);
            Future<int[]> future_4 = executor_1.submit(histo4);
//////
//

            ArrayList<Integer> histo = new ArrayList<>();
            int[] final_result_1 = new int[10];
//
            try {
                final_result_1 = future_1.get();
//                histo.add(0, final_result_1[0]);
                future_1.cancel(true);
            } catch (InterruptedException ie) {
                Log.d("Histo", "hist_1 interrupted");
            } catch (ExecutionException ex) {
                Log.d("Histo", "hist_1 execution interrupted");

            }
//
            int[] final_result_2 = new int[10];
            try {
                final_result_2 = future_2.get();
//                histo.add(1, final_result_2[0]);
                future_2.cancel(true);
            } catch (InterruptedException ie) {
                Log.d("Histo", "hist_2 interrupted");
            } catch (ExecutionException ex) {
                Log.d("Histo", "hist_2 execution interrupted");

            }

            int[] final_result_3 = new int[10];
            try {
                final_result_3 = future_3.get();
//                histo.add(2, final_result_3[0]);
                future_3.cancel(true);
            } catch (InterruptedException ie) {
                Log.d("Histo", "hist_3 interrupted");
            } catch (ExecutionException ex) {
                Log.d("Histo", "hist_3 execution interrupted");

            }

            int[] final_result_4 = new int[10];

            try {
                final_result_4 = future_4.get();
//                histo.add(3, final_result_4[0]);
                future_4.cancel(true);
            } catch (InterruptedException ie) {
                Log.d("Histo", "hist_4 interrupted");
            } catch (ExecutionException ex) {
                Log.d("Histo", "hist_4 execution interrupted");

            }
//
//


            executor_1.shutdownNow();

            int confidence_1=52;

            int confidence_2=52;

            int confidence_3=52;

            int confidence_4=52;

            int con_val=0;


            for(int i=0;i<10;i++){
//                Toast.makeText(context,"Confidence values are "+final_result_1[i]+" "+final_result_2[i]+" "+final_result_3[i]+" "+final_result_4[i],Toast.LENGTH_SHORT).show();
                if( final_result_1[i] > confidence_1 && final_result_2[i] > confidence_2 && final_result_3[i] > confidence_3 && final_result_4[i] >confidence_4){

                    con_val=con_val+1;

                    confidence_1=final_result_1[i];
                    confidence_2=final_result_2[i];
                    confidence_3=final_result_3[i];
                    confidence_4=final_result_4[i];



                    histo.add(0, final_result_1[i]);
                    histo.add(1, final_result_2[i]);
                    histo.add(2,final_result_3[i]);
                    histo.add(3, final_result_4[i]);

                    unlockHomeButton();
                    ((Activity)context).stopLockTask();
                   ((Activity) context).setResult(Activity.RESULT_OK);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    if(prefs.getInt("data",1) > 10){
                        if(prefs.getInt("Checker",0)==0){
                            editor.putInt("Checker",1);
                        }else{
                            editor.putInt("Checker",0);
                        }
                        editor.apply();
                    }
                    ((Activity)context).finish();
                    Log.d("Face Recognition","Completed");
                    break;





                }

            }
            if(confidence_1==52 && confidence_2==52 && confidence_3==52 && confidence_4==52) {
                Toast.makeText(context, "Face not recognised", Toast.LENGTH_SHORT).show();
                mCamera=cameraInstance();
            }else{
                Toast.makeText(context, "Face recognised", Toast.LENGTH_SHORT).show();
            }




        }
    };

    public int[][] crop_image(Bitmap bitmap) {


        Log.d("Crop image", "Starting to rescale");
        Matrix matrix = new Matrix();
        matrix.setScale(1, 1);
        matrix.postScale(bitmap.getWidth() / 2000f, bitmap.getHeight() / 2000f);
        matrix.postTranslate(bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);

        Log.d("Crop image", "Rescaling complete");


        RectF new_scale_rect = new RectF();

        new_scale_rect.left     =  face.leftEye.x - (face.rect.width() / 6);//80;
        new_scale_rect.top      =  face.leftEye.y - (face.rect.height() / 4);//80;
        new_scale_rect.right    =  face.rightEye.x + (face.rect.width() / 6);//100;
        new_scale_rect.bottom   =  face.mouth.y + (face.rect.height() / 6);//((int)(face.rect.height()*0.38));//250;
//
        matrix.mapRect(new_scale_rect);

        Log.d("Crop image", "Rect created");

        int width = (int) new_scale_rect.width();//-105;
        int height = (int) new_scale_rect.height();//-300;


        Log.d("Crop image", "New width and Height ready");


//        FaceDetector fd=new FaceDetector(bitmap.getWidth(),bitmap.getHeight(),1);
//        FaceDetector.Face[] faces = new FaceDetector.Face[1];
//        Log.d("DetectionListener", "Detector created fine ");
//
//        int id=fd.findFaces(bitmap,faces);
//        Log.d("DetectionListener", "faces");
//        if(id>0){
//            Log.d("DetectionListener", "Faces detected");
//        for (int i = 0; i < id; i++) {
//            faces[i].getMidPoint(eyescenter);
//            //Changed faces[0] to faces[i], look out for any error
//            eyesdist = faces[i].eyesDistance();


        int lefteye;


        lefteye = (int) new_scale_rect.left;//+60;


        Log.d("DetectionListener", "Left and right eye ready ");

        int y_init = (int) new_scale_rect.top;//+30;
//
        Log.d("DetectionListener", "y_co-ords ready ");
        Bitmap bmp=Bitmap.createBitmap(bitmap,lefteye,y_init,width,height);

        bmp=Bitmap.createScaledBitmap(bmp,200,200,true);
//
        int[][] color_mat = new int[(bmp.getWidth()) + 13][(bmp.getHeight()) + 13];
        double gamma = (0.2);
        for (int x = 0; x < bmp.getWidth(); x++) {
            for (int y = 0; y <bmp.getHeight(); y++) {
                int pixel = bmp.getPixel(x, y);
                // retrieve color of all channels
                int A = Color.alpha(pixel);

                double R = Color.red(pixel);
                double G = Color.green(pixel);
                double B = Color.blue(pixel);
                // take conversion up to one single value

                R = ((int) (0.299 * R + 0.587 * G + 0.114 * B));

//

                int pixels = Math.abs((int) (255* (Math.pow(R/255 , (gamma)))));

                if(pixels > 255){
                    pixels=255;
                }

                if(pixels < 0){
                    pixels = 0;
                }


                // set new pixel color to output bitmap

//                    bmp.setPixel(x, y,Color.argb(A,pixels,pixels,pixels));
                color_mat[x + 7][y + 7] = pixels;

                if(x < 7 && y < 7){
                    color_mat[7-x][7-y]=pixels;
                }

                if(x > 7 && y < 7){
                    color_mat[x][7-y]=pixels;
                }

                if(x < 7 && y > 7){
                    color_mat[7-x][y]=pixels;
                }
                if(y+7 >= bmp.getHeight()-1  && x > 7){
                    int loc=Math.abs(y+7-bmp.getHeight()-1);

                    color_mat[x][color_mat[0].length-1-loc]=pixels;

                }

                if(x+7 >= bmp.getWidth()-1  && y > 7){
                    int loc=Math.abs(x+7-bmp.getWidth()-1);

                    color_mat[color_mat.length-1-loc][y]=pixels;

                }
            }
        }

        // int[][] equilised_mat= Histo_equiliser(color_mat);

        Log.d("Crop image", "Returning colr mat");
        bitmap.recycle();
        return color_mat;
//
//
    }


    public Bitmap DOG(int[][] col_mat) {

//         double [][] DOG_1=new double[][]{{ 0.0006,0.0024,0.0038,0.0024,0.0006},{ 0.0024,0.0095,0.0151,0.0095,0.0024},{ 0.0038,0.0151,0.0239,0.0151,0.0038},{0.0024,0.0095,0.0151,0.0095,0.0024},{0.0006,0.0024,0.0038,0.0024,0.0006}};
//         double [][] DOG_2=new double[][]{{-0.0268,0.0084,-0.0268},{0.0084,0.0734,0.0084},{-0.0268,0.0084,-0.0268}};
//
//          double[][] DOG_1 = new double[][]{{0.0586, 0.0966, 0.0586}, {0.0966, 0.1592, 0.0966}, {0.0586, 0.0966, 0.0586}};

//         double[][] DOG_2 = new double[][]{{0.0146, 0.0213, 0.0241, 0.0213, 0.0146}, {0.0213, 0.0310, 0.0351, 0.0310, 0.0213}, {0.0241, 0.0351, 0.0398, 0.0351, 0.0241}, {0.0213, 0.0310, 0.0351, 0.0310, 0.0213}, {0.0146, 0.0213, 0.0241, 0.0213, 0.0146}};


//        double[][] DOG_1=new double[][] {{0.003765, 0.015019, 0.023792, 0.015019, 0.003765},{0.015019, 0.059912, 0.094907, 0.059912, 0.015019},{0.023792, 0.094907, 0.150342, 0.094907, 0.023792},{0.015019, 0.059912, 0.094907, 0.059912, 0.015019},{0.003765, 0.015019, 0.023792, 0.015019, 0.003765}};

//        double[][] DOG_2=new double[][]{{0.023528, 0.033969, 0.038393, 0.033969, 0.023528},{0.033969, 0.049045, 0.055432, 0.049045, 0.033969},{0.038393, 0.055432, 0.062651, 0.055432, 0.038393},{0.033969, 0.049045, 0.055432, 0.049045, 0.033969}, {0.023528, 0.033969, 0.038393, 0.033969, 0.023528}};

        double [][] dog_1=new double[][]{{0.000036, 0.000363, 0.001446, 0.002291, 0.001446, 0.000363, 0.000036},{0.000363, 0.003676, 0.014662, 0.023226, 0.014662, 0.003676, 0.000363},{0.001446, 0.014662, 0.058488, 0.092651, 0.058488, 0.014662, 0.001446},{0.002291, 0.023226, 0.092651, 0.146768, 0.092651, 0.023226, 0.002291},{0.001446, 0.014662, 0.058488, 0.092651, 0.058488, 0.014662, 0.001446},{0.000363, 0.003676, 0.014662, 0.023226, 0.014662, 0.003676, 0.000363},{0.000036, 0.000363, 0.001446, 0.002291, 0.001446, 0.000363, 0.000036}};

        double[][] dog_2=new double[][]{{0.000006, 0.000022, 0.000067, 0.000158, 0.000291, 0.000421, 0.000476, 0.000421, 0.000291, 0.000158, 0.000067, 0.000022, 0.000006},{0.000022, 0.000086, 0.000258, 0.000608, 0.001121, 0.001618, 0.001829, 0.001618, 0.001121, 0.000608, 0.000258, 0.000086,0.000022},{0.000067, 0.000258, 0.000777, 0.00183, 0.003375, 0.004873, 0.005508, 0.004873, 0.003375, 0.00183, 0.000777, 0.000258,0.000067},{0.000158, 0.000608, 0.00183, 0.004312, 0.007953, 0.011483, 0.012978, 0.011483, 0.007953, 0.004312, 0.00183, 0.000608, 0.000158},{0.000291, 0.001121, 0.003375, 0.007953, 0.014669, 0.021179, 0.023938, 0.021179, 0.014669, 0.007953, 0.003375, 0.001121, 0.000291},{0.000421, 0.001618, 0.004873, 0.011483, 0.021179, 0.030579, 0.034561, 0.030579, 0.021179, 0.011483, 0.004873, 0.001618, 0.000421},{0.000476, 0.001829, 0.005508, 0.012978, 0.023938, 0.034561, 0.039062, 0.034561, 0.023938, 0.012978, 0.005508, 0.001829, 0.000476},{0.000421, 0.001618, 0.004873, 0.011483, 0.021179, 0.030579, 0.034561, 0.030579, 0.021179, 0.011483, 0.004873, 0.001618, 0.000421},{0.000291, 0.001121, 0.003375, 0.007953, 0.014669, 0.021179, 0.023938, 0.021179, 0.014669, 0.007953, 0.003375, 0.001121, 0.000291},{0.000158, 0.000608, 0.00183, 0.004312, 0.007953, 0.011483, 0.012978, 0.011483, 0.007953, 0.004312, 0.00183, 0.000608, 0.000158},{0.000067, 0.000258, 0.000777, 0.00183, 0.003375, 0.004873, 0.005508, 0.004873, 0.003375, 0.00183, 0.000777, 0.000258,0.000067},{0.000022, 0.000086, 0.000258, 0.000608, 0.001121, 0.001618, 0.001829, 0.001618, 0.001121, 0.000608, 0.000258, 0.000086,0.000022},{0.000006, 0.000022, 0.000067, 0.000158, 0.000291, 0.000421, 0.000476, 0.000421, 0.000291, 0.000158, 0.000067, 0.000022, 0.000006}};
//      double [][] DOG_1=new double [][]{{ 0.3679,0.6065,0.3679},{ 0.6065,1.0000,0.6065},{ 0.3679,0.6065,0.3679}};
//        double[][] DOG_2=new double [][]{{  0.7788,0.8825,0.7788},{  0.8825,1.0000,0.8825},{  0.7788,0.8825,0.7788}};
        int[][] gray_col = col_mat;


        int rows = col_mat.length - 12;
        int col = col_mat[0].length - 12;


        int [][] new_dog_mat=new int[rows][col];

        ExecutorService executor = Executors.newFixedThreadPool(16);
        Callable<int[][]> callable1 = new DOGFilter(dog_1, gray_col, rows, col);
        Callable<int[][]> callable2 = new DOGFilter(dog_2, gray_col, rows, col);

        Future<int[][]> future_1 = executor.submit(callable1);
        Future<int[][]> future_2 = executor.submit(callable2);

        while (!future_1.isDone()  || !future_2.isDone()) {
            continue;
        }

        int[][] difference_mat_1 = null;
        int[][] difference_mat_2 = null;

        try {
            difference_mat_1 = future_1.get();
            future_1.cancel(true);
        } catch (InterruptedException ie) {
            Log.d("Diff 1", "diff_1 interrupted");
        } catch (ExecutionException ex) {
            Log.d("Diff 1", "diff_1 execution interrupted");

        }

        try {
            difference_mat_2 = future_2.get();
            future_2.cancel(true);
        } catch (InterruptedException ie) {
            Log.d("Diff 2", "diff_2 interrupted");
        } catch (ExecutionException ex) {
            Log.d("Diff 2", "diff_2 execution interrupted");

        }
        executor.shutdownNow();


        Bitmap blur_diff = Bitmap.createBitmap(rows, col, Bitmap.Config.RGB_565);


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < col; j++) {
                int diff = (difference_mat_1[i][j] - difference_mat_2[i][j]);

                if(diff<0){
                    diff=0;
                }

                else if(diff>255){
                    diff=255;
                }
//                diff=(int)(255*(Math.pow(diff/255,0.2)));
//                Log.d("Diff Values","diff "+diff);
                new_dog_mat[i][j]=diff;
//                blur_diff.setPixel(i, j, Color.argb(255, diff, diff, diff));
//

            }
        }

        int [][] new_colr_mat=Histo_equiliser(new_dog_mat);
        for(int i=0;i<rows;i++){
            for(int j=0;j<col;j++){
                int red_pix=new_colr_mat[i][j];

//                int red_pix=(int)(255*(Math.pow((double)new_colr_mat[i][j]/255,0.2)));

                blur_diff.setPixel(i, j, Color.argb(255, red_pix, red_pix, red_pix));
            }
        }


        return blur_diff;
    }






    public int[][] Histo_equiliser(int [][] color_matrix){

        int row=color_matrix.length;
        int col=color_matrix[0].length;
        int [] histo=histo = new int[256];

//        int h_min ;

//        int loc = 0;



        for(int i = 0;i < row; i++) {
            for (int j = 0; j < col; j++) {


//
//                 h_min = (row/4)*(col/4);

//                loc = 0;

//                for(int k = i;k < i+(row/2); k++){

//                    for(int l = j;l < j+(col/2); l++){


                histo[color_matrix[i][j]] = histo[color_matrix[i][j]] + 1;

            }
        }

        int [] histo_2=new int[256];
        int [] temp_=new int[256];

        for(int a=0;a<255;a++){
            temp_[a]=histo[a];
        }


        for(int m = 0;m < 255;m++){
            for(int n=m+1;n<255;n++) {
                if(temp_[m]!=0 && temp_[n]!=0) {
                    if (temp_[n] < temp_[m]) {
                        int temp = temp_[m];
                        temp_[m] = temp_[n];
                        temp_[n] = temp;
                    }
                }
            }

        }
        int val=0;
        for(int k = 0; k < 255; k++){

            for(int l = 0; l < 255; l++){

                if(temp_[k] != 0){
                    if(temp_[k] == histo[l]){


                        val = val + temp_[k];

                        Log.d("Sorted sum","Values "+val+" "+temp_[k]);

                        int temp=(int)(((double)((val - temp_[0])*1.0f) / ((((row)*(col))-1)*1.0f)) * 255);

                        if(temp>25){
                            temp=255;
                        }


                        histo_2[l]=temp;
                    }
                }
            }
        }



//        for(int k=0;k<255;k++){
//            if(histo_2[k]!=0) {
//
//                Log.d("Histo",  " Val "  +temp_[0]+ " "+histo[k]+" "+histo_2[k]);
//
//            }
//        }

        for(int m = 0; m < row; m++){

            for(int n = 0; n < col; n++){

                color_matrix[m][n]=histo_2[color_matrix[m][n]];

            }
        }

//                j=j+(col/2)-1;
//            }

//            i=i+(row/2)-1;
//        }











        return color_matrix;


    }
    public void unlockHomeButton() {
        mLockscreenUtils.unlock();

    }

    public Camera cameraInstance(){
        Camera c=null;
        try {

            c = Camera.open(1);
            // attempt to get a Camera instance

            Log.v("LockActivity","Camera is opened");

            cameraPreview = new CameraPreview(context, c);
//            Camera.Parameters param = c.getParameters();
//            param.setRotation(90);
//            c.setParameters(param);
            Log.v("LockActivity","Preview Ready");

            c.setFaceDetectionListener(new CheckinFaceListener(context,cameraPreview,mLockscreenUtils));

            Log.v("LockActivity","Detector Ready");

//            c.setDisplayOrientation(90);

            FrameLayout preview = (FrameLayout) ((Activity) context).findViewById(R.id.cam_preview);

            Log.v("LockActivity","Layout id ready");

            preview.addView(cameraPreview);

            Log.v("LockActivity","Preview Added");

        } catch (Exception e){
            // Camera is not available (in use or does not exist)

            Log.v("LockActivity","Camera error");

        }

        return c; // returns null if camera is unavailable
    }



}
