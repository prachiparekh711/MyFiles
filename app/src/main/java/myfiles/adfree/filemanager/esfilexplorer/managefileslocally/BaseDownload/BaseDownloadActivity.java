package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.HomeActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.VaultActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload.DownloadListAdapter.viewClose;

public class BaseDownloadActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    public static String dirNamedir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    ArrayList<BaseModel> arrayList=new ArrayList<>();
    ImageView back;
    public static ImageView view,more,close;
    public static CardView card1,card2,card3,card4;
    LinearLayout mSelectAllC1,mRefreshC1;
    LinearLayout mDeselectAllC2,mVaultC2;
    LinearLayout mSelectAllC3,mVaultC3;
    LinearLayout mOpenWithC4,mSelectAllC4,mRenameC4,mVaultC4,mInfoC4;
    TextInputEditText et_rename;
    LinearLayout llRename;
    public static BottomSheetBehavior mBottomSheetBehavior1;
    TextView renameOK,renameCancel;
    MediaScannerConnection msConn;
    public static LinearLayout actionLL,titleLL;
    public static RelativeLayout headerRL;
    public static ImageView delete,share;
    public static TextView count;
    int backPresse=0;
    DownloadAdapter downloadAdapter;
    DownloadListAdapter downloadListAdapter;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 19;
    AlertDialog alertadd1 ;
    RelativeLayout rootLayout;
    public static ImageView proImage;
    public static TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    RecyclerView mDownloadRec;
    TextView successText;
    int vaultCount=0,moveCount=0;
    AlertDialog alertVault;
    public static RelativeLayout rl_progress;
    public static AVLoadingIndicatorView avi;


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
        setContentView(R.layout.activity_base_download);
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(BaseDownloadActivity.this);

        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseDownloadActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);
        arrayList.clear();
        arrayList=getAllData(arrayList);
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

    @Override
    protected void onResume(){
        super.onResume();
        if (msConn!=null)
            this.msConn.connect();

       
        if(Util.VIEW_TYPE == "Grid"){
            mDownloadRec.setLayoutManager(new GridLayoutManager(BaseDownloadActivity.this,5));
            view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
            downloadAdapter = new DownloadAdapter(arrayList,BaseDownloadActivity.this);
            mDownloadRec.setAdapter(downloadAdapter);

        }else{
            mDownloadRec.setLayoutManager(new LinearLayoutManager(BaseDownloadActivity.this,RecyclerView.VERTICAL,false));
            view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
            downloadListAdapter = new DownloadListAdapter(arrayList,BaseDownloadActivity.this);
            mDownloadRec.setAdapter(downloadListAdapter);
        }
    }
    
    public void init(){
        rl_progress=findViewById(R.id.rl_progress);
        avi=findViewById(R.id.avi);

        mDownloadRec=findViewById(R.id.rvImages);
        
        back=findViewById(R.id.back);
        view=findViewById(R.id.view);
        more=findViewById(R.id.more);

        card1=findViewById(R.id.card1);
        mSelectAllC1=findViewById(R.id.selectAllC1);
        mRefreshC1=findViewById(R.id.refreshC1);
        mSelectAllC1.setOnClickListener(this);
        mRefreshC1.setOnClickListener(this);

        card2=findViewById(R.id.card2);
        mDeselectAllC2=findViewById(R.id.deSelectAllC2);
        mVaultC2=findViewById(R.id.vaultC2);
        mDeselectAllC2.setOnClickListener(this);        
        mVaultC2.setOnClickListener(this);

        card3=findViewById(R.id.card3);
        mSelectAllC3=findViewById(R.id.selectAllC3);        
        mVaultC3=findViewById(R.id.vaultC3);
        mSelectAllC3.setOnClickListener(this);        
        mVaultC3.setOnClickListener(this);

        card4=findViewById(R.id.card4);
        mOpenWithC4=findViewById(R.id.openWithC4);
        mSelectAllC4=findViewById(R.id.selectAllC4);
        mRenameC4=findViewById(R.id.renameC4);
        mVaultC4=findViewById(R.id.vaultC4);
        mInfoC4=findViewById(R.id.infoC4);
        mOpenWithC4.setOnClickListener(this);
        mSelectAllC4.setOnClickListener(this);
        mRenameC4.setOnClickListener(this);
        mVaultC4.setOnClickListener(this);
        mInfoC4.setOnClickListener(this);

        view.setOnClickListener(this);
        more.setOnClickListener(this);
        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(BaseDownloadActivity.this,HomeActivity.class);
                startActivity(in);
            }
        });

        actionLL=findViewById(R.id.l3);
        titleLL=findViewById(R.id.l1);
        headerRL=findViewById(R.id.header);
        delete=findViewById(R.id.delete);
        share=findViewById(R.id.share);
        delete.setOnClickListener(this);
        share.setOnClickListener(this);

        count=findViewById(R.id.count);
        alertadd1 = new AlertDialog.Builder(BaseDownloadActivity.this).create();
        rootLayout=findViewById(R.id.rootLayout);
        rootLayout.setOnTouchListener(this);

        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);

        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);

        close=findViewById(R.id.ic_close);
        close.setOnClickListener(this);
    }

    public static ArrayList<BaseModel> getAllData(ArrayList<BaseModel> allDataList) {

        File dir = new File(dirNamedir);
        boolean success = true;

        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                if(!listFile[i].getName().startsWith(".")){
                    BaseModel dataModel = new BaseModel();
                    dataModel.setName(listFile[i].getName());
                    dataModel.setSize(listFile[i].length());
                    dataModel.setPath(listFile[i].getAbsolutePath());
                    dataModel.setDate(Util.convertTimeDateModified1(listFile[i].lastModified()));
                  
                    allDataList.add(dataModel);
                }
            }

        }

        Collections.reverse(allDataList);
        return allDataList;
    }



    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.view:
                changeViewIcon();
                break;
            case R.id.more:
                if(Util.isAllSelected){
                    card2.setVisibility(View.VISIBLE);
                    card1.setVisibility(View.GONE);
                    card3.setVisibility(View.GONE);
                    card4.setVisibility(View.GONE);
                }else  if(Util.mSelectedDownloadList.size()==1){
                    card4.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    card3.setVisibility(View.GONE);
                }else  if(Util.mSelectedDownloadList.size()>1){
                    card4.setVisibility(View.GONE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    card3.setVisibility(View.VISIBLE);
                } else{
                    card1.setVisibility(View.VISIBLE);
                    card3.setVisibility(View.GONE);
                    card2.setVisibility(View.GONE);
                    card4.setVisibility(View.GONE);
                }
                break;
            case R.id.selectAllC1: {
                card1.setVisibility(View.GONE);
                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedDownloadList.clear();
               
                for(int j = 0;j < arrayList.size();j++){
                    if(!Util.mSelectedDownloadList.contains(arrayList.get(j).getPath())){
                        Util.mSelectedDownloadList.add(arrayList.get(j).getPath());

                    }                
                }
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        onResume();
                    }
                });

            }
            break;
            case R.id.refreshC1:
                card1.setVisibility(View.GONE);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        onResume();
                    }
                });
                break;
            case R.id.deSelectAllC2:{
                card2Close();
            }

            break;           
            case R.id.vaultC2:

            case R.id.vaultC3:

            case R.id.vaultC4:
                fireAnalytics("Downloads", "Vault");
                if (SharedPreference.getPasswordProtect(getBaseContext()).equals("")) {
                   Intent in=new Intent(BaseDownloadActivity.this, VaultActivity.class);
                   Util.VaultFromOther=true;
                   startActivity(in);
                }else {
                    moveToVault();
                    card2Close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onResume();
                        }
                    });
                }
                break;
            case R.id.selectAllC3:{
                card3.setVisibility(View.GONE);

                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedDownloadList.clear();
                
                for(int j = 0;j < arrayList.size();j++){
                    if(!Util.mSelectedDownloadList.contains(arrayList.get(j).getPath())){
                        Util.mSelectedDownloadList.add(arrayList.get(j).getPath());
                    }
                }
                
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        onResume();
                    }
                });

            }
            break;
            case R.id.openWithC4:
                fireAnalytics("Downloads", "Open");
                try{
                    card4.setVisibility(View.GONE);
                    Uri uri = Uri.parse(Util.mSelectedDownloadList.get(0));
                    Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW);
                    String mime = "*/*";
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    if(mimeTypeMap.hasExtension(
                            MimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                        mime = mimeTypeMap.getMimeTypeFromExtension(
                                MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                    intent1.setDataAndType(uri,mime);
                    startActivity(intent1);
                }catch(Exception e){
                    Toast.makeText(BaseDownloadActivity.this,"None of your apps can open this file.",Toast.LENGTH_SHORT).show();
                }
                card2Close();
                break;
            case R.id.selectAllC4:{
                card4.setVisibility(View.GONE);

                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedDownloadList.clear();

                for(int j = 0;j < arrayList.size();j++){
                    if(!Util.mSelectedDownloadList.contains(arrayList.get(j).getPath())){
                        Util.mSelectedDownloadList.add(arrayList.get(j).getPath());
                    }
                }
                
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        onResume();
                    }
                });

            }

            break;
            case R.id.renameC4:
                fireAnalytics("Downloads", "Rename");
                card4.setVisibility(View.GONE);
                ActionRename();
                break;
            case R.id.infoC4:
                fireAnalytics("Downloads", "Info");
                File imgFile = new  File(Util.mSelectedDownloadList.get(0));
                if(imgFile.exists()){

                    fileInfo(imgFile,BaseDownloadActivity.this);
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Toast.makeText(BaseDownloadActivity.this,"Problem with file.",Toast.LENGTH_SHORT).show();
                }
                card2Close();
                card4.setVisibility(View.GONE);
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case R.id.share:
                fireAnalytics("Downloads", "Share");
                ArrayList<Uri> uris = new ArrayList<>();
                String path;
                File file;
                Uri contentUri;
                for(int i = 0;i<Util.mSelectedDownloadList.size();i++){
                    path= Util.mSelectedDownloadList.get(i);
                    file=new File(path);
                    contentUri = FileProvider.getUriForFile(BaseDownloadActivity.this,getPackageName() + ".provider",file);
                    uris.add(contentUri);
                }

