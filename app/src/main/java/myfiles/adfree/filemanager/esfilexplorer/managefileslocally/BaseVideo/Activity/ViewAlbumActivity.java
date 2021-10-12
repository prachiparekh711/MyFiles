package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.VaultActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.FilesAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.AlbumChildListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.AlbumChildAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.MyMediaScannerConnectionClient;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

public class ViewAlbumActivity extends AppCompatActivity  implements View.OnClickListener{

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
    ArrayList<Directory<BaseModel>> directories = new ArrayList<>();
    private List<BaseModel> imageFiles = new ArrayList<>();
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
    EditText compressName;
    TextView compressPath,compressType;
    RelativeLayout pathRL;
    RecyclerView compressPathRec;
    FilesAdapter compressFileAdapter;
    AlertDialog alertadd1 ;
    RadioGroup typeGroup;
    String type="zip";
    boolean isCompress=false,isMove=false;
    int movePosition=0;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 19;
    MediaScannerConnection msConn;
    RelativeLayout rootLayout;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;

    TextView renameOK,renameCancel;
    TextInputEditText et_rename;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    TextView textCancel,textAccept;
    TextView successText;
    int vaultCount=0,moveCount=0;
    AlertDialog alertVault;



    private FirebaseAnalytics mFirebaseAnalytics;
    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_album2);

        ButterKnife.bind(ViewAlbumActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedVideoList.clear();
        AlbumChildAdapter.viewClose=true;
        DirName = getIntent().getStringExtra("DirName");
        //   Log.e("LLLL_DirName: ", DirName);
        tv_albumName.setText(DirName);

        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ViewAlbumActivity.this);


        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);
    }

    public void init(){
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

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                Intent in=new Intent(ViewAlbumActivity.this,BaseVideoActivity.class);
                startActivity(in);
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

            if(!AlbumChildAdapter.viewClose){
                AlbumChildAdapter.viewClose = true;
                Util.mSelectedVideoList.clear();
                changeToOriginalView();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(albumChildAdapter!=null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });
            }else{
                backPresse++;
                if(backPresse == 1){
                    AlbumChildAdapter.viewClose = false;
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
                }else  if(Util.mSelectedVideoList.size()==1){
                    card4.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    card3.setVisibility(View.GONE);
                }else  if(Util.mSelectedVideoList.size()>1){
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
                AlbumChildAdapter.viewClose=false;
                Util.mSelectedVideoList.clear();
                for(int j = 0;j < imageFiles.size();j++){
                    if(!Util.mSelectedVideoList.contains(imageFiles.get(j).getPath())){
                        Util.mSelectedVideoList.add(imageFiles.get(j).getPath());
                    }
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter!=null)
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
                        if(albumChildAdapter!=null)
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
            case R.id.compressC2: {
                compress();
            }
            break;
            case R.id.moveC2:
            case R.id.moveC3:
                moveImages();
                break;
            case R.id.vaultC2:
            case R.id.vaultC3:
            case R.id.vaultC4:
                if (SharedPreference.getPasswordProtect(getBaseContext()).equals("")) {
                   Intent in=new Intent(ViewAlbumActivity.this, VaultActivity.class);
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
                AlbumChildAdapter.viewClose=false;
                Util.mSelectedVideoList.clear();

                List<BaseModel> list = imageFiles;
                for(int j = 0;j < list.size();j++){
                    if(!Util.mSelectedVideoList.contains(list.get(j).getPath())){
                        Util.mSelectedVideoList.add(list.get(j).getPath());
                    }
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter!=null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter != null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });

            }
            break;
            case R.id.compressC3:
                compress();
                break;
            case R.id.openWithC4:
                fireAnalytics("Video", "Open with");
                //   Log.e("Selected list1 :",String.valueOf(Util.mSelectedVideoList.size()));
                card4.setVisibility(View.GONE);
                Uri uri =  Uri.parse(Util.mSelectedVideoList.get(0));
                Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW);
                String mime = "*/*";
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                if (mimeTypeMap.hasExtension(
                        MimeTypeMap.getFileExtensionFromUrl(uri.toString())))
                    mime = mimeTypeMap.getMimeTypeFromExtension(
                            MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
                intent1.setDataAndType(uri,mime);
                startActivity(intent1);
                break;
            case R.id.selectAllC4:{
                card4.setVisibility(View.GONE);

                Util.showCircle=true;
                AlbumChildAdapter.viewClose=false;
                Util.mSelectedVideoList.clear();


                List<BaseModel> list = imageFiles;
                for(int j = 0;j < list.size();j++){
                    if(!Util.mSelectedVideoList.contains(list.get(j).getPath())){
                        Util.mSelectedVideoList.add(list.get(j).getPath());
                    }
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter!=null)
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
            case R.id.compressC4:
                card4.setVisibility(View.GONE);
                compress();
                break;
            case R.id.moveC4:
                card4.setVisibility(View.GONE);
                moveImages();
                break;
            case R.id.infoC4:
                fireAnalytics("Video", "Info");
                fileInfo();
                card2Close();
                card4.setVisibility(View.GONE);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case R.id.share:
                fireAnalytics("Video", "Share");
                ArrayList<Uri> uris = new ArrayList<>();
                String path;
                File file;
                Uri contentUri;
                for(int i = 0;i< Util.mSelectedVideoList.size();i++){
                    path= Util.mSelectedVideoList.get(i);
                    file=new File(path);
                    contentUri = FileProvider.getUriForFile(ViewAlbumActivity.this,getPackageName() + ".provider",file);
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
                ActionDelete();
                break;
            case R.id.search:
                fireAnalytics("Video", "Search");
                card2Close();
                Intent in=new Intent(ViewAlbumActivity.this,SearchVideoActivity.class);
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
        File file1=new File(Util.mSelectedVideoList.get(0));
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
                fireAnalytics("Video", "Rename");
                if (et_rename.length() > 0) {
                    File file = new File(Util.mSelectedVideoList.get(0));
                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                    File sdcard = new File(file.getParentFile().getAbsolutePath());
                    File newFileName = new File(sdcard, et_rename.getText().toString()  + extension);
                    boolean isRename = file.renameTo(newFileName);
//                    //   Log.e("LLL_Name: ", et_rename.getText().toString() + "." + extension + "    " + newFileName.getAbsolutePath() + "  " + isRename);
                    if (isRename) {
                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        resolver.delete(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA +
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
                for(int j = 0;j < Util.mSelectedVideoList.size();j++){
                    //   Log.e("package:",Util.mSelectedVideoList.get(j));
                    isDeleteImg=true;
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedVideoList.get(j));

                    File sampleFile = Environment.getExternalStorageDirectory();
                    File file1 = new File(directories.get(0).getPath());

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
                                isDelete =   Util.delete(ViewAlbumActivity.this,new File(Util.mSelectedVideoList.get(j)));
                                //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                            }
                        }
                    }
                    if(isDelete){
                        count++;
                    }
                }

                if(count==Util.mSelectedVideoList.size()){
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

        count.setText(Util.mSelectedVideoList.size() + " Selected");

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
        Util.mSelectedVideoList.clear();
        disableCard();
        l1.setVisibility(View.VISIBLE);
        l3.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
        AlbumChildAdapter.viewClose=true;
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
        new LoadImages(ViewAlbumActivity.this).execute();
        compressFileAdapter = new FilesAdapter(directories, ViewAlbumActivity.this, new FilesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                alertadd1.dismiss();
                if(isCompress){
                    compressPath.setText(directories.get(position).getName());
                    isCompress=false;
                }
                if(isMove){
                    isMove=false;
                    movePosition=position;
                }
            }
        });
        new LoadAlbumImages(ViewAlbumActivity.this).execute();
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

            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uri = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }

            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.BUCKET_ID,
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                    DATE_ADDED
            };

            Cursor data = fragmentActivity.getContentResolver().query(uri, FILE_PROJECTION, null, null, DATE_ADDED + " DESC");

            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                 if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)).startsWith(".")){
                     if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)).startsWith(".")){
                         BaseModel img = new BaseModel();

                         img.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
                         img.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                         img.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
                         img.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                         img.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)));
                         img.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)));
                         img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));

                         //Create a Directory
                         Directory<BaseModel> directory = new Directory<>();
                         directory.setId(img.getBucketId());
                         directory.setName(img.getBucketName());
                         directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                         if(!directories1.contains(directory)){
                             directory.addFile(img);
                             directories.add(directory);
                             directories1.add(directory);
                         }else{
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

            imageFiles.clear();
            for (int i = 0; i < directories.size(); i++) {
                if (DirName.equals(directories.get(i).getName())) {
                    imageFiles = directories.get(i).getFiles();
                    int finalI = i;
                    runOnUiThread(() -> {
                        //   Log.e("LLL_Date: ", DirName + "  Directory: " + directories.get(finalI).getName() + " Size: " + directories.get(finalI).getFiles().size());
                        if (Util.VIEW_TYPE=="Grid") {
                            rv_albumImage.setLayoutManager(new GridLayoutManager(ViewAlbumActivity.this, Util.COLUMN_TYPE));
                            albumChildAdapter = new AlbumChildAdapter(imageFiles, ViewAlbumActivity.this, finalI);
                            rv_albumImage.setAdapter(albumChildAdapter);
                        } else {
                            rv_albumImage.setLayoutManager(new LinearLayoutManager(ViewAlbumActivity.this,RecyclerView.VERTICAL,false));
                            albumChildListAdapter = new AlbumChildListAdapter(imageFiles, ViewAlbumActivity.this, finalI);
                            rv_albumImage.setAdapter(albumChildListAdapter);
                        }
                    });
                }
            }

        }

    }

    class LoadAlbumImages extends AsyncTask<Void, Void, List<Directory<BaseModel>>> {

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;
        List<BaseModel> list1 = new ArrayList<>();

        public LoadAlbumImages(FragmentActivity fragmentActivity) {
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
                if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)).startsWith(".")){
                        BaseModel img = new BaseModel();

                        img.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
                        img.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                        img.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
                        img.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                        img.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)));
                        img.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)));
                        img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));

                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(img.getBucketId());
                        directory.setName(img.getBucketName());
                        directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                        if(!directories1.contains(directory)){
                            directory.addFile(img);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(img);
                        }
                    }
                }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);

            fragmentActivity.runOnUiThread(() -> {
                compressFileAdapter.clearData();
                compressFileAdapter.addAll(directories);
            });
        }
    }

    public void fileInfo(){
        File imgFile = new  File(Util.mSelectedVideoList.get(0));
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            proImage.setImageBitmap(myBitmap);
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
        for(int i = 0;i< Util.mSelectedVideoList.size();i++){

            File file1 = new File(Util.mSelectedVideoList.get(i));

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
        File[] files=new File[Util.mSelectedVideoList.size()];
        for(int i=0;i<Util.mSelectedVideoList.size();i++){
            files[i]=new File(Util.mSelectedVideoList.get(i));
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

        try {
            File from=new File(samplefile.getAbsolutePath());
            File folder = new File(from.getParentFile().getAbsolutePath());
            if(!folder.exists())
                folder.mkdir();
            File to=new File(folder.getAbsolutePath() + "/." + samplefile.getName());
            from.renameTo(to);

            ArrayList<String> hideFileList = SharedPreference.getHideFileList(ViewAlbumActivity.this);
            hideFileList.add(to.getAbsolutePath());
            SharedPreference.setHideFileList(ViewAlbumActivity.this, hideFileList);

            vaultCount++;
//            Toast.makeText(this, "Hide " + samplefile.getName(), Toast.LENGTH_LONG).show();

                boolean isDelete=samplefile.delete();
                if(!isDelete)
                    isDelete=Util.delete(ViewAlbumActivity.this,samplefile);
                super.onBackPressed();

            MediaScannerConnection.MediaScannerConnectionClient client =
                    new MyMediaScannerConnectionClient(
                            getApplicationContext(), samplefile, null);
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    if(albumChildAdapter!=null)
                        albumChildAdapter.notifyDataSetChanged();
                    if(albumChildListAdapter != null)
                        albumChildListAdapter.notifyDataSetChanged();
                }
            });

            if (imageFiles.isEmpty())
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
                moveAlbum(directories.get(movePosition).getPath(),directories.get(movePosition).getName());
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
        AlbumChildAdapter.viewClose=true;
        Util.mSelectedVideoList.clear();

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                Util.isAllSelected=false;
                if(albumChildAdapter!=null)
                    albumChildAdapter.notifyDataSetChanged();
                if(albumChildListAdapter != null)
                    albumChildListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void compress(){
        isCompress=true;
        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
        final View view = factory.inflate(R.layout.compress_dialog,null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);

        compressName=view.findViewById(R.id.compressName);
        compressPath=view.findViewById(R.id.compressPath);
        pathRL=view.findViewById(R.id.pathRL);
//        typeGroup=view.findViewById(R.id.typeGroup);
        compressName.setText("Myfiles");
        compressType=view.findViewById(R.id.zip);
        textAccept=view.findViewById(R.id.text5);
        textCancel=view.findViewById(R.id.text4);
        pathRL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
                alertadd1.setMessage("Compress Path");
                final View view = factory.inflate(R.layout.compress_targetfile_dialog,null);
                alertadd1.setView(view);

                compressPathRec=view.findViewById(R.id.targetList);
                compressPathRec.setLayoutManager(new LinearLayoutManager(ViewAlbumActivity.this, RecyclerView.VERTICAL, false));
                compressPathRec.setAdapter(compressFileAdapter);

                alertadd1.show();
            }
        });

        textAccept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fireAnalytics("Video", "Compress");
