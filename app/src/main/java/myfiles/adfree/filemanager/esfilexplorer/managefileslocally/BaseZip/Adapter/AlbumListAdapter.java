package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.ViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;


public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.MyClassView> {

    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    List<Directory<BaseModel>> files;
    Activity activity;

    public AlbumListAdapter(List<Directory<BaseModel>> files,Activity activity) {
        this.files = files;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AlbumListAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_album_list, parent, false);
        return new AlbumListAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapter.MyClassView holder,int position) {
        Directory<BaseModel> DateName = files.get(position);
        holder.tv_album.setText(DateName.getName());

        holder.tv_album_count.setText("("+DateName.getFiles().size()+")");

        BaseModel file = DateName.getFiles().get(0);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ViewAlbumActivity.class);
            intent.putExtra("DirName",DateName.getName());
            activity.startActivity(intent);

        });
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

    public void clearData(){
        this.files.clear();
    }


    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView mImage;
        TextView tv_album,tv_album_count;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.imgAlbum);
            tv_album = itemView.findViewById(R.id.tv_album_name);
            tv_album_count = itemView.findViewById(R.id.tv_album_size);
        }
    }
}


