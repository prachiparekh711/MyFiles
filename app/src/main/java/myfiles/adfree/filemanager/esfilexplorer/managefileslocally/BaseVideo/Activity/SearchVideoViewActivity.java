package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.VideoViewAdapter.isRotate;

public class SearchVideoViewActivity extends AppCompatActivity implements View.OnClickListener{

    BaseModel mSearchedVideo;
    VideoView mVideo;
    ImageView mRotate,mBack10,mPlay,mForward10,mPause;
    TextView startTime,endTime;
    SeekBar seekbar;
    RelativeLayout mBottomRL,rootRL,toolbar;
    @BindView(R.id.tv_imgName)
    TextView tv_imgName;
    ImageView share,info;

    @BindView(R.id.imgBack)
    ImageView imgBack;
   
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    String mFrom="";
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_video_view);
        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        ButterKnife.bind(SearchVideoViewActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedImgList.clear();
        init();

        mFrom=getIntent().getStringExtra("From");
//        //   Log.e("From:", mFrom + " !");
        if(!mFrom.equals("Vault")){
            mSearchedVideo = (BaseModel)getIntent().getSerializableExtra("Searched");
        }else{
            String path=getIntent().getStringExtra("Searched");
//            //   Log.e("From path:", path + " !");
            File file=new File(path);
            mSearchedVideo=new BaseModel();
            mSearchedVideo.setName(file.getName());
            mSearchedVideo.setPath(file.getPath());
            mSearchedVideo.setSize(file.length());
        }
        tv_imgName.setText(mSearchedVideo.getName());
       
        Uri uri = Uri.parse(mSearchedVideo.getPath());
        mVideo.setVideoURI(uri);

        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp){
                mVideo.start();
                if(mVideo.isPlaying()){
                    mPlay.setVisibility(View.GONE);
                    mPause.setVisibility(View.VISIBLE);
                }

                int duration =  mVideo.getDuration()/1000;
                seekbar.setMax(mVideo.getDuration());
//                //   Log.e("Duration max",String.valueOf(seekbar.getMax()));
                int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60) ;
                String formatted;
                if(hours==0){
                    formatted = String.format("%02d:%02d", minutes, seconds);
                }else{
                    formatted = String.format("%d:%02d:%02d",hours,minutes,seconds);
                }
                endTime.setText(formatted+"");
                timerCounter();
            }
        });


        imgBack.setOnClickListener(v -> onBackPressed());
        fileInfo();

        mPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
                mVideo.start();
            }
        });

        mPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
                mVideo.pause();

            }
        });


        mBack10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int current = mVideo.getCurrentPosition();
                int backTime=current-10000;
                mVideo.seekTo(backTime);
                timer.cancel();
                timerCounter();
            }
        });

        mForward10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int current = mVideo.getCurrentPosition();
                int backTime=current+10000;
                mVideo.seekTo(backTime);
                timer.cancel();
                timerCounter();
            }
        });


        mRotate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!isRotate){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                            isRotate=true;
                        }
                    });
                }else{
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            isRotate=false;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        }
                    });
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                int current_pos = seekBar.getProgress();
                mVideo.seekTo(current_pos);
            }
        });


        rootRL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                expand(mBottomRL);
                expand(mRotate);
                if(isRotate)
                expand(toolbar);
                bottomHandler();
            }
        });

        bottomHandler();
    }

    public void bottomHandler(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                collapse(mBottomRL);
                collapse(mRotate);
                if(isRotate){
                    collapse(toolbar);
                }
            }
        }, 3000);
    }

    private Timer timer;
    private void timerCounter(){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        };
        timer.schedule(task, 0, 100);
    }

    private void updateUI(){
        int current = mVideo.getCurrentPosition();
        seekbar.setProgress(current);

        int duration = current/1000;
        int hours = duration / 3600;
        int minutes = (duration / 60) - (hours * 60);
        int seconds = duration - (hours * 3600) - (minutes * 60) ;
        String formatted;
        if(hours==0){
            formatted = String.format("%02d:%02d", minutes, seconds);
        }else{
            formatted = String.format("%d:%02d:%02d",hours,minutes,seconds);
        }
        startTime.setText(formatted+"");
        if(startTime.getText().equals(endTime.getText())){
            mPlay.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.GONE);

        }
    }
    
    public void init(){
        toolbar=findViewById(R.id.rl_toolbar);
        mVideo = findViewById(R.id.mVideo);
        mRotate=findViewById(R.id.mRotate);
        mBack10=findViewById(R.id.mBack10);
        mPlay=findViewById(R.id.mPlay);
        mForward10=findViewById(R.id.mForward10);
        mPause=findViewById(R.id.mPause);
        startTime=findViewById(R.id.startTime);
        endTime=findViewById(R.id.endTime);
        seekbar=findViewById(R.id.seekBar);
        mBottomRL=findViewById(R.id.bootomRL);
        rootRL=findViewById(R.id.rl2);
        share=findViewById(R.id.imgShare);
        info=findViewById(R.id.opt5);

        share.setOnClickListener(this);
        info.setOnClickListener(this);

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
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.imgShare:{

                String path=mSearchedVideo.getPath();
                File file=new File(path);
                Uri contentUri = FileProvider.getUriForFile(SearchVideoViewActivity.this,getPackageName() + ".provider",file);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
            }
            break;
            case R.id.opt5:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            break;
        }
    }


    public void fileInfo(){
        File imgFile = new  File(mSearchedVideo.getPath());
        if(imgFile.exists()){
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(imgFile.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            proImage.setImageBitmap(thumb);
            proName.setText(imgFile.getName());
            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");

            String date = spf.format(lastModDate);

            proDate.setText(date);
            String s= Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(mSearchedVideo.getPath());
        }
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

// Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

// Expansion speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

// Collapse speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }


    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }if(isRotate){
            isRotate=false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            expand(toolbar);
        } else{
            super.onBackPressed();
        }
    }
}