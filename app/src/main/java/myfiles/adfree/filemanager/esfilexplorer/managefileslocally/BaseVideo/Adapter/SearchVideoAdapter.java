package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.SearchVideoViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;


public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoAdapter.MyClassView> implements Filterable{

    List<BaseModel> videos;
    List<BaseModel> filteredVideos;
    Activity activity;
    public SearchVideoAdapter(List<BaseModel> videos,Activity activity) {
        this.videos = videos;
        this.filteredVideos=videos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.search_viditem, parent, false);
        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {
        BaseModel file = filteredVideos.get(position);
        String path=file.getPath();

        holder.tv_image_name.setText(path.substring(path.lastIndexOf("/") + 1));

        holder.mImage.setClipToOutline(true);
        holder.tv_image_size.setText(Util.getFileSize(file.getSize()));
        holder.tv_image_date.setText(file.getDate());

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

        holder.itemView.setOnClickListener(v -> {
            ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
           SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
            SharedPreference.setRecentList(activity, recentList);
            Intent intent = new Intent(activity,SearchVideoViewActivity.class);
            intent.putExtra("Searched",file);
            intent.putExtra("From","Search");
            activity.startActivity(intent);
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

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ) {
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

                // refresh the list with filtered data
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

