package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyClassView> {

    List<Directory<BaseModel>> files;
    Activity activity;
    private FilesAdapter.ItemClickListener mItemClickListener;
    public static int globalPos=-1;
    public FilesAdapter(ArrayList<Directory<BaseModel>> files,Activity activity,FilesAdapter.ItemClickListener itemClickListener) {
        this.files = files;
        this.activity = activity;
        this.mItemClickListener = itemClickListener;
    }

    public void addItemClickListener(FilesAdapter.ItemClickListener listener) {
        mItemClickListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.MyClassView holder,int position) {
        Directory<BaseModel> DateName = files.get(position);
        holder.tv_album.setText(DateName.getName());

        if(position==globalPos){
            holder.tv_album.setTextColor(activity.getResources().getColor(R.color.themeColor));
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        }else{
            holder.tv_album.setTextColor(activity.getResources().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {

            globalPos=position;
            notifyDataSetChanged();

        });
    }

    @NonNull
    @Override
    public FilesAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.target_files, parent, false);
        return new FilesAdapter.MyClassView(itemView);
    }

    //Define your Interface method here
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void addAll(List<Directory<BaseModel>> imgMain1DownloadList) {
        this.files.addAll(imgMain1DownloadList);
        if (this.files.size() >= 10)
            notifyItemRangeChanged(this.files.size() - 10,this.files.size());
        else
            notifyDataSetChanged();
    }

    public void clearData() {
        this.files.clear();
        notifyDataSetChanged();
    }


    public class MyClassView extends RecyclerView.ViewHolder {


        TextView tv_album;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            tv_album = itemView.findViewById(R.id.tv_album_name);
        }
    }
}

