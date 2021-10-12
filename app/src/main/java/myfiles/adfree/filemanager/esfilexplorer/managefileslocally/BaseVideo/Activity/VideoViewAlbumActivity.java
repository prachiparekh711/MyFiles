package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.VaultActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.CreateAlbumAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.VideoViewAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
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

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.MediaColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.VideoViewAdapter.isRotate;

public class VideoViewAlbumActivity extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener{

    private final List<BaseModel> videoFiles = new ArrayList<>();
    public List<Directory<BaseModel>> dateFiles = new ArrayList<>();
    public ArrayList<String> privateList = new ArrayList<>();
    private static int position = 0;
    private int DirPos = 0;
    private static int directoryPosition = 0;
    int finalPosition1=0;

    public static TextView mName;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    public static RecyclerView recyclerView;
    VideoViewAdapter demoAdapter;
    ImageView mshare,mDelete,mMore;
    public static int sharePosition=0;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    int firstVisiblePosition=0;
    CreateAlbumAdapter createAlbumAdapter;
    private static String ALBUMNAME = "";
    private static boolean isCreateAlbum = false;
    private static int createAlbumPosition = 0;
    BottomSheetBehavior movePopUpBehaviour;
    BottomSheetBehavior albumCreatePopUpBehaviour;
    @BindView(R.id.rvListView)
    RecyclerView rvListView;
    @BindView(R.id.rl_createAlbum)
    RelativeLayout rl_createAlbum;
    @BindView(R.id.llCreateAnAlbum)
    LinearLayout llCreateAnAlbum;
    @BindView(R.id.llCreateAlbum)
    RelativeLayout llCreateAlbum;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
    MediaScannerConnection msConn;
    CardView cardOptions;
    LinearLayout moveLayout,vaultLayout,infoLayout;
    public static RelativeLayout toolbar1;
    String inputPath, inputFile,  outputPath;
    TextView successText;
    AlertDialog alertVault;

    @Override
    public void onBackPressed() {
        if (movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            movePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (albumCreatePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }if(isRotate){
            isRotate=false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else{
            super.onBackPressed();
        }
    }

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
        setContentView(R.layout.activity_video_view_album);


        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(VideoViewAlbumActivity.this);
        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(VideoViewAlbumActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);

        position = getIntent().getIntExtra("Position", 0);
        DirPos = getIntent().getIntExtra("DirPos", 0);
        directoryPosition = getIntent().getIntExtra("directoryPosition", 0);
        createAlbumAdapter = new CreateAlbumAdapter(dateFiles, VideoViewAlbumActivity.this, new CreateAlbumAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ALBUMNAME = new File(dateFiles.get(position).getPath()).getName();
                isCreateAlbum = false;
                createAlbumPosition = position;

                if (movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED || movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    movePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {
                    movePopUpBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
//                moveAlbum();
                moveFile();
            }
        });
        rvListView.setLayoutManager(new LinearLayoutManager(VideoViewAlbumActivity.this, RecyclerView.VERTICAL, false));
        rvListView.setAdapter(createAlbumAdapter);
        new VideoViewAlbumActivity.LoadVideos(VideoViewAlbumActivity.this).execute();
        new VideoViewAlbumActivity.LoadAlbumImages(VideoViewAlbumActivity.this).execute();

        imgBack.setOnClickListener(v -> onBackPressed());
        rl_createAlbum.setOnClickListener(v -> {
            movePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            isCreateAlbum = true;
            createAlbumPosition = 0;
            if (albumCreatePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }
//            moveAlbum();
            moveFile();
        });
    }

    public void init(){
        Util.isBottomPlayerExpanded=true;
        ButterKnife.bind(VideoViewAlbumActivity.this);
        mName=findViewById(R.id.tv_imgName);

        recyclerView=findViewById(R.id.recycler);
        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);
        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);

