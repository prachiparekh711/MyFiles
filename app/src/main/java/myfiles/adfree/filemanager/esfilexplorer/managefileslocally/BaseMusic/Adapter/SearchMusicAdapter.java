package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter;

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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.SearchMusicViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;


public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.MyClassView> implements Filterable{

    List<BaseModel> videos;
    List<BaseModel> filteredVideos;
    Activity activity;
    public SearchMusicAdapter(List<BaseModel> videos,Activity activity) {
        this.videos = videos;
        this.filteredVideos=videos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SearchMusicAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.search_musicitem, parent, false);
        return new SearchMusicAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull SearchMusicAdapter.MyClassView holder,int position) {
        BaseModel file = filteredVideos.get(position);
        String path=file.getPath();

        holder.tv_image_name.setText(path.substring(path.lastIndexOf("/") + 1));

        holder.mImage.setClipToOutline(true);
        holder.tv_image_size.setText(Util.getFileSize(file.getSize()));
        holder.tv_image_date.setText(file.getDate());



        holder.itemView.setOnClickListener(v -> {
            ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
           SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);
            SharedPreference.setRecentList(activity, recentList);
            Intent intent = new Intent(activity,SearchMusicViewActivity.class);
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


