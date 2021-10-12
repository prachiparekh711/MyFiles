package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter.WelcomeScreenAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro.FirstFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro.SecondFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro.ThirdFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
   ImageView nextBtn;


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
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
//       
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(MainActivity.this);
        nextBtn=findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);

        if (SharedPreference.getLogin(MainActivity.this)) {
            fireAnalytics("Login", "Old");
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            intent.putExtra("Login","old");
            startActivity(intent);
        }else{

            nextBtn.setVisibility(View.VISIBLE);
            setViewPager();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_btn:
                if(viewPager.getCurrentItem()==0)
                {
                    viewPager.setCurrentItem(1);
                    break;
                }else if(viewPager.getCurrentItem()==1){
                    viewPager.setCurrentItem(2);
                    break;
                }else if(viewPager.getCurrentItem()==2){
                    fireAnalytics("Login", "New");
//                    SharedPreference.setLogin(MainActivity.this,true);
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("Login","new");
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private void setViewPager() {
        WelcomeScreenAdapter adapter = new WelcomeScreenAdapter(getSupportFragmentManager());
        adapter.addFragment(new FirstFragment(), "FIRST_FRAGMENT");
        adapter.addFragment(new SecondFragment(), "SECOND_FRAGMENT");
        adapter.addFragment(new ThirdFragment(), "THIRD_FRAGMENT");
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


}