        mshare=findViewById(R.id.imgShare);
        mDelete=findViewById(R.id.imgDelete);
        mMore=findViewById(R.id.imgMore);
        mshare.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        cardOptions=findViewById(R.id.cardOptions);
        mMore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cardOptions.setVisibility(View.VISIBLE);
            }
        });

        movePopUpBehaviour = BottomSheetBehavior.from(llCreateAlbum);
        albumCreatePopUpBehaviour = BottomSheetBehavior.from(llCreateAnAlbum);
        moveLayout=findViewById(R.id.option3);
        vaultLayout=findViewById(R.id.option4);
        infoLayout=findViewById(R.id.option5);
        moveLayout.setOnClickListener(this);
        vaultLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);


        toolbar1=findViewById(R.id.rl_toolbar1);

        recyclerView.setOnTouchListener(this);
        toolbar1.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        if(cardOptions.getVisibility()==View.VISIBLE)
            cardOptions.setVisibility(View.GONE);
        return false;
    }


    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.option3:
                cardOptions.setVisibility(View.GONE);
                movePopUpBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                break;
            case R.id.option4:
                if (SharedPreference.getPasswordProtect(getBaseContext()).equals("")) {
                    Intent in=new Intent(VideoViewAlbumActivity.this, VaultActivity.class);
                    Util.VaultFromOther=true;
                    startActivity(in);
                }else {
                    cardOptions.setVisibility(View.GONE);
                    File sampleFile = Environment.getExternalStorageDirectory();
                    File file1 = new File(videoFiles.get(firstVisiblePosition).getPath());


                    if (file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())) {

                        SaveImage(file1);
                    } else {
                        if (!SharedPreference.getSharedPreference(VideoViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())) {
                            Intent in = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                            in.putExtra("android.content.extra.SHOW_ADVANCED", true);
                            startActivityForResult(in, REQUEST_CODE_OPEN_DOCUMENT_TREE);
                        } else {
                            getData(file1);
                        }
                    }
                }
                break;
            case R.id.option5:
                cardOptions.setVisibility(View.GONE);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.imgShare:{
                fireAnalytics("Video", "Share");
                String path=videoFiles.get(sharePosition).getPath();
                File file=new File(path);
                Uri contentUri = FileProvider.getUriForFile(VideoViewAlbumActivity.this,getPackageName() + ".provider",file);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
            }
            break;
            case R.id.imgDelete:{
                ActionDelete();
            }
            break;
            case R.id.imgMore:{

            }
            break;
        }
    }

    public void ActionDelete(){
        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(VideoViewAlbumActivity.this);
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
                isDeleteImg = true;
                boolean isDelete=false;
                File sourceFile = new File(videoFiles.get(firstVisiblePosition).getPath());

                File sampleFile = Environment.getExternalStorageDirectory();
                File file1 = new File(dateFiles.get(createAlbumPosition).getPath());

                if (file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())) {
                    if (sourceFile.exists()){
                        Util.MoveToTrash(VideoViewAlbumActivity.this,sourceFile);
                        isDelete = Util.delete(VideoViewAlbumActivity.this, sourceFile);
                        //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
                    }
                } else {
                    if (!SharedPreference.getSharedPreference(VideoViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
                    } else {
                        if (sourceFile.exists()) {
                            Util.MoveToTrash(VideoViewAlbumActivity.this,sourceFile);
                            isDelete = Util.delete(VideoViewAlbumActivity.this, sourceFile);
                            //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
                        }
                    }
                }

                if(isDelete){
                    fireAnalytics("Video", "Delete");
                    successText.setText( "Delete successfully."  );
                    alertVault.show();
                    Util.IsUpdate=true;
                    Util.IsAlbumUpdate=true;
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            alertVault.dismiss();
                        }
                    }, 2000);
                    if(firstVisiblePosition == videoFiles.size() - 1){
                        videoFiles.remove(firstVisiblePosition);
                        onBackPressed();
                    }else{
                        videoFiles.remove(firstVisiblePosition);
                        if(demoAdapter!=null)
                            demoAdapter.notifyDataSetChanged();
                    }

                    if(videoFiles.size() == 0)
                        onBackPressed();
                }else{
                    Toast.makeText(VideoViewAlbumActivity.this,"Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }
                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    class LoadVideos extends AsyncTask<Void, Void,List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;

        public LoadVideos(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            videoFiles.clear();
            dateFiles.clear();
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
                    DATE_ADDED,
            };

            Cursor data = fragmentActivity.getContentResolver().query(uri, FILE_PROJECTION, null, null, DATE_ADDED + " DESC");
            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                BaseModel vid = new BaseModel();

                if(!data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(TITLE)).startsWith(".")){
                        vid.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
                        vid.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                        vid.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
                        vid.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                        vid.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)));
                        vid.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)));
                        vid.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));

                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(vid.getBucketId());
                        directory.setName(vid.getBucketName());
                        directory.setPath(Util.extractPathWithoutSeparator(vid.getPath()));

                        if(!directories1.contains(directory)){
                            directory.addFile(vid);
                            directories.add(directory);
                            directories1.add(directory);
                        }else{
                            directories.get(directories.indexOf(directory)).addFile(vid);
                        }

                    }

                }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);

            privateList = SharedPreference.getHideFileList(VideoViewAlbumActivity.this);

            dateFiles = directories;
            videoFiles.clear();
            int finalPosition = position;

            int count = -1;
            for (int i = 0; i < directories.size(); i++) {
                if (i == DirPos) {
                    videoFiles.addAll(directories.get(i).getFiles());
                    break;
                }
            }
            finalPosition1 = finalPosition;

            sharePosition=finalPosition1;
            SnapHelper snapHelper = new PagerSnapHelper();
            recyclerView.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(recyclerView);
            demoAdapter = new VideoViewAdapter(VideoViewAlbumActivity.this,videoFiles,finalPosition,"Album");
            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(VideoViewAlbumActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManagaer);
            recyclerView.setAdapter(demoAdapter);
            recyclerView.scrollToPosition(finalPosition1);
            mName.setText(videoFiles.get(finalPosition1).getName());

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView,int dx,int dy){
                    super.onScrolled(recyclerView,dx,dy);
                    firstVisiblePosition = horizontalLayoutManagaer.findFirstVisibleItemPosition();
                    fileInfo(firstVisiblePosition);
                    mName.setText(videoFiles.get(firstVisiblePosition).getName());
                }
            });
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
                    DATE_ADDED,
            };

            Cursor data = fragmentActivity.getContentResolver().query(uri, FILE_PROJECTION, null, null, DATE_ADDED + " DESC");

            List<Directory<BaseModel>> directories = new ArrayList<>();
            List<Directory> directories1 = new ArrayList<>();

            if (data.getPosition() != -1) {
                data.moveToPosition(-1);
            }

            while (data.moveToNext()) {
                //Create a File instance
                BaseModel img = new BaseModel();

                img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                img.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));

                //Create a Directory
                Directory<BaseModel> directory = new Directory<>();
                directory.setId(img.getBucketId());
                directory.setName(img.getBucketName());
                directory.setPath(Util.extractPathWithoutSeparator(img.getPath()));

                if (!directories1.contains(directory)) {
                    directory.addFile(img);
                    directories.add(directory);
                    directories1.add(directory);
                } else {
                    directories.get(directories.indexOf(directory)).addFile(img);
                }
            }

            return directories;
        }

        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);


            fragmentActivity.runOnUiThread(() -> {
                createAlbumAdapter.clearData();
                createAlbumAdapter.addAll(directories);
            });
        }
    }

    public void fileInfo(int finalPosition1){
        File imgFile = new  File(videoFiles.get(finalPosition1).getPath());
        if(imgFile.exists()){

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imgFile.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);

            proImage.setImageBitmap(thumb);
            proName.setText(videoFiles.get(finalPosition1).getName());
            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");

            String date = spf.format(lastModDate);

            proDate.setText(date);
            String s= Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(videoFiles.get(finalPosition1).getPath());
        }
    }


    private void moveFile() {

        File sourceFile = new File(videoFiles.get(firstVisiblePosition).getPath());
        inputFile=sourceFile.getName();
        inputPath=sourceFile.getPath();
        outputPath=dateFiles.get(createAlbumPosition).getPath();


        File sampleFile = Environment.getExternalStorageDirectory();
        File file1 =null;
        file1 = new File(dateFiles.get(createAlbumPosition).getPath());
        if (file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())) {
            moveData(sourceFile,file1.getName(),file1);
        } else {
            if (!SharedPreference.getSharedPreference(VideoViewAlbumActivity.this).contains(file1.getParentFile().getAbsolutePath())) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);

            } else {
//                    moveData(inputFile,inputPath,outputPath);
                moveSDImage(new File(inputPath),new File(outputPath));
            }
        }

