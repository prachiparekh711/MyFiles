package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Intent;
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
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.BottomImagesAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.CreateAlbumAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.ImageViewAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Interface.BottomImgsInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class ImageViewActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{


    private static final String TAG = ImageViewActivity.class.getSimpleName();
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
    private static int position = 0;
    private static int directoryPosition = 0;
    private static String from = "";
    private static ViewPager imgViewPager;
    private final List<BaseModel> imageFiles = new ArrayList<>();
    public List<Directory<BaseModel>> dateFiles = new ArrayList<>();
    public ArrayList<String> privateList = new ArrayList<>();
    int finalPosition1=0;
    @BindView(R.id.tv_imgName)
    TextView tv_imgName;
    @BindView(R.id.rvListView)
    RecyclerView rvListView;
    @BindView(R.id.llCreateAlbum)
    RelativeLayout llCreateAlbum;
    @BindView(R.id.rl_createAlbum)
    RelativeLayout rl_createAlbum;
    @BindView(R.id.llCreateAnAlbum)
    LinearLayout llCreateAnAlbum;
    @BindView(R.id.tvOk)
    TextView tvOk;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.et_albumName)
    TextInputEditText et_albumName;
    DiscreteScrollView imgRecycler;
    BottomImagesAdapter adapter;
    BottomImgsInterface imgsInterface;
    ImageView share,delete,more;
    CardView cardOptions;
    TextView successText;
    int vaultCount=0,moveCount=0;
    AlertDialog alertVault;
    //    @BindView(R.id.imgProtect)
