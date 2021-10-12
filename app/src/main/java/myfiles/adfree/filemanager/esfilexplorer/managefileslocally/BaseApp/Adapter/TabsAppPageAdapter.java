package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Fragment.APKFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Fragment.InstalledFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class TabsAppPageAdapter extends FragmentPagerAdapter{

    Context context;
    View v;

    public TabsAppPageAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;

    }

    public View getTabView(int position){

        v = LayoutInflater.from(context).inflate(R.layout.header_tab,null);
        TextView img_tab = v.findViewById(R.id.img_tab);

        if(position == 0){
            if(BaseAppActivity.actionLL.getVisibility()==View.VISIBLE){
                img_tab.setTextColor(context.getResources().getColor(R.color.white));
            }else{
                img_tab.setTextColor(context.getResources().getColor(R.color.tabtextColor));
            }
            img_tab.setText("Installed Apps");
        }
        else{
            if(BaseAppActivity.actionLL.getVisibility()==View.VISIBLE){
                img_tab.setTextColor(context.getResources().getColor(R.color.white));
            }else{
                img_tab.setTextColor(context.getResources().getColor(R.color.tabtextColor));
            }
            img_tab.setText("APK Files");
        }
        return v;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position){
        if(position==0){
            return "Installed Apps";
        }else{
            return "APK Files";
        }
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){

            return new InstalledFragment();
        }
        else{
            return new APKFragment();
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


