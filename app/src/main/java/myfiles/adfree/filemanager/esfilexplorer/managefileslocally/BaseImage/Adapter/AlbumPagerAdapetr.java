package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;



public class AlbumPagerAdapetr extends PagerAdapter{
    private final LayoutInflater inflater;
    private final Activity activity;
    List<BaseModel> files;
    private int directoryPosition = 0;

    public AlbumPagerAdapetr(Activity activity, List<BaseModel> images, int directoryPosition) {
        this.activity = activity;
        this.files = images;
        this.directoryPosition = directoryPosition;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container,int position,Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view,@NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getCount() {
        return files.size();
    }


    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        ImageView imgview;
        View myImageLayout = inflater.inflate(R.layout.list_pager, view, false);
        imgview = myImageLayout.findViewById(R.id.imgImages);
        BaseModel file = files.get(position);

        //   Log.e("LLL_ActualPath: ", file.getPath());

        RequestOptions options = new RequestOptions();
        if (file.getPath().endsWith(".PNG") || file.getPath().endsWith(".png")) {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.skipMemoryCache(true)
                            .priority(Priority.LOW)
                            .format(DecodeFormat.PREFER_ARGB_8888))

                    .into(imgview);
        } else {
            Glide.with(activity)
                    .load(file.getPath())
                    .apply(options.skipMemoryCache(true)
                            .priority(Priority.LOW))

                    .into(imgview);
        }

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    public void addAll(List<BaseModel> imgMain1DownloadList) {
        this.files.addAll(imgMain1DownloadList);
        notifyDataSetChanged();
    }
}

