package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Model.AppModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;


public class InstalledAppListAdapter extends RecyclerView.Adapter<InstalledAppListAdapter.MyClassView> {

    List<AppModel> images;
    Activity activity;
    SelectionInterface selectionInterface;

    public InstalledAppListAdapter(List<AppModel> images,Activity activity,SelectionInterface anInterface) {
        this.images = images;
        this.activity = activity;

        this.selectionInterface=anInterface;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_app_list, parent, false);
        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {
        AppModel file = images.get(position);

        String extension = "";
        int j = file.getAppPath().lastIndexOf('.');
        if (j > 0) {
            extension = file.getAppPath().substring(j+1);
        }
        holder.tv_image_name.setText(file.getAppName() );

        holder.mImage.setClipToOutline(true);

        Glide.with(activity)
                .load(file.getAppIcon())
                .into(holder.mImage);

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
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseAppActivity.disableCard();

                if(Util.mSelectedAppList.equals(file.getAppPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedAppList.remove(file.getAppPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedAppList.add(file.getAppPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedAppList.contains(images.get(i).getAppPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedAppList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseAppActivity.count.setText(Util.mSelectedAppList.size() + " Selected");
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

                BaseAppActivity.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedAppList.add(file.getAppPath());
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

//        if(mSelectedAppList.contains(file)){
//            holder.mSelect.setVisibility(View.VISIBLE);
//            holder.mDeselect.setVisibility(View.GONE);
//        }
        for(int i = 0;i < Util.mSelectedAppList.size();i++){
            if(Util.mSelectedAppList.get(i).equals(file.getAppPath())){
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
        BaseAppActivity.count.setText(Util.mSelectedAppList.size() + " Selected");

    }

    public void changeToOriginalView(){
        Util.mSelectedAppList.clear();
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


