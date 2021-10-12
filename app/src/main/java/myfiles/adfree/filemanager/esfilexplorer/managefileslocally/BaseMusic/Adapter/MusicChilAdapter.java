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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.BaseMusicActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.MusicPlayerActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.BaseMusicActivity.viewPager;

public class MusicChilAdapter extends RecyclerView.Adapter<MusicChilAdapter.MyClassView>{

    List<BaseModel> images;
    Activity activity;
    int directoryPosition;
    SelectionInterface selectionInterface;
    int match=0;

    public MusicChilAdapter(List<BaseModel> images,Activity activity,int directoryPosition,SelectionInterface anInterface) {
        this.images = images;
        this.activity = activity;
        this.directoryPosition = directoryPosition;
        this.selectionInterface=anInterface;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.music_view, parent, false);
//        ViewGroup.LayoutParams params = itemView.getLayoutParams();
//        if (params != null) {
//            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
//            int width = wm.getDefaultDisplay().getWidth();
//            params.height = width / 5;
//        }
        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {

        BaseModel file = images.get(position);

        holder.mImage.setClipToOutline(true);

//        activity.runOnUiThread(new Runnable(){
//            @Override
//            public void run(){
//                Bitmap image = null;
//                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q){
//                    image = ThumbnailUtils.createAudioThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
//                }
//                if(image==null)
//                    holder.mImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.music_icon));
//                else
//                    holder.mImage.setImageBitmap(image);
//            }
//        });

        String path=file.getPath();

        holder.tv_image_name.setText(path.substring(file.getPath().lastIndexOf("/") + 1));

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
                BaseMusicActivity.disableCard();
                ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
               SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
                SharedPreference.setRecentList(activity, recentList);
                Intent intent = new Intent(activity,MusicPlayerActivity.class);
                intent.putExtra("Position",position);
                intent.putExtra("from","ImageFragment");
                intent.putExtra("directoryPosition",directoryPosition);
                activity.startActivity(intent);
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseMusicActivity.disableCard();

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
                BaseMusicActivity.disableCard();

                MusicParentadapter.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedMusicList.add(file.getPath());
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


        for(int i = 0;i < Util.mSelectedMusicList.size();i++){
            if(Util.mSelectedMusicList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(MusicParentadapter.viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView mImage;
        ImageView mSelect,mDeselect;
        TextView tv_image_name;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            tv_image_name = itemView.findViewById(R.id.tv_image_name);
        }
    }

    public void changeHomeView(){
        viewPager.setPagingEnabled(false);
        BaseMusicActivity.titleLL.setVisibility(View.GONE);
        BaseMusicActivity.actionLL.setVisibility(View.VISIBLE);
        BaseMusicActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseMusicActivity.tabLayout.setVisibility(View.GONE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.white));
        BaseMusicActivity.delete.setVisibility(View.VISIBLE);
        BaseMusicActivity.share.setVisibility(View.VISIBLE);
        BaseMusicActivity.view.setVisibility(View.GONE);
        BaseMusicActivity.search.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(BaseMusicActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseMusicActivity.count.setText(Util.mSelectedMusicList.size() + " Selected");

    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true);
        Util.mSelectedMusicList.clear();
        BaseMusicActivity.titleLL.setVisibility(View.VISIBLE);
        BaseMusicActivity.actionLL.setVisibility(View.GONE);
        BaseMusicActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseMusicActivity.tabLayout.setVisibility(View.VISIBLE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.themeColor));
        BaseMusicActivity.delete.setVisibility(View.GONE);
        BaseMusicActivity.share.setVisibility(View.GONE);
        BaseMusicActivity.view.setVisibility(View.VISIBLE);
        BaseMusicActivity.search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(BaseMusicActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        MusicParentadapter.viewClose=true;
        notifyDataSetChanged();

    }

}

