package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;


public class CreateAlbumAdapter extends RecyclerView.Adapter<CreateAlbumAdapter.MyClassView> {

    private static final String TAG = ImageParentAdapter.class.getSimpleName();
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    List<Directory<BaseModel>> files;
    Activity activity;
    private ItemClickListener mItemClickListener;

    public CreateAlbumAdapter(List<Directory<BaseModel>> files,Activity activity,ItemClickListener itemClickListener) {
        this.files = files;
        this.activity = activity;
        this.mItemClickListener = itemClickListener;
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {
        Directory<BaseModel> DateName = files.get(position);
        holder.tv_album.setText(DateName.getName());

        holder.tv_album_count.setText(String.valueOf(DateName.getFiles().size()));

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
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_album_list, parent, false);
        return new MyClassView(itemView);
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
