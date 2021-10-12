package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter.InstalledAppAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter.InstalledAppListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Model.AppModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class InstalledFragment extends Fragment{
    public static ArrayList<AppModel> applicationModels=new ArrayList<>();
    public RecyclerView rvImages;

    public static InstalledAppAdapter albumAdapter;
    public static InstalledAppListAdapter albumListAdapter;
    SelectionInterface selectionInterface;
    private MyReceiver myReceiver;

    public InstalledFragment(){
        // Required empty public constructor
    }


    public static InstalledFragment newInstance(String param1,String param2){
        InstalledFragment fragment = new InstalledFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(myReceiver,
                new IntentFilter("TAG_REFRESH"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_installed,container,false);

        ButterKnife.bind(this,view);
        rvImages = view.findViewById(R.id.rvImages);

        applicationModels.clear();
        getApplications();

        selectionInterface=new SelectionInterface(){
            @Override
            public void onSelectItem(){
                if(albumAdapter!=null)
                    albumAdapter.notifyDataSetChanged();
                if(albumListAdapter!=null)
                    albumListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeselectDate(int position){

            }

            @Override
            public void onSelectDate(int position){

            }
        };

        setAdapter();
        return view;

    }

    public void setAdapter(){
        albumAdapter = new InstalledAppAdapter(applicationModels, getActivity(),selectionInterface);
        albumListAdapter = new InstalledAppListAdapter(applicationModels, getActivity(),selectionInterface);

        if (Util.VIEW_TYPE=="Grid") {
            rvImages.setLayoutManager(new GridLayoutManager(getContext(),3));
            rvImages.setLayoutAnimation(null);

            rvImages.setAdapter(albumAdapter);
        } else {
            rvImages.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
            rvImages.setLayoutAnimation(null);
            rvImages.setAdapter(albumListAdapter);
        }
    }

    public void onResume() {
        super.onResume();
        if(Util.IsUpdate){
            Util.IsUpdate=false;
            applicationModels.clear();
            getApplications();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setAdapter();
                }
            });

        }else {
            if (Util.VIEW_TYPE == "Grid") {
                rvImages.setLayoutManager(new GridLayoutManager(getContext(), 3));
                rvImages.setLayoutAnimation(null);

                rvImages.setAdapter(albumAdapter);
                albumAdapter.notifyDataSetChanged();
            } else {
                rvImages.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                rvImages.setLayoutAnimation(null);
                rvImages.setAdapter(albumListAdapter);
                albumListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(myReceiver);
    }

    public void getApplications() {
        applicationModels.clear();
        // Get installed APK List and it's logo.
        final PackageManager pm = getActivity().getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if ((packageInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
            } else {
                // It is installed by the user
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                }

                try {
                    Drawable icon = getActivity().getPackageManager().getApplicationIcon(packageInfo.packageName);
                    Bitmap bitmap = getBitmapFromDrawable(icon);
                    String appName = "";
                    PackageManager packageManagers= getActivity().getApplicationContext().getPackageManager();
                    try {
                        appName = (String) packageManagers.getApplicationLabel(packageManagers.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    AppModel applicationModel = new AppModel();
                    applicationModel.setAppIcon(bitmap);
                    applicationModel.setAppName(appName);
                    applicationModel.setAppPath(packageInfo.sourceDir);
                    applicationModel.setPackageName(packageInfo.packageName);
                    applicationModels.add(applicationModel);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    static private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }


    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            onResume();
        }
    }

}