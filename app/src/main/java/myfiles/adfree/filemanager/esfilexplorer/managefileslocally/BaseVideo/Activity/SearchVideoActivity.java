package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity;

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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.SearchVideoAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

public class SearchVideoActivity extends AppCompatActivity{

    SearchView searchView;
    RecyclerView recyclerView;
    SearchVideoAdapter vidAdapter;
    private List<BaseModel> videoFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video);

        init();

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchVideoActivity.this,RecyclerView.VERTICAL,false));
        vidAdapter = new SearchVideoAdapter(videoFiles,SearchVideoActivity.this);
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
        new LoadVideos(SearchVideoActivity.this).execute();
    }

    public void init(){
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.recyclerView);
    }

    class LoadVideos extends AsyncTask<Void, Void,List<BaseModel>>{

        Activity context;

        public LoadVideos(Activity mContext) {
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
                uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }

            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    DATE_ADDED,
            };

            Cursor data = context.getContentResolver().query(uri, FILE_PROJECTION, null, null, DATE_ADDED + " DESC");
            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)).startsWith(".")){
                        BaseModel vid = new BaseModel();


                        vid.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
                        vid.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                        vid.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
                        vid.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                        vid.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)));
                        vid.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)));
                        vid.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));
                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(vid.getBucketId());
                        directory.setName(vid.getDate());
                        directory.setPath(Util.extractPathWithoutSeparator(vid.getPath()));

                        if(!directories1.contains(directory)){
                            directory.addFile(vid);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(vid);
                        }
                        videoFiles.add(vid);
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