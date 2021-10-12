package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.DuplicateActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;


public class DuplicateChildAdapter extends RecyclerView.Adapter<DuplicateChildAdapter.MyClassView> {

    List<BaseModel> ChildItemList;
    Activity activity;

    public DuplicateChildAdapter(List<BaseModel> ChildItemList,Activity activity) {
        this.ChildItemList = ChildItemList;
        this.activity = activity;

    }

    @NonNull
    @Override
    public DuplicateChildAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.child_item_duplicate, parent, false);

        return new DuplicateChildAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull DuplicateChildAdapter.MyClassView holder,int position) {

        BaseModel file = ChildItemList.get(position);

        if(DuplicateActivity.select){
            if(position == 0){
                holder.mDeselect.setVisibility(View.VISIBLE);
                holder.mSelect.setVisibility(View.GONE);
            }else{
                holder.mDeselect.setVisibility(View.GONE);
                holder.mSelect.setVisibility(View.VISIBLE);
            }

        }else{
            holder.mDeselect.setVisibility(View.VISIBLE);
            holder.mSelect.setVisibility(View.GONE);
        }

        RequestOptions options = new RequestOptions();
        holder.mImage.setClipToOutline(true);

        holder.mSize.setText(Util.getSize(file.getSize()));
        holder.mFolder.setText(file.getBucketName());
        if (file.getPath().endsWith(".docx") || file.getPath().endsWith(".doc")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_word))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".xls")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_excel))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".pdf")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_pdf))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".ppt") || file.getPath().endsWith(".pptx")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_powerpoint))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".png") || file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".jpeg")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".zip")) {

            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.zip_btn))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".mp3")) {

            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.music_icon))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".apk")) {

            holder.player.setVisibility(View.GONE);
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
        }else if (file.getPath().toLowerCase().endsWith(".txt") || file.getPath().toLowerCase().endsWith(".rtf")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_other))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".mp4") ) {
            holder.player.setVisibility(View.VISIBLE);

            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else{
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_other))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }

        holder.mImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Util.mSelectedDuplicateList.size() == 0){
                    changeView();
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedDuplicateList.add(file.getPath());
                }else{
                    if(Util.mSelectedDuplicateList.contains(file.getPath())){
                        holder.mDeselect.setVisibility(View.VISIBLE);
                        holder.mSelect.setVisibility(View.GONE);
                        Util.mSelectedDuplicateList.remove(file.getPath());
                    }else{
                        holder.mSelect.setVisibility(View.VISIBLE);
                        holder.mDeselect.setVisibility(View.GONE);
                        Util.mSelectedDuplicateList.add(file.getPath());
                    }
                }
                if(Util.mSelectedDuplicateList.size() == 0){
                    changeToOriginalView();
                }else{
                    DuplicateActivity.count.setText(Util.mSelectedDuplicateList.size() + " Selected");
                }
            }
        });
    }

    public void changeToOriginalView(){

        Util.mSelectedDuplicateList.clear();
        DuplicateActivity.titleLL.setVisibility(View.VISIBLE);
        DuplicateActivity.actionLL.setVisibility(View.GONE);
        DuplicateActivity.mHeaderRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        DuplicateActivity.view.setVisibility(View.VISIBLE);
        DuplicateActivity.mDeleteCard.setCardBackgroundColor(activity.getResources().getColor(R.color.delete_disable));

    }

    public void changeView(){

        Util.mSelectedDuplicateList.clear();
        DuplicateActivity.titleLL.setVisibility(View.GONE);
        DuplicateActivity.actionLL.setVisibility(View.VISIBLE);
        DuplicateActivity.mHeaderRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        DuplicateActivity.view.setVisibility(View.GONE);
        DuplicateActivity.mDeleteCard.setCardBackgroundColor(activity.getResources().getColor(R.color.themeColor));

    }

    @Override
    public int getItemCount() {
        return ChildItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView mImage,player;
        ImageView mSelect,mDeselect;
        TextView mSize,mFolder;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            player = itemView.findViewById(R.id.img_play);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            mSize = itemView.findViewById(R.id.mSize);
            mFolder = itemView.findViewById(R.id.mFolder);
        }
    }



}