//    ImageView imgProtect;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    RelativeLayout rootRL;

    MediaScannerConnection msConn;
    private ImageViewAdapter viewPagerAdapter;
    LinearLayout setAsLayout,openWithLayout,moveLayout,vaultLayout,infoLayout;
    TextView cancelDel,okDel;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    CreateAlbumAdapter createAlbumAdapter;
    private static String ALBUMNAME = "";
    private static boolean isCreateAlbum = false;
    private static int createAlbumPosition = 0;
    BottomSheetBehavior movePopUpBehaviour;
    BottomSheetBehavior albumCreatePopUpBehaviour;
    private static boolean isCopyImg = false;
    private static boolean isDeleteImg = false;
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
        setContentView(R.layout.activity_image_view);

        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        ButterKnife.bind(ImageViewActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedImgList.clear();
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(ImageViewActivity.this);

        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(ImageViewActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);

        from = getIntent().getStringExtra("from");
        position = getIntent().getIntExtra("Position", 0);
        directoryPosition = getIntent().getIntExtra("directoryPosition", 0);
        rvListView.setLayoutManager(new LinearLayoutManager(ImageViewActivity.this, RecyclerView.VERTICAL, false));

        createAlbumAdapter = new CreateAlbumAdapter(dateFiles, ImageViewActivity.this, new CreateAlbumAdapter.ItemClickListener() {
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
                moveAlbum();
            }
        });
        rvListView.setAdapter(createAlbumAdapter);
        new LoadImages(ImageViewActivity.this).execute();
        new LoadAlbumImages(ImageViewActivity.this).execute();


        imgsInterface=new BottomImgsInterface(){
            @Override
            public void onImgClick(int position){
                imgViewPager.setCurrentItem(position);
                fileInfo();
            }
        };
        adapter = new BottomImagesAdapter(ImageViewActivity.this,imageFiles,directoryPosition,imgsInterface);
        imgRecycler.setSlideOnFling(true);
        imgRecycler.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

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

        });

        tvCancel.setOnClickListener(v -> {
            if (albumCreatePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (albumCreatePopUpBehaviour.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            movePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (albumCreatePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }


    public void init(){
        imgViewPager = findViewById(R.id.imgViewPager);
        imgRecycler=findViewById(R.id.bottomRecycler);
        rootRL=findViewById(R.id.rootRL);
        share=findViewById(R.id.imgShare);
        delete=findViewById(R.id.imgDelete);
        more=findViewById(R.id.imgMore);
        cardOptions=findViewById(R.id.cardOptions);
        more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cardOptions.setVisibility(View.VISIBLE);
            }
        });
        rootRL.setOnTouchListener(this);
        imgRecycler.setOnTouchListener(this);
        imgViewPager.setOnTouchListener(this);

        setAsLayout=findViewById(R.id.option1);
        openWithLayout=findViewById(R.id.option2);
        moveLayout=findViewById(R.id.option3);
        vaultLayout=findViewById(R.id.option4);
        infoLayout=findViewById(R.id.option5);

        setAsLayout.setOnClickListener(this);
        openWithLayout.setOnClickListener(this);
        moveLayout.setOnClickListener(this);
        vaultLayout.setOnClickListener(this);
        infoLayout.setOnClickListener(this);

        share.setOnClickListener(this);
        delete.setOnClickListener(this);

        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);

        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);

        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        movePopUpBehaviour = BottomSheetBehavior.from(llCreateAlbum);
        albumCreatePopUpBehaviour = BottomSheetBehavior.from(llCreateAnAlbum);
    }

    public void moveAlbum(){
        File sourceFile = new File(imageFiles.get(imgViewPager.getCurrentItem()).getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath());

        File sampleFile = Environment.getExternalStorageDirectory();
        File file1 =null;

        if (isCreateAlbum)
            file1 = new File(sampleFile, ALBUMNAME);
        else
            file1 = new File(dateFiles.get(createAlbumPosition).getPath());
        if (file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())) {
            moveImage(bitmap, sourceFile, isCreateAlbum, createAlbumPosition);

        } else {
            if (!SharedPreference.getSharedPreference(ImageViewActivity.this).contains(file1.getParentFile().getAbsolutePath())) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE);
            } else {
                File file = new File(dateFiles.get(position).getPath());
                moveSDImage(sourceFile, isCreateAlbum, createAlbumPosition);
            }
        }

        if (movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED || movePopUpBehaviour.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            movePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        if(cardOptions.getVisibility()==View.VISIBLE)
            cardOptions.setVisibility(View.GONE);
        return false;
    }

    private void setWallpaper(String wallpaper) {
        Bitmap b = BitmapFactory.decodeFile(wallpaper);
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try{
            fireAnalytics("Image", "Wallpaper");
            manager.setBitmap(b);
            Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v){
        String path=imageFiles.get(imgViewPager.getCurrentItem()).getPath();
        File file=new File(path);
        switch (v.getId()){
            case R.id.option1:
                cardOptions.setVisibility(View.GONE);
                setWallpaper( path);

//                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
//                intent.setDataAndType(Uri.parse(path), "image/*");
//                intent.putExtra("mimeType", "image/*");
//                startActivityForResult(Intent.createChooser(intent, "Set image as"), 200);
                break; 
            case R.id.option2:
                fireAnalytics("Image", "View");
                cardOptions.setVisibility(View.GONE);
                Uri uri =  Uri.parse(path);
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
            case R.id.option3:
                cardOptions.setVisibility(View.GONE);
                movePopUpBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                break;
            case R.id.option4:
                if (SharedPreference.getPasswordProtect(getBaseContext()).equals("")) {
                    Intent in=new Intent(ImageViewActivity.this, VaultActivity.class);
                    Util.VaultFromOther=true;
                    startActivity(in);
                }else {
                    cardOptions.setVisibility(View.GONE);
                    File sampleFile = Environment.getExternalStorageDirectory();
                    File file1 = new File(imageFiles.get(imgViewPager.getCurrentItem()).getPath());


                    if (file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
                        SaveImage(bitmap, file1);
                    } else {
                        if (!SharedPreference.getSharedPreference(ImageViewActivity.this).contains(file1.getParentFile().getAbsolutePath())) {
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
            case R.id.imgShare:
                fireAnalytics("Image", "Share");
                cardOptions.setVisibility(View.GONE);
                Uri contentUri = FileProvider.getUriForFile(ImageViewActivity.this,getPackageName() + ".provider",file);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
                break;
            case R.id.imgDelete:
                cardOptions.setVisibility(View.GONE);
                AlertDialog alertadd = new AlertDialog.Builder(this).create();
                LayoutInflater factory = LayoutInflater.from(ImageViewActivity.this);
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
                        File sourceFile = new File(imageFiles.get(imgViewPager.getCurrentItem()).getPath());
                        boolean isDelete = false;
                        File sampleFile = Environment.getExternalStorageDirectory();
                        File file1 = new File(dateFiles.get(createAlbumPosition).getPath());

                        if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                            if(sourceFile.exists()){
                                Util.MoveToTrash(ImageViewActivity.this,sourceFile);
                                isDelete = sourceFile.delete();
                                if(!isDelete)
                                    isDelete = Util.delete(ImageViewActivity.this,sourceFile);

                            }
                        }else{
                            if(!SharedPreference.getSharedPreference(ImageViewActivity.this).contains(file1.getParentFile().getAbsolutePath())){
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                                intent.putExtra("android.content.extra.SHOW_ADVANCED",true);
                                startActivityForResult(intent,REQUEST_CODE_OPEN_DOCUMENT_TREE);
                            }else{
                                if(sourceFile.exists()){
                                    Util.MoveToTrash(ImageViewActivity.this,sourceFile);
                                    isDelete = Util.delete(ImageViewActivity.this,sourceFile);
                                    //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                                }
                            }
                        }
                        if(isDelete){
                            fireAnalytics("Image", "Delete");
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
                            scanPhoto(sourceFile.getPath());
                            if(imgViewPager.getCurrentItem() == imageFiles.size() - 1){
                                imageFiles.remove(imgViewPager.getCurrentItem());
                                onBackPressed();
                            }else{
                                imageFiles.remove(imgViewPager.getCurrentItem());
                                if(viewPagerAdapter != null)
                                    viewPagerAdapter.notifyDataSetChanged();
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run(){
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            }

                            if(imageFiles.size() == 0)
                                onBackPressed();
                        }
                        alertadd.dismiss();
                    }
                });
                alertadd.show();
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.tvOk:
                if (albumCreatePopUpBehaviour.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    albumCreatePopUpBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                //   Log.e("LLLL_Name: ", et_albumName.getText().toString());
                if (et_albumName.length() > 0) {
                    /*Util.hideKeyboard(ImageViewActivity.this);*/
                    ALBUMNAME = et_albumName.getText().toString();
                }
                moveAlbum();
                break;
        }
            
    }


    private void SaveImage(Bitmap finalBitmap, File samplefile) {

        File file = new File(samplefile.getParentFile().getAbsolutePath(), "." + samplefile.getName());

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ArrayList<String> hideFileList = SharedPreference.getHideFileList(ImageViewActivity.this);
            hideFileList.add(file.getAbsolutePath());
            SharedPreference.setHideFileList(ImageViewActivity.this, hideFileList);
            fireAnalytics("Image", "Vault");
            successText.setText(  " 1 File moved to vault."  );
            alertVault.show();

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertVault.dismiss();
                }
            }, 2000);
            if (samplefile.exists()) {
                samplefile.delete();
            }

            scanPhoto(samplefile.toString());
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    imageFiles.remove(imgViewPager.getCurrentItem());
                    if(viewPagerAdapter!=null)
                       viewPagerAdapter.notifyDataSetChanged();
                    if(adapter!=null)
                        adapter.notifyDataSetChanged();
                }
            });

            if (imageFiles.isEmpty())
                onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void scanPhoto(final String imageFileName) {
        msConn = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
//                //   Log.e("msClient obj", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
//                //   Log.e("msClient obj", "scan completed");
            }
        });
        this.msConn.connect();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {

            File file1 = new File(imageFiles.get(imgViewPager.getCurrentItem()).getPath());
            String[] parts = (file1.getAbsolutePath()).split("/");

            DocumentFile documentFile = DocumentFile.fromTreeUri(this, resultData.getData());
            documentFile = documentFile.findFile(parts[parts.length - 1]);

            if (documentFile == null) {
                File sourceFile = new File(privateList.get(imgViewPager.getCurrentItem()));
                File file = new File(dateFiles.get(createAlbumPosition).getPath());
                //   Log.e("LLL_Path: ", dateFiles.get(createAlbumPosition).getPath() + "    : " + file.getAbsolutePath());
                String[] parts1 = (file.getAbsolutePath()).split("/");
                DocumentFile documentFile1 = DocumentFile.fromTreeUri(this, resultData.getData());

                SharedPreference.setSharedPreferenceUri(ImageViewActivity.this, documentFile1.getUri());
                SharedPreference.setSharedPreference(ImageViewActivity.this, file1.getParentFile().getAbsolutePath());

                if (isCopyImg) {
                    isCopyImg = false;
                    copySDImage(sourceFile, isCreateAlbum, createAlbumPosition);
                } else if (isDeleteImg) {
                    isDeleteImg = false;
                    if (sourceFile.exists()) {
                        Util.MoveToTrash(ImageViewActivity.this,sourceFile);
                        boolean isDelete = Util.delete(ImageViewActivity.this, sourceFile);
                        //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
                    }
                    imageFiles.remove(imgViewPager.getCurrentItem());
                    if(viewPagerAdapter!=null)
                         viewPagerAdapter.notifyDataSetChanged();
                    if (imageFiles.size() ==0)
                        onBackPressed();
                } else {
                    moveSDImage(sourceFile, isCreateAlbum, createAlbumPosition);
                }
            } else {

                //   Log.e("LLL_Data: ", String.valueOf(documentFile.getUri()));

                SharedPreference.setSharedPreferenceUri(ImageViewActivity.this, documentFile.getUri());
                SharedPreference.setSharedPreference(ImageViewActivity.this, file1.getParentFile().getAbsolutePath());

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
            ArrayList<String> hideFileList = SharedPreference.getHideFileList(ImageViewActivity.this);
            hideFileList.add(file3.getAbsolutePath());
            SharedPreference.setHideFileList(ImageViewActivity.this, hideFileList);

            if (file1.exists()) {
                boolean isDelete = Util.delete(ImageViewActivity.this, file1);
            }

            imageFiles.remove(imgViewPager.getCurrentItem());
            if(viewPagerAdapter!=null)
               viewPagerAdapter.notifyDataSetChanged();
            if (imageFiles.isEmpty())
                onBackPressed();
            Toast.makeText(this, "Hide " + file1.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void copySDImage(File sourceFile, boolean isCreateAlbum, int position) {

        File file;
        if (!isCreateAlbum) {
            file = new File(dateFiles.get(position).getPath(), sourceFile.getName());
        } else {
            File mainDir = Environment.getExternalStorageDirectory();
            File destinationFile = new File(mainDir, ALBUMNAME);

            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
            //   Log.e("LLL_Name: ", destinationFile.getAbsolutePath());

            file = new File(destinationFile, sourceFile.getName());
        }

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

            /*ArrayList<String> favFileList = SharedPrefrance.getFavouriteFileList(AlbumImgViewActivity.this);
            favFileList.add(sourceFile.getAbsolutePath());
            favFileList.add(file.getAbsolutePath());
            SharedPrefrance.setFavouriteFileList(AlbumImgViewActivity.this, new ArrayList<>());
            SharedPrefrance.setFavouriteFileList(AlbumImgViewActivity.this, favFileList);*/

            scanPhoto(file.toString());

//            privateList.remove(imgViewPager.getCurrentItem());
//            viewPagerAdapter.notifyDataSetChanged();
//            if (privateList.isEmpty())
//                onBackPressed();
            Toast.makeText(this, "Copy " + sourceFile.getName(), Toast.LENGTH_LONG).show();

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

        Uri treeUri = Uri.parse(SharedPreference.getSharedPreferenceUri(ImageViewActivity.this));

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(ImageViewActivity.this, treeUri);

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
                    Log.w(TAG, "Unexpected external file dir: " + file.getAbsolutePath());
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

   
    class LoadImages extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        FragmentActivity fragmentActivity;

        public LoadImages(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageFiles.clear();
            dateFiles.clear();
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
                //Create a File instance
                BaseModel img = new BaseModel();
                if(!data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(TITLE)).startsWith(".")){
                        img.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                        img.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                        img.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                        img.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                        img.setBucketId(data.getString(data.getColumnIndexOrThrow(BUCKET_ID)));
                        img.setBucketName(data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)));
                        img.setDate(String.valueOf(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));


                        //Create a Directory
                        Directory<BaseModel> directory = new Directory<>();
                        directory.setId(img.getBucketId());
                        directory.setName(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));
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

            privateList = SharedPreference.getHideFileList(ImageViewActivity.this);
            for(int i=0;i<privateList.size();i++){

//                //   Log.e("privateList i" + i,privateList.get(i));
            }
            dateFiles = directories;
            imageFiles.clear();
            int finalPosition = 0;
            int count = -1;
            for (int i = 0; i < directories.size(); i++) {
                for (int j = 0; j < directories.get(i).getFiles().size(); j++) {
                    count++;
                    if (directories.get(directoryPosition).getFiles().get(position).getPath().equals(directories.get(i).getFiles().get(j).getPath())) {
//                        //   Log.e("LLLL_Path: ", directories.get(directoryPosition).getFiles().get(position).getPath() + "  " + directories.get(i).getFiles().get(j).getPath() + " j " + j + " count: " + count);
                        finalPosition = count;
                    }
                }
                imageFiles.addAll(directories.get(i).getFiles());
            }
            //   Log.e(TAG, "LLL_onPostExecute: " + "done " + imageFiles.size() + "  " + finalPosition);

            finalPosition1 = finalPosition;
            fragmentActivity.runOnUiThread(() -> {
                viewPagerAdapter = new ImageViewAdapter(ImageViewActivity.this, imageFiles, directoryPosition);

                imgViewPager.setAdapter(viewPagerAdapter);
                imgViewPager.setCurrentItem(finalPosition1);

                BaseModel imageFile = imageFiles.get(finalPosition1);
                tv_imgName.setText(imageFile.getName());

                imgViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        fileInfo();
                    }

                    @Override
                    public void onPageSelected(int position) {
                        BaseModel imageFile = imageFiles.get(position);
                        tv_imgName.setText(imageFile.getName());
                        imgRecycler.scrollToPosition(position);
                        fileInfo();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

            });

            imgRecycler.setAdapter(adapter);
            imgRecycler.scrollToPosition(finalPosition1);
            fileInfo();

        }

    }

    public void fileInfo(){
        File imgFile = new  File(imageFiles.get(imgViewPager.getCurrentItem()).getPath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            proImage.setImageBitmap(myBitmap);
            proName.setText(imageFiles.get(imgViewPager.getCurrentItem()).getName());
            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");

            String date = spf.format(lastModDate);

            proDate.setText(date);
            String s= Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(imageFiles.get(imgViewPager.getCurrentItem()).getPath());
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
                //Create a File instance
                if(!data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME)).startsWith(".")){
                    if(!data.getString(data.getColumnIndexOrThrow(TITLE)).startsWith(".")){
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
            Log.d(TAG, "onPostExecute: " + "done");

            fragmentActivity.runOnUiThread(() -> {
                createAlbumAdapter.clearData();
                createAlbumAdapter.addAll(directories);
            });
        }
    }

    private void moveImage(Bitmap finalBitmap, File sourceFile, boolean isCreateAlbum, int position) {


        File file;
        if (!isCreateAlbum) {
            file = new File(dateFiles.get(position).getPath(), sourceFile.getName());
        } else {
            File mainDir = Environment.getExternalStorageDirectory();
            File destinationFile = new File(mainDir, ALBUMNAME);

            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
//               Log.e("LLL_Name: ", destinationFile.getAbsolutePath());

            file = new File(destinationFile, sourceFile.getName());
        }
        if(sourceFile.getPath().equals(file.getPath())){
            Toast.makeText(ImageViewActivity.this,"Already in same folder", Toast.LENGTH_SHORT).show();
        }else {
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                successText.setText(" File moved successfully.");
                alertVault.show();
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertVault.dismiss();
                    }
                }, 2000);
