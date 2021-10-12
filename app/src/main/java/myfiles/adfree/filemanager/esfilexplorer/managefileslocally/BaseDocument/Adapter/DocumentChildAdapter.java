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
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity.BaseDocumentActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity.BaseDocumentActivity.viewPager;


public class DocumentChildAdapter extends RecyclerView.Adapter<DocumentChildAdapter.MyClassView> {

    List<BaseModel> images;
    Activity activity;
    int directoryPosition;
    SelectionInterface selectionInterface;
   
    public DocumentChildAdapter(List<BaseModel> images,Activity activity,int directoryPosition,SelectionInterface anInterface) {
        this.images = images;
        this.activity = activity;
        this.directoryPosition = directoryPosition;
        this.selectionInterface=anInterface;
    }

    @NonNull
    @Override
    public DocumentChildAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.doc_view, parent, false);

        return new DocumentChildAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull DocumentChildAdapter.MyClassView holder,int position) {

        BaseModel file = images.get(position);
//            //   Log.e("Selected List:",mSelectedDocumentList.size() + "");
        holder.mName.setText(file.getName());
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

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
                BaseDocumentActivity.disableCard();
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
                    BaseDocumentActivity.fileInfo(new File(file.getPath()),activity);
                }
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseDocumentActivity.disableCard();

                if(Util.mSelectedDocumentList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedDocumentList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedDocumentList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedDocumentList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedDocumentList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseDocumentActivity.count.setText(Util.mSelectedDocumentList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                BaseDocumentActivity.disableCard();
                viewPager.setPagingEnabled(false  );
                DocumentParentAdapter.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedDocumentList.add(file.getPath());
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


        for(int i = 0;i < Util.mSelectedDocumentList.size();i++){
            if(Util.mSelectedDocumentList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(DocumentParentAdapter.viewClose){
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
        TextView mName;
        ImageView mSelect,mDeselect;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.mImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            mName = itemView.findViewById(R.id.tv_album);
        }
    }

    public void changeHomeView(){
        BaseDocumentActivity.titleLL.setVisibility(View.GONE);
        BaseDocumentActivity.actionLL.setVisibility(View.VISIBLE);
        BaseDocumentActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseDocumentActivity.tabLayout.setVisibility(View.GONE);
        BaseDocumentActivity.delete.setVisibility(View.VISIBLE);
        BaseDocumentActivity.share.setVisibility(View.VISIBLE);
        BaseDocumentActivity.view.setVisibility(View.GONE);
        BaseDocumentActivity.search.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(BaseDocumentActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseDocumentActivity.count.setText(Util.mSelectedDocumentList.size() + " Selected");

    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true  );
        Util.mSelectedDocumentList.clear();
        BaseDocumentActivity.titleLL.setVisibility(View.VISIBLE);
        BaseDocumentActivity.actionLL.setVisibility(View.GONE);
        BaseDocumentActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseDocumentActivity.tabLayout.setVisibility(View.VISIBLE);
        BaseDocumentActivity.delete.setVisibility(View.GONE);
        BaseDocumentActivity.share.setVisibility(View.GONE);
        BaseDocumentActivity.view.setVisibility(View.VISIBLE);
        BaseDocumentActivity.search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(BaseDocumentActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        DocumentParentAdapter.viewClose=true;
        notifyDataSetChanged();

    }

}

