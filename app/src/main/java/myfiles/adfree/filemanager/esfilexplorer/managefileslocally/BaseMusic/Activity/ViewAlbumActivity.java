package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity;

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
import androidx.viewpager.widget.ViewPager;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.VaultActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter.AlbumChildAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter.AlbumChildListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.FilesAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter.TabsBasePagerAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.MyMediaScannerConnectionClient;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.MediaStore.Audio.AudioColumns.ALBUM_ID;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter.AlbumChildAdapter.viewClose;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util.MoveToTrash;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util.convertTimeDateModified;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util.getSize;

public class ViewAlbumActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{

    @BindView(R.id.tv_albumName)
    TextView tv_albumName;
    @BindView(R.id.rv_albumImage)
    RecyclerView rv_albumImage;
    private String DirName = "";

    @BindView(R.id.imgBack)
    ImageView imgBack;

    RelativeLayout rootLayout;
    public static CardView card1,card2,card3,card4;
    LinearLayout mSelectAllC1,mRefreshC1;
    LinearLayout mDeselectAllC2,mMoveC2,mVaultC2;
    LinearLayout mSelectAllC3,mMoveC3,mVaultC3;
    LinearLayout mOpenWithC4,mSelectAllC4,mRenameC4,mMoveC4,mVaultC4,mInfoC4;
    ArrayList<Directory<BaseModel>> directories = new ArrayList<>();
    private List<BaseModel> musicFiles = new ArrayList<>();
    public static TabsBasePagerAdapter tabsPagerAdapter;
    public static TabLayout tabLayout;
    ViewPager viewPager;
    ImageView back;
    public static LinearLayout l1,l2,l3,l4;
    public static RelativeLayout toolbar;
    public static LinearLayout actionLL,titleLL;
    public static RelativeLayout headerRL;
    public static ImageView delete,share;
    public static TextView count;
    int backPresse=0;
    public static ImageView view,more,search,close;
    AlertDialog alertadd1 ;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    LinearLayout llRename;
    TextView renameOK,renameCancel;
    TextInputEditText et_rename;
    MediaScannerConnection msConn;
    boolean isMove=false;
    RecyclerView compressPathRec;
    FilesAdapter FileAdapter;
    int movePosition=0;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 19;
    String inputPath, inputFile,  outputPath;
    AlbumChildAdapter albumChildAdapter;
    AlbumChildListAdapter albumChildListAdapter;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    AlertDialog alertVault;
    TextView successText;
    int vaultCount=0,moveCount=0;


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
        setContentView(R.layout.activity_view_album3);
        ButterKnife.bind(ViewAlbumActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedMusicList.clear();
        viewClose=true;
        DirName = getIntent().getStringExtra("DirName");
        //   Log.e("LLLL_DirName: ", DirName);
        tv_albumName.setText(DirName);
     
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ViewAlbumActivity.this);

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

