package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.ImageParentAdapter;
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

public class ImageFragment extends Fragment{

    public List<Directory<BaseModel>> dateFiles = new ArrayList<>();
    public RecyclerView rvImages;

    public static ImageParentAdapter imageParentAdapter;
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
        view= inflater.inflate(R.layout.fragment_image,container,false);
        ButterKnife.bind(this,view);

        rvImages = view.findViewById(R.id.rvImages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rvImages.setLayoutManager(linearLayoutManager);
        rvImages.setLayoutAnimation(null);

        new LoadImages(getActivity()).execute();
        imageParentAdapter = new ImageParentAdapter(dateFiles, getActivity());
        rvImages.setAdapter(imageParentAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.IsUpdate){
            new LoadImages(getActivity()).execute();
            Util.IsUpdate=false;
        }else {
            imageParentAdapter.notifyDataSetChanged();
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
                BaseModel img = new BaseModel();

                if (!data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)).startsWith(".")) {
                if (!data.getString(data.getColumnIndexOrThrow(TITLE)).startsWith(".")) {
//                    //   Log.e("LLL_name: ",data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                    img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                    img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                    img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                    img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                    img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                    img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                    img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));
//                    //   Log.e("Name :" ,img.getName() + " Converted :" + img.getDate());
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
                }
            }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);


            fragmentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageParentAdapter.clear();
                    imageParentAdapter.addAll(directories);
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