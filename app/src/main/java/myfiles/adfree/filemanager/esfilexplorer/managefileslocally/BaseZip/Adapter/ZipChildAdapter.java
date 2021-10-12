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

public class ZipChildAdapter extends RecyclerView.Adapter<ZipChildAdapter.MyClassView> {

    List<BaseModel> mZipList;
    Activity activity;
    int directoryPosition;
    SelectionInterface selectionInterface;
    String unZipPAth;
    boolean IsUnzip;
    String path;
    private static final String EXTRA_DIRECTORY = "com.blackmoonit.intent.extra.DIRECTORY";

    public ZipChildAdapter(List<BaseModel> mZipList,Activity activity,int directoryPosition,SelectionInterface anInterface,String path) {
        this.mZipList = mZipList;
        this.activity = activity;
        this.directoryPosition = directoryPosition;
        this.selectionInterface=anInterface;
        this.unZipPAth=path;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.all_zip_view, parent, false);

        return new MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ZipChildAdapter.MyClassView holder,int position) {

        BaseModel file = mZipList.get(position);
        holder.mImage.setClipToOutline(true);
//        //   Log.e("Child:",file.getName());
        holder.zipName.setText(file.getName());

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

//                // new AsynchUnzip().execute((Void[]) null);

            });
        }else{
            holder.itemView.setEnabled(true);
            holder.itemView.setOnClickListener(v -> {
                BaseZipActivity.disableCard();

                if(Util.mSelectedZipList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedZipList.remove(file.getPath());

//                  selectionInterface.onDeselectDate(directoryPosition);
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

                //   Log.e("Selected List:",Util.mSelectedZipList.size() + "");
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
                //   Log.e("Long click:",file.getPath());
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

    public void changeHomeView(){
        viewPager.setPagingEnabled(false);
        titleLL.setVisibility(View.GONE);
        actionLL.setVisibility(View.VISIBLE);
        headerRL.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));
        tabLayout.setVisibility(View.GONE);
        delete.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        BaseZipActivity.count.setText(Util.mSelectedZipList.size() + " Selected");
    }


    @Override
    public int getItemCount() {
        return mZipList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView mImage;
        ImageView mSelect,mDeselect;
        TextView zipName;

        public MyClassView(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.albumImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            zipName = itemView.findViewById(R.id.zipName);
        }
    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true);
        Util.mSelectedImgList.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        tabLayout.setVisibility(View.VISIBLE);
        delete.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        notifyDataSetChanged();

    }




}

