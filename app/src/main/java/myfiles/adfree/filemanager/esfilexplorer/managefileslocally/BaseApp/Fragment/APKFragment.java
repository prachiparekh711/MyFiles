package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter.APKAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter.APKListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class APKFragment extends Fragment{
    public static ArrayList<BaseModel>  applicationModels=new ArrayList<>();
    public RecyclerView rvImages;

    public static APKAdapter albumAdapter;
    public static APKListAdapter albumListAdapter;
    SelectionInterface selectionInterface;
    private MyReceiver myReceiver;
    private String[] mSuffix;
    List<Directory<BaseModel>> mAll = new ArrayList<>();

    public APKFragment(){
        // Required empty public constructor
    }

    public void onResume() {
        super.onResume();
        Log.e("IS update:", String.valueOf(Util.IsUpdate));
        if(Util.IsAPKUpdate){
            Util.IsAPKUpdate=false;
            applicationModels.clear();
            new LoadAPK(getActivity()).execute();
        }else {
            if (Util.VIEW_TYPE == "Grid") {
                rvImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
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
        View view =  inflater.inflate(R.layout.fragment_a_p_k,container,false);

        ButterKnife.bind(this,view);
        rvImages = view.findViewById(R.id.rvImages);
        applicationModels.clear();
        mSuffix = new String[]{"apk"};
        selectionInterface=new SelectionInterface(){
            @Override
            public void onSelectItem(){
                if(albumAdapter!=null)
                    albumAdapter.notifyDataSetChanged();
                if(albumListAdapter!=null)
                    albumListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeselectDate(int position){

            }

            @Override
            public void onSelectDate(int position){

            }
        };

        new LoadAPK(getActivity()).execute();
        return view;
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myReceiver);
    }

    class LoadAPK extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        Context fragmentActivity;

        public LoadAPK(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Directory<BaseModel>> doInBackground(Void... voids) {
            Log.e("Load","APK");
            List<Directory<BaseModel>> directories = new ArrayList<>();
            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_ADDED,

                    //Normal File
                    MediaStore.Files.FileColumns.MIME_TYPE};

            Cursor data = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external"), FILE_PROJECTION, null, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

            while (data.moveToNext()) {
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                if (path != null && contains(path)) {
                    //Create a File instance
                    BaseModel file = new BaseModel();
                    file.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                    file.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                    file.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                    file.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                    file.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));
                    //Create a Directory
                    Directory<BaseModel> directory = new Directory<>();
                    directory.setName(Util.extractFileNameWithSuffix(file.getPath()));
                    directory.setPath(Util.extractPathWithoutSeparator(file.getPath()));

                    if (!directories.contains(directory)) {
                        directory.addFile(file);
                        directories.add(directory);
                        Log.e("name:",directory.getName());
                    } else {
                        directories.get(directories.indexOf(directory)).addFile(file);
                    }

                }
            }

            // Refresh folder list

            mAll = directories;
            ArrayList<BaseModel> list = new ArrayList<>();
            for (Directory<BaseModel> directory1 : directories) {
                list.addAll(directory1.getFiles());
            }

            applicationModels = list;
//            Log.wtf("LLL_PackageSize: ", String.valueOf(applicationModels.size()));


            return directories;
        }


        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);
            albumAdapter = new APKAdapter(applicationModels, getActivity(),selectionInterface);
            albumListAdapter = new APKListAdapter(applicationModels, getActivity(),selectionInterface);

            if (Util.VIEW_TYPE=="Grid") {
                rvImages.setLayoutManager(new GridLayoutManager(getContext(),3));
                rvImages.setLayoutAnimation(null);

                rvImages.setAdapter(albumAdapter);
            } else {
                rvImages.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
                rvImages.setLayoutAnimation(null);
                rvImages.setAdapter(albumListAdapter);
            }
        }
    }



    private boolean contains(String path) {
        String mSuffixRegex;
        mSuffixRegex = obtainSuffixRegex(mSuffix);
        String name = Util.extractFileNameWithSuffix(path);
        Pattern pattern = Pattern.compile(mSuffixRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private String obtainSuffixRegex(String[] suffixes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < suffixes.length ; i++) {
            if (i ==0) {
                builder.append(suffixes[i].replace(".", ""));
            } else {
                builder.append("|\\.");
                builder.append(suffixes[i].replace(".", ""));
            }
        }
        return ".+(\\." + builder.toString() + ")$";
    }


    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }
    
}