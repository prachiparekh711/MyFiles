package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Adapter.SearchDocumentAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchDocumentActivity extends AppCompatActivity{

    SearchView searchView;
    RecyclerView recyclerView;
    SearchDocumentAdapter vidAdapter;
    private List<BaseModel> mDocFiles = new ArrayList<>();
    public  static List<Directory<BaseModel>> directories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_document);
        init();

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchDocumentActivity.this,RecyclerView.VERTICAL,false));
        vidAdapter = new SearchDocumentAdapter(mDocFiles,SearchDocumentActivity.this);
        recyclerView.setAdapter(vidAdapter);

        searchView.setActivated(true);
        searchView.setQueryHint("Type your keyword here");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                vidAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                vidAdapter.getFilter().filter(newText);

                return false;
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        getListFolder();
    }

    public void init(){
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.recyclerView);
    }


    private ArrayList<BaseModel> getListFolder() {

        mDocFiles.clear();
        directories.clear();

        mDocFiles = getDocumentsFolder(BaseDocumentActivity.docDirNamedir,(ArrayList<BaseModel>)mDocFiles);

        return (ArrayList<BaseModel>)mDocFiles;
    }

    public static ArrayList<BaseModel> getDocumentsFolder(String filePath, ArrayList<BaseModel> docDataList) {
        List<Directory> directories1 = new ArrayList<>();
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

                        if (!listFile[i].getName().startsWith(".")){
                            BaseModel BaseModel = new BaseModel();
                            BaseModel.setName(listFile[i].getName());
                            BaseModel.setBucketName(listFile[i].getParentFile().getName());
                            BaseModel.setPath(listFile[i].getAbsolutePath());
                            BaseModel.setSize(listFile[i].length());
                            BaseModel.setDate(Util.convertTimeDateModified(listFile[i].lastModified()));

                            Directory<BaseModel> directory = new Directory<>();
                            directory.setId(BaseModel.getBucketId());
                            directory.setName(BaseModel.getDate());
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
                } else if (listFile[i].isDirectory()) {
                    getDocumentsFolder(listFile[i].getAbsolutePath(), docDataList);
                }
            }
        }

        Collections.reverse(docDataList);
        return docDataList;
    }
    

}