package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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

public class SearchMusicViewActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener{


    BaseModel mSearchedMusic;

    public static TextView mName;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;

    ImageView mshare,info;

    public static RelativeLayout toolbar,rootRL;
    int duration=0;
    ImageView mBack10,mPlay,mForward10,mPause;
    TextView startTime,endTime;
    SeekBar seekbar;
    public static RelativeLayout mBottomRL;
    @BindView(R.id.tv_imgName)
    TextView tv_imgName;
    public static MediaPlayer mp=new MediaPlayer();
    String mFrom="";

    @Override
    public void onBackPressed() {
        if(mp!=null && mp.isPlaying()){
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        } if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }else{
            finish();
//            super.onBackPressed();
        }
    }


    public void ChangeMusic(){

        if(mp!=null){
            if(mp.isPlaying()){
                mp.stop();
                mp.reset();
                mp.release();
                mp = null;
                mp = new MediaPlayer();
            }
        }
        else{
            mp = new MediaPlayer();
        }
        try{
            mp.setDataSource(getBaseContext(),Uri.parse(mSearchedMusic.getPath()));
            mp.prepare();
            duration = mp.getDuration() / 1000;
//                //   Log.e("Player duration ",duration + " ");
        }catch(IOException e){
            e.printStackTrace();
        }
        mp.start();
        if(mp.isPlaying()){
            mPlay.setVisibility(View.GONE);
            mPause.setVisibility(View.VISIBLE);
        }

        seekbar.setMax(duration);
//            //   Log.e("Player duration ",seekbar.getMax() + " ");
        int hours = duration / 3600;
        int minutes = (duration / 60) - (hours * 60);
        int seconds = duration - (hours * 3600) - (minutes * 60);
        String formatted;
        if(hours == 0){
            formatted = String.format("%02d:%02d",minutes,seconds);
        }else{
            formatted = String.format("%d:%02d:%02d",hours,minutes,seconds);
        }
        endTime.setText(formatted + "");

        hdlr.postDelayed(UpdateSongTime, 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music_view);
        ButterKnife.bind(SearchMusicViewActivity.this);
        Util.showCircle=false;
        Util.clickEnable=true;
        Util.mSelectedImgList.clear();
        init();

        mFrom=getIntent().getStringExtra("From");
//        //   Log.e("From:", mFrom + " !");
        if(!mFrom.equals("Vault")){
            mSearchedMusic = (BaseModel)getIntent().getSerializableExtra("Searched");
        }else{
            String path=getIntent().getStringExtra("Searched");
//            //   Log.e("From path:", path + " !");
            File file=new File(path);
            mSearchedMusic=new BaseModel();
            mSearchedMusic.setName(file.getName());
            mSearchedMusic.setPath(file.getPath());
            mSearchedMusic.setSize(file.length());
        }
        String path=mSearchedMusic.getPath();
        tv_imgName.setText(path.substring(path.lastIndexOf("/") + 1));
        ChangeMusic();
        fileInfo();
        bottomHandler();
    }


    public void init(){
        Util.isBottomPlayerExpanded=true;
        ButterKnife.bind(SearchMusicViewActivity.this);
        mName=findViewById(R.id.tv_imgName);

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
        info=findViewById(R.id.opt5);
        mshare.setOnClickListener(this);
        info.setOnClickListener(this);

        toolbar=findViewById(R.id.rl_toolbar);
        rootRL=findViewById(R.id.rootRL);
        rootRL.setOnTouchListener(this);

        mBack10=findViewById(R.id.mBack10);
        mPlay=findViewById(R.id.mPlay);
        mForward10=findViewById(R.id.mForward10);
        mPause=findViewById(R.id.mPause);
        startTime=findViewById(R.id.startTime);
        endTime=findViewById(R.id.endTime);
        seekbar=findViewById(R.id.seekBar);
        mBottomRL=findViewById(R.id.bootomRL);

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
                //   Log.e("Player stop on  ",current_pos+ " ");
                mp.seekTo(current_pos * 1000);
            }
        });

        mBack10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int current = mp.getCurrentPosition();
                int backTime=current-10000;
                mp.seekTo(backTime);
                hdlr.postDelayed(UpdateSongTime, 100);
            }
        });

        mForward10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int current = mp.getCurrentPosition();
                int backTime=current+10000;
                mp.seekTo(backTime);
                hdlr.postDelayed(UpdateSongTime, 100);
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mPlay.setVisibility(View.GONE);
                mPause.setVisibility(View.VISIBLE);
                mp.start();
                //   Log.e("Current:",String.valueOf(mp.getCurrentPosition()));
            }
        });

        mPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mPlay.setVisibility(View.VISIBLE);
                mPause.setVisibility(View.GONE);
                mp.pause();
                //   Log.e("Current:",String.valueOf(mp.getCurrentPosition() / 1000));
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {           
            case R.id.opt5:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.imgShare:{

                String path=mSearchedMusic.getPath();
                File file=new File(path);
                Uri contentUri = FileProvider.getUriForFile(SearchMusicViewActivity.this,getPackageName() + ".provider",file);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,contentUri);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
            }
            break;
           
        }
    }   

    public static void bottomHandler(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                collapse(mBottomRL);
            }
        }, 4000);
    }

    Handler hdlr = new Handler();
    private final Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            if(mp!=null){
                int current = mp.getCurrentPosition() / 1000;
                seekbar.setProgress(current);

                int duration = current;
                int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60);
                String formatted;
                if(hours == 0){
                    formatted = String.format("%02d:%02d",minutes,seconds);
                }else{
                    formatted = String.format("%d:%02d:%02d",hours,minutes,seconds);
                }
                startTime.setText(formatted + "");
                if(startTime.getText().equals(endTime.getText())){
                    mPlay.setVisibility(View.VISIBLE);
                    mPause.setVisibility(View.GONE);
                }

                hdlr.postDelayed(this,100);
            }
        }
    };

    public void fileInfo(){
        File imgFile = new  File(mSearchedMusic.getPath());
        if(imgFile.exists()){


            String path=mSearchedMusic.getPath();

            proName.setText(path.substring(path.lastIndexOf("/") + 1));
            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");

            String date = spf.format(lastModDate);

            proDate.setText(date);
            String s= Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(path);
        }
    }

    public static void expand(final View v) {
        Util.isBottomPlayerExpanded=true;
//            //   Log.e("isBottomPlayerExpanded",String.valueOf(isBottomPlayerExpanded));
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View)v.getParent()).getWidth(),View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec,wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

// Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation(){
            @Override
            protected void applyTransformation(float interpolatedTime,Transformation t){
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds(){
                return true;
            }
        };

// Expansion speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);

    }

    public static void collapse(final View v) {
        Util.isBottomPlayerExpanded=false;
//        //   Log.e("isBottomPlayerExpanded",String.valueOf(isBottomPlayerExpanded));
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
    public boolean onTouch(View v,MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if(Util.isBottomPlayerExpanded){
                    collapse(mBottomRL);
            }else{
                expand(mBottomRL);
                bottomHandler();
            }
        }
        return false;
    }
}