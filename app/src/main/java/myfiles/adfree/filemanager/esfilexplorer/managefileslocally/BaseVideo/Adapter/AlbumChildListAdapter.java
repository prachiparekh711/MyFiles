package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.VideoViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.ViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumChildListAdapter extends RecyclerView.Adapter<AlbumChildListAdapter.MyClassView> {

    List<BaseModel> images;
    Activity activity;
    int direPos = 0;
    public AlbumChildListAdapter(List<BaseModel> images,Activity activity,int direPos) {
        this.images = images;
        this.activity = activity;
        this.direPos = direPos;
    }

    @NonNull
    @Override
    public AlbumChildListAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_album_child, parent, false);
        return new AlbumChildListAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumChildListAdapter.MyClassView holder,int position) {
        BaseModel file = images.get(position);

        holder.tv_image_name.setText(file.getName());

        holder.mImage.setClipToOutline(true);
        holder.tv_image_size.setText(Util.getFileSize(file.getSize()));
        holder.tv_image_date.setText(file.getDate());

        RequestOptions options = new RequestOptions();
        if (file.getPath().endsWith(".PNG") || file.getPath().endsWith(".png")) {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.mImage);
        } else {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
                ViewAlbumActivity.disableCard();
                ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
               SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
                SharedPreference.setRecentList(activity, recentList);
                Intent intent = new Intent(activity, VideoViewAlbumActivity.class);
                intent.putExtra("Position", position);
                intent.putExtra("DirPos", direPos);
                intent.putExtra("from", "VidFragment");
                activity.startActivity(intent);
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                ViewAlbumActivity.disableCard();

                if(Util.mSelectedVideoList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedVideoList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedVideoList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedVideoList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedVideoList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseVideoActivity.count.setText(Util.mSelectedVideoList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                ViewAlbumActivity.disableCard();

                AlbumChildAdapter.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedVideoList.add(file.getPath());

                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        notifyDataSetChanged();
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

//        if(mSelectedVideoList.contains(file)){
//            holder.mSelect.setVisibility(View.VISIBLE);
//            holder.mDeselect.setVisibility(View.GONE);
//        }

        for(int i = 0;i < Util.mSelectedVideoList.size();i++){
            if(Util.mSelectedVideoList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(AlbumChildAdapter.viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        TextView tv_image_name,tv_image_date,tv_image_size;
        ImageView mImage;
        ImageView mSelect,mDeselect;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_image_date = itemView.findViewById(R.id.tv_image_date);
            tv_image_size = itemView.findViewById(R.id.tv_image_size);
            mImage = itemView.findViewById(R.id.imgAlbum);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
        }
    }

    public void changeHomeView(){

        ViewAlbumActivity.l1.setVisibility(View.GONE);
        ViewAlbumActivity.l3.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l2.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l4.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));

        ViewAlbumActivity.count.setText(Util.mSelectedVideoList.size() + " Selected");

    }


    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedVideoList.clear();
        ViewAlbumActivity.l1.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l3.setVisibility(View.GONE);
        ViewAlbumActivity.l2.setVisibility(View.GONE);
        ViewAlbumActivity.l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        AlbumChildAdapter.viewClose=true;
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.white));

    }

}
