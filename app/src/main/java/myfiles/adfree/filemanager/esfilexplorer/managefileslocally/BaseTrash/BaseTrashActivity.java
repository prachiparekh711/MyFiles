package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseTrash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.MyMediaScannerConnectionClient;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.docDirName;

public class BaseTrashActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView mTrashRec;
    TrashAdapter adapter;
    TrashGridAdapter gridAdapter;
    ArrayList<BaseModel> mTrashList=new ArrayList<>();
    ArrayList<BaseModel> mTrashFinalList=new ArrayList<>();
    ArrayList<String> mTrashPathList=new ArrayList<>();
    ArrayList<String> mTrashScanList=new ArrayList<>();
    public static ImageView view,close,back;
    public static TextView count;
    public static LinearLayout actionLL,titleLL;
    public static RelativeLayout headerRL;
    RelativeLayout mRemove,mRestore;
    TextView cancelDel,okDel;
    ImageView dustbin;
    MediaScannerConnection msConn;
    CardView mBottomRL;
    AlertDialog alertVault;
    TextView successText;


    private FirebaseAnalytics mFirebaseAnalytics;
    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_trash);

        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(BaseTrashActivity.this);
        close();

        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseTrashActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);
    }

    public void init(){
        mTrashRec = findViewById(R.id.rvImages);
        view = findViewById(R.id.view);
        count = findViewById(R.id.count);
        close = findViewById(R.id.ic_close);
        view.setOnClickListener(this);
        count.setOnClickListener(this);
        close.setOnClickListener(this);

        actionLL=findViewById(R.id.l3);
        titleLL=findViewById(R.id.l1);
        headerRL=findViewById(R.id.header);
        mRemove=findViewById(R.id.remove);
        mRestore=findViewById(R.id.restore);
        dustbin=findViewById(R.id.dustbin);
        mBottomRL=findViewById(R.id.bottomCard);

        mRemove.setOnClickListener(this);
        mRestore.setOnClickListener(this);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public  ArrayList<BaseModel> getTrashFolder(String filePath,ArrayList<BaseModel> docDataList) {
        File dir = new File(filePath);
        boolean success = true;
        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            if(listFile!=null){
                for(int i = 0;i < listFile.length;i++){
                    if(listFile[i].isFile()){
                        if(listFile[i].getParentFile().getName().equals(".TrashFiles")){
                            if (!listFile[i].getName().startsWith(".")){
                                BaseModel BaseModel = new BaseModel();
                                BaseModel.setName(listFile[i].getName());
                                BaseModel.setBucketName(listFile[i].getParentFile().getName());
                                BaseModel.setPath(listFile[i].getAbsolutePath());
                                BaseModel.setSize(listFile[i].length());
                                BaseModel.setDate(Util.convertTimeDateModified1(listFile[i].lastModified()));
                                docDataList.add(BaseModel);
                            }
                        }
                    }else if(listFile[i].isDirectory()){
                        getTrashFolder(listFile[i].getAbsolutePath(),docDataList);
                    }
                }
            }
        }

        Collections.reverse(docDataList);
        return docDataList;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (msConn!=null)
            this.msConn.connect();
        mTrashList.clear();
        mTrashFinalList.clear();
        mTrashPathList= SharedPreference.getTrashList(getBaseContext());
        mTrashList=getTrashFolder(docDirName,mTrashList);
        for(int i = 0;i < mTrashList.size();i++){
//            Log.e("Trash file:",mTrashList.get(i).getPath());
            for(int j = 0;j < mTrashPathList.size();j++){
                File file=new File(mTrashPathList.get(j));
                if(mTrashList.get(i).getName().equals(file.getName())){
                    mTrashFinalList.add(mTrashList.get(i));
                }
            }
        }


        if(mTrashFinalList.size()==0){
            dustbin.setVisibility(View.VISIBLE);
            mBottomRL.setVisibility(View.GONE);
        }else{
            dustbin.setVisibility(View.GONE);
            mBottomRL.setVisibility(View.VISIBLE);
            if(Util.VIEW_TYPE == "Grid"){
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        mTrashRec.setLayoutManager(new GridLayoutManager(BaseTrashActivity.this,5));
                        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                        gridAdapter = new TrashGridAdapter(mTrashFinalList,BaseTrashActivity.this);
                        mTrashRec.setAdapter(gridAdapter);
                    }
                });

            }else{
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        mTrashRec.setLayoutManager(new LinearLayoutManager(BaseTrashActivity.this,RecyclerView.VERTICAL,false));
                        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                        adapter = new TrashAdapter(mTrashFinalList,BaseTrashActivity.this);
                        mTrashRec.setAdapter(adapter);
                    }
                });

            }
        }
    }

    public void changeViewIcon(){
        if(Util.VIEW_TYPE=="Grid"){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                }
            });
            Util.VIEW_TYPE="List";
        }
        else{
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                }
            });
            Util.VIEW_TYPE="Grid";
        }

        onResume();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.view:
                changeViewIcon();
                break;
            case R.id.ic_close:
                close();
                break;
            case R.id.remove:
                if(Util.mSelectedTrashList.size()>0) {
                    ActionDelete();
                }else{
                    Toast.makeText(BaseTrashActivity.this,"Select at least one file for delete or restore.",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.restore:
                restore();
                break;
        }
    }

    public void restore(){
        if(Util.mSelectedTrashList.size()>0) {

            int countRestore = 0;
            String fileName;
            String basePath = "";

            for (int i = 0; i < Util.mSelectedTrashList.size(); i++) {
                String path = Util.mSelectedTrashList.get(i);
                fileName = path.substring(path.lastIndexOf("/") + 1);


                File from = new File(Util.mSelectedTrashList.get(i));
                for (int j = 0; j < mTrashPathList.size(); j++) {

                    if (mTrashPathList.get(j).contains(fileName)) {
                        basePath = mTrashPathList.get(j);

                        break;
                    }
                }
                File to = new File(basePath);
                if (!to.getParentFile().exists())
                    to.mkdir();
                boolean isRename = from.renameTo(to);
                if (isRename) {
                    countRestore++;
                    mTrashScanList.add(to.getAbsolutePath());
//               Log.e("is rename",String.valueOf(isRename));
                }
            }

            if (countRestore > 0) {
                fireAnalytics("Trash", "Restore");
                successText.setText(countRestore + " File(s) restore successfully.");
                alertVault.show();

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        alertVault.dismiss();
                    }
                }, 2000);
            }


            for (int i = 0; i < mTrashScanList.size(); i++) {

                MediaScannerConnection.MediaScannerConnectionClient client =
                        new MyMediaScannerConnectionClient(
                                getApplicationContext(), new File(mTrashScanList.get(i)), null);
            }

            close();
        }else{
            Toast.makeText(BaseTrashActivity.this,"Select at least one file for delete or restore.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        if(Util.mSelectedTrashList.size()==0){
            super.onBackPressed();
        }else{
            close();
        }
    }

    void close(){
        Util.clickEnable=true;
        Util.showCircle=false;
        TrashAdapter.viewClose=true;
        Util.mSelectedTrashList.clear();
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                Util.isAllSelected=false;
                onResume();
            }
        });
    }

    public void ActionDelete(){

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseTrashActivity.this);
        final View view = factory.inflate(R.layout.trash_dialog,null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDel=view.findViewById(R.id.text4);
        okDel=view.findViewById(R.id.text5);

        cancelDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertadd.dismiss();
            }
        });

        okDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int count=0;
                for(int j = 0;j < Util.mSelectedTrashList.size();j++){
                    //   Log.e("package:",Util.mSelectedTrashList.get(j));
                   
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedTrashList.get(j));
                   
                        if(sourceFile.exists()){
                            isDelete = sourceFile.delete();
                            //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                        }
                    
                    if(isDelete){
                        count++;
                    }
                }

                if(count==Util.mSelectedTrashList.size()){
                    fireAnalytics("Trash", "Delete");
                    successText.setText( "Delete successfully."  );
                    alertVault.show();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            close();
                            alertVault.dismiss();
                        }
                    }, 2000);
                }else{
                    Toast.makeText(BaseTrashActivity.this,"Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }

                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedTrashList.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(getResources().getColor(R.color.header_color));
        view.setVisibility(View.VISIBLE);

    }

//    public void scanPhoto(File[] imageFileName) {
//        msConn = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
//            public void onMediaScannerConnected() {
//                if (msConn!=null)
//                    msConn.connect();
//
//                Log.i("msClient obj", "scan completed");
//                for(int i=0;i<imageFileName.length;i++){
//                    MediaScannerConnection.scanFile(getApplicationContext(),new String[]{ imageFileName[i].getPath() },new String[]{ "*/*" },null);
//                }
//            }
//
//            public void onScanCompleted(String path, Uri uri) {
//                msConn.disconnect();
//
//            }
//        });
//        this.msConn.connect();
//    }

}