//                //   Log.e("Uri list",String.valueOf(uris.size()));
                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("*/*");
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
                card2Close();
                break;
            case R.id.delete:
                fireAnalytics("Downloads", "Delete");
                ActionDelete();
                break;

            case R.id.ic_close:
                Util.clickEnable=true;
                card2Close();

                onResume();

                break;
        }
    }

    public void ActionRename(){
        File file1=new File(Util.mSelectedDownloadList.get(0));
        int pos = file1.getName().lastIndexOf(".");

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseDownloadActivity.this);
        final View view = factory.inflate(R.layout.rename_dialog,null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        renameOK=view.findViewById(R.id.tvRenameOk);
        renameCancel=view.findViewById(R.id.tvRenameCan);
        et_rename=view.findViewById(R.id.et_rename);
        et_rename.setText(file1.getName().substring(0, pos));
        renameCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertadd.dismiss();
            }
        });

        renameOK.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (et_rename.length() > 0) {
                    File file = new File(Util.mSelectedDownloadList.get(0));
                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                    File sdcard = new File(file.getParentFile().getAbsolutePath());
                    File newFileName = new File(sdcard, et_rename.getText().toString()  + extension);
                    boolean isRename = file.renameTo(newFileName);
                    if (isRename) {
                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        resolver.delete(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA +
                                        " =?", new String[]{file.getAbsolutePath()});
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(newFileName));
                        getApplicationContext().sendBroadcast(intent);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){
                                Util.clickEnable=true;
                                card2Close();
                                scanPhoto(newFileName);
                            }
                        });

                        successText.setText( "Rename successfully."  );
                        alertVault.show();

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onResume();
                                alertVault.dismiss();
                            }
                        }, 2000);
                    }else{
                        Toast.makeText(BaseDownloadActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                    }
                    alertadd.dismiss();
                }
            }
        });
        alertadd.show();
    }

    public void ActionDelete(){

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseDownloadActivity.this);
        final View view = factory.inflate(R.layout.delete_dialog,null);
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
                for(int j = 0;j < Util.mSelectedDownloadList.size();j++){
                    //   Log.e("package:",Util.mSelectedDownloadList.get(j));
                    isDeleteImg=true;
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedDownloadList.get(j));
                    if(sourceFile.exists()){
                        Util.MoveToTrash(BaseDownloadActivity.this,sourceFile);
                        isDelete = Util.delete(BaseDownloadActivity.this,sourceFile);
                        //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                    }

                    if(isDelete){
                        count++;
                    }
                }

                if(count==Util.mSelectedDownloadList.size()){
                    successText.setText( "Delete successfully."  );
                    alertVault.show();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            card2Close();
                            Util.IsUpdate=true;
                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BaseDownloadActivity.this);
                            Intent localIn = new Intent("TAG_REFRESH");
                            lbm.sendBroadcast(localIn);
                            alertVault.dismiss();
                        }
                    }, 2000);
                }else{
                    Toast.makeText(BaseDownloadActivity.this,"Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }


                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public void moveToVault(){
        File sampleFile = Environment.getExternalStorageDirectory();
        for(int i = 0;i< Util.mSelectedDownloadList.size();i++){

            File file1 = new File(Util.mSelectedDownloadList.get(i));
            if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                SaveImage(file1);
            }else{
                if(!SharedPreference.getSharedPreference(BaseDownloadActivity.this).contains(file1.getParentFile().getAbsolutePath())){
                    Intent in = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    in.putExtra("android.content.extra.SHOW_ADVANCED",true);
                    startActivityForResult(in,REQUEST_CODE_OPEN_DOCUMENT_TREE);
                }else{
                    getData(file1);
                }
            }
        }


        successText.setText( vaultCount + " File(s) moved to vault."  );
        alertVault.show();
        File[] files=new File[Util.mSelectedDownloadList.size()];
        for(int i=0;i<Util.mSelectedDownloadList.size();i++){
            files[i]=new File(Util.mSelectedDownloadList.get(i));
        }
        scanPhoto(files);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.IsUpdate=true;
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BaseDownloadActivity.this);
                Intent localIn = new Intent("TAG_REFRESH");
                lbm.sendBroadcast(localIn);
                alertVault.dismiss(); vaultCount=0;
            }
        }, 2000);
    }

    public void scanPhoto(File imageFileName) {
        msConn = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
//                if (msConn!=null)
//                    msConn.connect();
                msConn.scanFile(imageFileName.getPath(), null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
                MediaScannerConnection.scanFile(getApplicationContext(), new String[] { imageFileName.getAbsolutePath() }, new String[] { "video/*" }, null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(imageFileName));
                    sendBroadcast(mediaScanIntent);
                }
                else
                {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(imageFileName.getAbsolutePath())));
                }
            }
        });
        this.msConn.connect();
    }

    public void scanPhoto(File[] imageFileName) {
        msConn = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                if (msConn!=null)
                    msConn.connect();

                Log.i("msClient obj", "scan completed");
                for(int i=0;i<imageFileName.length;i++){
                    MediaScannerConnection.scanFile(getApplicationContext(),new String[]{ imageFileName[i].getPath() },new String[]{ "video/*" },null);
                }
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
            }
        });
        this.msConn.connect();
    }

    private void SaveImage(File samplefile) {

        File from=new File(samplefile.getAbsolutePath());
        File folder = new File(from.getParentFile().getAbsolutePath());
        if(!folder.exists())
            folder.mkdir();
        File to=new File(folder.getAbsolutePath() + "/." + samplefile.getName());
        from.renameTo(to);

        ArrayList<String> hideFileList = SharedPreference.getHideFileList(BaseDownloadActivity.this);
        hideFileList.add(to.getAbsolutePath());
        SharedPreference.setHideFileList(BaseDownloadActivity.this, hideFileList);

        try {
                vaultCount++;
//            Toast.makeText(this, "Hide " + samplefile.getName(), Toast.LENGTH_LONG).show();
            if (samplefile.exists()) {
                boolean isDelete=samplefile.delete();
                //   Log.e("File deleted:",String.valueOf(isDelete));

            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void card2Close(){

        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
        Util.showCircle=false;
        viewClose=true;
        Util.mSelectedDownloadList.clear();
        Util.clickEnable=true;
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                Util.isAllSelected=false;
                onResume();
            }
        });
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
    public boolean onTouch(View v,MotionEvent event){
        //   Log.e("View ID:",String.valueOf(v.getId()));
//        if(v.getId()!=R.id.card1 || v.getId()!=R.id.card2 || v.getId()!=R.id.card3 || v.getId()!=R.id.card4)
//        {
        if(v.getId()!=R.id.more){
            disableCard();
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            Util.showCircle = false;
            Util.clickEnable = true;
            if(Util.mSelectedDownloadList.size()==0){
                viewClose=false;

                super.onBackPressed();
            }

            if(!viewClose){
                changeToOriginalView();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        onResume();
                    }
                });

            }else{
                backPresse++;
                if(backPresse == 1){
                    viewClose = false;
                    finish();
                }
            }
        }

    }
    
    public static void fileInfo(File imgFile,Context context){
        RequestOptions options = new RequestOptions();
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        if(imgFile.exists()){
            if(imgFile.getPath().endsWith(".ppt") || imgFile.getPath().endsWith(".pptx")){
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_powerpoint));
            }else if(imgFile.getPath().endsWith(".pdf")){
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pdf));
            }else if(imgFile.getPath().endsWith(".xls")){
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_excel));
            }else if(imgFile.getPath().endsWith(".docx") || imgFile.getPath().endsWith(".doc")){
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_word));
            }else if(imgFile.getPath().toLowerCase().endsWith(".png") || imgFile.getPath().toLowerCase().endsWith(".jpg") || imgFile.getPath().toLowerCase().endsWith(".jpeg")){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                proImage.setImageBitmap(myBitmap);
            }else if(imgFile.getPath().endsWith(".zip")){
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.zip_btn));
            }else if(imgFile.getPath().endsWith(".mp3")){
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.music_icon));
            }else if(imgFile.getPath().toLowerCase().endsWith(".apk")){
                try{
                    String APKFilePath = imgFile.getPath();
                    PackageInfo packageInfo = context.getPackageManager()
                            .getPackageArchiveInfo(APKFilePath, PackageManager.GET_ACTIVITIES);
                    if(packageInfo != null){
                        ApplicationInfo appInfo = packageInfo.applicationInfo;
                        if(Build.VERSION.SDK_INT >= 8){
                            appInfo.sourceDir = APKFilePath;
                            appInfo.publicSourceDir = APKFilePath;
                        }
                        Drawable icon = appInfo.loadIcon(context.getPackageManager());
                       proImage.setImageDrawable(icon);
                    }
                }catch(Exception e){
                    Glide.with(context)
                            .load(context.getResources().getDrawable(R.drawable.apps))
                            .apply(options.centerCrop()
                                    .skipMemoryCache(true)
                                    .priority(Priority.LOW)
                                    .format(DecodeFormat.PREFER_ARGB_8888))

                            .into(proImage);
                }
            }else if (imgFile.getPath().toLowerCase().endsWith(".mp4") ) {
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imgFile.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                proImage.setImageBitmap(thumb);
            }else{
                proImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_other));
            }
            proName.setText(imgFile.getName());

            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
            String date = spf.format(lastModDate);
            proDate.setText(date);
            String s = Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(imgFile.getPath());
        }

    }

    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedDownloadList.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(getResources().getColor(R.color.header_color));
        delete.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        disableCard();
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
        viewClose=true;
    }
    


    private void getData(File file1) {

        File file2 = new File(file1.getParentFile().getParentFile().getAbsolutePath(), "." + file1.getName());
        File file3 = new File(file1.getParentFile().getAbsolutePath(), "." + file1.getName());
        String[] parts = (file1.getAbsolutePath()).split("/");

        OutputStream fileOutupStream = null;
        DocumentFile targetDocument = getDocumentFile(file2, false);

        try {
            fileOutupStream = getContentResolver().openOutputStream(targetDocument.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutupStream);
            fileOutupStream.flush();
            fileOutupStream.close();
            ArrayList<String> hideFileList = SharedPreference.getHideFileList(BaseDownloadActivity.this);
            hideFileList.add(file3.getAbsolutePath());
            SharedPreference.setHideFileList(BaseDownloadActivity.this, hideFileList);

            if (file1.exists()) {
                boolean isDelete = Util.delete(BaseDownloadActivity.this, file1);
            }

            if (arrayList.isEmpty())
                onBackPressed();
            Toast.makeText(this, "Hide " + file1.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void disableCard(){
        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
    }

    public DocumentFile getDocumentFile(final File file, final boolean isDirectory) {
        String baseFolder = getExtSdCardFolder(file);

        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            relativePath = fullPath.substring(baseFolder.length() + 1);
        } catch (IOException e) {
            return null;
        }

        Uri treeUri = Uri.parse(SharedPreference.getSharedPreferenceUri(BaseDownloadActivity.this));

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(BaseDownloadActivity.this, treeUri);

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    public String getExtSdCardFolder(final File file) {
        String[] extSdPaths = getExtSdCardPaths();
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String[] getExtSdCardPaths() {
        List<String> paths = new ArrayList<>();
        for (File file : getExternalFilesDirs("external")) {
            if (file != null && !file.equals(getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("TAG", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }

    public void changeHomeView(){
        titleLL.setVisibility(View.GONE);
        actionLL.setVisibility(View.VISIBLE);
        headerRL.setBackgroundColor(getResources().getColor(R.color.themeColor));
        delete.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        count.setText(Util.mSelectedDownloadList.size() + " Selected");

    }

}