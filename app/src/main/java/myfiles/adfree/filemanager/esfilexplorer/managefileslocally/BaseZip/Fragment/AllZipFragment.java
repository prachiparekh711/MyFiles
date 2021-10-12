package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.ZipParentAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.docDirName;

public class AllZipFragment extends Fragment{

    public static List<Directory<BaseModel>> mZipList=new ArrayList<>();
    ArrayList<BaseModel>  mZipFiles=new ArrayList<>();
    RecyclerView mVideoRecycler;
    public static ZipParentAdapter parentAdapter;
    View view;
    private MyReceiver myReceiver;
    public static List<Directory> directories1 = new ArrayList<>();


    public AllZipFragment(){
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
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_all_zip,container,false);
        mVideoRecycler = view.findViewById(R.id.allVideoRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mVideoRecycler.setLayoutManager(linearLayoutManager);
        mVideoRecycler.setScrollContainer(true);
        mVideoRecycler.setLayoutAnimation(null);
        mVideoRecycler.setItemAnimator(null);
        mZipFiles.clear();
        mZipList.clear();
        directories1.clear();
        mZipFiles=getZipFolder(docDirName,mZipFiles);
        Collections.sort(mZipList, new SortByDate());
        Collections.reverse(mZipList);
        if(getActivity()!=null) {
            parentAdapter = new ZipParentAdapter(mZipList, getActivity());
            mVideoRecycler.setAdapter(parentAdapter);
        }
        return view;
    }



    @Override
    public void onResume(){
        super.onResume();
        if(Util.IsUpdate){
            Util.IsUpdate=false;
            mZipFiles.clear();
            mZipList.clear();
            directories1.clear();
            mZipFiles=getZipFolder(docDirName,mZipFiles);
            Collections.sort(mZipList, new SortByDate());
            Collections.reverse(mZipList);
            if(getActivity()!=null) {
                parentAdapter = new ZipParentAdapter(mZipList, getActivity());
                mVideoRecycler.setAdapter(parentAdapter);
            }
        }else {
            if (parentAdapter != null) {
                mVideoRecycler.setAdapter(parentAdapter);
            }
        }
    }

    public static ArrayList<BaseModel> getZipFolder(String filePath, ArrayList<BaseModel> docDataList) {


        File dir = new File(filePath);
        boolean success = true;
        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            if(listFile!=null){
                for(int i = 0;i < listFile.length;i++){
                    if(listFile[i].isFile()){
                        if(listFile[i].getAbsolutePath().contains(".zip")){
                            if (!(listFile[i].getParentFile().getName()).startsWith(".")){
                            if (!(listFile[i].getName()).startsWith(".")){
                                BaseModel BaseModel = new BaseModel();
                                BaseModel.setName(listFile[i].getName());
                                BaseModel.setFolderName(listFile[i].getParentFile().getName());
                                BaseModel.setPath(listFile[i].getAbsolutePath());
                                BaseModel.setSize(listFile[i].length());
                                BaseModel.setDate(Util.convertTimeDateModified1(listFile[i].lastModified()));

//                            //   Log.e("Name :" ,BaseModel.getName() + " Converted :" + BaseModel.getDate());

                                Directory<BaseModel> directory = new Directory<>();
                                directory.setId(BaseModel.getBucketId());
                                directory.setDate(listFile[i].lastModified());
                                directory.setName(BaseModel.getDate());
                                directory.setPath(Util.extractPathWithoutSeparator(BaseModel.getPath()));
//                            //   Log.e("directory :" + i,directory.getName() +":"+ directory.getPath());
                                if(!directories1.contains(directory)){
                                    directory.addFile(BaseModel);
                                    mZipList.add(directory);
                                    directories1.add(directory);
                                }else{
                                    mZipList.get(mZipList.indexOf(directory)).addFile(BaseModel);
                                }
                                docDataList.add(BaseModel);
                            }
                            }
                        }
                    }else if(listFile[i].isDirectory()){
                        getZipFolder(listFile[i].getAbsolutePath(),docDataList);
                    }
                }
            }
        }


        return docDataList;
    }

    static class SortByDate implements Comparator<Directory<BaseModel>>{
        @Override
        public int compare(Directory<BaseModel> a, Directory<BaseModel> b) {
            return a.getDate().compareTo(b.getDate());
        }
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }

}