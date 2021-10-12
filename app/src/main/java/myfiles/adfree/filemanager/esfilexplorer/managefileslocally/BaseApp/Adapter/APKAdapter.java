package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.actionLL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.headerRL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.more;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.share;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.tabLayout;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.titleLL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.view;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.viewClose;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.viewPager;

public class APKAdapter extends RecyclerView.Adapter<APKAdapter.MyClassView>  {

    ArrayList<BaseModel> applicationModels;
    Activity activity;
    SelectionInterface selectionInterface;


    public APKAdapter(ArrayList<BaseModel> applicationModels, Activity activity, SelectionInterface selectionInterface) {
        this.applicationModels = applicationModels;
        this.activity = activity;
        this.selectionInterface=selectionInterface;

    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.app_apk_grid, null, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = (width / 3) ;
        }
        return new MyClassView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {
        BaseModel file = applicationModels.get(position);

        holder.tv_app_name.setText(file.getName());
        RequestOptions options = new RequestOptions();

        try{
            String APKFilePath = file.getPath();
            PackageInfo packageInfo = activity.getPackageManager()
                    .getPackageArchiveInfo(APKFilePath, PackageManager.GET_ACTIVITIES);
            if(packageInfo != null){
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                if(Build.VERSION.SDK_INT >= 8){
                    appInfo.sourceDir = APKFilePath;
                    appInfo.publicSourceDir = APKFilePath;
                }
                Drawable icon = appInfo.loadIcon(activity.getPackageManager());
                holder.img_app_icon.setImageDrawable(icon);
            }
        }catch(Exception e){
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.apps))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.img_app_icon);
        }

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }


        if(Util.clickEnable == true){

            holder.itemView.setOnClickListener(v -> {
                BaseAppActivity.disableCard();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!activity.getPackageManager().canRequestPackageInstalls()) {
                        activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", activity.getPackageName()))), 1234);
                    } else {
                        Intent in=new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        in.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        in.setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                        in.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true);
                        in.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                        in.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,activity.getApplicationInfo().packageName);
                        Uri uri=FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName() + ".provider",new File(file.getPath()));
                        in.setData(uri);
                        activity.startActivity(in);
                    }
                }else{
                    Toast.makeText(activity.getBaseContext(),"APK Installation require Android Version 8 or Above.",Toast.LENGTH_SHORT).show();
                }


                //Storage Permission

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }

                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
//                
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseAppActivity.disableCard();

                if(Util.mSelectedApkList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedApkList.remove(file.getPath());
                }else{
//                    Log.e("On click :", file.getPath());
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedApkList.add(file.getPath());
                }
                if(Util.mSelectedApkList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseAppActivity.count.setText(Util.mSelectedApkList.size() + " Selected");
                }

                //   Log.e("Selected List:",Util.mSelectedApkList.size() + "");
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                BaseAppActivity.disableCard();
                viewPager.setPagingEnabled(false  );
                viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
//                holder.mSelect.setVisibility(View.VISIBLE);
//                   Log.e("Long click:",file.getPath());
                Util.mSelectedApkList.add(file.getPath());
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        selectionInterface.onSelectItem();
                        changeHomeView();
                    }
                });
                return false;
            }
        });

        if(Util.showCircle){
            holder.mDeselect.setVisibility(View.VISIBLE);
        }else{
            holder.mDeselect.setVisibility(View.GONE);
        }

        for(int i = 0;i < Util.mSelectedApkList.size();i++){
            if(Util.mSelectedApkList.get(i).equals(file.getPath())){
//                Log.e("Add:",file.getPath());
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return applicationModels.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView img_app_icon, mDeselect, mSelect;
        TextView tv_app_name;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            img_app_icon = itemView.findViewById(R.id.mImage);
            tv_app_name = itemView.findViewById(R.id.tv_album);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            mSelect = itemView.findViewById(R.id.img_select);
        }
    }

    public void changeHomeView(){
        titleLL.setVisibility(View.GONE);
        actionLL.setVisibility(View.VISIBLE);
        headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        tabLayout.setVisibility(View.GONE);
        share.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseAppActivity.count.setText(Util.mSelectedApkList.size() + " Selected");
    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true);
        Util.mSelectedApkList.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        tabLayout.setVisibility(View.VISIBLE);
        share.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        notifyDataSetChanged();

    }

}


