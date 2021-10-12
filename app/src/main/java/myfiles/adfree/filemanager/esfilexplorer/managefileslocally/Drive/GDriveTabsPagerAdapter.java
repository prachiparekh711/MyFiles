package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Drive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

public class GDriveTabsPagerAdapter extends FragmentPagerAdapter{

    private final String[] tabTitles = new String[]{"All", "Images", "Videos", "Audios", "Documents", "Other"};
    Context context;

    public GDriveTabsPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    public View getTabView(int position) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v;
        v = LayoutInflater.from(context).inflate(R.layout.header_tab, null);
        TextView tv = v.findViewById(R.id.img_tab);
        tv.setText(tabTitles[position]);


            tv.setTextColor(context.getResources().getColor(R.color.tabtextColor));


        return v;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new AllGFragment(); //ChildFragment1 at position 0
            case 1:
                return new ImageGFragment(); //ChildFragment2 at position 1
            case 2:
                return new VideoGFragment();
            case 3:
                return new AudioGFragment();
            case 4:
                return new DocumentGFragment();
            case 5:
                return new OtherGFragment();
        }

        return null;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 6;
    }
}
