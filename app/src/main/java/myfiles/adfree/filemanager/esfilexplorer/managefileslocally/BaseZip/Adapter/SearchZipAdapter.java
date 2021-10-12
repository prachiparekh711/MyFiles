package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SearchZipAdapter extends RecyclerView.Adapter<SearchZipAdapter.MyClassView> implements Filterable{

    List<BaseModel> videos;
    List<BaseModel> filteredVideos;
    Activity activity;
    String unZipPAth;
    ArrayList<Directory<BaseModel>> mZipDir = new ArrayList<>();
    String path1;
    private static final String EXTRA_DIRECTORY = "com.blackmoonit.intent.extra.DIRECTORY";
    public SearchZipAdapter(List<BaseModel> videos,Activity activity,ArrayList<Directory<BaseModel>> mZipDir) {
        this.videos = videos;
        this.filteredVideos=videos;
        this.activity = activity;
        this.mZipDir=mZipDir;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.search_zipitem, parent, false);
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


        File file1=new File(file.getPath());
        holder.itemView.setOnClickListener(v -> {
            unZipPAth=file1.getParentFile().getAbsolutePath();
            //   Log.e("Unzip path:",unZipPAth);
            path1=file.getPath();
            Intent theIntent = new Intent(Intent.ACTION_VIEW);
            theIntent.setDataAndType(Uri.fromFile(new File(path)),"application/zip");
            theIntent.putExtra(EXTRA_DIRECTORY,unZipPAth); //optional default location (version 7.4+)
            try {
                activity.startActivity(theIntent);
            } catch (ActivityNotFoundException Ae){
                //When No application can perform zip file
                Uri uri = Uri.parse("market://search?q=" + "application/zip");
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);

                try {
                    activity.startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    //the device hasn't installed Google Play
                    Toast.makeText(activity, "You don't have Google Play installed", Toast.LENGTH_LONG).show();
                }
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


