package com.example.ashwin.facelock;

/**
 * Created by ashwin on 7/18/2017.
 */
import java.util.concurrent.Callable;

public class DOGFilter implements Callable<int[][]> {
    double[][] DOG_mat;
    int[][] extendedMat;
    int row;
    int col;

    public DOGFilter(double[][] DOGMat, int[][] extendedMat, int row, int col) {
        this.DOG_mat = DOGMat;
        this.extendedMat = extendedMat;
        this.row = row;
        this.col = col;
    }

    @Override
    public int[][] call() throws Exception {
        int[][] filter_mat = new int[row][col];
        int corr;
        if (DOG_mat.length / 2 >= 6) {
            corr = 0;
        } else
            corr = 4;
//        Log.d("Corr value","Corr value is "+DOG_mat.length+" "+corr);
        for (int i = corr; i < row - corr; i++) {
            for (int j = corr; j < col - corr; j++) {
                double value = 0.0;
                for (int k = 0; k < DOG_mat.length; k++) {
                    for (int l = 0; l < DOG_mat[0].length; l++) {
                        value = value + (DOG_mat[k][l] * (double)extendedMat[i + k][j + l]);
                    }
                }
                filter_mat[i - corr][j - corr] = (int) value;
                // a.add(" "+(int)value);
            }
        }
        //Log.d("Final values"," "+a);
        return filter_mat;
        //  return new int[0][];
    }



}