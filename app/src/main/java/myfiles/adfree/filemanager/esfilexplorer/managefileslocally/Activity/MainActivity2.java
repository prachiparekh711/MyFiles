package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity2 extends BaseActivity{


    @Override
    public void permissionGranted(){
        SharedPreference.setLogin(MainActivity2.this,true);
        if(getIntent().getStringExtra("Login").equals("new")) {
            File dir = new File(Environment.getExternalStorageDirectory() + "/.TrashFiles");
//        Log.e("Trash dir:", dir.getPath());
            if (dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
//                Log.e("Exception: ",e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        new Handler( Looper.getMainLooper ( ) ).postDelayed ( ( ) -> {

        },1000);

        new Handler( Looper.getMainLooper ( ) ).postDelayed ( ( ) -> {
            Intent intent = new Intent(MainActivity2.this, HomeActivity.class);
            startActivity(intent);
            finish();
        },100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        stopAnim();
    }



}