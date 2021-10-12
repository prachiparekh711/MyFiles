package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity.viewPager;

public class APKListAdapter extends RecyclerView.Adapter<APKListAdapter.MyClassView> {

    List<BaseModel> images;
    Activity activity;
    SelectionInterface selectionInterface;

    public APKListAdapter(List<BaseModel> images,Activity activity,SelectionInterface anInterface) {
        this.images = images;
        this.activity = activity;

        this.selectionInterface=anInterface;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_apk_list, parent, false);
        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {
        BaseModel file = images.get(position);

        String extension = "";
        int j = file.getPath().lastIndexOf('.');
        if (j > 0) {
            extension = file.getPath().substring(j+1);
        }
        holder.tv_image_name.setText(file.getName() + "." + extension);

        holder.mImage.setClipToOutline(true);
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
                holder.mImage.setImageDrawable(icon);
            }
        }catch(Exception e){
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.apps))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.mImage);
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
                        Uri uri= FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName() + ".provider",new File(file.getPath()));
                        in.setData(uri);
                        activity.startActivity(in);
                    }
                }else{
                    Toast.makeText(activity.getBaseContext(),"APK Installation require Android Version 8 or Above.",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            BaseAppActivity.disableCard();

            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                if(Util.mSelectedApkList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedApkList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedApkList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedApkList.contains(images.get(i).getAppPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedApkList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseAppActivity.count.setText(Util.mSelectedApkList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                BaseAppActivity.disableCard();
                viewPager.setPagingEnabled(false  );
                BaseAppActivity.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
//                holder.mSelect.setVisibility(View.VISIBLE);
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

//        if(mSelectedApkList.contains(file)){
//            holder.mSelect.setVisibility(View.VISIBLE);
//            holder.mDeselect.setVisibility(View.GONE);
//        }
        for(int i = 0;i < Util.mSelectedApkList.size();i++){
            if(Util.mSelectedApkList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }

        if(BaseAppActivity.viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        TextView tv_image_name;
        ImageView mImage;
        ImageView mSelect,mDeselect;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            mImage = itemView.findViewById(R.id.imgAlbum);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
        }
    }

    public void changeHomeView(){
        BaseAppActivity.titleLL.setVisibility(View.GONE);
        BaseAppActivity.actionLL.setVisibility(View.VISIBLE);
        BaseAppActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseAppActivity.tabLayout.setVisibility(View.GONE);
        BaseAppActivity.share.setVisibility(View.VISIBLE);
        BaseAppActivity.view.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(BaseAppActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseAppActivity.count.setText(Util.mSelectedApkList.size() + " Selected");

    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true  );
        Util.mSelectedApkList.clear();
        BaseAppActivity.titleLL.setVisibility(View.VISIBLE);
        BaseAppActivity.actionLL.setVisibility(View.GONE);
        BaseAppActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseAppActivity.tabLayout.setVisibility(View.VISIBLE);
        BaseAppActivity.share.setVisibility(View.GONE);
        BaseAppActivity.view.setVisibility(View.VISIBLE);
        BaseAppActivity.viewClose=true;
        ImageViewCompat.setImageTintList(BaseAppActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        notifyDataSetChanged();

    }

}