//            Toast.makeText(this, "Move File to " + file.getParentFile().getAbsolutePath(), Toast.LENGTH_LONG).show();
                if (sourceFile.exists()) {
                    Util.delete(ImageViewActivity.this, sourceFile);
                }

                scanPhoto(file.toString());

                if (imgViewPager.getCurrentItem() == imageFiles.size() - 1) {
                    imageFiles.remove(imgViewPager.getCurrentItem());
                    onBackPressed();
                } else {
                    imageFiles.remove(imgViewPager.getCurrentItem());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (viewPagerAdapter != null)
                                viewPagerAdapter.notifyDataSetChanged();
                            if (adapter != null)
                                adapter.notifyDataSetChanged();
                        }
                    });

                }

                if (imageFiles.size() == 0)
                    onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void moveSDImage(File sourceFile, boolean isCreateAlbum, int position) {

        File file;
        if (!isCreateAlbum) {
            file = new File(dateFiles.get(position).getPath(), sourceFile.getName());
        } else {
            File mainDir = Environment.getExternalStorageDirectory();
            File destinationFile = new File(mainDir, ALBUMNAME);

            if (!destinationFile.exists()) {
                destinationFile.mkdirs();
            }
            //   Log.e("LLL_Name: ", destinationFile.getAbsolutePath());

            file = new File(destinationFile, sourceFile.getName());
        }

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
                boolean isDelete = Util.delete(ImageViewActivity.this, sourceFile);
                //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
            }

            scanPhoto(file.toString());


            if (imgViewPager.getCurrentItem() == imageFiles.size() - 1) {
                imageFiles.remove(imgViewPager.getCurrentItem());
                onBackPressed();
            } else {
                imageFiles.remove(imgViewPager.getCurrentItem());
                if(viewPagerAdapter!=null)
                    viewPagerAdapter.notifyDataSetChanged();
            }
            if (imageFiles.size() == 0)
                onBackPressed();
            Toast.makeText(this, "Move " + sourceFile.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}