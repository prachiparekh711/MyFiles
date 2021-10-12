package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity.BaseDocumentActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload.BaseDownloadActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.BaseImageActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.BaseMusicActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.ArrayList;

public class RecentAdapter1 extends RecyclerView.Adapter<RecentAdapter1.MyClassView> {

    private final RecentAdapter1.musicActionListerner actionListerner;
    ArrayList<BaseModel> fileList;
    Activity activity;


    public RecentAdapter1(ArrayList<BaseModel> fileList, Activity activity, RecentAdapter1.musicActionListerner actionListerner) {
        this.fileList = fileList;
        this.activity = activity;
        this.actionListerner = actionListerner;

    }

    @NonNull
    @Override
    public RecentAdapter1.MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_recent, null, false);
        return new RecentAdapter1.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecentAdapter1.MyClassView holder, int position) {

        BaseModel file = fileList.get(position);

            holder.tv_filename.setText(file.getName());

        RequestOptions options = new RequestOptions();
        holder.list_item_image.setClipToOutline(true);
        Bitmap bitmap = null;
        if (!file.isDirectory()) {
            if (file.getPath().endsWith(".mp4") ||
                    file.getPath().endsWith(".mkv")) {
                holder.img_play.setVisibility(View.VISIBLE);
                bitmap = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
                Glide
                        .with(activity)
                        .load(bitmap)
                        .into(holder.list_item_image);
            } else if (file.getPath().endsWith(".mp3") ||
                    file.getPath().endsWith(".wav")) {
                holder.img_play.setVisibility(View.GONE);
                holder.list_item_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.music_icon));
            } else if (file.getPath().endsWith(".png") ||
                    file.getPath().endsWith(".jpeg") ||
                    file.getPath().endsWith(".webp") ||
                    file.getPath().endsWith(".JPG") ||
                    file.getPath().endsWith(".CR2") ||
                    file.getPath().endsWith(".jpg")) {
                holder.img_play.setVisibility(View.GONE);

                Glide.with(activity)
                        .load(file.getPath())
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))

                        .into(holder.list_item_image);

            } else if (file.getPath().endsWith(".docx") ) {
                holder.img_play.setVisibility(View.GONE);
                holder.list_item_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.folder));
            }else if (file.getPath().endsWith(".pdf") ) {
                holder.img_play.setVisibility(View.GONE);
                holder.list_item_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_pdf));
            } else if (file.getPath().endsWith(".ppt") || file.getPath().endsWith(".pptx")) {
                holder.img_play.setVisibility(View.GONE);
                holder.list_item_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_powerpoint));
            } else if (file.getPath().endsWith(".zip")) {
                holder.img_play.setVisibility(View.GONE);
                holder.list_item_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.zip_btn));
            }else if (file.getPath().endsWith(".apk")) {
                holder.img_play.setVisibility(View.GONE);
                try{
                    String APKFilePath = file.getPath();
                    PackageInfo packageInfo = activity.getPackageManager()
                            .getPackageArchiveInfo(APKFilePath, PackageManager.GET_ACTIVITIES);
                    if(packageInfo != null){
                        ApplicationInfo appInfo = packageInfo.applicationInfo;
                        if(Build.VERSION.SDK_INT >= 8){
                            appInfo.sourceDir = APKFilePath;
                            appInfo.publicSourceDir = APKFilePath;
                        }
                        Drawable icon = appInfo.loadIcon(activity.getPackageManager());
                        holder.list_item_image.setImageDrawable(icon);
                    }
                }catch(Exception e){
                    Glide.with(activity)
                            .load(activity.getResources().getDrawable(R.drawable.apps))
                            .apply(options.centerCrop()
                                    .skipMemoryCache(true)
                                    .priority(Priority.LOW)
                                    .format(DecodeFormat.PREFER_ARGB_8888))

                            .into(holder.list_item_image);
                }
            }

        } else {
            holder.img_play.setVisibility(View.GONE);
            holder.list_item_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_other));
        }

        holder.layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                    if(file.getPath().toLowerCase().endsWith(".png") || file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".jpeg")){
                        Intent intent = new Intent(activity, BaseImageActivity.class);
                        activity.startActivity(intent);
                    }else if(file.getPath().endsWith(".zip")){
                        Intent intent = new Intent(activity, BaseZipActivity.class);
                        activity.startActivity(intent);
//                        final File backupDBFolder = new File(file.getPath());
//                        try{
//                            Util.ZipManager.unzip(file.getPath(),backupDBFolder.getParentFile().getAbsolutePath(),activity.getBaseContext());
//                        }catch(IOException e){
//                            e.printStackTrace();
//                        }
                    }else if(file.getPath().toLowerCase().endsWith(".mp3")){
                        Intent intent = new Intent(activity, BaseMusicActivity.class);
                        activity.startActivity(intent);
                    }else if(file.getPath().toLowerCase().endsWith(".apk")){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            if(!activity.getPackageManager().canRequestPackageInstalls()){
                                activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s",activity.getPackageName()))),1234);
                            }else{
                                Intent in = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                in.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                in.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true);
                                in.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                                in.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,activity.getApplicationInfo().packageName);
                                Uri uri = FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName() + ".provider",new File(file.getPath()));
                                in.setData(uri);
                                activity.startActivity(in);
                            }
                        }

                        //Storage Permission

                        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(activity,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE },1);
                        }

                        if(ContextCompat.checkSelfPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(activity,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE },1);
                        }
                    }else if(file.getPath().toLowerCase().endsWith(".mp4")){
                        Intent intent = new Intent(activity, BaseVideoActivity.class);
                        activity.startActivity(intent);
                    }else if(file.getPath().toLowerCase().endsWith(".mp4") || file.getPath().toLowerCase().endsWith(".mp4") || file.getPath().toLowerCase().endsWith(".rtf") ||
                            file.getPath().toLowerCase().endsWith(".ppt") || file.getPath().toLowerCase().endsWith(".pptx") || file.getPath().toLowerCase().endsWith(".doc") ||
                            file.getPath().toLowerCase().endsWith(".docx") || file.getPath().toLowerCase().endsWith(".pdf") || file.getPath().toLowerCase().endsWith(".txt")){
                        Intent intent = new Intent(activity, BaseDocumentActivity.class);
                        activity.startActivity(intent);
                    }else{
                        Intent intent = new Intent(activity, BaseDownloadActivity.class);
                        activity.startActivity(intent);
                    }

            }
        });
    }



    @Override
    public int getItemCount() {
        if(fileList.size()>4)
            return 4;
        else
            return fileList.size();
    }

    public interface musicActionListerner {
        void onMusicItemClicked(View view, int position);
    }

    public class MyClassView extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView list_item_image, img_play;
        TextView tv_filename;
        RelativeLayout layout;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            list_item_image = itemView.findViewById(R.id.list_item_image);
            img_play = itemView.findViewById(R.id.img_play);
            tv_filename = itemView.findViewById(R.id.tv_filename);
            layout = itemView.findViewById(R.id.recentLL);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            RecentAdapter1.this.actionListerner.onMusicItemClicked(v, getAdapterPosition());
        }
    }
}

