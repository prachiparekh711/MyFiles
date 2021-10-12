package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.BaseMusicActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Fragment.AlbumFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Fragment.AllMusicFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class TabsBasePagerAdapter extends FragmentPagerAdapter{

    Context context;
    View v;

    public TabsBasePagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    public View getTabView(int position){

        v = LayoutInflater.from(context).inflate(R.layout.header_tab,null);
        TextView img_tab = v.findViewById(R.id.img_tab);

        if(position == 0){
            if(BaseMusicActivity.actionLL.getVisibility()==View.VISIBLE){
                img_tab.setTextColor(context.getResources().getColor(R.color.white));
            }else{
                img_tab.setTextColor(context.getResources().getColor(R.color.tabtextColor));
            }
            img_tab.setText("All");
        }
        else{
            if(BaseMusicActivity.actionLL.getVisibility()==View.VISIBLE){
                img_tab.setTextColor(context.getResources().getColor(R.color.white));
            }else{
                img_tab.setTextColor(context.getResources().getColor(R.color.tabtextColor));
            }
            img_tab.setText("Albums");
        }
        return v;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        if(position==0){
            return "All";
        }else{
            return "Albums";
        }
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){

            return new AllMusicFragment();
        }
        else{
            return new AlbumFragment();
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


