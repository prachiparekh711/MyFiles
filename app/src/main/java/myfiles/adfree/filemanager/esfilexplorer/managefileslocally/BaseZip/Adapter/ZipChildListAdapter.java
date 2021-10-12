package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.List;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.actionLL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.delete;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.headerRL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.more;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.search;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.share;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.tabLayout;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.titleLL;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.view;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity.viewPager;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Adapter.ZipParentAdapter.viewClose;

public class ZipChildListAdapter extends RecyclerView.Adapter<ZipChildListAdapter.MyClassView> {

    List<BaseModel> mVideoList;
    Activity activity;
    int direPos = 0;
    SelectionInterface selectionInterface;
    String unZipPath;
    String path;
    private static final String EXTRA_DIRECTORY = "com.blackmoonit.intent.extra.DIRECTORY";

    public ZipChildListAdapter(List<BaseModel> mVideoList,Activity activity,int direPos,SelectionInterface anInterface,String path) {
        this.mVideoList = mVideoList;
        this.activity = activity;
        this.direPos = direPos;
        this.selectionInterface=anInterface;
        this.unZipPath=path;
    }

    @NonNull
    @Override
    public ZipChildListAdapter.MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.list_zip_list, parent, false);
        return new ZipChildListAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ZipChildListAdapter.MyClassView holder,int position) {
        BaseModel file = mVideoList.get(position);

        holder.tv_image_name.setText(file.getName());

        holder.mImage.setClipToOutline(true);

        holder.tv_image_date.setText(file.getDate());


        holder.tv_image_size.setText(Util.getSize(file.getSize()));

        //*****************************

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable == true){
            holder.itemView.setOnClickListener(v -> {
                BaseZipActivity.disableCard();
                path=file.getPath();
                Intent theIntent = new Intent(Intent.ACTION_VIEW);
                theIntent.setDataAndType(Uri.fromFile(new File(path)),"application/zip");
                theIntent.putExtra(EXTRA_DIRECTORY,unZipPath); //optional default location (version 7.4+)
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
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseZipActivity.disableCard();

                if(Util.mSelectedZipList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedZipList.remove(file.getPath());

//                    selectionInterface.onDeselectDate(directoryPosition);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedZipList.add(file.getPath());
//                    for(int i=0;i<images.size();i++){
//                        if(mSelectedImgList.contains(images.get(i).getPath())){
//                            if(position==directoryPosition)
//                            match++;
//                        }
//                    }
                }
                if(Util.mSelectedZipList.size() == 0){
                    changeToOriginalView();
                }else{
                    BaseZipActivity.count.setText(Util.mSelectedZipList.size() + " Selected");
                }
//                if(match==images.size()){
//                    selectionInterface.onSelectDate(directoryPosition);
//                }
            });
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                BaseZipActivity.disableCard();

                viewClose=false;
                Util.clickEnable=false;
                Util.showCircle=true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedZipList.add(file.getPath());
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        selectionInterface.onSelectItem();
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


        for(int i = 0;i < Util.mSelectedZipList.size();i++){
            if(Util.mSelectedZipList.get(i).equals(file.getPath())){
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
        return mVideoList.size();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        TextView tv_image_name,tv_image_date,tv_image_size;
        ImageView mImage;
        ImageView mSelect,mDeselect;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            tv_image_name = itemView.findViewById(R.id.tv_image_name);
            tv_image_date = itemView.findViewById(R.id.tv_image_date);
            tv_image_size = itemView.findViewById(R.id.tv_image_size);
            mImage = itemView.findViewById(R.id.imgAlbum);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
        }
    }

    public void changeHomeView(){
        viewPager.setPagingEnabled(false);
        BaseZipActivity.titleLL.setVisibility(View.GONE);
        BaseZipActivity.actionLL.setVisibility(View.VISIBLE);
        BaseZipActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        BaseZipActivity.tabLayout.setVisibility(View.GONE);
        BaseZipActivity.delete.setVisibility(View.VISIBLE);
        BaseZipActivity.share.setVisibility(View.VISIBLE);
        BaseZipActivity.view.setVisibility(View.GONE);
        BaseZipActivity.search.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseZipActivity.count.setText(Util.mSelectedZipList.size() + " Selected");

    }


    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true);
        Util.mSelectedImgList.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        tabLayout.setVisibility(View.VISIBLE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.themeColor));
        delete.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        notifyDataSetChanged();

    }


}


