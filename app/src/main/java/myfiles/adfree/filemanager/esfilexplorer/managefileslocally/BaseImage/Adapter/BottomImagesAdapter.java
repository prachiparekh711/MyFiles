package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Interface.BottomImgsInterface;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;


public class BottomImagesAdapter extends RecyclerView.Adapter<BottomImagesAdapter.ViewHolder> {

    List<BaseModel> files;
    private final LayoutInflater inflater;
    private int directoryPosition = 0;
    private final Activity activity;
    BottomImgsInterface imgsInterface;
    public BottomImagesAdapter(Activity activity, List<BaseModel> images, int directoryPosition,BottomImgsInterface anInterface) {
        this.activity = activity;
        this.files = images;
        this.directoryPosition = directoryPosition;
        inflater = LayoutInflater.from(activity);
        this.imgsInterface=anInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bottom_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        BaseModel file = files.get(position);

//        //   Log.e("LLL_ActualPath: ",file.getPath());
        viewHolder.imgview.setClipToOutline(true);
        RequestOptions options = new RequestOptions();
        if (file.getPath().endsWith(".PNG") || file.getPath().endsWith(".png")) {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(viewHolder.imgview);
        } else {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(viewHolder.imgview);
        }

        viewHolder.imgview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                imgsInterface.onImgClick(position);
            }
        });


    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgview;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            imgview = view.findViewById(R.id.imgImages);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
