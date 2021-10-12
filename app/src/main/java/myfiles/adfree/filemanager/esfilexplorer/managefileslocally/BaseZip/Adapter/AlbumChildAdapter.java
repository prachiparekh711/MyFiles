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

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.ViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.io.File;
import java.util.List;

public class AlbumChildAdapter extends RecyclerView.Adapter<AlbumChildAdapter.MyClassView>{

    List<BaseModel> images;
    Activity activity;
    int direPos = 0;
    public static boolean viewClose=false;
    String unZipPath;
    private static final String EXTRA_DIRECTORY = "com.blackmoonit.intent.extra.DIRECTORY";
    boolean IsUnzip;
    String path;

    public AlbumChildAdapter(List<BaseModel> images,Activity activity,int i,String path){
        this.images = images;
        this.activity = activity;
        this.direPos = i;
        this.unZipPath=path;
    }

    @Override
    public AlbumChildAdapter.MyClassView onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_zip_child_view,parent,false);

        return new AlbumChildAdapter.MyClassView(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull AlbumChildAdapter.MyClassView holder,int position){
        BaseModel file = images.get(position);
        holder.mImage.setClipToOutline(true);       

        holder.mZipName.setText(file.getName());

        if(Util.isAllSelected){
            holder.itemView.setEnabled(false);
            Util.clickEnable=false;
        }

        if(Util.clickEnable==true){
            holder.itemView.setOnClickListener(v -> {
                ViewAlbumActivity.disableCard();

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
                ViewAlbumActivity.disableCard();

                if(Util.mSelectedZipList.contains(file.getPath())){
                    holder.mDeselect.setVisibility(View.VISIBLE);
                    holder.mSelect.setVisibility(View.GONE);
                    Util.mSelectedZipList.remove(file.getPath());
//                    selectionInterface.onDeselectDate(direPos);
                }else{
                    holder.mSelect.setVisibility(View.VISIBLE);
                    holder.mDeselect.setVisibility(View.GONE);
                    Util.mSelectedZipList.add(file.getPath());
                    if(Util.mSelectedZipList.containsAll(images)){
//                        selectionInterface.onSelectDate(direPos);
                    }
                }
                if(Util.mSelectedZipList.size()==0){
                    changeToOriginalView();
                }else{
                    ViewAlbumActivity.count.setText(Util.mSelectedZipList.size() + " Selected");
                }
                //   Log.e("Selected list :",String.valueOf(Util.mSelectedZipList.size()));
            });

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                ViewAlbumActivity.disableCard();

                viewClose = false;
                Util.clickEnable = false;
                Util.showCircle = true;
                holder.itemView.setEnabled(false);
                holder.mSelect.setVisibility(View.VISIBLE);
                Util.mSelectedZipList.add(file.getPath());
                //   Log.e("Selected list :",String.valueOf(Util.mSelectedZipList.size()));
                activity.runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        notifyDataSetChanged();
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
    public int getItemCount(){

        return images.size();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    public class MyClassView extends RecyclerView.ViewHolder{

        ImageView mImage;
        ImageView mSelect, mDeselect;
        TextView mZipName;

        public MyClassView(@NonNull View itemView){
            super(itemView);
            mImage = itemView.findViewById(R.id.albumImage);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            mZipName = itemView.findViewById(R.id.zipName);
        }
    }

    public void changeHomeView(){

        ViewAlbumActivity.l1.setVisibility(View.GONE);
        ViewAlbumActivity.l3.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l2.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l4.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.white)));
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.themeColor));

        ViewAlbumActivity.count.setText(Util.mSelectedZipList.size() + " Selected");

    }


    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedZipList.clear();
        ViewAlbumActivity.l1.setVisibility(View.VISIBLE);
        ViewAlbumActivity.l3.setVisibility(View.GONE);
        ViewAlbumActivity.l2.setVisibility(View.GONE);
        ViewAlbumActivity.l4.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(ViewAlbumActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        ViewAlbumActivity.toolbar.setBackgroundColor(activity.getResources().getColor(R.color.white));

    }


}


