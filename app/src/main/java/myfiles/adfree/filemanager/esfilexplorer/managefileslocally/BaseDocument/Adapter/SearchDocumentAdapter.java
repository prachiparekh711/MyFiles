package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SearchDocumentAdapter extends RecyclerView.Adapter<SearchDocumentAdapter.MyClassView> implements Filterable{

    List<BaseModel> videos;
    List<BaseModel> filteredVideos;
    Activity activity;
    public SearchDocumentAdapter(List<BaseModel> videos,Activity activity) {
        this.videos = videos;
        this.filteredVideos=videos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SearchDocumentAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.search_docitem, parent, false);
        return new SearchDocumentAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull SearchDocumentAdapter.MyClassView holder,int position) {
        BaseModel file = filteredVideos.get(position);
        String path=file.getPath();

        holder.tv_image_name.setText(path.substring(path.lastIndexOf("/") + 1));

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

        holder.tv_image_size.setText(Util.getFileSize(file.getSize()));
        holder.tv_image_date.setText(file.getDate());


        holder.itemView.setOnClickListener(v -> {
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
                Toast.makeText(activity,"None of your apps can open this file.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredVideos.size();
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredVideos = videos;
                } else {
                    List<BaseModel> filteredList = new ArrayList<>();
                    for (BaseModel row : videos) {
                        String path=row.getPath();
                        String name=path.substring(path.lastIndexOf("/") + 1);
                        if (name.toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }
                    filteredVideos = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredVideos;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredVideos = (ArrayList<BaseModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        TextView tv_image_name,tv_image_date,tv_image_size;
        ImageView mImage;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_image_date = itemView.findViewById(R.id.tv_image_date);
            tv_image_size = itemView.findViewById(R.id.tv_image_size);
            mImage = itemView.findViewById(R.id.imgAlbum);

        }
    }
}



