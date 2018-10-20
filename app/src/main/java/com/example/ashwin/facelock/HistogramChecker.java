package com.example.ashwin.facelock;

/**
 * Created by ashwin on 7/18/2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by ashwin on 6/24/2017.
 */
public class HistogramChecker implements Callable<int[]> {

    Bitmap bitmap;
    int row;
    int col;
    int x;
    int y;
    String string;
    Context context;
    int[] bitArray = new int[8];
    ArrayList<Integer> val = new ArrayList<>();
    int [] bit_array2=new int [256];
    int[] result = new int[10];

    public HistogramChecker(Bitmap bitmap, int r_no, int col_no, int x_init, int y_init, String str, Context cont) {

        this.bitmap = Bitmap.createBitmap(bitmap);
        this.row = r_no;
        this.col = col_no;
        this.x = x_init;
        this.y = y_init;
        this.string = str;
        this.context = cont;
    }

    @Override
    public int[] call() throws Exception {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Bitmap crop = Bitmap.createBitmap(bitmap, x, y, row, col);

        int size = prefs.getInt(string, 0);
        int dec = 0;
        int selectedPointer = 0;
        int similarity = 0;
        for (int i = 0; i < row - 2; i++) {
            for (int j = 0; j < col - 2; j++) {
                int k = i;
                int l = j;

                int centre = Color.red(crop.getPixel(k + 1, l + 1));


                int checker = 0;


                int pointer = 0;

                if (centre <= 20) {

                    while (i != (k + 1) || (j != l + 1)) {
                        int neighbour = Color.red(crop.getPixel(i, j));
                        if (neighbour - centre >= 0) {
                            bitArray[pointer] = 1;
                            dec = dec + (int) Math.pow(2, pointer);
                        } else {
                            bitArray[pointer] = 0;
                        }
                        if (pointer != 0 && bitArray[pointer] != bitArray[pointer - 1]) {
                            checker = checker + 1;
                        }
                        pointer = pointer + 1;
                        if (i == k + 2 && j == l) {
                            i = i - 1;
                        } else if (i == k + 2) {
                            j = j - 1;
                        } else if (j == l + 2) {
                            i = i + 1;
                        } else {
                            j = j + 1;
                        }
                    }
                    if (checker > 0 && checker <= 2) {

                        bit_array2[dec] = bit_array2[dec] + 1;
//                    if (selectedPointer < size) {
//                        int diff = Math.abs(dec - prefs.getInt(string + selectedPointer, dec));
//                        if (diff < 20) {
//                            val.add(0);
//                            similarity = similarity + 1;
//                        } else {
//                            val.add(diff);
//                        }
//                        selectedPointer = selectedPointer + 1;
//                    }
                    }
                    dec = 0;
                    i = i - 1;
                    j = j - 1;
                }
            }
        }


        int [] mean=new int [prefs.getInt("data",10)];
        for(int loc=1;loc<=mean.length;loc++) {
            int zeros=0;
            similarity=0;

            for (int i = 0; i < 256; i++) {
                if (bit_array2[i] == 0 && prefs.getInt(string+loc+"_"+ i, 0) == 0) {

                    zeros = zeros + 1;
                }

                if (bit_array2[i] != 0 && prefs.getInt(string+loc+"_"+ i, 0) != 0) {
                    int diff = bit_array2[i] - prefs.getInt(string+loc+"_"+ i, 0);

                    if (Math.abs(diff) < 5) {
                        similarity = similarity + 1;
                    }
                }
            }

            int Act_simi = 255 - zeros;
            mean[loc-1]=((int) ( ((similarity *1.0f)/ (Act_simi*1.0f)) * 100));
        }

//        int avg=0;
//
//        for(int i=0;i<10;i++){
//            avg=avg+mean[i];
//        }

        result = mean;
        Log.d("Decision Threshold", " " + string + " " + result[0]);
        return result;
    }
}
