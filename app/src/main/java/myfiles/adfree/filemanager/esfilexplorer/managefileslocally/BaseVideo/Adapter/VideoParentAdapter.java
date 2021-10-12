package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Interface.SelectionInterface;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VideoParentAdapter extends RecyclerView.Adapter<VideoParentAdapter.MyClassView> {

    List<Directory<BaseModel>> files;
    Activity activity;
    VideoChildAdapter mVideoChildAdapter;
    VideoChildListAdapter mVideoChildListAdapter;
    public static boolean viewClose=false;
    public static int mPosToDeselect=-1;
    public static int mPosToSelect=-1;
    SelectionInterface selectionInterface;

    public VideoParentAdapter(List<Directory<BaseModel>> files,Activity activity) {
//        //   Log.e("LLL Parent size:",String.valueOf(files.size()));
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
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.video_date_layout, parent, false);
        return new MyClassView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder,int position) {

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

//                //   Log.e("Child Ssize:",String.valueOf(DateName.getFiles().size()));
                mVideoChildAdapter = new VideoChildAdapter(DateName.getFiles(), activity, position,selectionInterface);
                holder.rvImages.setAdapter(mVideoChildAdapter);
            } else {
                holder.rvImages.setLayoutManager(new LinearLayoutManager(activity,RecyclerView.VERTICAL,false));
                mVideoChildListAdapter = new VideoChildListAdapter(DateName.getFiles(), activity, position,selectionInterface);
                holder.rvImages.setAdapter(mVideoChildListAdapter);
            }
        });
        holder.rvImages.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {

        return files.size();
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



}

