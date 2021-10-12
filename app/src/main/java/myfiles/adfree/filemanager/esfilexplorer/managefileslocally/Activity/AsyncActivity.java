package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class AsyncActivity extends AppCompatActivity{

    boolean aBoolean=false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        AsyncTaskExample asyncTask=new AsyncTaskExample();
        asyncTask.execute(String.valueOf(new  String[0]));
    }

    @Override
    protected void onPause() {
        super.onPause();
        aBoolean=true;
//        Log.e("On","Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.e("On","Resume");
        if(aBoolean)
            onBackPressed();
    }

    private class AsyncTaskExample extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            if(!aBoolean) {
                Util.DuplicateMainCall();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String bitmap) {
            super.onPostExecute(bitmap);
            if(!aBoolean) {
                Intent intent = new Intent(AsyncActivity.this, CleanActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


}