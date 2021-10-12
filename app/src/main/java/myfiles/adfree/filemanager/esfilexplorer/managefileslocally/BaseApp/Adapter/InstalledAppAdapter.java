package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Model.AppModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

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

public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.MyClassView>  {

    ArrayList<AppModel> applicationModels;
    Activity activity;
    SelectionInterface selectionInterface;

    public InstalledAppAdapter(ArrayList<AppModel> applicationModels, Activity activity, SelectionInterface selectionInterface) {
        this.applicationModels = applicationModels;
        this.activity = activity;
        this.selectionInterface=selectionInterface;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.app_install_grid, null, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = (width / 3) ;
        }
        return new MyClassView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {
        AppModel file = applicationModels.get(position);

//        //   Log.e("Called"," ....." + file.getAppName());

        Glide.with(activity)
                .load(file.getAppIcon())
                .into(holder.img_app_icon);

        holder.tv_app_name.setText(file.getAppName());

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
                BaseAppActivity.disableCard();

                Intent in = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                in.addCategory(Intent.CATEGORY_DEFAULT);
                in.setData(Uri.parse("package:" + file.getPackageName()));
                activity.startActivity(in);
//                
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseAppActivity.disableCard();

                if(Util.mSelectedAppList.equals(file.getAppPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedAppList.remove(file.getAppPath());
                    Util.mSelectedAppPackage.remove(file.getPackageName());
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedAppList.add(file.getAppPath());
                    Util.mSelectedAppPackage.add(file.getPackageName());
                }
                if(Util.mSelectedAppList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseAppActivity.count.setText(Util.mSelectedAppList.size() + " Selected");
                }

                //   Log.e("Selected List:",Util.mSelectedAppList.size() + "");
                //   Log.e("click:",file.getPackageName());
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
                holder.mSelect.setVisibility(View.VISIBLE);
                //   Log.e("Long click:",file.getPackageName());
                Util.mSelectedAppList.add(file.getAppPath());
                Util.mSelectedAppPackage.add(file.getPackageName());
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

        for(int i = 0;i < Util.mSelectedAppList.size();i++){
            if(Util.mSelectedAppList.get(i).equals(file.getAppPath())){
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
        BaseAppActivity.count.setText(Util.mSelectedAppList.size() + " Selected");
    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true  );
        Util.mSelectedAppList.clear();
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

