package com.example.ashwin.facelock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Checker",0);
        editor.apply();

        TextView face_lock=(TextView) findViewById(R.id.Lock_Screen);

        face_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getInt("data",1) < 10){
                    Toast.makeText(MainActivity.this,"Not enough facial features",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Intent intentMain = new Intent(MainActivity.this , face_lock.class);
                    MainActivity.this.startActivityForResult(intentMain,1);
                    MainActivity.this.finish();
                }
            }
        });

        TextView Add_face=(TextView) findViewById(R.id.Add_Faces);

        Add_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getInt("data",1) < 10 && prefs.getInt("Checker",0)==0){

                    Intent intentMain = new Intent(MainActivity.this , Add_Face.class);
                    MainActivity.this.startActivity(intentMain);

                } else if(prefs.getInt("data",1) > 10 && prefs.getInt("Checker",0)==0) {

                    Intent intentMain = new Intent(MainActivity.this , face_open.class);
                    MainActivity.this.startActivity(intentMain);

                } else if(prefs.getInt("data",1) > 10 && prefs.getInt("Checker",0)==1){
                    Intent intentMain = new Intent(MainActivity.this , Add_Face.class);
                    MainActivity.this.startActivity(intentMain);
                }

            }
        });


        TextView Delete=(TextView) findViewById(R.id.Delete);

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.getInt("data",1) < 10){

                    Clean(editor);
                    Toast.makeText(MainActivity.this,"Data cleared",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intentMain = new Intent(MainActivity.this , face_open.class);
                    MainActivity.this.startActivityForResult(intentMain,2);
                }

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        if(requestCode == 1 && resultCode == RESULT_OK){
            this.getPackageManager().clearPackagePreferredActivities(this.getPackageName());
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory("android.intent.category.MONKEY");
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
         if(requestCode == 2 && resultCode == RESULT_OK){

           editor.clear();
            editor.apply();

            Toast.makeText(MainActivity.this,"Data cleared",Toast.LENGTH_SHORT).show();
        }
    }

    private void Clean(SharedPreferences.Editor editor){
        editor.clear();
        editor.apply();

    }
}
