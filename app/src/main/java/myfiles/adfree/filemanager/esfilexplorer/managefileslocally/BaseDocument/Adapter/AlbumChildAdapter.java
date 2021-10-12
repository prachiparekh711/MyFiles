package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Adapter;

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
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity.ViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
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
    }

    @Override
    public AlbumChildAdapter.MyClassView onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doc_album_view,parent,false);

        return new AlbumChildAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumChildAdapter.MyClassView holder,int position){
        BaseModel file = images.get(position);

        holder.tv_album.setText(file.getName());
        holder.mImage.setClipToOutline(true);

        RequestOptions options = new RequestOptions();
        if (file.getPath().endsWith(".docx") || file.getPath().endsWith(".doc")) {
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_word))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".xls")) {
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_excel))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".pdf")) {
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_pdf))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".ppt") || file.getPath().endsWith(".pptx")) {
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_powerpoint))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else{
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

        if(Util.clickEnable==true){
            holder.itemView.setOnClickListener(v -> {
                ViewAlbumActivity.disableCard();
                ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));

               SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
                SharedPreference.setRecentList(activity, recentList);
            Intent in = new Intent(Intent.ACTION_VIEW);
            Uri uri= FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName()+".provider",new File(file.getPath()));
            try{
                if(file.getPath().endsWith(".pdf")){
                    in.setDataAndType(uri,"application/pdf");
                }else if(file.getPath().endsWith(".doc") || file.getPath().endsWith(".docx")){
                    in.setDataAndType(uri,"application/msword");
                }else if(file.getPath().endsWith(".xls")){
                    in.setDataAndType(uri,"application/vnd.ms-excel");
                }else if(file.getPath().endsWith(".ppt") || file.getPath().endsWith(".pptx")){
                    in.setDataAndType(uri,"application/vnd.ms-powerpoint");
                }else{
                    in.setDataAndType(uri,"application/*");
                }
                in.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                in.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(in);
            }
            catch(Exception e){
                ViewAlbumActivity.fileInfo(new File(file.getPath()),activity);
            }
        });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                ViewAlbumActivity.disableCard();

                if(Util.mSelectedDocumentList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedDocumentList.remove(file.getPath());
//                    selectionInterface.onDeselectDate(direPos);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedDocumentList.add(file.getPath());
                    if(Util.mSelectedDocumentList.containsAll(images)){
//                        selectionInterface.onSelectDate(direPos);
                    }
                }
                if(Util.mSelectedDocumentList.size()==0){
                    changeToOriginalView();
                }else{
                    ViewAlbumActivity.count.setText(Util.mSelectedDocumentList.size() + " Selected");
                }
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
                Util.mSelectedDocumentList.add(file.getPath());
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

        for(int i = 0;i < Util.mSelectedDocumentList.size();i++){
            if(Util.mSelectedDocumentList.get(i).equals(file.getPath())){
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
        TextView tv_album;

        public MyClassView(@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            tv_album = itemView.findViewById(R.id.tv_album);
        }
    }

    public void changeHomeView(){

        ViewAlbumActivity.l1.setVisibility(View.GONE);
        ViewAlbumActivity.l3.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l2.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l4.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));

        ViewAlbumActivity.count.setText(Util.mSelectedDocumentList.size() + " Selected");

    }


    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedDocumentList.clear();
        ViewAlbumActivity.l1.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l3.setVisibility(View.GONE);
        ViewAlbumActivity.l2.setVisibility(View.GONE);
        ViewAlbumActivity.l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.white));

    }

}

