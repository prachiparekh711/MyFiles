package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.SearchZipAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.docDirName;

public class SearchZipActivity extends AppCompatActivity{

    SearchView searchView;
    RecyclerView recyclerView;
    SearchZipAdapter vidAdapter;
    ArrayList<BaseModel> mZipFiles = new ArrayList<>();
    ArrayList<Directory<BaseModel>> mZipDir = new ArrayList<>();
    public static RelativeLayout rl_progress;
    public static AVLoadingIndicatorView avi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_zip);

        init();

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchZipActivity.this,RecyclerView.VERTICAL,false));
        vidAdapter = new SearchZipAdapter(mZipFiles,SearchZipActivity.this,mZipDir);
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
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                mZipFiles=getZipFolder(docDirName,mZipFiles);
            }
        });
    }

    public void init(){
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.recyclerView);

        rl_progress=findViewById(R.id.rl_progress);
        avi=findViewById(R.id.avi);
    }

    public static   void startAnim() {
//        Log.e("start anim","!!!");
        rl_progress.setVisibility(View.VISIBLE);
//        avi.show();
        avi.smoothToShow();
    }

    public static void stopAnim() {
//        Log.e("stop   anim","!!!");
        rl_progress.setVisibility(View.GONE);
        avi.hide();
        // or avi.smoothToHide();
    }



    public  ArrayList<BaseModel> getZipFolder(String filePath, ArrayList<BaseModel> docDataList) {
        List<Directory> directories1 = new ArrayList<>();
        File dir = new File(filePath);
        boolean success = true;
        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            if(listFile!=null){
                for(int i = 0;i < listFile.length;i++){
                    if(listFile[i].isFile()){
                        if(listFile[i].getAbsolutePath().endsWith(".zip")){

                            BaseModel BaseModel = new BaseModel();
                            BaseModel.setName(listFile[i].getName());
                            BaseModel.setFolderName(listFile[i].getParentFile().getName());
                            BaseModel.setPath(listFile[i].getAbsolutePath());
                            BaseModel.setSize(listFile[i].length());
                            docDataList.add(BaseModel);
//                            //   Log.e("i :" + i,BaseModel.getName() + " Path :" + BaseModel.getPath());

                            Directory<BaseModel> directory = new Directory<>();
                            directory.setId(BaseModel.getBucketId());
                            directory.setName(BaseModel.getFolderName());
                            directory.setPath(Util.extractPathWithoutSeparator(BaseModel.getPath()));
//                            //   Log.e("directory :" + i,directory.getName() +":"+ directory.getPath());
                            if (!directories1.contains(directory)) {
                                directory.addFile(BaseModel);
                                mZipDir.add(directory);
                                directories1.add(directory);
                            } else {
                                mZipDir.get(mZipDir.indexOf(directory)).addFile(BaseModel);
                            }

                        }
                    }else if(listFile[i].isDirectory()){
                        getZipFolder(listFile[i].getAbsolutePath(),docDataList);
                    }
                }
            }
        }

        Collections.reverse(docDataList);
        return docDataList;
    }

    
}