package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro.CleanFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro.HomeFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro.ToolFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;

public class TabsAdapter extends FragmentPagerAdapter{
    private final ArrayList<Fragment> mData = new ArrayList<>();
    private final Context mContext;

    public TabsAdapter(Context context,FragmentManager fragmentManager){
        super(fragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        mContext = context;
        initData();
    }


    private void initData(){

        mData.add(new HomeFragment());
        mData.add(new CleanFragment());
        mData.add(new ToolFragment());


    }

    @Override
    public int getCount(){
        return 3;
    }

    @NonNull
    @Override
    public Fragment getItem(int position){
        return mData.get(position);
    }

    public View getTabView(int position){
        View v;
        v = LayoutInflater.from(mContext).inflate(R.layout.custom_tab,null);
        TextView tv = v.findViewById(R.id.tabIcon);

        if(position == 0){
            tv.setText("Home");
        }
        if(position == 1){
            tv.setText("Clean");
        }
        if(position == 2){
            tv.setText("Tools");
        }

        return v;
    }


    @Override
    public CharSequence getPageTitle(int position){
        String title = null;
        if (position == 0)
        {
            title = "Home";
        }
        else if (position == 1)
        {
            title = "Cache";
        }
        else if (position == 2)
        {
            title = "Tools";
        }
        return title;
    }
}

