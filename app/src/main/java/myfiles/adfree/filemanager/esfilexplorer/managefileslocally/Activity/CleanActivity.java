package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.RecentAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

public class CleanActivity extends BaseActivity{

    @Override
    public void permissionGranted(){

    }



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);

        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(CleanActivity.this);
        CalculateJunk();
        setAdapter();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Util.DuplicateMainCall();
//                setAdapter();
            }
        }, 3000);
    }


    int junkSize=0;
    ImageView junkImg,no_junk;
    TextView txtJunkSize,txtJunkType,mDuplicateSize,mDupRealease,mLargeSize,mLargeRealease,mWImgSize,mWImgRealease,mWVidSize,mWVidRealease;

    RecyclerView mDuplicateRec,mLargeRec,mWImgRec,mWVidRec;
    RecentAdapter recentAdapter;
    CardView moreDupCard,moreLargeCard,moreWImgCard,moreWVidCard;
    RelativeLayout mCleanLayer1,mCleanLayer2,mCleanLayer3,mCleanLayer4,mCleanLayer5,mainRL;
    RelativeLayout rl_progress;
    AVLoadingIndicatorView avi;
    final Handler handler = new Handler(Looper.getMainLooper());
    ImageView mBack;
    public static long total=0;

    @Override
    public void onResume(){
        super.onResume();

        if(Util.mDuplicateDelete){
            total-=Util.mDupRealeaseSize/2;
            mCleanLayer2.setVisibility(View.GONE);
        }else{
            if(!Util.mFinalList.isEmpty())
             mCleanLayer2.setVisibility(View.VISIBLE);
        }
        if(Util.mLargeDelete){
            total-=Util.mLargeRealeaseSize;
            mCleanLayer3.setVisibility(View.GONE);
        }else{
            if(!Util.mLargeFinalList.isEmpty())
            mCleanLayer3.setVisibility(View.VISIBLE);
        }
        if(Util.mWImgDelete){
            total-=Util.mWImgRelease;
            mCleanLayer4.setVisibility(View.GONE);
        }else{
            if(!Util.mWhatsappImgList.isEmpty())
                 mCleanLayer4.setVisibility(View.VISIBLE);
        }
        if(Util.mWVideoDelete){
            total-=Util.mWVidRelease;
            mCleanLayer5.setVisibility(View.GONE);
        }else{
            if(!Util.mWhatsappVideoList.isEmpty())
            mCleanLayer5.setVisibility(View.VISIBLE);
        }

        if(mCleanLayer2.getVisibility()==View.GONE && mCleanLayer3.getVisibility()==View.GONE && mCleanLayer4.getVisibility()==View.GONE && mCleanLayer5.getVisibility()==View.GONE ){
            mainRL.setVisibility(View.GONE);
            no_junk.setVisibility(View.VISIBLE);
        }else{
            mainRL.setVisibility(View.VISIBLE);
            no_junk.setVisibility(View.GONE);
        }

        if(total>1024) {
            String s = Util.getSize(total);
            String junkData = s.substring(0, s.lastIndexOf(" ") + 1);
            String junkType = s.substring(s.lastIndexOf(" ") + 1);
            txtJunkSize.setText(junkData);
            txtJunkType.setText(junkType);
        }else{
            txtJunkSize.setText("0");
            txtJunkType.setText("Bytes");
        }
    }

    public  void startAnim() {
        //   Log.e("start anim","!!!");
        rl_progress.setVisibility(View.VISIBLE);
        avi.show();
        // or avi.smoothToShow();
    }

    public  void stopAnim() {
        //   Log.e("stop   anim","!!!");
        rl_progress.setVisibility(View.GONE);
        avi.hide();
        // or avi.smoothToHide();
    }

    public void init(){

        rl_progress=findViewById(R.id.rl_progress);
        avi=findViewById(R.id.avi);
        junkImg=findViewById(R.id.img1);
        txtJunkSize=findViewById(R.id.junkSize);
        txtJunkType=findViewById(R.id.junkType);

        mCleanLayer1=findViewById(R.id.cleanLayer1);
        mCleanLayer2=findViewById(R.id.cleanLayer2);
        mCleanLayer3=findViewById(R.id.cleanLayer3);
        mCleanLayer4=findViewById(R.id.cleanLayer4);
        mCleanLayer5=findViewById(R.id.cleanLayer5);

        mDuplicateRec=findViewById(R.id.duplicateRec);
        mLargeRec=findViewById(R.id.largeRec);
        mWImgRec=findViewById(R.id.wImgRec);
        mWVidRec=findViewById(R.id.wVidRec);

        mDuplicateSize=findViewById(R.id.mDupSize);
        mLargeSize=findViewById(R.id.mLargeSize);
        mWImgSize=findViewById(R.id.mWImgSize);
        mWVidSize=findViewById(R.id.mWVidSize);

        moreDupCard=findViewById(R.id.moreDuplicate);
        moreLargeCard=findViewById(R.id.moreLarge);
        moreWImgCard=findViewById(R.id.moreWImg);
        moreWVidCard=findViewById(R.id.moreWVid);

        mDupRealease=findViewById(R.id.mDupRealease);
        mLargeRealease=findViewById(R.id.mlargeRealease);
        mWImgRealease=findViewById(R.id.mWImgRealease);
        mWVidRealease=findViewById(R.id.mWVidRealease);

        mCleanLayer2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                fireAnalytics("Clean", "Duplicate");
                Intent intent = new Intent(CleanActivity.this,DuplicateActivity.class);
                startActivity(intent);
            }
        });

        mCleanLayer3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fireAnalytics("Clean", "Large");
                Intent intent = new Intent(CleanActivity.this,LargeActivity.class);
                intent.putExtra("From","Large");
                startActivity(intent);
            }
        });

        mCleanLayer4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fireAnalytics("Clean", "Whatsapp Image");
                Intent intent = new Intent(CleanActivity.this,LargeActivity.class);
                intent.putExtra("From","WImage");
                startActivity(intent);
            }
        });

        mCleanLayer5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                fireAnalytics("Clean", "Whatsapp Video");
                Intent intent = new Intent(CleanActivity.this,LargeActivity.class);
                intent.putExtra("From","WVideo");
                startActivity(intent);
            }
        });

        mainRL=findViewById(R.id.mainRL);
        no_junk=findViewById(R.id.no_junk);
        mBack=findViewById(R.id.mBack);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void CalculateJunk(){
        File[] files = getBaseContext().getCacheDir().listFiles();
        for(int i=0;i<files.length;i++){
            junkSize+=files[i].length();
        }
        String s= Util.getSize(junkSize);
        //   Log.e("Junk size:", s + "");
        Glide.with(getBaseContext()).load("file:///android_asset/junk.gif").into(junkImg);

        String junkData=s.substring(0,s.lastIndexOf(" ") + 1);
        String junkType=s.substring(s.lastIndexOf(" ") + 1);

//        txtJunkSize.setText(junkData);
//        txtJunkType.setText(junkType);
    }

    public void setAdapter(){

        if(Util.mFinalList.size()>0){
            mCleanLayer2.setVisibility(View.VISIBLE);

            mDuplicateRec.setLayoutManager(new GridLayoutManager(getBaseContext(),4));
            mDuplicateRec.setNestedScrollingEnabled(false);

            recentAdapter = new RecentAdapter("Duplicate", Util.mFinalList,CleanActivity.this,(view1, position) -> {
            });
            mDuplicateRec.setAdapter(recentAdapter);

            if(Util.mFinalList.size()>4){
                mDuplicateSize.setText("+" + (Util.mFinalList.size() - 4));
                moreDupCard.setVisibility(View.VISIBLE);
            }else{
                moreDupCard.setVisibility(View.GONE);
            }
            total+=Util.mDupRealeaseSize/2;
            mDupRealease.setText("Releasable Storage " + Util.getSize(Util.mDupRealeaseSize/2));
        }else{
            mCleanLayer2.setVisibility(View.GONE);
        }

        if(Util.mLargeFinalList.size()>0){
            mCleanLayer3.setVisibility(View.VISIBLE);
            mLargeRec.setLayoutManager(new GridLayoutManager(getBaseContext(),4));
            mLargeRec.setNestedScrollingEnabled(false);

            recentAdapter = new RecentAdapter("Large", Util.mLargeFinalList,CleanActivity.this,(view1, position) -> {
            });
            mLargeRec.setAdapter(recentAdapter);

            if(Util.mLargeFinalList.size()>4){
                mLargeSize.setText("+" + (Util.mLargeFinalList.size() - 4));
                moreLargeCard.setVisibility(View.VISIBLE);
            }else{
                moreLargeCard.setVisibility(View.GONE);
            }

            total+=Util.mLargeRealeaseSize;
            mLargeRealease.setText("Releasable Storage " + Util.getSize(Util.mLargeRealeaseSize));
        }else{
            mCleanLayer3.setVisibility(View.GONE);
        }

//        Log.e("W Image:",String.valueOf(mWhatsappImgList.size()));
        if(Util.mWhatsappImgList.isEmpty()){
            mCleanLayer4.setVisibility(View.GONE);
        }else{

            mCleanLayer4.setVisibility(View.VISIBLE);
            mWImgRec.setLayoutManager(new GridLayoutManager(getBaseContext(),4));
            mWImgRec.setNestedScrollingEnabled(false);

            recentAdapter = new RecentAdapter("WImage", Util.mWhatsappImgList,CleanActivity.this,(view1, position) -> {
            });
            mWImgRec.setAdapter(recentAdapter);

            if(Util.mWhatsappImgList.size()>4){
                mWImgSize.setText("+" + (Util.mWhatsappImgList.size() - 4));
                moreWImgCard.setVisibility(View.VISIBLE);
            }else{
                moreWImgCard.setVisibility(View.GONE);
            }
            total+=Util.mWImgRelease;
            mWImgRealease.setText("Releasable Storage " + Util.getSize(Util.mWImgRelease));
        }

        if(Util.mWhatsappVideoList.isEmpty()){

            mCleanLayer5.setVisibility(View.GONE);
        }else{

            mCleanLayer5.setVisibility(View.VISIBLE);
            mWVidRec.setLayoutManager(new GridLayoutManager(getBaseContext(),4));
            mWVidRec.setNestedScrollingEnabled(false);

            recentAdapter = new RecentAdapter("WVideo", Util.mWhatsappVideoList,CleanActivity.this,(view1, position) -> {
            });
            mWVidRec.setAdapter(recentAdapter);

            if(Util.mWhatsappVideoList.size()>4){
                mWVidSize.setText("+" + (Util.mWhatsappVideoList.size() - 4));
                moreWVidCard.setVisibility(View.VISIBLE);
            }else{
                moreWVidCard.setVisibility(View.GONE);
            }
            total+=Util.mWVidRelease;
            mWVidRealease.setText("Releasable Storage " + Util.getSize(Util.mWVidRelease));
        }

        handler.removeCallbacksAndMessages(null);

        if(total>1024) {
            String s = Util.getSize(total);
            String junkData = s.substring(0, s.lastIndexOf(" ") + 1);
            String junkType = s.substring(s.lastIndexOf(" ") + 1);
            txtJunkSize.setText(junkData);
            txtJunkType.setText(junkType);
        }else{
            txtJunkSize.setText("0");
            txtJunkType.setText("Bytes");
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

}