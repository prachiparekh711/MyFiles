package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.TabsAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity{

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    TabsAdapter tabsAdapter;



    @Override
    public void permissionGranted(){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);


            tabsAdapter = new TabsAdapter(HomeActivity.this,getSupportFragmentManager());

            mViewPager.setAdapter(tabsAdapter);
            mViewPager.setOffscreenPageLimit(3);
            mTabLayout.setupWithViewPager(mViewPager);
            for(int i = 0;i < mTabLayout.getTabCount();i++){
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                Objects.requireNonNull(tab).setCustomView(tabsAdapter.getTabView(i));
            }
            Objects.requireNonNull(mTabLayout.getTabAt(0)).select();
            mTabLayout.setTabIndicatorFullWidth(false);
            TabLayout.Tab tab = mTabLayout.getTabAt(0);
            mTabLayout.selectTab(tab);
            TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tabIcon);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setBackground(getResources().getDrawable(R.drawable.ic_blue_tab));
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                @Override
                public void onTabSelected(TabLayout.Tab tab){

                    TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tabIcon);
                    if(tab.getPosition() == 0){
                        tv.setText("Home");
                    }
                    if(tab.getPosition() == 1){
                        tv.setText("Clean");
                    }
                    if(tab.getPosition() == 2){
                        tv.setText("Tools");
                    }
                    tv.setTextColor(getResources().getColor(R.color.white));
                    tv.setBackground(getResources().getDrawable(R.drawable.ic_blue_tab));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab){
                    TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tabIcon);
                    if(tab.getPosition() == 0){
                        tv.setText("Home");

                    }
                    if(tab.getPosition() == 1){
                        tv.setText("Clean");
                    }
                    if(tab.getPosition() == 2){
                        tv.setText("Tools");
                    }
                    tv.setTextColor(getResources().getColor(R.color.tabtextColor));
                    tv.setBackgroundColor(getResources().getColor(R.color.transparent));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab){

                }
            });


    }



    @Override
    public void onBackPressed(){
        Util.mDuplicateDelete=false;
        Util.mLargeDelete=false;
        Util.mWImgDelete=false;
        Util.mWVideoDelete=false;
        if(mTabLayout.getSelectedTabPosition() == 0 || mTabLayout.getSelectedTabPosition() == 2){
            mTabLayout.selectTab(mTabLayout.getTabAt(1));
        }else{
            finishAffinity();
        }
    }
}