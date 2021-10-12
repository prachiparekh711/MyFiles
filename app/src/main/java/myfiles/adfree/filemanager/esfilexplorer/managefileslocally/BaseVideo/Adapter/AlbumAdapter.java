package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.ViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyClassView> {

  
    List<Directory<BaseModel>> files;
    Activity activity;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public AlbumAdapter(List<Directory<BaseModel>> files,Activity activity) {
        this.files = files;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AlbumAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_album, parent, false);
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params != null) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            params.height = width / Util.COLUMN_TYPE;
        }
        return new AlbumAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.MyClassView holder,int position) {
        Directory<BaseModel> DateName = files.get(position);
        holder.tv_album.setText(DateName.getName());

        holder.tv_album_count.setText("("+DateName.getFiles().size()+")");

        BaseModel file = DateName.getFiles().get(0);

        holder.mImage.setClipToOutline(true);
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

            mImage = itemView.findViewById(R.id.mImage);
            tv_album = itemView.findViewById(R.id.tv_album);
            tv_album_count = itemView.findViewById(R.id.tv_album_count);
        }
    }
}

