package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Adapter;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.AlbumMusicPlayerActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.MusicPlayerActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;

public class MusicPlayerAdapter extends RecyclerView.Adapter<MusicPlayerAdapter.ViewHolder> implements View.OnTouchListener{
    List<BaseModel> files;
    private final LayoutInflater inflater;
    private int finalPosition = 0;
    Activity activity;
    public static int playingPosition=0;
    static boolean setCustom=false;
    String from="";

    public MusicPlayerAdapter(Activity activity, List<BaseModel> images, int directoryPosition,String from) {
        this.activity = activity;
        this.files = images;
        this.finalPosition = directoryPosition;
        inflater = LayoutInflater.from(activity);
        this.from=from;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.music_pager, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,int position) {
        BaseModel file;
        viewHolder.rootRL.setOnTouchListener(this);
        if(setCustom)
            file = files.get(playingPosition);
        else
            file = files.get(position);

    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            if(from.equals("All")){
                if(MusicPlayerActivity.cardOptions.getVisibility() == View.VISIBLE)
                    MusicPlayerActivity.cardOptions.setVisibility(View.GONE);
            }else{
                if(AlbumMusicPlayerActivity.cardOptions1.getVisibility() == View.VISIBLE)
                    AlbumMusicPlayerActivity.cardOptions1.setVisibility(View.GONE);
            }

            if(Util.isBottomPlayerExpanded){
                if(from.equals("All"))
                    MusicPlayerActivity.collapse(MusicPlayerActivity.mBottomRL);
                else
                    MusicPlayerActivity.collapse(AlbumMusicPlayerActivity.mBottomRL1);
            }else{
                if(from.equals("All"))
                    MusicPlayerActivity.expand(MusicPlayerActivity.mBottomRL);
                else
                    MusicPlayerActivity.expand(AlbumMusicPlayerActivity.mBottomRL1);
                bottomHandler();
            }
        }
        return true;
    }

    public void bottomHandler(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(from.equals("All"))
                    MusicPlayerActivity.collapse(MusicPlayerActivity.mBottomRL);
                else
                    MusicPlayerActivity.collapse(AlbumMusicPlayerActivity.mBottomRL1);
            }
        }, 4000);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mBack10,mPlayBack,mPlay,mPlayforward,mForward10,mPause;
        TextView startTime,endTime;
        SeekBar seekbar;
        RelativeLayout mBottomRL,rootRL;

        public ViewHolder(View v) {
            super(v);
            mBack10=v.findViewById(R.id.mBack10);
            mPlayBack=v.findViewById(R.id.mPlayBack);
            mPlay=v.findViewById(R.id.mPlay);
            mPlayforward=v.findViewById(R.id.mPlayforward);
            mForward10=v.findViewById(R.id.mForward10);
            mPause=v.findViewById(R.id.mPause);
            startTime=v.findViewById(R.id.startTime);
            endTime=v.findViewById(R.id.endTime);
            seekbar=v.findViewById(R.id.seekBar);
            mBottomRL=v.findViewById(R.id.bootomRL);
            rootRL=v.findViewById(R.id.rl2);
        }
    }

    @Override
    public int getItemCount() {
        return files.size();
    }


}