//create output directory if it doesn't exist
        if (movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED || movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            movePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }



    }


    public void moveData(File sourceFile ,String targetName,File destinationFile){
        try{


            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }


            File to=new File(destinationFile, sourceFile.getName());
            if(to.getPath().equals(sourceFile.getPath())){
                Toast.makeText(VideoViewAlbumActivity.this,"Already in same folder", Toast.LENGTH_SHORT).show();
            }else {
                boolean isRename = sourceFile.renameTo(to);
//            Log.e("From path",sourceFile.getPath());
//            Log.e("To path",to.getPath());

//            if (sourceFile.exists()) {
                boolean isDelete = Util.delete(VideoViewAlbumActivity.this, sourceFile);
                if (!isDelete)
                    isDelete = sourceFile.delete();
//            Log.e("is Delete : " ,String.valueOf(isDelete));
//            }

                if (isDelete == true && isRename == true) {
                    scanPhoto(sourceFile.getPath());
                    successText.setText(" File(s) moved successfully.");
                    alertVault.show();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Util.IsUpdate = true;
                            Util.IsAlbumUpdate = true;
                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(VideoViewAlbumActivity.this);
                            Intent localIn = new Intent("TAG_REFRESH");
                            lbm.sendBroadcast(localIn);
                            alertVault.dismiss();
                        }
                    }, 2000);

                }
            }
        }
        catch (Exception e) {
            //   Log.e("tag", e.getMessage());
        }
    }

    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                //   Log.e("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                //   Log.e("msClient obj", "scan completed");
            }
        });
        this.msConn.connect();
    }
    public DocumentFile getDocumentFile(final File file,final boolean isDirectory) {
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

        Uri treeUri = Uri.parse(SharedPreference.getSharedPreferenceUri(VideoViewAlbumActivity.this));

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(VideoViewAlbumActivity.this, treeUri);

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

    private void SaveImage(File samplefile) {

        File from=new File(samplefile.getAbsolutePath());
        File folder = new File(from.getParentFile().getAbsolutePath());
        if(!folder.exists())
            folder.mkdir();
        File to=new File(folder.getAbsolutePath() + "/." + samplefile.getName());
        from.renameTo(to);


        ArrayList<String> hideFileList = SharedPreference.getHideFileList(VideoViewAlbumActivity.this);
        hideFileList.add(to.getAbsolutePath());
        SharedPreference.setHideFileList(VideoViewAlbumActivity.this, hideFileList);

        try {

                boolean isDelete=samplefile.delete();
                if(!isDelete)
                    isDelete=Util.delete(VideoViewAlbumActivity.this,samplefile);
                super.onBackPressed();
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    videoFiles.remove(firstVisiblePosition);
                    if(demoAdapter!=null)
                        demoAdapter.notifyDataSetChanged();
                }
            });
            fireAnalytics("Video", "Hide");
            scanPhoto(samplefile.getPath());
            successText.setText(  " 1 File moved to vault."  );
            alertVault.show();

            if (videoFiles.isEmpty())
                onBackPressed();
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
            ArrayList<String> hideFileList =SharedPreference.getHideFileList(VideoViewAlbumActivity.this);
            hideFileList.add(file3.getAbsolutePath());
            SharedPreference.setHideFileList(VideoViewAlbumActivity.this, hideFileList);

            if (file1.exists()) {
                boolean isDelete = Util.delete(VideoViewAlbumActivity.this, file1);
            }

            videoFiles.remove(firstVisiblePosition);
            if(demoAdapter!=null)
                demoAdapter.notifyDataSetChanged();
            if (videoFiles.isEmpty())
                onBackPressed();
            Toast.makeText(this, "Hide " + file1.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {

            File file1 = new File(videoFiles.get(firstVisiblePosition).getPath());
            String[] parts = (file1.getAbsolutePath()).split("/");

            DocumentFile documentFile = DocumentFile.fromTreeUri(this, resultData.getData());
            documentFile = documentFile.findFile(parts[parts.length - 1]);

            if (documentFile == null) {
                File sourceFile = new File(videoFiles.get(firstVisiblePosition).getPath());
                File file = new File(dateFiles.get(createAlbumPosition).getPath());
                //   Log.e("LLL_Path: ", dateFiles.get(createAlbumPosition).getPath() + "    : " + file.getAbsolutePath());
                String[] parts1 = (file.getAbsolutePath()).split("/");
                DocumentFile documentFile1 = DocumentFile.fromTreeUri(this, resultData.getData());

                SharedPreference.setSharedPreferenceUri(VideoViewAlbumActivity.this, documentFile1.getUri());
                SharedPreference.setSharedPreference(VideoViewAlbumActivity.this, file.getParentFile().getAbsolutePath());

                if (isDeleteImg) {
                    isDeleteImg = false;
                    if (sourceFile.exists()) {
                        Util.MoveToTrash(VideoViewAlbumActivity.this,sourceFile);
                        boolean isDelete = Util.delete(VideoViewAlbumActivity.this, sourceFile);
                        //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
                    }
                    videoFiles.remove(firstVisiblePosition);
                    if(demoAdapter!=null)
                        demoAdapter.notifyDataSetChanged();
                    if (videoFiles.size() ==0)
                        onBackPressed();
                } else {
                    moveSDImage(new File(inputPath),new File(outputPath));
                }
            } else {

                //   Log.e("LLL_Data: ", String.valueOf(documentFile.getUri()));

                SharedPreference.setSharedPreferenceUri(VideoViewAlbumActivity.this, documentFile.getUri());
                SharedPreference.setSharedPreference(VideoViewAlbumActivity.this, file1.getParentFile().getAbsolutePath());

                getData(file1);
            }
        }
    }

    private void moveSDImage(File sourceFile,File destinationFile) {

        File file;
        if (!isCreateAlbum) {
            file = new File(dateFiles.get(position).getPath(), sourceFile.getName());
        } else {
            File mainDir = Environment.getExternalStorageDirectory();

            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
            //   Log.e("LLL_Name: ", destinationFile.getAbsolutePath());

        }

        try {
            File to=new File(destinationFile, sourceFile.getName());
            boolean isRename=sourceFile.renameTo(to);
//            Log.e("From path",sourceFile.getPath());
//            Log.e("To path",to.getPath());

//            if (sourceFile.exists()) {
            boolean isDelete=Util.delete(VideoViewAlbumActivity.this,sourceFile);
            if(!isDelete)
                isDelete=sourceFile.delete();
//            Log.e("is Delete : " ,String.valueOf(isDelete));

            scanPhoto(sourceFile.toString());


            if (firstVisiblePosition == videoFiles.size() - 1) {
                videoFiles.remove(firstVisiblePosition);
                onBackPressed();
            } else {
                videoFiles.remove(firstVisiblePosition);
                if(demoAdapter!=null)
                    demoAdapter.notifyDataSetChanged();
            }
            if (videoFiles.size() == 0)
                onBackPressed();
            if(isDelete==true && isRename==true){
                scanPhoto(inputPath);
                successText.setText(  " File(s) moved successfully."  );
                alertVault.show();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Util.IsUpdate=true;
                        Util.IsAlbumUpdate=true;
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(VideoViewAlbumActivity.this);
                        Intent localIn = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(localIn);
                        alertVault.dismiss();
                    }
                }, 2000);

            }


        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}