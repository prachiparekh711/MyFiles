package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter;

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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.AlbumMusicPlayerActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.BaseMusicActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.ViewAlbumActivity;
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
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_albummusic_child, parent, false);
        return new AlbumChildListAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumChildListAdapter.MyClassView holder,int position) {
        BaseModel file = images.get(position);

        String extension = "";
        int j = file.getPath().lastIndexOf('.');
        if (j > 0) {
            extension = file.getPath().substring(j+1);
        }
        holder.tv_image_name.setText(file.getName() + "." + extension);

        holder.mImage.setClipToOutline(true);
//        Bitmap image = null;
//
//        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
//            image = ThumbnailUtils.createAudioThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
//        }
//        if(image==null)
//            holder.mImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.music_icon));
//        else
//            holder.mImage.setImageBitmap(image);
        holder.tv_image_size.setText(Util.getFileSize(file.getSize()));
        holder.tv_image_date.setText(file.getDate());

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
                Intent intent = new Intent(activity, AlbumMusicPlayerActivity.class);
                intent.putExtra("Position", position);
                intent.putExtra("DirPos", direPos);
                intent.putExtra("from", "VidFragment");
                activity.startActivity(intent);
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                ViewAlbumActivity.disableCard();

                if(Util.mSelectedMusicList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedMusicList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedMusicList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedMusicList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedMusicList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseMusicActivity.count.setText(Util.mSelectedMusicList.size() + " Selected");
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
                Util.mSelectedMusicList.add(file.getPath());

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

//        if(mSelectedMusicList.contains(file)){
//            holder.mSelect.setVisibility(View.VISIBLE);
//            holder.mDeselect.setVisibility(View.GONE);
//        }

        for(int i = 0;i < Util.mSelectedMusicList.size();i++){
            if(Util.mSelectedMusicList.get(i).equals(file.getPath())){
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

        ViewAlbumActivity.count.setText(Util.mSelectedMusicList.size() + " Selected");

    }


    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedMusicList.clear();
        ViewAlbumActivity.l1.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l3.setVisibility(View.GONE);
        ViewAlbumActivity.l2.setVisibility(View.GONE);
        ViewAlbumActivity.l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        AlbumChildAdapter.viewClose=true;
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.white));

    }

}

