package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.SearchImageAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class SearchImgActivity extends AppCompatActivity{

    SearchView searchView;
    RecyclerView recyclerView;
    SearchImageAdapter imageAdapter;
    private List<BaseModel> imageFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_img);

        init();

        recyclerView.setLayoutManager(new  LinearLayoutManager(SearchImgActivity.this,RecyclerView.VERTICAL,false));
        imageAdapter = new SearchImageAdapter(imageFiles,SearchImgActivity.this);
        recyclerView.setAdapter(imageAdapter);

        searchView.setActivated(true);
        searchView.setQueryHint("Type your keyword here");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                imageAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                imageAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        new LoadImages(SearchImgActivity.this).execute();
    }

    public void init(){
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.recyclerView);
    }


    class LoadImages extends AsyncTask<Void, Void,List<BaseModel>>{

        Activity context;

        public LoadImages(Activity mContext) {
            context=mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<BaseModel> doInBackground(Void... voids) {

            imageFiles.clear();
            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.BUCKET_ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    DATE_ADDED,
                    MediaStore.Images.Media.ORIENTATION
            };

            String selection = MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=? or " + MediaStore.MediaColumns.MIME_TYPE + "=?";

            String[] selectionArgs;
            selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"};

            Cursor data = getBaseContext().getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    FILE_PROJECTION,
                    selection,
                    selectionArgs,
                    null);

            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                if(!data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(TITLE)).startsWith(".")){
                        BaseModel img = new BaseModel();

                        img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                        img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                        img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                        img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                        img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                        img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                        img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));

                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(img.getBucketId());
                        directory.setName(img.getDate());
                        directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                        if(!directories1.contains(directory)){
                            directory.addFile(img);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(img);
                        }
                        imageFiles.add(img);
                    }
                }
            }
            return imageFiles;
        }

        @Override
        protected void onPostExecute(List<BaseModel> directories) {
            super.onPostExecute(directories);
            imageFiles=directories;
            //   Log.e("Size of list",String.valueOf(directories.size()));
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(imageAdapter!=null)
                    imageAdapter.notifyDataSetChanged();
                }
            });
        }

    }
}