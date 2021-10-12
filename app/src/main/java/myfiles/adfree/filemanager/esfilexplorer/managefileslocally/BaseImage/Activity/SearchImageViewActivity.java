package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;


public class SearchImageViewActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener{


    BaseModel searchedImg;
    private static ImageView imgViewPager;
    @BindView(R.id.tv_imgName)
    TextView tv_imgName;


    ImageView share,more;
    CardView cardOptions;

    @BindView(R.id.imgBack)
    ImageView imgBack;
    RelativeLayout rootRL;

    LinearLayout setAsLayout,openWithLayout,infoLayout;

    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    String mFrom="";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_image_view);

        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        ButterKnife.bind(SearchImageViewActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedImgList.clear();
        init();

        if(getIntent().getExtras() != null)
        mFrom=getIntent().getStringExtra("From");
//        //   Log.e("From:", mFrom + " !");
        if(!mFrom.equals("Vault")){
            searchedImg = (BaseModel)getIntent().getSerializableExtra("Searched");
        }else{
            String path=getIntent().getStringExtra("Searched");
//            //   Log.e("From path:", path + " !");
            File file=new File(path);
            searchedImg=new BaseModel();
            searchedImg.setName(file.getName());
            searchedImg.setPath(file.getPath());
            searchedImg.setSize(file.length());
        }
        tv_imgName.setText(searchedImg.getName());
        //   Log.e("Image :",searchedImg.toString());
        RequestOptions options = new RequestOptions();
        Glide.with(getBaseContext())
                .load(searchedImg.getPath())
                .apply(options.fitCenter()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW))

                .into(imgViewPager);
        imgBack.setOnClickListener(v -> onBackPressed());
        fileInfo();
    }

    @Override
    public void onBackPressed() {
         if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            super.onBackPressed();
        }
    }


    public void init(){
        imgViewPager = findViewById(R.id.imgViewPager);
        rootRL=findViewById(R.id.rootRL);
        share=findViewById(R.id.imgShare);
        more=findViewById(R.id.imgMore);
        cardOptions=findViewById(R.id.cardOptions);
        more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                cardOptions.setVisibility(View.VISIBLE);
            }
        });
        rootRL.setOnTouchListener(this);


        setAsLayout=findViewById(R.id.option1);
        openWithLayout=findViewById(R.id.option2);

        infoLayout=findViewById(R.id.option5);

        setAsLayout.setOnClickListener(this);
        openWithLayout.setOnClickListener(this);

        infoLayout.setOnClickListener(this);

        share.setOnClickListener(this);

        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);

        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);

        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);

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
            manager.setBitmap(b);
            Toast.makeText(this, "Wallpaper set!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v){
        String path=searchedImg.getPath();
        File file=new File(path);
        switch (v.getId()){
            case R.id.option1:
                cardOptions.setVisibility(View.GONE);
                setWallpaper( path);
                break;
            case R.id.option2:
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

            case R.id.option5:
                cardOptions.setVisibility(View.GONE);
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.imgShare:
                cardOptions.setVisibility(View.GONE);
                Uri contentUri = FileProvider.getUriForFile(SearchImageViewActivity.this,getPackageName() + ".provider",file);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("image/jpeg");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }



    public void fileInfo(){
        File imgFile = new  File(searchedImg.getPath());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            proImage.setImageBitmap(myBitmap);
            proName.setText(imgFile.getName());
            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");

            String date = spf.format(lastModDate);

            proDate.setText(date);
            String s= Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(searchedImg.getPath());
        }
    }


}