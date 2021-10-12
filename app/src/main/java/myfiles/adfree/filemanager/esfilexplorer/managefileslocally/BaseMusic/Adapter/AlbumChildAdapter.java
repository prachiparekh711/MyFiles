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
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.ViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;


public class AlbumChildAdapter extends RecyclerView.Adapter<AlbumChildAdapter.MyClassView>{

    List<BaseModel> images;
    Activity activity;
    int direPos = 0;
    public static boolean viewClose=false;

    public AlbumChildAdapter(List<BaseModel> images,Activity activity,int i){
        this.images = images;
        this.activity = activity;
        this.direPos = i;
//        //   Log.e("In ","Albumchildadapter");
    }

    @Override
    public AlbumChildAdapter.MyClassView onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_music_child_view,parent,false);

        return new AlbumChildAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumChildAdapter.MyClassView holder,int position){
        BaseModel file = images.get(position);
        holder.mImage.setClipToOutline(true);
        String path=file.getPath();

        holder.tv_image_name.setText(path.substring(file.getPath().lastIndexOf("/") + 1));

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable==true){
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
//                    selectionInterface.onDeselectDate(direPos);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedMusicList.add(file.getPath());
                    if(Util.mSelectedMusicList.containsAll(images)){
//                        selectionInterface.onSelectDate(direPos);
                    }
                }
                if(Util.mSelectedMusicList.size()==0){
                    changeToOriginalView();
                }else{
                    ViewAlbumActivity.count.setText(Util.mSelectedMusicList.size() + " Selected");
                }
                //   Log.e("Selected list :",String.valueOf(Util.mSelectedMusicList.size()));
            });

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                ViewAlbumActivity.disableCard();

                viewClose = false;
                Util.clickEnable = false;
                Util.showCircle = true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedMusicList.add(file.getPath());
                //   Log.e("Selected list :",String.valueOf(Util.mSelectedMusicList.size()));
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        notifyDataSetChanged();
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

        for(int i = 0;i < Util.mSelectedMusicList.size();i++){
            if(Util.mSelectedMusicList.get(i).equals(file.getPath())){
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
    public int getItemCount(){

        return images.size();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    public class MyClassView extends RecyclerView.ViewHolder{

        ImageView mImage;
        ImageView mSelect, mDeselect;
        TextView tv_image_name;

        public MyClassView(@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.albumImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            tv_image_name = itemView.findViewById(R.id.tv_image_name);
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
        viewClose=true;
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.white));

    }

}

