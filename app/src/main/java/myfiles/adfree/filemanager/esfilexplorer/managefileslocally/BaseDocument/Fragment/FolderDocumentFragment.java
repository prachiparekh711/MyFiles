package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Fragment;

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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Adapter.AlbumAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Adapter.AlbumListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Adapter.DocumentParentAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity.BaseDocumentActivity;


public class FolderDocumentFragment extends Fragment{


    public static List<Directory<BaseModel>> directories = new ArrayList<>();
    public static List<Directory> directories1 = new ArrayList<>();
    public static DocumentParentAdapter parentAdapter;
    private ArrayList<BaseModel> mDocumentFiles = new ArrayList<>();
    public RecyclerView rvImages;

    public static AlbumAdapter albumAdapter;
    public static AlbumListAdapter albumListAdapter;

    private FolderDocumentFragment.MyReceiver myReceiver;



    public FolderDocumentFragment() {
        // Required empty public constructor
    }

    public static FolderDocumentFragment newInstance(String param1, String param2) {
        FolderDocumentFragment fragment = new FolderDocumentFragment();

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.IsAlbumUpdate) {
            Util.IsAlbumUpdate = false;
            getListFolder();
        }else {
            if (Util.VIEW_TYPE == "Grid") {
                albumAdapter = new AlbumAdapter(directories, getActivity());
                rvImages.setLayoutManager(new GridLayoutManager(getContext(), Util.COLUMN_TYPE));
                rvImages.setLayoutAnimation(null);

                rvImages.setAdapter(albumAdapter);
            } else {
                albumListAdapter = new AlbumListAdapter(directories, getActivity());
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

        myReceiver = new FolderDocumentFragment.MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_folder_document, container, false);

        ButterKnife.bind(this,view);
        rvImages = view.findViewById(R.id.rvImages);
        getListFolder();


        return view;
    }


    private ArrayList<BaseModel> getListFolder() {

        mDocumentFiles.clear();
        directories.clear();
        directories1.clear();
        mDocumentFiles = getDocumentsFolder(BaseDocumentActivity.docDirNamedir, mDocumentFiles);


        if (Util.VIEW_TYPE=="Grid") {
            albumAdapter = new AlbumAdapter(directories, getActivity());
            rvImages.setLayoutManager(new GridLayoutManager(getContext(), Util.COLUMN_TYPE));
            rvImages.setLayoutAnimation(null);

            rvImages.setAdapter(albumAdapter);
        } else {
            albumListAdapter = new AlbumListAdapter(directories, getActivity());
            rvImages.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            rvImages.setLayoutAnimation(null);

            rvImages.setAdapter(albumListAdapter);
        }

        return mDocumentFiles;
    }

    public static ArrayList<BaseModel> getDocumentsFolder(String filePath, ArrayList<BaseModel> docDataList) {

        File dir = new File(filePath);
        boolean success = true;
        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isFile()) {
                    if (listFile[i].getAbsolutePath().contains(".docx") ||
                            listFile[i].getAbsolutePath().contains(".doc") ||
                            listFile[i].getAbsolutePath().contains(".xls") ||   //excel
                            listFile[i].getAbsolutePath().contains(".pdf") ||
                            listFile[i].getAbsolutePath().contains(".rtf") ||
                            listFile[i].getAbsolutePath().contains(".txt") ||
                            listFile[i].getAbsolutePath().contains(".odp") ||
                            listFile[i].getAbsolutePath().contains(".ods") ||
                            listFile[i].getAbsolutePath().contains(".odt") ||
                            listFile[i].getAbsolutePath().contains(".xml") ||       //excel
                            listFile[i].getAbsolutePath().contains(".ppt") ||
                            listFile[i].getAbsolutePath().contains(".pptx") ||
                            listFile[i].getAbsolutePath().contains(".html")) {
                        if(!(listFile[i].getParentFile().getName()).startsWith(".")){
                        if(!(listFile[i].getName()).startsWith(".")){
                            if(!listFile[i].getName().startsWith(".")){
                                BaseModel BaseModel = new BaseModel();
                                BaseModel.setName(listFile[i].getName());
                                BaseModel.setBucketName(listFile[i].getParentFile().getName());
                                BaseModel.setPath(listFile[i].getAbsolutePath());
                                BaseModel.setSize(listFile[i].length());
                                BaseModel.setDate(Util.convertTimeDateModified(listFile[i].lastModified()));

                                Directory<BaseModel> directory = new Directory<>();
                                directory.setId(BaseModel.getBucketId());
                                directory.setName(BaseModel.getBucketName());
                                directory.setPath(Util.extractPathWithoutSeparator(BaseModel.getPath()));

                                if(!directories1.contains(directory)){
                                    directory.addFile(BaseModel);
                                    directories.add(directory);
                                    directories1.add(directory);

                                }else{
                                    directories.get(directories.indexOf(directory)).addFile(BaseModel);
                                }

                                docDataList.add(BaseModel);
                            }
                        }
                        }

                    }
                } else if (listFile[i].isDirectory()) {
                    getDocumentsFolder(listFile[i].getAbsolutePath(), docDataList);
                }
            }
        }

        Collections.reverse(docDataList);
        return docDataList;
    }



    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }

}