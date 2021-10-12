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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.VideoParentAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

public class AllVideoFragment extends Fragment{

    List<Directory<BaseModel>>  mVideoList=new ArrayList<>();
    ArrayList<BaseModel>  mVideoFiles=new ArrayList<>();
    RecyclerView mVideoRecycler;
    public static VideoParentAdapter parentAdapter;
    View view;
    private MyReceiver myReceiver;

    public AllVideoFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH"));
    }

    @Override
    public void onResume(){
        super.onResume();
        if(Util.IsUpdate){
            Util.IsUpdate=false;
            if(getActivity()!=null) {
                new LoadVideos(getActivity()).execute();
            }
        }else {
            if (parentAdapter != null)
                mVideoRecycler.setAdapter(parentAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_all_video,container,false);

        mVideoRecycler = view.findViewById(R.id.allVideoRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mVideoRecycler.setLayoutManager(linearLayoutManager);
        mVideoRecycler.setScrollContainer(true);
        mVideoRecycler.setLayoutAnimation(null);
        mVideoRecycler.setItemAnimator(null);
        if(getActivity()!=null)
            new LoadVideos(getActivity()).execute();
        return view;
    }

    class LoadVideos extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;

        public LoadVideos(FragmentActivity fragmentActivity) {
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
                BaseModel vid = new BaseModel();

                if (!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)).startsWith(".")) {
                if (!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)).startsWith(".")) {
//                    //   Log.e("LLL_name: ",data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
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

                    if (!directories1.contains(directory)) {
                        directory.addFile(vid);
                        directories.add(directory);
                        directories1.add(directory);
                    } else {
                        directories.get(directories.indexOf(directory)).addFile(vid);
                    }
                    mVideoFiles.add(vid);
                }
            }
            }
            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);

            mVideoList=directories;
            parentAdapter = new VideoParentAdapter(mVideoList, getActivity());
            mVideoRecycler.setAdapter(parentAdapter);
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }


}