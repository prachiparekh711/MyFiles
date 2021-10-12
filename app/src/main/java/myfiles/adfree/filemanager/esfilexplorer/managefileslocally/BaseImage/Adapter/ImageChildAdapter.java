package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.ImageViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.BaseImageActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

public class ImageChildAdapter extends RecyclerView.Adapter<ImageChildAdapter.MyClassView> {

    List<BaseModel> images;
    Activity activity;
    int directoryPosition;
   SelectionInterface selectionInterface;
   int match=0;


    public ImageChildAdapter(List<BaseModel> images,Activity activity,int directoryPosition,SelectionInterface anInterface) {
        this.images = images;
        this.activity = activity;
        this.directoryPosition = directoryPosition;
       this.selectionInterface=anInterface;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.image_view, parent, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / 3;
        }
        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        BaseModel file = images.get(position);
//            //   Log.e("Selected List:",mSelectedImgList.size() + "");

        holder.mImage.setClipToOutline(true);

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
                BaseImageActivity.disableCard();
                ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
               SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
                SharedPreference.setRecentList(activity, recentList);

                Intent intent = new Intent(activity,ImageViewActivity.class);
                intent.putExtra("Position",position);
                intent.putExtra("from","ImageFragment");
                intent.putExtra("directoryPosition",directoryPosition);
                activity.startActivity(intent);
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseImageActivity.disableCard();

                if(Util.mSelectedImgList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedImgList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedImgList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedImgList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedImgList.size() == 0){
                    changeToOriginalView();
                }else{

                    BaseImageActivity.count.setText(Util.mSelectedImgList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                BaseImageActivity.disableCard();
                ImageParentAdapter.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedImgList.add(file.getPath());
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

        for(int i = 0;i < Util.mSelectedImgList.size();i++){
            if(Util.mSelectedImgList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(ImageParentAdapter.viewClose){
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

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
        }
    }

    public void changeHomeView(){
        BaseImageActivity.viewPager.setPagingEnabled(false  );
        BaseImageActivity.titleLL.setVisibility(View.GONE);
        BaseImageActivity.actionLL.setVisibility(View.VISIBLE);
        BaseImageActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseImageActivity.tabLayout.setVisibility(View.GONE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.white));
        BaseImageActivity.delete.setVisibility(View.VISIBLE);
        BaseImageActivity.share.setVisibility(View.VISIBLE);
        BaseImageActivity.view.setVisibility(View.GONE);
        BaseImageActivity.search.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(BaseImageActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseImageActivity.count.setText(Util.mSelectedImgList.size() + " Selected");

    }

    public void changeToOriginalView(){
        BaseImageActivity.viewPager.setPagingEnabled(true  );
        Util.mSelectedImgList.clear();
        BaseImageActivity.titleLL.setVisibility(View.VISIBLE);
        BaseImageActivity.actionLL.setVisibility(View.GONE);
        BaseImageActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseImageActivity.tabLayout.setVisibility(View.VISIBLE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.themeColor));
        BaseImageActivity.delete.setVisibility(View.GONE);
        BaseImageActivity.share.setVisibility(View.GONE);
        BaseImageActivity.view.setVisibility(View.VISIBLE);
        BaseImageActivity.search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(BaseImageActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        ImageParentAdapter.viewClose=true;
        notifyDataSetChanged();

    }


}
