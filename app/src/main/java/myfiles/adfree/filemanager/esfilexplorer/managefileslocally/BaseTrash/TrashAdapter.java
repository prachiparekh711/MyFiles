package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseTrash;

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
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;


public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.MyClassView> {

    ArrayList<BaseModel> mTrashList=new ArrayList<>();
    public static boolean viewClose=false;
    Activity activity;
    public TrashAdapter(ArrayList<BaseModel> images,Activity activity) {
        this.activity = activity;
        mTrashList=images;
    }

    @NonNull
    @Override
    public TrashAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.trash_layout, parent, false);
        return new TrashAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull TrashAdapter.MyClassView holder,int position) {
        BaseModel  file = mTrashList.get(position);

//        //   Log.e("Trash file :",file.getPath());
        holder.tv_image_name.setText(file.getName());

        holder.mImage.setClipToOutline(true);
        holder.tv_image_size.setText(Util.getSize(file.getSize()));

        holder.tv_image_date.setText(file.getDate());

        RequestOptions options = new RequestOptions();
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

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
//                final File backupDBFolder = new File(unZipPath);
//                try{
//                    Util.ZipManager.unzip(file.getPath(), backupDBFolder.getPath(),activity.getBaseContext());
//                }catch(IOException e){
//                    e.printStackTrace();
//                }
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                if(Util.mSelectedTrashList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedTrashList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedTrashList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedTrashList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedTrashList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseTrashActivity.count.setText(Util.mSelectedTrashList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedTrashList.add(file.getPath());

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

//        if(mSelectedTrashList.contains(file)){
//            holder.mSelect.setVisibility(View.VISIBLE);
//            holder.mDeselect.setVisibility(View.GONE);
//        }

        for(int i = 0;i < Util.mSelectedTrashList.size();i++){
            if(Util.mSelectedTrashList.get(i).equals(file.getPath())){
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
        return mTrashList.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        TextView tv_image_name,tv_image_date,tv_image_size;
        ImageView mImage;
        ImageView mSelect,mDeselect;
        ImageView player;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_image_date = itemView.findViewById(R.id.tv_image_date);
            tv_image_size = itemView.findViewById(R.id.tv_image_size);
            mImage = itemView.findViewById(R.id.imgAlbum);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            player = itemView.findViewById(R.id.player);
        }
    }

    public void changeHomeView(){
        BaseTrashActivity.titleLL.setVisibility(View.GONE);
        BaseTrashActivity.actionLL.setVisibility(View.VISIBLE);
        BaseTrashActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseTrashActivity.view.setVisibility(View.GONE);
        BaseTrashActivity.count.setText(Util.mSelectedTrashList.size() + " Selected");

    }

    public void changeToOriginalView(){
        Util.mSelectedTrashList.clear();
        BaseTrashActivity.titleLL.setVisibility(View.VISIBLE);
        BaseTrashActivity.actionLL.setVisibility(View.GONE);
        BaseTrashActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseTrashActivity.view.setVisibility(View.VISIBLE);
        viewClose=true;
        notifyDataSetChanged();

    }


}


