package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.AlbumAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.AlbumListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class VideoFolderFragment extends Fragment{


    public List<Directory<BaseModel>> dateFiles = new ArrayList<>();

    public RecyclerView rvImages;

    public static AlbumAdapter albumAdapter;
    public static AlbumListAdapter albumListAdapter;
    private final List<BaseModel> mVideoFiles = new ArrayList<>();

    private MyReceiver myReceiver;


    public VideoFolderFragment() {
        // Required empty public constructor
    }

    public static VideoFolderFragment newInstance(String param1, String param2) {
        VideoFolderFragment fragment = new VideoFolderFragment();

        return fragment;
    }

  

    @Override
    public void onResume() {
        super.onResume();
        if(Util.IsAlbumUpdate){
            Util.IsAlbumUpdate=false;
            new LoadImages(getActivity()).execute();
        }else {
            if (Util.VIEW_TYPE == "Grid") {
                rvImages.setLayoutManager(new GridLayoutManager(getContext(), Util.COLUMN_TYPE));
                rvImages.setLayoutAnimation(null);

                rvImages.setAdapter(albumAdapter);

            } else {
                rvImages.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvImages.setLayoutAnimation(null);

                rvImages.setAdapter(albumListAdapter);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_video_folder, container, false);

        ButterKnife.bind(this,view);
        rvImages = view.findViewById(R.id.rvImages);


        albumAdapter = new AlbumAdapter(dateFiles, getActivity());
        albumListAdapter = new AlbumListAdapter(dateFiles, getActivity());
        new LoadImages(getActivity()).execute();
        return view;
    }

    class LoadImages extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;
        List<BaseModel> list1 = new ArrayList<>();

        public LoadImages(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Directory<BaseModel>> doInBackground(Void... voids) {

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
                    DATE_ADDED
            };

            Cursor data = fragmentActivity.getContentResolver().query(uri, FILE_PROJECTION, null, null, DATE_ADDED + " DESC");

            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                if(!(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)).startsWith(".")) ){
                if(!(data.getString(data.getColumnIndexOrThrow(TITLE)).startsWith(".")) ){
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
                    directory.setName(img.getBucketName());
                    directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                    if(!directories1.contains(directory)){
                        directory.addFile(img);
                        directories.add(directory);
                        directories1.add(directory);
                    }else{
                        directories.get(directories.indexOf(directory)).addFile(img);
                    }
                    mVideoFiles.add(img);
                }
                }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);
      
            fragmentActivity.runOnUiThread(() -> {
                if (Util.VIEW_TYPE=="Grid") {
                    rvImages.setLayoutManager(new GridLayoutManager(getContext(), Util.COLUMN_TYPE));
                    rvImages.setLayoutAnimation(null);

                    rvImages.setAdapter(albumAdapter);
                    albumAdapter.clearData();

                    albumAdapter.addAll(directories);
                } else {
                    rvImages.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
                    rvImages.setLayoutAnimation(null);

                    rvImages.setAdapter(albumListAdapter);
                    albumListAdapter.clearData();

                    albumListAdapter.addAll(directories);
                }

            });
        }

    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }

}