//                int id=typeGroup.getCheckedRadioButtonId();
//                compressType=view.findViewById(id);
                if(compressPath.getText()==null){
                    Toast.makeText(ViewAlbumActivity.this,"Select Path",Toast.LENGTH_SHORT ).show();
                }else{
                    String[] s = new String[Util.mSelectedVideoList.size()];
                    for(int i = 0;i< Util.mSelectedVideoList.size();i++){
                        s[i]= Util.mSelectedVideoList.get(i);
                    }
                    switch ((String)compressType.getText()) {
                        case "Zip":                                type="zip";break;
                        case "7 Zip":                               type="7z";break;
                        case "TAR":                               type="tar";break;
                    }

                    String target=Environment.getExternalStorageDirectory().getPath() + "/" + compressPath.getText() + "/" + compressName.getText() ;
//                    //   Log.e("Target:",target);

                    if(type=="zip"){
                        BaseVideoActivity.ZipManager zipManager = new BaseVideoActivity.ZipManager();
                        zipManager.zip(s,target + "." + type);
                    }else if(type=="tar"){

                    }
                    card2Close();
                    alertadd.dismiss();
                }
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertadd.dismiss();
            }
        });


        alertadd.show();
    }

    public void moveAlbum(String targetPath,String targetName){

        for(int i = 0;i< Util.mSelectedVideoList.size();i++){

            File sourceFile = new File(Util.mSelectedVideoList.get(i));

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

    private void moveImage( File sourceFile ,String targetName) {
        File mainDir = Environment.getExternalStorageDirectory();
        File destinationFile = new File(mainDir, targetName);

        if (!destinationFile.exists()) {
            destinationFile.mkdirs();
        }

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
                fireAnalytics("Video", "Move");
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
            Bitmap bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutupStream);
            fileOutupStream.flush();
            fileOutupStream.close();

            if (sourceFile.exists()) {
                boolean isDelete = Util.delete(ViewAlbumActivity.this, sourceFile);
                //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
            }

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

            for(int i = 0;i< Util.mSelectedVideoList.size();i++){
                File file1 = new File(Util.mSelectedVideoList.get(i));
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

            if (imageFiles.isEmpty())
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