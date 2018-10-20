package com.example.ashwin.facelock;

/**
 * Created by ashwin on 7/18/2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class HistogramCounter implements Callable<int[]> {

    ArrayList<Integer> histo=new ArrayList<>();
    Bitmap bmp_port;
    int row;
    int col;
    int x;
    int y;
    String string;
    Context context;
    int [] bit_array1=new int [8];
    ArrayList<Integer> val=new ArrayList<>();
    int [] result=new int[1];
    int [] bit_array2=new int[256];

    public HistogramCounter(Bitmap bitmap, int r_no, int col_no, int x_init, int y_init, String str, Context cont){
        this.bmp_port=Bitmap.createBitmap(bitmap);
        this.row=r_no;
        this.col=col_no;
        this.x=x_init;
        this.y=y_init;
        this.string=str;
        this.context=cont;
    }

    @Override
    public int [] call() throws Exception {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences prefs = context.getSharedPreferences("face recog",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Bitmap crop=Bitmap.createBitmap(bmp_port,x,y,row,col);
//        int [] dec_val=new int [row*col];
        int dec=0;
        int pointer2=0;
        for(int i=0;i<row-2;i++){
            for(int j=0;j<col-2;j++){
                int k=i;
                int l=j;
                int centre=Color.red(crop.getPixel(k+1,l+1));



                int checker=0;

                int pointer=0;







                if(centre<=20) {

                    while (i != (k + 1) || (j != l + 1)) {

                        int neighbour = Color.red(crop.getPixel(i, j));

                        if (neighbour - centre >= 0) {
                            bit_array1[pointer] = 1;

                            dec = dec + (int) Math.pow(2, pointer);
                        } else {
                            bit_array1[pointer] = 0;
                        }

                        if (pointer != 0 && bit_array1[pointer] != bit_array1[pointer - 1]) {
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
//                        val.add(dec);
//                   dec_val[pointer2]=dec;
                        bit_array2[dec] = bit_array2[dec] + 1;
//                    editor.putInt(string+pointer2,dec);
//                    pointer2=pointer2+1;
                    }
                    dec = 0;
                    i = i - 1;
                    j = j - 1;
                }
            }
        }
        int size=0;
        for(int i=0;i<256;i++){
            editor.putInt(string+i,bit_array2[i]);
            if(bit_array2[i]>0){
                size=size+1;
            }

        }
        editor.apply();


        result[0]=size;


        crop.recycle();

        return result;



    }
}

