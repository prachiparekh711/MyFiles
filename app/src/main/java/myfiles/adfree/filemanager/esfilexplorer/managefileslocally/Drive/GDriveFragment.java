package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Drive;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GDriveFragment extends Fragment{

    @BindView(R.id.tab_download)
    TabLayout tab_download;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    ImageView mBack;

    GDriveTabsPagerAdapter tabsPagerAdapter;

    public GDriveFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_g_drive,container,false);
        ButterKnife.bind(this, view);

        mBack=view.findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        tabsPagerAdapter = new GDriveTabsPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        viewPager.setAdapter(tabsPagerAdapter);

        tab_download.setupWithViewPager(viewPager);

        for (int i = 0; i < tab_download.getTabCount(); i++) {
            TabLayout.Tab tab = tab_download.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
        }

        Objects.requireNonNull(tab_download.getTabAt(0)).select();
        tab_download.setTabIndicatorFullWidth(false);
        TabLayout.Tab tab=tab_download.getTabAt(0);
        tab_download.selectTab(tab);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && viewPager.getCurrentItem() == 1) {
                Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();

                return true;
            } else
                return false;

        });

        tab_download.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
                TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.img_tab);
                tv.setTextColor(getResources().getColor(R.color.themeColor));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.img_tab);
                tv.setTextColor(getResources().getColor(R.color.tabtextColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}
