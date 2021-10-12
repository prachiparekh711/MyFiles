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
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.VideoViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.actionLL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.delete;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.headerRL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.more;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.search;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.share;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.tabLayout;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.titleLL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.view;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity.viewPager;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter.VideoParentAdapter.viewClose;

public class VideoChildListAdapter extends RecyclerView.Adapter<VideoChildListAdapter.MyClassView> {

    List<BaseModel> mVideoList;
    Activity activity;
    int direPos = 0;
    SelectionInterface selectionInterface;

    public VideoChildListAdapter(List<BaseModel> mVideoList,Activity activity,int direPos,SelectionInterface anInterface) {
        this.mVideoList = mVideoList;
        this.activity = activity;
        this.direPos = direPos;
        this.selectionInterface=anInterface;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_video_list, parent, false);
        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {
        BaseModel file = mVideoList.get(position);

        holder.tv_image_name.setText(file.getName());

        holder.mImage.setClipToOutline(true);

        holder.tv_image_date.setText(file.getDate());

        RequestOptions options = new RequestOptions();

        Glide.with(activity)
                .load(file.getPath())
                .apply(options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW))

                .into(holder.mImage);


        holder.tv_image_size.setText(Util.getSize(file.getSize()));

        //*****************************

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
                BaseVideoActivity.disableCard();
                ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
               SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
                SharedPreference.setRecentList(activity, recentList);
                Intent intent = new Intent(activity,VideoViewActivity.class);
                intent.putExtra("Position",position);
                intent.putExtra("from","ImageFragment");
                intent.putExtra("directoryPosition",direPos);
                activity.startActivity(intent);
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseVideoActivity.disableCard();

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
//                        if(mSelectedImgList.contains(images.get(i).getPath())){
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
                BaseVideoActivity.disableCard();

                viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedVideoList.add(file.getPath());
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


        for(int i = 0;i < Util.mSelectedVideoList.size();i++){
            if(Util.mSelectedVideoList.get(i).equals(file.getPath())){
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
        return mVideoList.size();
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
        viewPager.setPagingEnabled(false);
        BaseVideoActivity.titleLL.setVisibility(View.GONE);
        BaseVideoActivity.actionLL.setVisibility(View.VISIBLE);
        BaseVideoActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseVideoActivity.tabLayout.setVisibility(View.GONE);
        BaseVideoActivity.delete.setVisibility(View.VISIBLE);
        BaseVideoActivity.share.setVisibility(View.VISIBLE);
        BaseVideoActivity.view.setVisibility(View.GONE);
        BaseVideoActivity.search.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseVideoActivity.count.setText(Util.mSelectedVideoList.size() + " Selected");

    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true);
        Util.mSelectedImgList.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        tabLayout.setVisibility(View.VISIBLE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.themeColor));
        delete.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        notifyDataSetChanged();

    }
  

}

