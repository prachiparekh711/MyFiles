package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Drive;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class AllGdriveAdapter extends RecyclerView.Adapter<AllGdriveAdapter.MyClassView> {

    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;
    ArrayList<BaseModel> gDriveArrayList;
    Activity activity;
    String name;

    public AllGdriveAdapter(ArrayList<BaseModel> gDriveArrayList, Activity activity, String name) {
        this.gDriveArrayList = gDriveArrayList;
        this.activity = activity;
        this.name = name;
    }

    @NonNull
    @Override
    public MyClassView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.child_list_duplicate, null, false);
        return new MyClassView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassView holder, int position) {
        BaseModel gDrive = gDriveArrayList.get(position);

        RequestOptions options = new RequestOptions();
        holder.selectRL.setVisibility(View.GONE);
        if (gDrive.getName().endsWith(".docx") || gDrive.getName().endsWith(".doc")) {
//            holder.player.setVisibility(View.GONE);
            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_word))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().endsWith(".xls")) {

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_excel))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().endsWith(".pdf")) {

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_pdf))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))
                    .into(holder.list_item_image);
        }else if (gDrive.getName().endsWith(".ppt") || gDrive.getName().endsWith(".pptx")) {

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_powerpoint))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().toLowerCase().endsWith(".png") || gDrive.getName().toLowerCase().endsWith(".jpg") || gDrive.getName().toLowerCase().endsWith(".jpeg")) {

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.image))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().endsWith(".zip")) {

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.zip_btn))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().toLowerCase().endsWith(".mp3")) {


            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.music_icon))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().toLowerCase().endsWith(".apk")) {

                Glide.with(activity)
                        .load(activity.getResources().getDrawable(R.drawable.apps))
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))

                        .into(holder.list_item_image);

        }else if (gDrive.getName().toLowerCase().endsWith(".txt") || gDrive.getName().toLowerCase().endsWith(".rtf")) {

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_other))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else if (gDrive.getName().toLowerCase().endsWith(".mp4") ) {


            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.video))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }else{

            Glide.with(activity)
                    .load(activity.getResources().getDrawable(R.drawable.ic_other))
                    .apply(options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(holder.list_item_image);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/a/google.com/uc?id=" + gDrive.getDid() + "&export=download"));
                activity.startActivity(browserIntent);
            }
        });

        holder.tv_filesize.setText(Util.getFileSize(gDrive.getSize()));
        holder.tv_filename.setText(gDrive.getName());
    }

    @Override
    public int getItemCount() {
        return gDriveArrayList.size();
    }


    public class MyClassView extends RecyclerView.ViewHolder {

        ImageView list_item_image;
        TextView tv_filename, tv_filesize;
        RelativeLayout  selectRL;

        public MyClassView(@NonNull View itemView) {
            super(itemView);

            list_item_image = itemView.findViewById(R.id.mImage);
            tv_filename = itemView.findViewById(R.id.mFolder);
            tv_filesize = itemView.findViewById(R.id.mSize);
            selectRL = itemView.findViewById(R.id.selectRL);
        }
    }
}
