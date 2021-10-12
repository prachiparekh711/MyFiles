package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity;

import androidx.annotation.RequiresApi;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.FilesAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.AlbumChildAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.AlbumChildListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.MyMediaScannerConnectionClient;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.AlbumChildAdapter.viewClose;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.docDirName;

public class ViewAlbumActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.tv_albumName)
    TextView tv_albumName;
    @BindView(R.id.rv_albumImage)
    RecyclerView rv_albumImage;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    public static LinearLayout l1,l2,l3,l4;
    public static RelativeLayout toolbar;
    private String DirName = "";
    private AlbumChildAdapter albumChildAdapter;
    private AlbumChildListAdapter albumChildListAdapter;
    ArrayList<Directory<BaseModel>> mZipDir = new ArrayList<>();
    private List<BaseModel> mZipFiles = new ArrayList<>();
    int backPresse=0;
    public static ImageView search,view,more,back,close;
    public static CardView card1,card2,card3,card4;
    LinearLayout mSelectAllC1,mRefreshC1;
    LinearLayout mDeselectAllC2,mCompressC2,mMoveC2,mVaultC2;
    LinearLayout mSelectAllC3,mCompressC3,mMoveC3,mVaultC3;
    LinearLayout mOpenWithC4,mSelectAllC4,mRenameC4,mCompressC4,mMoveC4,mVaultC4,mInfoC4;
    public static LinearLayout actionLL,titleLL;
    public static RelativeLayout headerRL;
    public static ImageView delete,share;
    public static TextView count;
    TextView compressPath;
    RecyclerView compressPathRec;
    FilesAdapter compressFileAdapter;
    AlertDialog alertadd1 ;
    boolean isCompress=false,isMove=false;
    int movePosition=0;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 19;
    MediaScannerConnection msConn;
    RelativeLayout rootLayout;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    LinearLayout llRename;
    TextView renameOK,renameCancel;
    TextInputEditText et_rename;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    TextView successText;
    int vaultCount=0,moveCount=0;
    AlertDialog alertVault;
    public static RelativeLayout rl_progress;
    public static AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_album4);
        ButterKnife.bind(ViewAlbumActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedZipList.clear();
        viewClose=true;
        DirName = getIntent().getStringExtra("DirName");
        //   Log.e("LLLL_DirName: ", DirName);
        tv_albumName.setText(DirName);

        init();

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);
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

    public void init(){
        rl_progress=findViewById(R.id.rl_progress);
        avi=findViewById(R.id.avi);

        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        l3=findViewById(R.id.l3);
        l4=findViewById(R.id.l4);
        toolbar=findViewById(R.id.rl_toolbar);
        count=findViewById(R.id.count);
        search=findViewById(R.id.search);
        back=findViewById(R.id.imgBack);
        more=findViewById(R.id.more);
        view=findViewById(R.id.view);
        close=findViewById(R.id.ic_close);
        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));

        view.setOnClickListener(this);
        more.setOnClickListener(this);
        search.setOnClickListener(this);
        close.setOnClickListener(this);

        card1=findViewById(R.id.card1);
        mSelectAllC1=findViewById(R.id.selectAllC1);
        mRefreshC1=findViewById(R.id.refreshC1);
        mSelectAllC1.setOnClickListener(this);
        mRefreshC1.setOnClickListener(this);

        card2=findViewById(R.id.card2);
        mDeselectAllC2=findViewById(R.id.deSelectAllC2);
        mCompressC2=findViewById(R.id.compressC2);
        mMoveC2=findViewById(R.id.moveC2);
        mVaultC2=findViewById(R.id.vaultC2);
        mDeselectAllC2.setOnClickListener(this);
        mCompressC2.setOnClickListener(this);
        mMoveC2.setOnClickListener(this);
        mVaultC2.setOnClickListener(this);

        card3=findViewById(R.id.card3);
        mSelectAllC3=findViewById(R.id.selectAllC3);
        mCompressC3=findViewById(R.id.compressC3);
        mMoveC3=findViewById(R.id.moveC3);
        mVaultC3=findViewById(R.id.vaultC3);
        mSelectAllC3.setOnClickListener(this);
        mCompressC3.setOnClickListener(this);
        mMoveC3.setOnClickListener(this);
        mVaultC3.setOnClickListener(this);

        card4=findViewById(R.id.card4);
        mOpenWithC4=findViewById(R.id.openWithC4);
        mSelectAllC4=findViewById(R.id.selectAllC4);
        mRenameC4=findViewById(R.id.renameC4);
        mCompressC4=findViewById(R.id.compressC4);
        mMoveC4=findViewById(R.id.moveC4);
        mVaultC4=findViewById(R.id.vaultC4);
        mInfoC4=findViewById(R.id.infoC4);
        mOpenWithC4.setOnClickListener(this);
        mSelectAllC4.setOnClickListener(this);
        mRenameC4.setOnClickListener(this);
        mCompressC4.setOnClickListener(this);
        mMoveC4.setOnClickListener(this);
        mVaultC4.setOnClickListener(this);
        mInfoC4.setOnClickListener(this);

        if(Util.VIEW_TYPE=="Grid"){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                }
            });

        }
        else{
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                }
            });
        }

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });

        actionLL=findViewById(R.id.l3);
        titleLL=findViewById(R.id.l1);
        headerRL=findViewById(R.id.header);
        delete=findViewById(R.id.delete);
        share=findViewById(R.id.share);
        count=findViewById(R.id.count);
        alertadd1 = new AlertDialog.Builder(ViewAlbumActivity.this).create();
        rootLayout=findViewById(R.id.rootLayout);


        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);

        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);



        share.setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    @Override
    public void onBackPressed(){
         if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            Util.showCircle = false;
            Util.clickEnable = true;

            if(!viewClose){
                viewClose = true;
                Util.mSelectedZipList.clear();
                changeToOriginalView();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(albumChildAdapter != null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
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
                }else  if(Util.mSelectedZipList.size()==1){
                    card4.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    card3.setVisibility(View.GONE);
                }else  if(Util.mSelectedZipList.size()>1){
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
                Util.mSelectedZipList.clear();
                //   Log.e("Zipfile size:",String.valueOf(mZipFiles.size()));
                for(int j = 0;j < mZipFiles.size();j++){
                    //   Log.e("Zipfile path:",mZipFiles.get(j).getPath());
                    if(!Util.mSelectedZipList.contains(mZipFiles.get(j).getPath())){
                        Util.mSelectedZipList.add(mZipFiles.get(j).getPath());
                    }
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter != null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });

            }
            break;
            case R.id.refreshC1:
                card1.setVisibility(View.GONE);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(albumChildAdapter != null)
                              albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.deSelectAllC2:{
                card2Close();
            }

            break;

            case R.id.moveC2:

            case R.id.moveC3:
                moveImages();
                break;
            case R.id.vaultC2:
            case R.id.vaultC3:
                moveToVault();
                card2Close();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        onResume();
                    }
                });
                break;
            case R.id.selectAllC3:{
                card3.setVisibility(View.GONE);

                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedZipList.clear();

                List<BaseModel> list = mZipFiles;
                for(int j = 0;j < list.size();j++){
                    if(!Util.mSelectedZipList.contains(list.get(j).getPath())){
                        Util.mSelectedZipList.add(list.get(j).getPath());
                    }
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter != null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });

            }
            break;
            case R.id.openWithC4:

                Uri uri =  Uri.parse(Util.mSelectedZipList.get(0));
                File file3=new File(Util.mSelectedZipList.get(0));
                String extension1 = file3.getAbsolutePath().substring(file3.getAbsolutePath().lastIndexOf("."));
                Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW);
                String mime="*/*";
                if(extension1.equals("zip"))
                    mime = "*/zip";
                else
                    mime="*/*";
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                if (mimeTypeMap.hasExtension(
                        MimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                    mime = mimeTypeMap.getMimeTypeFromExtension(
                            MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                intent1.setDataAndType(uri,mime);
                startActivity(intent1);
                card2Close();
                break;
            case R.id.selectAllC4:{
                card4.setVisibility(View.GONE);

                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedZipList.clear();


                List<BaseModel> list = mZipFiles;
                for(int j = 0;j < list.size();j++){
                    if(!Util.mSelectedZipList.contains(list.get(j).getPath())){
                        Util.mSelectedZipList.add(list.get(j).getPath());
                    }
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter != null)
                         albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });

            }

            break;
            case R.id.renameC4:

                card4.setVisibility(View.GONE);
                ActionRename();

                break;
            case R.id.moveC4:
                card4.setVisibility(View.GONE);
                moveImages();
                break;
            case R.id.vaultC4:
                card4.setVisibility(View.GONE);
                moveToVault();
                card2Close();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        onResume();
                    }
                });
                break;
            case R.id.infoC4:
                fileInfo();
                card2Close();
                card4.setVisibility(View.GONE);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case R.id.share:

                ArrayList<Uri> uris = new ArrayList<>();
                String path;
                File file;
                Uri contentUri;
                for(int i = 0;i< Util.mSelectedZipList.size();i++){
                    path= Util.mSelectedZipList.get(i);
                    file=new File(path);
                    contentUri = FileProvider.getUriForFile(ViewAlbumActivity.this,getPackageName() + ".provider",file);
                    uris.add(contentUri);
                }

                //   Log.e("Uri list",String.valueOf(uris.size()));
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
                ActionDelete();
                break;
            case R.id.search:
                card2Close();
                Intent in=new Intent(ViewAlbumActivity.this,SearchZipActivity.class);
                startActivity(in);
                break;
            case R.id.ic_close:
                Util.clickEnable=true;
                card2Close();
                final Handler handler1 = new Handler(Looper.getMainLooper());
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Util.IsUpdate=true;
                        Util.IsAlbumUpdate=true;
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(ViewAlbumActivity.this);
                        Intent localIn = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(localIn);
                    }
                }, 1000);
                break;
        }
    }

    public void ActionRename(){
        File file1=new File(Util.mSelectedZipList.get(0));
        int pos = file1.getName().lastIndexOf(".");

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
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
                    File file = new File(Util.mSelectedZipList.get(0));
                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                    File sdcard = new File(file.getParentFile().getAbsolutePath());
                    File newFileName = new File(sdcard, et_rename.getText().toString()  + extension);
                    boolean isRename = file.renameTo(newFileName);
                    //   Log.e("LLL_Name: ", et_rename.getText().toString() + "." + extension + "    " + newFileName.getAbsolutePath() + "  " + isRename);
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
                                scanPhoto(newFileName.toString());

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
                        Toast.makeText(ViewAlbumActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
                    }
                    alertadd.dismiss();
                }
            }
        });
        alertadd.show();
    }

    public void ActionDelete(){

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
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
                for(int j = 0;j < Util.mSelectedZipList.size();j++){
                    //   Log.e("package:",Util.mSelectedZipList.get(j));
                    isDeleteImg=true;
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedZipList.get(j));

                    File sampleFile = Environment.getExternalStorageDirectory();
                    File file1 = new File(mZipDir.get(0).getPath());

                    if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                        if(sourceFile.exists()){
                            Util.MoveToTrash(ViewAlbumActivity.this,sourceFile);
                            isDelete = Util.delete(ViewAlbumActivity.this,sourceFile);
                            //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                        }
                    }else{
                        if(!SharedPreference.getSharedPreference(ViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())){
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                            intent.putExtra("android.content.extra.SHOW_ADVANCED",true);
                            startActivityForResult(intent,REQUEST_CODE_OPEN_DOCUMENT_TREE);
                        }else{
                            if(sourceFile.exists()){
                                isDelete =   Util.delete(ViewAlbumActivity.this,new File(Util.mSelectedZipList.get(j)));
                                //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                            }
                        }
                    }
                    if(isDelete){
                        count++;
                    }
                }

                if(count==Util.mSelectedZipList.size()){
                    successText.setText( "Delete successfully."  );
                    alertVault.show();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            card2Close();
                            onResume();
                            alertVault.dismiss();
                        }
                    }, 2000);
                }else{
                    Toast.makeText(ViewAlbumActivity.this,"Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }


                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public void changeHomeView(){

        l1.setVisibility(View.GONE);
        l3.setVisibility(View.VISIBLE);
        l2.setVisibility(View.VISIBLE);
        l4.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColor));

        count.setText(Util.mSelectedZipList.size() + " Selected");

    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
            }
        });
        this.msConn.connect();
    }

    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedZipList.clear();
        disableCard();
        l1.setVisibility(View.VISIBLE);
        l3.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
        viewClose=true;
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));

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

        //   Log.e("LLLLView change:" , Util.VIEW_TYPE);
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                onResume();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (msConn!=null)
            this.msConn.connect();
        mZipFiles.clear();
        mZipDir.clear();
        mZipFiles=getZipFolder(docDirName,mZipFiles);
