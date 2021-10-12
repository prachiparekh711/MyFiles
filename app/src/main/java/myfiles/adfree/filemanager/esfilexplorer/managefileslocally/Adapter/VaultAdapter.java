package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity.VaultActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.SearchImageViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.SearchMusicViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.SearchVideoViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.List;


public class VaultAdapter extends RecyclerView.Adapter<VaultAdapter.MyClassView> {

    List<String> images;
    Activity activity;
    public static boolean viewClose=false;

    public VaultAdapter(List<String> images,Activity activity) {
        this.images = images;
        this.activity = activity;
    }

    @NonNull
    @Override
    public VaultAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.child_item_duplicate, parent, false);

        return new VaultAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull VaultAdapter.MyClassView holder,int position) {

        File file = new File( images.get(position));
//            //   Log.e("Selected List:",mSelectedVaultList.size() + "");

        holder.mImage.setClipToOutline(true);
        holder.mSize.setText(Util.getSize(file.length()));
        if(file.getName().startsWith("."))
            holder.mFolder.setText(file.getName().substring(1));
        else
            holder.mFolder.setText(file.getName());

        RequestOptions options = new RequestOptions();
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
                if(file.getPath().toLowerCase().endsWith(".png") || file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".jpeg")){
                    Intent intent = new Intent(activity, SearchImageViewActivity.class);
                    intent.putExtra("Searched",file.getPath());
                    intent.putExtra("From","Vault");
                    activity.startActivity(intent);
                }else if(file.getPath().endsWith(".zip")){

                    Toast.makeText(activity,"Unhide to extract zip files.",Toast.LENGTH_SHORT).show();
//                    final File backupDBFolder = new File(file.getPath());
//                    try{
//                        Util.ZipManager.unzip(file.getPath(), backupDBFolder.getParentFile().getAbsolutePath(),activity.getBaseContext());
//                    }catch(IOException e){
//                        e.printStackTrace();
//                    }
                }else if(file.getPath().toLowerCase().endsWith(".mp3")){
                    Intent intent = new Intent(activity, SearchMusicViewActivity.class);
                    intent.putExtra("Searched",file.getPath());
                    intent.putExtra("From","Vault");
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
                            Uri uri= FileProvider.getUriForFile(activity,activity.getApplicationContext().getPackageName() + ".provider",new File(file.getPath()));
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
                    Intent intent = new Intent(activity, SearchVideoViewActivity.class);
                    intent.putExtra("Searched",file.getPath());
                    intent.putExtra("From","Vault");
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
                        Toast.makeText(activity,"Enable to open file !!!", Toast.LENGTH_SHORT);
                    }
                }

            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                if(Util.mSelectedVaultList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedVaultList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedVaultList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedVaultList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedVaultList.size() == 0){
                    changeToOriginalView();
                }else{
                    VaultActivity.count.setText(Util.mSelectedVaultList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedVaultList.add(file.getPath());
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


        for(int i = 0;i < Util.mSelectedVaultList.size();i++){
            if(Util.mSelectedVaultList.get(i).equals(file.getPath())){
                holder.mSelect.setVisibility(View.VISIBLE);
                holder.mDeselect.setVisibility(View.GONE);
            }
        }
        if(viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView mImage,player;
        ImageView mSelect,mDeselect;
        TextView mSize,mFolder;
        public MyClassView(@NonNull View itemView) {
            super(itemView);
            player = itemView.findViewById(R.id.img_play);
            mSize = itemView.findViewById(R.id.mSize);
            mFolder = itemView.findViewById(R.id.mFolder);
            mImage = itemView.findViewById(R.id.mImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
        }
    }

    public void changeHomeView(){
        VaultActivity.titleLL.setVisibility(View.GONE);
        VaultActivity.actionLL.setVisibility(View.VISIBLE);
        VaultActivity.mHeaderRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        VaultActivity.delete.setVisibility(View.VISIBLE);
        VaultActivity.view.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(VaultActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        VaultActivity.count.setText(Util.mSelectedVaultList.size() + " Selected");

    }

    public void changeToOriginalView(){
        Util.mSelectedVaultList.clear();
        VaultActivity.titleLL.setVisibility(View.VISIBLE);
        VaultActivity.actionLL.setVisibility(View.GONE);
        VaultActivity.mHeaderRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        VaultActivity.delete.setVisibility(View.GONE);
        VaultActivity.view.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(VaultActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        notifyDataSetChanged();

    }

}
