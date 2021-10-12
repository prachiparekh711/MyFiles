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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.LargeActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;


public class LargeListAdapter extends RecyclerView.Adapter<LargeListAdapter.MyClassView> {

    List<BaseModel> ChildItemList;
    Activity activity;
    String from;
    ArrayList<String> arrayList=new ArrayList<>();

    public LargeListAdapter(String from,List<BaseModel> ChildItemList,Activity activity) {
        this.ChildItemList = ChildItemList;
        this.activity = activity;
        this.from=from;
    }

    @NonNull
    @Override
    public LargeListAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.child_list_duplicate, parent, false);

        return new LargeListAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull LargeListAdapter.MyClassView holder,int position) {

        if(from.equals("Large"))
            arrayList= Util.mSelectedLargeList;
        else if(from.equals("WImage"))
            arrayList=Util.mSelectedWImageList;
        else if(from.equals("WVideo"))
            arrayList=Util.mSelectedWVideoList;
        BaseModel file = ChildItemList.get(position);

        holder.mDeselect.setVisibility(View.VISIBLE);
        holder.mSelect.setVisibility(View.GONE);

        for(int i = 0;i < arrayList.size();i++){
            if(arrayList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        RequestOptions options = new RequestOptions();
        holder.mImage.setClipToOutline(true);

        holder.mSize.setText(Util.getSize(file.getSize()));
        holder.mFolder.setText(file.getName());
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

        holder.listRL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(arrayList.size() == 0){
                    changeView();
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    arrayList.add(file.getPath());
                }else{
                    if(arrayList.contains(file.getPath())){
                        holder.mDeselect.setVisibility(View.VISIBLE);
                        holder.mSelect.setVisibility(View.GONE);
                        arrayList.remove(file.getPath());
                    }else{
                        holder.mSelect.setVisibility(View.VISIBLE);
                        holder.mDeselect.setVisibility(View.GONE);
                        arrayList.add(file.getPath());
                    }

                }
                if(arrayList.size() == 0){
                    changeToOriginalView();
                }else{
                    LargeActivity.count.setText(arrayList.size() + " Selected");
                }
            }
        });
    }

    public void changeToOriginalView(){

        arrayList.clear();
        LargeActivity.titleLL.setVisibility(View.VISIBLE);
        LargeActivity.actionLL.setVisibility(View.GONE);
        LargeActivity.mHeaderRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        LargeActivity.view.setVisibility(View.VISIBLE);
        LargeActivity.mDeleteCard.setCardBackgroundColor(activity.getResources().getColor(R.color.delete_disable));

    }

    public void changeView(){

        arrayList.clear();
        LargeActivity.titleLL.setVisibility(View.GONE);
        LargeActivity.actionLL.setVisibility(View.VISIBLE);
        LargeActivity.mHeaderRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        LargeActivity.view.setVisibility(View.GONE);
        LargeActivity.mDeleteCard.setCardBackgroundColor(activity.getResources().getColor(R.color.themeColor));

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
        RelativeLayout listRL;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            listRL = itemView.findViewById(R.id.listRL);
            player = itemView.findViewById(R.id.img_play);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            mSize = itemView.findViewById(R.id.mSize);
            mFolder = itemView.findViewById(R.id.mFolder);
        }
    }



}


