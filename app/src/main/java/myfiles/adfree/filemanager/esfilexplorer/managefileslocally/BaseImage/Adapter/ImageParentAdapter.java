package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.BaseImageActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ImageParentAdapter extends RecyclerView.Adapter<ImageParentAdapter.MyClassView> {

    List<Directory<BaseModel>> files;
    Activity activity;
    SelectionInterface selectionInterface;
    ImageChildAdapter imageChildAdapter;
    ImageChildListAdapter imageChildListAdapter;
    public static int mPosToDeselect=-1;
    public static int mPosToSelect=-1;
    public static boolean viewClose=false;

    public ImageParentAdapter(List<Directory<BaseModel>> files,Activity activity) {
        this.files = files;
        this.activity = activity;
        selectionInterface=new SelectionInterface(){
            @Override
            public void onSelectItem(){
                notifyDataSetChanged();
//                AlbumFragment.albumChildAdapter.notifyDataSetChanged();
//                if(AlbumFragment.albumChildListAdapter!=null)
//                AlbumFragment.albumChildListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDeselectDate(int pos){
                mPosToDeselect=pos;
                notifyDataSetChanged();
            }

            @Override
            public void onSelectDate(int position){
                mPosToSelect=position;
                notifyDataSetChanged();
            }
        };
    }



    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.date_layout, parent, false);
        return new MyClassView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {

        Directory<BaseModel> DateName = files.get(position);
        try {
            String dateString = DateName.getName();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.yyyy");
            Date date = sdf.parse(dateString);

            long startDate = date.getTime();
            String dateTime = getFormattedDate(startDate);
            holder.dateTv.setText(dateTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        activity.runOnUiThread(() -> {

            if (Util.VIEW_TYPE=="Grid") {
                final GridLayoutManager layoutManager = new GridLayoutManager(activity, Util.COLUMN_TYPE);
                holder.rvImages.setLayoutManager(layoutManager);

                imageChildAdapter = new ImageChildAdapter(DateName.getFiles(), activity, position,selectionInterface);
                holder.rvImages.setAdapter(imageChildAdapter);
            } else {
                holder.rvImages.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.VERTICAL,false));
                imageChildListAdapter = new ImageChildListAdapter(DateName.getFiles(), activity, position,selectionInterface);
                holder.rvImages.setAdapter(imageChildListAdapter);
            }
        });
        holder.rvImages.setNestedScrollingEnabled(false);


        if(Util.isAllSelected){
            holder.mSelect.setVisibility(View.VISIBLE);
            holder.mDeselect.setVisibility(View.GONE);
        }

        if(Util.showCircle){
            holder.mDeselect.setVisibility(View.VISIBLE);
        }else{
            holder.mDeselect.setVisibility(View.GONE);
        }

        if(mPosToDeselect==position){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.VISIBLE);
        }
        if(mPosToSelect==position){
            holder.mSelect.setVisibility(View.VISIBLE);
            holder.mDeselect.setVisibility(View.GONE);
        }
        if(viewClose){
            holder.mSelect.setVisibility(View.GONE);
            holder.mDeselect.setVisibility(View.GONE);
        }

//        holder.mDateSelectRL.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                if(holder.mSelect.getVisibility()==View.VISIBLE){
//                    holder.mSelect.setVisibility(View.GONE);
//                    holder.mDeselect.setVisibility(View.VISIBLE);
//
//                    for(int i = 0;i < DateName.getFiles().size();i++){
//                        List<BaseModel> imgList=DateName.getFiles();
//                        for(int j=0;j<imgList.size();j++){
//                            mSelectedImgList.remove(imgList.get(j).getPath());
//                        }
//                    }
//                }else{
//                    holder.mSelect.setVisibility(View.VISIBLE);
//                    holder.mDeselect.setVisibility(View.GONE);
//
//                    for(int i = 0;i < DateName.getFiles().size();i++){
//                        List<BaseModel> imgList=DateName.getFiles();
//                        for(int j=0;j<imgList.size();j++){
//                            mSelectedImgList.remove(imgList.get(j).getPath());
//                        }
//                    }
//                    for(int i = 0;i < DateName.getFiles().size();i++){
//                        List<BaseModel> imgList=DateName.getFiles();
//                        for(int j=0;j<imgList.size();j++){
//                            mSelectedImgList.add(imgList.get(j).getPath());
//                        }
//                    }
//
//                }
//                if(mSelectedImgList.size()==0){
//                    changeToOriginalView();
//                }else{
//                    count.setText(mSelectedImgList.size() + " Selected");
//                }
//                //   Log.e("Selected List:",mSelectedImgList.size() + "");
//                activity.runOnUiThread(new Runnable(){
//                    @Override
//                    public void run(){
//                        if(VIEW_TYPE=="Grid"){
//                            imageChildAdapter = new ImageChildAdapter(DateName.getFiles(),activity,position,selectionInterface);
//                            holder.rvImages.setAdapter(imageChildAdapter);
//                        }else{
//                            imageChildListAdapter = new ImageChildListAdapter(DateName.getFiles(),activity,position,selectionInterface);
//                            holder.rvImages.setAdapter(imageChildListAdapter);
//                        }
//                    }
//                });
//            }
//        });

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

    public void clear() {
        this.files.clear();
        notifyDataSetChanged();
    }

    public class MyClassView extends RecyclerView.ViewHolder {

        TextView dateTv;
        RecyclerView rvImages;
        ImageView mSelect,mDeselect;
        RelativeLayout mDateSelectRL;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            dateTv = itemView.findViewById(R.id.dateTv);
            rvImages = itemView.findViewById(R.id.rvImages);
            mSelect = itemView.findViewById(R.id.img_select);
            mDeselect = itemView.findViewById(R.id.img_unselect);
            mDateSelectRL = itemView.findViewById(R.id.dateSelectRL);
        }
    }

    public String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "dd MMM yyyy";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today ";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM yyyy", smsTime).toString();
        }
    }

    public void changeToOriginalView(){
        Util.mSelectedImgList.clear();
        BaseImageActivity.titleLL.setVisibility(View.VISIBLE);
        BaseImageActivity.actionLL.setVisibility(View.GONE);
        BaseImageActivity.headerRL.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
        BaseImageActivity.tabLayout.setVisibility(View.VISIBLE);
//        tabLayout.setBackgroundColor(activity.getResources().getColor(R.color.header_color));
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Objects.requireNonNull(tab).getCustomView().findViewById(R.id.img_tab).setVisibility(View.GONE);
//            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
//        }
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(activity.getBaseContext(), R.color.themeColor));
        BaseImageActivity.delete.setVisibility(View.GONE);
        BaseImageActivity.share.setVisibility(View.GONE);
        BaseImageActivity.view.setVisibility(View.VISIBLE);
        BaseImageActivity.search.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(BaseImageActivity.more, ColorStateList.valueOf(activity.getResources().getColor(R.color.themeColor)));
        viewClose=true;
        notifyDataSetChanged();

    }

}
