package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.SearchImageViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.SearchMusicViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.SearchVideoViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload.BaseDownloadActivity.startAnim;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload.BaseDownloadActivity.stopAnim;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyClassView>{

    ArrayList<BaseModel> mTrashList;
    Activity activity;
    String unZipPAth;
    boolean IsUnzip;
    String path1;
    private static final String EXTRA_DIRECTORY = "com.blackmoonit.intent.extra.DIRECTORY";
    public DownloadAdapter(ArrayList<BaseModel> images,Activity activity) {
        this.activity = activity;
        mTrashList=images;
    }

    @NonNull
    @Override
    public DownloadAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.trash_grid_layout, parent, false);
        return new DownloadAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull DownloadAdapter.MyClassView holder,int position) {

        BaseModel file = mTrashList.get(position);

        holder.mImage.setClipToOutline(true);

        String path=file.getPath();
        RequestOptions options = new RequestOptions();
        holder.tv_image_name.setText(path.substring(file.getPath().lastIndexOf("/") + 1));
        if (file.getPath().endsWith(".docx") || file.getPath().endsWith(".doc")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_word))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".xls")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_excel))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".pdf")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_pdf))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".ppt") || file.getPath().endsWith(".pptx")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_powerpoint))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".png") || file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".jpeg")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().endsWith(".zip")) {

            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.zip_btn))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".mp3")) {

            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.music_icon))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".apk")) {

            holder.player.setVisibility(View.GONE);
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
                    holder.mImage.setImageDrawable(icon);
                }
            }catch(Exception e){
                Glide.with(activity)
                        .load(activity.getResources().getDrawable(R.drawable.apps))
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))

                        .into(holder.mImage);
            }
        }else if (file.getPath().toLowerCase().endsWith(".txt") || file.getPath().toLowerCase().endsWith(".rtf")) {
            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_other))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else if (file.getPath().toLowerCase().endsWith(".mp4") ) {
            holder.player.setVisibility(View.VISIBLE);

            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.mImage);
        }else{
            holder.player.setVisibility(View.GONE);
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
                BaseDownloadActivity.disableCard();
                ArrayList<BaseModel> recentList = SharedPreference.getRecentList(activity);    file.setRecentDate(String.valueOf(System.currentTimeMillis()));
               SharedPreference.setRecentList(activity, new ArrayList<>());  recentList.add(0,file);

                SharedPreference.setRecentList(activity, recentList);
                if(file.getPath().toLowerCase().endsWith(".png") || file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".jpeg")){
                    Intent intent = new Intent(activity,SearchImageViewActivity.class);
                    intent.putExtra("Searched",file);
                    intent.putExtra("From","Search");
                    activity.startActivity(intent);
                }else if(file.getPath().endsWith(".zip")){
                    path1=file.getPath();
                    // new AsynchUnzip().execute((Void[]) null);
                    unZipPAth=file.getPath();Intent theIntent = new Intent(Intent.ACTION_VIEW);
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
                }else if(file.getPath().toLowerCase().endsWith(".mp3")){
                    Intent intent = new Intent(activity,SearchMusicViewActivity.class);
                    intent.putExtra("Searched",file);
                    intent.putExtra("From","Search");
                    activity.startActivity(intent);
                }else if(file.getPath().toLowerCase().endsWith(".apk")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!activity.getPackageManager().canRequestPackageInstalls()) {
                            activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", activity.getPackageName()))), 1234);
                        } else {
                            Intent in=new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            in.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                            in.setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION );
                            in.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true);
                            in.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                            in.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,activity.getApplicationInfo().packageName);
                            Uri uri=FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName() + ".provider",new File(file.getPath()));
                            in.setData(uri);
                            activity.startActivity(in);
                        }
                    }

                    //Storage Permission

                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }

                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }else if (file.getPath().toLowerCase().endsWith(".mp4") ) {
                    Intent intent = new Intent(activity,SearchVideoViewActivity.class);
                    intent.putExtra("Searched",file);
                    intent.putExtra("From","Search");
                    activity.startActivity(intent);
                }else{
                    Intent in = new Intent(Intent.ACTION_VIEW);
                    Uri uri = FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName() + ".provider",new File(file.getPath()));
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
                    }catch(Exception e){
                        BaseDownloadActivity.fileInfo(new File(file.getPath()),activity);
                    }
                }
                
            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseDownloadActivity.disableCard();
                if(Util.mSelectedDownloadList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedDownloadList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedDownloadList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedDownloadList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedDownloadList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseDownloadActivity.count.setText(Util.mSelectedDownloadList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                BaseDownloadActivity.disableCard();

                DownloadListAdapter.viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedDownloadList.add(file.getPath());
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        notifyDataSetChanged();
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


        for(int i = 0;i < Util.mSelectedDownloadList.size();i++){
            if(Util.mSelectedDownloadList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(DownloadListAdapter.viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTrashList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView mImage;
        ImageView mSelect,mDeselect;
        TextView tv_image_name;
        ImageView player;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.imgAlbum);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            tv_image_name = itemView.findViewById(R.id.tv_album);
            player = itemView.findViewById(R.id.player);
        }
    }

    public void changeHomeView(){
        BaseDownloadActivity.titleLL.setVisibility(View.GONE);
        BaseDownloadActivity.actionLL.setVisibility(View.VISIBLE);
        BaseDownloadActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));

        BaseDownloadActivity.delete.setVisibility(View.VISIBLE);
        BaseDownloadActivity.share.setVisibility(View.VISIBLE);
        BaseDownloadActivity.view.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(BaseDownloadActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseDownloadActivity.count.setText(Util.mSelectedDownloadList.size() + " Selected");

    }

    public void changeToOriginalView(){
        Util.mSelectedDownloadList.clear();
        BaseDownloadActivity.titleLL.setVisibility(View.VISIBLE);
        BaseDownloadActivity.actionLL.setVisibility(View.GONE);
        BaseDownloadActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseDownloadActivity.delete.setVisibility(View.GONE);
        BaseDownloadActivity.share.setVisibility(View.GONE);
        BaseDownloadActivity.view.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(BaseDownloadActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        DownloadListAdapter.viewClose=true;
        notifyDataSetChanged();

    }

    public class AsynchUnzip extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                startAnim();
            } catch (Exception e) {
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final File backupDBFolder = new File(unZipPAth);
            try{
                IsUnzip=Util.ZipManager.unzip(path1, backupDBFolder.getParentFile().getAbsolutePath(),activity.getBaseContext());
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                stopAnim();

            }catch (Exception e){}
        }
    }

}
