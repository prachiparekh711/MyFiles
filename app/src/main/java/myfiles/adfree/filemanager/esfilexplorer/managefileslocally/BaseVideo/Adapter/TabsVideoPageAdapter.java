package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Fragment.AllVideoFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Fragment.VideoFolderFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class TabsVideoPageAdapter extends FragmentPagerAdapter{

    Context context;
    View v;

    public TabsVideoPageAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;

    }

    public View getTabView(int position){

        v = LayoutInflater.from(context).inflate(R.layout.header_tab,null);
        TextView img_tab = v.findViewById(R.id.img_tab);

        if(position == 0){
            img_tab.setTextColor(context.getResources().getColor(R.color.tabtextColor));
            img_tab.setText("All");
        }
        else{
            img_tab.setTextColor(context.getResources().getColor(R.color.tabtextColor));
            img_tab.setText("Folder");
        }
        return v;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        if(position==0){
            return "All";
        }else{
            return "Folder";
        }
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){

            return new AllVideoFragment();
        }
        else{
            return new VideoFolderFragment();
        }

    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
    }
}