    public void mainFunction(){
        new ViewAlbumActivity.LoadMusic(ViewAlbumActivity.this).execute();

        FileAdapter = new FilesAdapter(directories, ViewAlbumActivity.this, new FilesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                alertadd1.dismiss();

                if(isMove){
                    isMove=false;
                    movePosition=position;
                }
            }
        });
        new ViewAlbumActivity.LoadAlbumMusic(ViewAlbumActivity.this).execute();
    }

    public void init(){
        l1=findViewById(R.id.l1);
        l2=findViewById(R.id.l2);
        l3=findViewById(R.id.l3);
        l4=findViewById(R.id.l4);
        toolbar=findViewById(R.id.rl_toolbar);

        rootLayout=findViewById(R.id.rootLayout);
        rootLayout.setOnTouchListener(this);
        tabLayout=findViewById(R.id.tab_imgFolder);
        viewPager=findViewById(R.id.viewPager);
        back=findViewById(R.id.imgBack);
        view=findViewById(R.id.view);
        more=findViewById(R.id.more);
        search=findViewById(R.id.search);

        card1=findViewById(R.id.card1);
        mSelectAllC1=findViewById(R.id.selectAllC1);
        mRefreshC1=findViewById(R.id.refreshC1);
        mSelectAllC1.setOnClickListener(this);
        mRefreshC1.setOnClickListener(this);

        card2=findViewById(R.id.card2);
        mDeselectAllC2=findViewById(R.id.deSelectAllC2);
        mMoveC2=findViewById(R.id.moveC2);
        mVaultC2=findViewById(R.id.vaultC2);
        mDeselectAllC2.setOnClickListener(this);
        mMoveC2.setOnClickListener(this);
        mVaultC2.setOnClickListener(this);

        card3=findViewById(R.id.card3);
        mSelectAllC3=findViewById(R.id.selectAllC3);
        mMoveC3=findViewById(R.id.moveC3);
        mVaultC3=findViewById(R.id.vaultC3);
        mSelectAllC3.setOnClickListener(this);
        mMoveC3.setOnClickListener(this);
        mVaultC3.setOnClickListener(this);

        card4=findViewById(R.id.card4);
        mOpenWithC4=findViewById(R.id.openWithC4);
        mSelectAllC4=findViewById(R.id.selectAllC4);
        mRenameC4=findViewById(R.id.renameC4);
        mMoveC4=findViewById(R.id.moveC4);
        mVaultC4=findViewById(R.id.vaultC4);
        mInfoC4=findViewById(R.id.infoC4);
        mOpenWithC4.setOnClickListener(this);
        mSelectAllC4.setOnClickListener(this);
        mRenameC4.setOnClickListener(this);
        mMoveC4.setOnClickListener(this);
        mVaultC4.setOnClickListener(this);
        mInfoC4.setOnClickListener(this);

        view.setOnClickListener(this);
        more.setOnClickListener(this);
        search.setOnClickListener(this);
        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
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
        delete.setOnClickListener(this);
        share.setOnClickListener(this);

        count=findViewById(R.id.count);
        alertadd1 = new AlertDialog.Builder(ViewAlbumActivity.this).create();
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

    @Override
    public void onBackPressed(){
        if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_EXPANDED){
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            Util.showCircle = false;
            Util.clickEnable = true;


            if(!viewClose){
                changeToOriginalView();
                Util.IsUpdate=true;
                Util.IsAlbumUpdate=true;
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(ViewAlbumActivity.this);
                Intent localIn = new Intent("TAG_REFRESH");
                lbm.sendBroadcast(localIn);

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
    protected void onResume(){
        super.onResume();
        if (msConn!=null)
            this.msConn.connect();
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
        mainFunction();
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
                }else  if(Util.mSelectedMusicList.size()==1){
                    card4.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    card3.setVisibility(View.GONE);
                }else  if(Util.mSelectedMusicList.size()>1){
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
                Util.mSelectedMusicList.clear();

                for(int j = 0;j < musicFiles.size();j++){
                    if(!Util.mSelectedMusicList.contains(musicFiles.get(j).getPath())){
                        Util.mSelectedMusicList.add(musicFiles.get(j).getPath());
                    }
                }
//   Log.e("size",String.valueOf(Util.mSelectedMusicList.size()));

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter!=null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter!=null)
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
                        if(albumChildListAdapter!=null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.deSelectAllC2:{
                card2Close();
            }

            break;
            case R.id.moveC2:{
                card4.setVisibility(View.GONE);
                moveMusic();

            }

            break;
            case R.id.vaultC2:{
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
            }

            break;
            case R.id.selectAllC3:{
                card3.setVisibility(View.GONE);

                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedMusicList.clear();

                for(int i=0;i<directories.size();i++){
                    List<BaseModel> list = directories.get(i).getFiles();
                    for(int j = 0;j < list.size();j++){
                        if(!Util.mSelectedMusicList.contains(list.get(j).getPath())){
                            Util.mSelectedMusicList.add(list.get(j).getPath());
                        }
                    }
                }
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter!=null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter!=null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });

            }
            break;
            case R.id.openWithC4:
                fireAnalytics("Audio", "Open with");
                card4.setVisibility(View.GONE);
                Uri uri =  Uri.parse(Util.mSelectedMusicList.get(0));
                Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW);
                String mime = "audio/*";
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
                Util.mSelectedMusicList.clear();

                for(int i=0;i<directories.size();i++){
                    List<BaseModel> list = directories.get(i).getFiles();
                    for(int j = 0;j < list.size();j++){
                        if(!Util.mSelectedMusicList.contains(list.get(j).getPath())){
                            Util.mSelectedMusicList.add(list.get(j).getPath());
                        }
                    }
                }
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(albumChildAdapter!=null)
                            albumChildAdapter.notifyDataSetChanged();
                        if(albumChildListAdapter!=null)
                            albumChildListAdapter.notifyDataSetChanged();
                    }
                });

            }

            break;
            case R.id.renameC4:
                fireAnalytics("Audio", "Rename");
                card4.setVisibility(View.GONE);
                ActionRename();

                break;
            case R.id.moveC4:
                card4.setVisibility(View.GONE);
                moveMusic();
                break;
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
            case R.id.infoC4:
                fireAnalytics("Audio", "Info");
                File imgFile = new  File(Util.mSelectedMusicList.get(0));
                if(imgFile.exists()){
                    fileInfo();
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Toast.makeText(ViewAlbumActivity.this,"Problem with file.",Toast.LENGTH_SHORT).show();
                }
                card2Close();

                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;

            case R.id.share:
                fireAnalytics("Audio", "Share");
                ArrayList<Uri> uris = new ArrayList<>();
                String path;
                File file;
                Uri contentUri;
                for(int i = 0;i< Util.mSelectedMusicList.size();i++){
                    path= Util.mSelectedMusicList.get(i);
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
                fireAnalytics("Audio", "Search");
                card2Close();
                Intent in=new Intent(ViewAlbumActivity.this,SearchMusicActivity.class);
                startActivity(in);
                break;
            case R.id.ic_close:
                Util.clickEnable=true;
                card2Close();
                onResume();
                break;
        }
    }

    public void ActionRename(){
        File file1=new File(Util.mSelectedMusicList.get(0));
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
                    File file = new File(Util.mSelectedMusicList.get(0));
                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                    File sdcard = new File(file.getParentFile().getAbsolutePath());
                    File newFileName = new File(sdcard, et_rename.getText().toString()  + extension);
                    boolean isRename = file.renameTo(newFileName);
                    //   Log.e("LLLL_Name isRename: ",String.valueOf(isRename));
                    if (isRename) {
                        ContentResolver resolver = getApplicationContext().getContentResolver();
                        resolver.delete(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.AudioColumns.DATA +
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
                for(int j = 0;j < Util.mSelectedMusicList.size();j++){
                    //   Log.e("package:",Util.mSelectedMusicList.get(j));
                    isDeleteImg=true;
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedMusicList.get(j));

                    File sampleFile = Environment.getExternalStorageDirectory();
                    File file1 = new File(directories.get(0).getPath());

                    if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                        if(sourceFile.exists()){
                            MoveToTrash(ViewAlbumActivity.this,sourceFile);
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
                                isDelete =   Util.delete(ViewAlbumActivity.this,new File(Util.mSelectedMusicList.get(j)));
                                //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                            }
                        }
                    }
                    if(isDelete){
                        count++;
                    }
                }

                if(count==Util.mSelectedMusicList.size()){
                    successText.setText( "Delete successfully."  );
                    alertVault.show();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            card2Close();
                            Util.IsUpdate=true;
                            Util.IsAlbumUpdate=true;
                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(ViewAlbumActivity.this);
                            Intent localIn = new Intent("TAG_REFRESH");
                            lbm.sendBroadcast(localIn);
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

    public void moveToVault(){
        File sampleFile = Environment.getExternalStorageDirectory();
        for(int i = 0;i< Util.mSelectedMusicList.size();i++){

            File file1 = new File(Util.mSelectedMusicList.get(i));
            if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                SaveImage(file1);
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
        File[] files=new File[Util.mSelectedMusicList.size()];
        for(int i=0;i<Util.mSelectedMusicList.size();i++){
            files[i]=new File(Util.mSelectedMusicList.get(i));
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

    private void SaveImage(File samplefile) {

        File from=new File(samplefile.getAbsolutePath());
        File folder = new File(from.getParentFile().getAbsolutePath());
        if(!folder.exists())
            folder.mkdir();
        File to=new File(folder.getAbsolutePath() + "/." + samplefile.getName());
        from.renameTo(to);


        ArrayList<String> hideFileList = SharedPreference.getHideFileList(ViewAlbumActivity.this);
        hideFileList.add(to.getAbsolutePath());
        SharedPreference.setHideFileList(ViewAlbumActivity.this, hideFileList);
//        //   Log.e("Hide List:",String.valueOf(hideFileList.size()));

        try {
            fireAnalytics("Music", "Audio hide");
            vaultCount++;
//            Toast.makeText(this, "Hide " + samplefile.getName(), Toast.LENGTH_LONG).show();
            if (samplefile.exists()) {
                boolean isDelete=Util.delete(ViewAlbumActivity.this,samplefile);
                //   Log.e("Video deleted:",String.valueOf(isDelete));
                super.onBackPressed();
            }


        } catch (Exception e) {
            e.printStackTrace();
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


            Toast.makeText(this, "Hide " + file1.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void scanPhoto(final String imageFileName) {
        File imageFile=new File(imageFileName);
        msConn = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
//                if (msConn!=null)
//                    msConn.connect();

                msConn.scanFile(imageFile.getPath(), null);
                Log.i("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient obj", "scan completed");
                MediaScannerConnection.scanFile(getApplicationContext(), new String[] { imageFile.getAbsolutePath() }, new String[] { "*/*" }, null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(imageFile));
                    sendBroadcast(mediaScanIntent);
                }
                else
                {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(imageFile.getAbsolutePath())));
                }
            }
        });
        this.msConn.connect();
    }

    public void moveMusic(){
        isMove=true;
        AlertDialog.Builder alertMove = new AlertDialog.Builder(ViewAlbumActivity.this);
        LayoutInflater factory = LayoutInflater.from(ViewAlbumActivity.this);
        alertMove.setMessage("Path to move");
        final View view = factory.inflate(R.layout.compress_targetfile_dialog,null);
        alertMove.setView(view);
        FilesAdapter.globalPos=-1;
        compressPathRec=view.findViewById(R.id.targetList);
        compressPathRec.setLayoutManager(new LinearLayoutManager(ViewAlbumActivity.this, RecyclerView.VERTICAL, false));
        compressPathRec.setAdapter(FileAdapter);

        alertMove.setPositiveButton("Move",new DialogInterface.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog,int which){

                moveFile(directories.get(movePosition).getPath(),directories.get(movePosition).getName());

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

    private void moveFile(String targetPath,String targetName) {
        for(int i = 0;i< Util.mSelectedMusicList.size();i++){

            File sourceFile = new File(Util.mSelectedMusicList.get(i));

            Log.e("Source file:",sourceFile.getPath());
            File sampleFile = Environment.getExternalStorageDirectory();
            File file1 = null;
            file1 = new File(targetPath);
            if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                moveData(sourceFile,targetName);
            } else {
                if (!SharedPreference.getSharedPreference(ViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                    startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);

                } else {
                    moveSDImage(new File(inputPath),new File(outputPath));
                }
            }

        }

    }

    public void moveData(File sourceFile ,String targetName){
        try{
            File mainDir = Environment.getExternalStorageDirectory();
            File destinationFile = new File(mainDir, targetName);

            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
            File to=new File(destinationFile, sourceFile.getName());
            if(to.getPath().equals(sourceFile.getPath())){
                Toast.makeText(ViewAlbumActivity.this,"Already in same folder", Toast.LENGTH_SHORT).show();
            }else {
                fireAnalytics("Music ", "Move");
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
            }
        }
        catch (Exception e) {
            //   Log.e("tag", e.getMessage());
        }
    }


    private void moveSDImage(File sourceFile,File destinationFile) {

        File file;

        File mainDir = Environment.getExternalStorageDirectory();

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

        InputStream in = null;
        try {
            in = new FileInputStream(inputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                fileOutupStream.write(buffer,0,read);
            }
            in.close();
            in = null;

            fileOutupStream.flush();
            fileOutupStream.close();


            if (sourceFile.exists()) {
                boolean isDelete = Util.delete(ViewAlbumActivity.this, sourceFile);
                //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
            }

            scanPhoto(file.toString());


            Toast.makeText(this, "Move " + sourceFile.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                onResume();
            }
        });
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        //   Log.e("View ID:",String.valueOf(v.getId()));
        if(v.getId()!=R.id.more){
            disableCard();
        }
        return false;
    }

    public static void disableCard(){
        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
    }

    class LoadMusic extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;
        List<BaseModel> list1 = new ArrayList<>();

        public LoadMusic(FragmentActivity fragmentActivity) {
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
                uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Audio.AudioColumns._ID,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.SIZE,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    ALBUM_ID,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    DATE_ADDED
            };


            Cursor data = fragmentActivity.getContentResolver().query(uri,
                    FILE_PROJECTION,
                    null,
                    null,
                    DATE_ADDED + " DESC");

            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).startsWith(".")){
                        BaseModel music = new BaseModel();

                        music.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)));
                        music.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)));
                        music.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)));
                        music.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)));
                        music.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                        music.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                        music.setDate(convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED))));


                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(music.getBucketId());
                        directory.setPath(Util.extractPathWithoutSeparator(music.getPath()));
                        File file=new File(directory.getPath());
                        directory.setName(file.getName());

                        if(!directories1.contains(directory)){
                            directory.addFile(music);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(music);
                        }
                        musicFiles.add(music);
                    }
                }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);

            musicFiles.clear();
            for (int i = 0; i < directories.size(); i++) {
                if (DirName.equals(directories.get(i).getName())) {
                    musicFiles = directories.get(i).getFiles();
                    int finalI = i;
                    runOnUiThread(() -> {
                        //   Log.e("LLL_Date: ", DirName + "  Directory: " + directories.get(finalI).getName() + " Size: " + directories.get(finalI).getFiles().size());
                        if (Util.VIEW_TYPE=="Grid") {
                            rv_albumImage.setLayoutManager(new GridLayoutManager(ViewAlbumActivity.this,5));
                            albumChildAdapter = new AlbumChildAdapter(musicFiles, ViewAlbumActivity.this, finalI);
                            rv_albumImage.setAdapter(albumChildAdapter);
                        } else {
                            rv_albumImage.setLayoutManager(new LinearLayoutManager(ViewAlbumActivity.this,RecyclerView.VERTICAL,false));
                            albumChildListAdapter = new AlbumChildListAdapter(musicFiles, ViewAlbumActivity.this, finalI);
                            rv_albumImage.setAdapter(albumChildListAdapter);
                        }
                    });
                }
            }

        }

    }

    class LoadAlbumMusic extends AsyncTask<Void, Void, List<Directory<BaseModel>>> {

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;
        List<BaseModel> list1 = new ArrayList<>();

        public LoadAlbumMusic(FragmentActivity fragmentActivity) {
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
                uri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else {
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Audio.AudioColumns._ID,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.SIZE,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    ALBUM_ID,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    DATE_ADDED
            };


            Cursor data = fragmentActivity.getContentResolver().query(uri,
                    FILE_PROJECTION,
                    null,
                    null,
                    DATE_ADDED + " DESC");


            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).startsWith(".")){
                        BaseModel music = new BaseModel();

                        music.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)));
                        music.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)));
                        music.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)));
                        music.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.SIZE)));
                        music.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                        music.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                        music.setDate(convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATE_ADDED))));

                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(music.getBucketId());
                        directory.setPath(Util.extractPathWithoutSeparator(music.getPath()));
                        File file=new File(directory.getPath());
                        directory.setName(file.getName());

                        if(!directories1.contains(directory)){
                            directory.addFile(music);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(music);
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
                FileAdapter.clearData();
                FileAdapter.addAll(directories);
            });
        }
    }

    public void changeHomeView(){

        l1.setVisibility(View.GONE);
        l3.setVisibility(View.VISIBLE);
        l2.setVisibility(View.VISIBLE);
        l4.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        toolbar.setBackgroundColor(getResources().getColor(R.color.themeColor));

        count.setText(Util.mSelectedMusicList.size() + " Selected");

    }
    public void card2Close(){

        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
        Util.showCircle=false;
        viewClose=true;
        Util.mSelectedMusicList.clear();

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                Util.isAllSelected=false;
                if(albumChildAdapter!=null)
                    albumChildAdapter.notifyDataSetChanged();
                if(albumChildListAdapter!=null)
                    albumChildListAdapter.notifyDataSetChanged();
            }
        });
    }

    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedMusicList.clear();
        disableCard();
        l1.setVisibility(View.VISIBLE);
        l3.setVisibility(View.GONE);
        l2.setVisibility(View.GONE);
        l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
        viewClose=true;
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));

    }


    public void fileInfo(){
        File imgFile = new  File(Util.mSelectedMusicList.get(0));
        if(imgFile.exists()){

            proName.setText(imgFile.getName());

            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
            String date = spf.format(lastModDate);
            proDate.setText(date);
            String s=getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(imgFile.getPath());
        }
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
                MediaScannerConnection.scanFile(getApplicationContext(), new String[] { imageFileName.getAbsolutePath() }, new String[] { "*/*" }, null);

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

                //   Log.e("msClient obj", "scan completed");
                for(int i=0;i<imageFileName.length;i++){
                    MediaScannerConnection.scanFile(getApplicationContext(),new String[]{ imageFileName[i].getPath() },new String[]{ "audio/*" },null);
                }
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
            }
        });
        this.msConn.connect();
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

            for(int i = 0;i< Util.mSelectedMusicList.size();i++){
                File file1 = new File(Util.mSelectedMusicList.get(i));
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

}