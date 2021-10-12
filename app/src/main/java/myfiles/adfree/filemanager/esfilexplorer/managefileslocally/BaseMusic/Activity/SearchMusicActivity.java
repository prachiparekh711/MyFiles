package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter.SearchMusicAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

public class SearchMusicActivity extends AppCompatActivity{

    SearchView searchView;
    RecyclerView recyclerView;
    SearchMusicAdapter vidAdapter;
    private List<BaseModel> videoFiles = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        init();

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchMusicActivity.this,RecyclerView.VERTICAL,false));
        vidAdapter = new SearchMusicAdapter(videoFiles,SearchMusicActivity.this);
        recyclerView.setAdapter(vidAdapter);

        searchView.setActivated(true);
        searchView.setQueryHint("Type your keyword here");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                vidAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                vidAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        new LoadMusic(SearchMusicActivity.this).execute();
    }

    public void init(){
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.recyclerView);
    }


    class LoadMusic extends AsyncTask<Void, Void,List<BaseModel>>{

        Activity context;

        public LoadMusic(Activity mContext) {
            context=mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<BaseModel> doInBackground(Void... voids) {
            videoFiles.clear();
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Audio.AudioColumns._ID,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.SIZE,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    ALBUM_ID,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    DATE_ADDED
            };


            Cursor data = context.getContentResolver().query(uri,
                    FILE_PROJECTION,
                    null,
                    null,
                    DATE_ADDED + " DESC");
            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).startsWith(".")){
                        BaseModel music = new BaseModel();


                        music.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)));
                        music.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)));
                        music.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)));
                        music.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)));
                        music.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                        music.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                        music.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED))));

                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(music.getBucketId());
                        directory.setName(music.getDate());
                        directory.setPath(Util.extractPathWithoutSeparator(music.getPath()));

                        if(!directories1.contains(directory)){
                            directory.addFile(music);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(music);
                        }
                        videoFiles.add(music);
                    }
                }
            }
            return videoFiles;
        }

        @Override
        protected void onPostExecute(List<BaseModel> directories) {
            super.onPostExecute(directories);
            videoFiles=directories;
            //   Log.e("Size of list",String.valueOf(directories.size()));
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(vidAdapter!=null)
                    vidAdapter.notifyDataSetChanged();
                }
            });
        }

    }
    
}