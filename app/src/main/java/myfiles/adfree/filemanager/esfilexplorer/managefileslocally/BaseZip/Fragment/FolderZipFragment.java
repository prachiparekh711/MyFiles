package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.AlbumAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.AlbumListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.docDirName;

public class FolderZipFragment extends Fragment{

    public static List<Directory<BaseModel>> mZipDir=new ArrayList<>();
    public static ArrayList<BaseModel>  mZipFiles=new ArrayList<>();
    public RecyclerView rvImages;

    public static AlbumAdapter albumAdapter;
    public static AlbumListAdapter albumListAdapter;

    private MyReceiver myReceiver;


    public FolderZipFragment(){
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.IsAlbumUpdate) {
            Util.IsAlbumUpdate = false;
            MainFunction();
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
        
        View view =  inflater.inflate(R.layout.fragment_folder_zip,container,false);

        ButterKnife.bind(this,view);
        rvImages = view.findViewById(R.id.rvImages);
      MainFunction();

        return view;
    }

    public void MainFunction(){
        mZipDir.clear();
        mZipFiles.clear();
        mZipDir=getZipFolder(docDirName,mZipFiles);
        albumAdapter = new AlbumAdapter(mZipDir, getActivity());
        albumListAdapter = new AlbumListAdapter(mZipDir, getActivity());

        if (Util.VIEW_TYPE=="Grid") {
            rvImages.setLayoutManager(new GridLayoutManager(getContext(), Util.COLUMN_TYPE));
            rvImages.setLayoutAnimation(null);

            rvImages.setAdapter(albumAdapter);
        } else {
            rvImages.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            rvImages.setLayoutAnimation(null);
            rvImages.setAdapter(albumListAdapter);
        }
    }

    public  List<Directory<BaseModel>> getZipFolder(String filePath, ArrayList<BaseModel> docDataList) {

        List<Directory> directories1 = new ArrayList<>();

        File dir = new File(filePath);
        boolean success = true;
        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            if(listFile!=null){
                for(int i = 0;i < listFile.length;i++){
                    if(listFile[i].isFile()){
                        if(listFile[i].getAbsolutePath().contains(".zip")){
                            if(!(listFile[i].getParentFile().getName().startsWith(".")) ){
                            if(!(listFile[i].getName().startsWith(".")) ){
                                BaseModel BaseModel = new BaseModel();
                                BaseModel.setName(listFile[i].getName());
                                BaseModel.setFolderName(listFile[i].getParentFile().getName());
                                BaseModel.setPath(listFile[i].getAbsolutePath());
                                BaseModel.setSize(listFile[i].length());
                                BaseModel.setFolderName(listFile[i].getParentFile().getName());
                                BaseModel.setDate(Util.convertTimeDateModified1(listFile[i].lastModified()));
                                docDataList.add(BaseModel);

                                Directory<BaseModel> directory = new Directory<>();
                                directory.setId(BaseModel.getBucketId());
                                directory.setName(BaseModel.getFolderName());
                                directory.setPath(Util.extractPathWithoutSeparator(BaseModel.getPath()));

                                if(!directories1.contains(directory)){

                                    directory.addFile(BaseModel);
                                    mZipDir.add(directory);
                                    directories1.add(directory);

                                }else{
                                    mZipDir.get(mZipDir.indexOf(directory)).addFile(BaseModel);
                                }
                                mZipFiles.add(BaseModel);
                            }
                            }
                        }
                    }else if(listFile[i].isDirectory()){
                        getZipFolder(listFile[i].getAbsolutePath(),docDataList);
                    }
                }
            }
        }

        Collections.reverse(mZipDir);
//        //   Log.e("Folder directory size:",String.valueOf(mZipDir.size()));

        return mZipDir;
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }
}