package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.AlbumAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.AlbumListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
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

public class AlbumFragment extends Fragment{

    private static final String TAG = AlbumFragment.class.getSimpleName();
    public List<Directory<BaseModel>> dateFiles = new ArrayList<>();

    public RecyclerView rvImages;

    public static AlbumAdapter albumAdapter;
    public static AlbumListAdapter albumListAdapter;
    private final List<BaseModel> imageFiles = new ArrayList<>();

    private MyReceiver myReceiver;



    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.IsAlbumUpdate){
            new LoadImages(getActivity()).execute();
            Util.IsAlbumUpdate=false;
        }else {
            if (Util.VIEW_TYPE == "Grid") {
                rvImages.setLayoutManager(new GridLayoutManager(getContext(), Util.COLUMN_TYPE));
                rvImages.setLayoutAnimation(null);

                rvImages.setAdapter(albumAdapter);
                albumAdapter.notifyDataSetChanged();
            } else {
                rvImages.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvImages.setLayoutAnimation(null);
                rvImages.setAdapter(albumListAdapter);
                albumAdapter.notifyDataSetChanged();
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

        View view = inflater.inflate(R.layout.fragment_album, container, false);

        ButterKnife.bind(this,view);
        rvImages = view.findViewById(R.id.rvImages);

        new LoadImages(getActivity()).execute();
        albumAdapter = new AlbumAdapter(dateFiles, getActivity());
        albumListAdapter = new AlbumListAdapter(dateFiles, getActivity());
        return view;
    }

    class LoadImages extends AsyncTask<Void, Void, List<Directory<BaseModel>>> {

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

            Cursor data = fragmentActivity.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    FILE_PROJECTION,
                    selection,
                    selectionArgs,
                    DATE_ADDED + " DESC");

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
                    imageFiles.add(img);
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

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    }

}