//        //   Log.e("mZipDir.size() :" ,String.valueOf(mZipDir.size()));
        for (int i = 0; i < mZipDir.size(); i++) {
//            //   Log.e("mZipDir.name() :" ,mZipDir.get(i).getName());
            if (DirName.equals(mZipDir.get(i).getName())) {
                mZipFiles = mZipDir.get(i).getFiles();
                int finalI = i;

//                //   Log.e("LLL_Date: ", DirName + "  Directory: " + mZipDir.get(finalI).getPath() + " Size: " + mZipDir.get(finalI).getFiles().size());
                if (Util.VIEW_TYPE=="Grid") {
                    rv_albumImage.setLayoutManager(new GridLayoutManager(ViewAlbumActivity.this,5));
                    albumChildAdapter = new AlbumChildAdapter(mZipFiles, ViewAlbumActivity.this, finalI,mZipDir.get(finalI).getPath());
                    rv_albumImage.setAdapter(albumChildAdapter);
                } else {
                    rv_albumImage.setLayoutManager(new LinearLayoutManager(ViewAlbumActivity.this,RecyclerView.VERTICAL,false));
                    albumChildListAdapter = new AlbumChildListAdapter(mZipFiles, ViewAlbumActivity.this, finalI,mZipDir.get(finalI).getPath());
                    rv_albumImage.setAdapter(albumChildListAdapter);
                }
            }
        }
        compressFileAdapter = new FilesAdapter(mZipDir, ViewAlbumActivity.this,new FilesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                alertadd1.dismiss();
                if(isCompress){
                    compressPath.setText(mZipDir.get(position).getName());
                    isCompress=false;
                }
                if(isMove){
                    isMove=false;
                    movePosition=position;
                }
            }
        });

    }


    public  List<BaseModel> getZipFolder(String filePath, List<BaseModel> docDataList) {

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
                            BaseModel.setDate(Util.convertTimeDateModified(listFile[i].lastModified()));
                            docDataList.add(BaseModel);
//                            //   Log.e("Date :" ,listFile[i].lastModified() + " Converted :" + BaseModel.getDate());

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

    public void fileInfo(){
        File imgFile = new  File(Util.mSelectedZipList.get(0));
        if(imgFile.exists()){

            proName.setText(imgFile.getName());

            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
            String date = spf.format(lastModDate);
            proDate.setText(date);
            long size=imgFile.length();
            if (size >= 1024) {
                proSize.setText((size / 1024) + " Mb");

            } else {
                proSize.setText(size + " Kb");
            }
            proPath.setText(imgFile.getPath());
        }
    }

    public void moveToVault(){
        File sampleFile = Environment.getExternalStorageDirectory();
        for(int i = 0;i< Util.mSelectedZipList.size();i++){

            File file1 = new File(Util.mSelectedZipList.get(i));

            if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
                SaveImage(bitmap,file1);
            }else{
                if(!SharedPreference.getSharedPreference(ViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())){
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
        File[] files=new File[Util.mSelectedZipList.size()];
        for(int i=0;i<Util.mSelectedZipList.size();i++){
            files[i]=new File(Util.mSelectedZipList.get(i));
        }
        scanPhoto(files);


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               onResume();
                alertVault.dismiss(); vaultCount=0;
            }
        }, 2000);
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

    private void SaveImage(Bitmap finalBitmap, File samplefile) {

        File file = new File(samplefile.getParentFile().getAbsolutePath(), "." + samplefile.getName());
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ArrayList<String> hideFileList = SharedPreference.getHideFileList(ViewAlbumActivity.this);
            hideFileList.add(file.getAbsolutePath());
            SharedPreference.setHideFileList(ViewAlbumActivity.this, hideFileList);

            vaultCount++;
//            Toast.makeText(this, "Hide " + samplefile.getName(), Toast.LENGTH_LONG).show();
            if (samplefile.exists()) {
                samplefile.delete();
            }

            MediaScannerConnection.MediaScannerConnectionClient client =
                    new MyMediaScannerConnectionClient(
                            getApplicationContext(), samplefile, null);
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    if(albumChildAdapter != null)
                        albumChildAdapter.notifyDataSetChanged();
                    if(albumChildListAdapter != null)
                        albumChildListAdapter.notifyDataSetChanged();
                }
            });

            if (mZipFiles.isEmpty())
                onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveImages(){
        isMove=true;
        AlertDialog.Builder alertMove = new AlertDialog.Builder(ViewAlbumActivity.this);
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
        alertMove.setMessage("Path to move");
        final View view = factory.inflate(R.layout.compress_targetfile_dialog,null);
        alertMove.setView(view);

        compressPathRec=view.findViewById(R.id.targetList);
        compressPathRec.setLayoutManager(new LinearLayoutManager(ViewAlbumActivity.this, RecyclerView.VERTICAL, false));
        compressPathRec.setAdapter(compressFileAdapter);

        alertMove.setPositiveButton("Move",new DialogInterface.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog,int which){
                moveAlbum(mZipDir.get(movePosition).getPath(),mZipDir.get(movePosition).getName());
                card2Close();
                dialog.dismiss();
            }
        });
        alertMove.setNegativeButton("CANCEL",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                dialog.dismiss();
            }
        });

        alertMove.show();
    }

    public void card2Close(){

        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
        Util.showCircle=false;
        viewClose=true;
        Util.mSelectedZipList.clear();

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                Util.isAllSelected=false;
                if(albumChildAdapter != null)
                    albumChildAdapter.notifyDataSetChanged();
                if(albumChildListAdapter != null)
                    albumChildListAdapter.notifyDataSetChanged();
            }
        });
    }
   
    public void moveAlbum(String targetPath,String targetName){

        for(int i = 0;i< Util.mSelectedZipList.size();i++){

            File sourceFile = new File(Util.mSelectedZipList.get(i));

            File sampleFile = Environment.getExternalStorageDirectory();
            File file1 = null;

            file1 = new File(targetPath);
            if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                moveImage(sourceFile,targetName);

            }else{
                if(!SharedPreference.getSharedPreference(ViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    intent.putExtra("android.content.extra.SHOW_ADVANCED",true);
                    startActivityForResult(intent,REQUEST_CODE_OPEN_DOCUMENT_TREE);
                }else{
                    moveSDImage(sourceFile,targetName);
                }
            }
        }

        successText.setText( moveCount + " File(s) moved successfully."  );
        alertVault.show();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onResume();
                alertVault.dismiss();
                moveCount=0;
            }
        }, 2000);
    }

    private void moveImage(File sourceFile ,String targetName) {
        File file;
        File mainDir = Environment.getExternalStorageDirectory();
        File destinationFile = new File(mainDir, targetName);

        if (!destinationFile.exists()) {
            destinationFile.mkdirs();
        }
        //
        try {
            File to=new File(destinationFile, sourceFile.getName());
            if(to.getPath().equals(sourceFile.getPath())){
                Toast.makeText(ViewAlbumActivity.this,"Already in same folder", Toast.LENGTH_SHORT).show();
            }else {
                sourceFile.renameTo(to);
//               Log.e("From path",sourceFile.getPath());
//               Log.e("To path",to.getPath());

//            if (sourceFile.exists()) {
                boolean isDelete = Util.delete(ViewAlbumActivity.this, sourceFile);
                if (!isDelete)
                    isDelete = sourceFile.delete();
//                Log.e("is Delete : " ,String.valueOf(isDelete));
//            }


                moveCount++;
                MediaScannerConnection.MediaScannerConnectionClient client =
                        new MyMediaScannerConnectionClient(
                                getApplicationContext(), to, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (albumChildAdapter != null)
                            albumChildAdapter.notifyDataSetChanged();
                        if (albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveSDImage(File sourceFile,String targetName) {

        File file;

        File mainDir = Environment.getExternalStorageDirectory();
        File destinationFile = new File(mainDir, targetName);

        if (!destinationFile.exists()) {
            destinationFile.mkdirs();
        }
        //   Log.e("LLL_Name: ", destinationFile.getAbsolutePath());

        file = new File(destinationFile, sourceFile.getName());


        OutputStream fileOutupStream = null;
        File file2 = new File(file.getParentFile().getParentFile().getAbsolutePath(), sourceFile.getName());
        DocumentFile targetDocument = getDocumentFile(file2, false);

        try {
            fileOutupStream = getContentResolver().openOutputStream(targetDocument.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            File to=new File(targetDocument.toString(), sourceFile.getName());
            sourceFile.renameTo(to);
//               Log.e("From path",sourceFile.getPath());
//               Log.e("To path",to.getPath());

//            if (sourceFile.exists()) {
            boolean isDelete=Util.delete(ViewAlbumActivity.this,sourceFile);
            if(!isDelete)
                isDelete=sourceFile.delete();
//                Log.e("is Delete : " ,String.valueOf(isDelete));
//            }


            MediaScannerConnection.MediaScannerConnectionClient client =
                    new MyMediaScannerConnectionClient(
                            getApplicationContext(), file, null);


        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

        Uri treeUri = Uri.parse(SharedPreference.getSharedPreferenceUri(ViewAlbumActivity.this));

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(ViewAlbumActivity.this, treeUri);

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {

            for(int i = 0;i< Util.mSelectedZipList.size();i++){
                File file1 = new File(Util.mSelectedZipList.get(i));
                String[] parts = (file1.getAbsolutePath()).split("/");

                DocumentFile documentFile = DocumentFile.fromTreeUri(this,resultData.getData());
                documentFile = documentFile.findFile(parts[parts.length - 1]);

                //   Log.e("LLL_Data: ",String.valueOf(documentFile.getUri()));

                SharedPreference.setSharedPreferenceUri(ViewAlbumActivity.this,documentFile.getUri());
                SharedPreference.setSharedPreference(ViewAlbumActivity.this,file1.getParentFile().getAbsolutePath());

                getData(file1);

            }
        }
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
            ArrayList<String> hideFileList = SharedPreference.getHideFileList(ViewAlbumActivity.this);
            hideFileList.add(file3.getAbsolutePath());
            SharedPreference.setHideFileList(ViewAlbumActivity.this, hideFileList);

            if (file1.exists()) {
                boolean isDelete = Util.delete(ViewAlbumActivity.this, file1);
            }

            if (mZipFiles.isEmpty())
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




}