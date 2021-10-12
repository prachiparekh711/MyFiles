package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter.MusicParentadapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.provider.MediaStore.Audio.AudioColumns.DATE_ADDED;

public class AllMusicFragment extends Fragment{

    public List<Directory<BaseModel>> dateFiles = new ArrayList<>();
    public RecyclerView rvImages;


    public static MusicParentadapter musicParentadapter;
    private final List<BaseModel> imageFiles = new ArrayList<>();
    View view;
    private MyReceiver myReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_all_music,container,false);
        ButterKnife.bind(this,view);

        rvImages = view.findViewById(R.id.rvImages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvImages.setLayoutManager(linearLayoutManager);
        rvImages.setScrollContainer(true);
        rvImages.setLayoutAnimation(null);
        rvImages.setItemAnimator(null);
        new LoadMusic(getActivity()).execute();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.IsUpdate){
            Util.IsUpdate=false;
            new LoadMusic(getActivity()).execute();
        }else {
            if (musicParentadapter != null)
                rvImages.setAdapter(musicParentadapter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myReceiver);

    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class LoadMusic extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;
        List<BaseModel> list1 = new ArrayList<>();

        public LoadMusic(FragmentActivity fragmentActivity) {
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
                    MediaStore.Audio.AudioColumns.ALBUM_ID,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.AudioColumns.DATE_ADDED
            };

            Cursor data = getActivity().getContentResolver().query(uri,
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

                BaseModel img = new BaseModel();

                if (!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).startsWith(".")) {
                if (!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)).startsWith(".")) {
//                    //   Log.e("LLL_name: ",data.getString(data.getColumnIndexOrThrow(TITLE)));
                    img.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)));
                    img.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)));
                    img.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)));
                    img.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)));
                    img.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                    img.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                    img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED))));

                    //Create a Directory
                    Directory<BaseModel> directory = new Directory<>();
                    directory.setId(img.getBucketId());
                    directory.setName(img.getDate());
                    directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                    if (!directories1.contains(directory)) {
                        directory.addFile(img);
                        directories.add(directory);
                        directories1.add(directory);
                    } else {
                        directories.get(directories.indexOf(directory)).addFile(img);
                    }
                    imageFiles.add(img);
//                    //   Log.e("MusicFiles",img.getName());
                }
            }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);
            dateFiles=directories;
            musicParentadapter = new MusicParentadapter(dateFiles, getActivity());
            rvImages.setAdapter(musicParentadapter);
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }



}