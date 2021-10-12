package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Adapter;

import android.app.Activity;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.VideoViewActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.VideoViewAlbumActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;


public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.ViewHolder> {
    List<BaseModel> files;
    private final LayoutInflater inflater;
    private int finalPosition = 0;
    Activity activity;
    public static int playingPosition=0;
    static boolean setCustom=false;
    public static boolean isRotate=false;
    String from="";

    public VideoViewAdapter(Activity activity, List<BaseModel> images, int directoryPosition,String from) {
        this.activity = activity;
        this.files = images;
        this.finalPosition = directoryPosition;
        inflater = LayoutInflater.from(activity);
        this.from=from;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.video_pager, viewGroup, false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,int position) {
        BaseModel file;
        if(setCustom)
            file = files.get(playingPosition);
        else
            file = files.get(position);
        Uri uri = Uri.parse(file.getPath());
        viewHolder. mVideo.setVideoURI(uri);

        viewHolder.mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            @Override
            public void onPrepared(MediaPlayer mp){
                viewHolder.mVideo.start();
                if(viewHolder.mVideo.isPlaying()){
                    viewHolder.mPlay.setVisibility(View.GONE);
                    viewHolder.mPause.setVisibility(View.VISIBLE);
                }

                int duration = viewHolder. mVideo.getDuration()/1000;
                viewHolder.seekbar.setMax(viewHolder.mVideo.getDuration());
//                //   Log.e("Duration max",String.valueOf(viewHolder.seekbar.getMax()));
                int hours = duration / 3600;
                int minutes = (duration / 60) - (hours * 60);
                int seconds = duration - (hours * 3600) - (minutes * 60) ;
                String formatted;
                if(hours==0){
                    formatted = String.format("%02d:%02d", minutes, seconds);
                }else{
                    formatted = String.format("%d:%02d:%02d",hours,minutes,seconds);
                }
                viewHolder.endTime.setText(formatted+"");
                timerCounter(viewHolder);
            }
        });

        viewHolder.mPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewHolder.mPlay.setVisibility(View.GONE);
                viewHolder.mPause.setVisibility(View.VISIBLE);
                viewHolder.mVideo.start();
            }
        });

        viewHolder.mPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                viewHolder.mPlay.setVisibility(View.VISIBLE);
                viewHolder.mPause.setVisibility(View.GONE);
                viewHolder.mVideo.pause();

            }
        });


        viewHolder.mBack10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int current = viewHolder.mVideo.getCurrentPosition();
                int backTime=current-10000;
                viewHolder.mVideo.seekTo(backTime);
                timer.cancel();
                timerCounter(viewHolder);
            }
        });

        viewHolder.mForward10.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int current = viewHolder.mVideo.getCurrentPosition();
                int backTime=current+10000;
                viewHolder.mVideo.seekTo(backTime);
                timer.cancel();
                timerCounter(viewHolder);
            }
        });

        viewHolder.mPlayBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(position!=0){
                    VideoViewActivity.recyclerView.scrollToPosition(position-1);
                    VideoViewActivity.sharePosition=position-1;
                    notifyDataSetChanged();
                }
                else
                    activity.finish();
            }
        });

        viewHolder.mPlayforward.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(position!=files.size()-1){
                    VideoViewActivity.recyclerView.scrollToPosition(position+1);
                    VideoViewActivity.sharePosition=position+1;
                    notifyDataSetChanged();
                }
                else
                    activity.finish();
            }
        });

        viewHolder.mRotate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(!isRotate){
                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                            isRotate=true;
                        }
                    });
                }else{
                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            isRotate=false;
                            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                        }
                    });
                }
            }
        });

        viewHolder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                int current_pos = seekBar.getProgress();
                viewHolder.mVideo.seekTo(current_pos);
            }
        });


        viewHolder.rootRL.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Util.isBottomPlayerExpanded){
                    collapse(viewHolder.mBottomRL);
                    collapse(viewHolder.mRotate);
                    //   Log.e("isRotate",String.valueOf(isRotate));
                    if(isRotate){
                        if(from.equals("All"))
                            collapse(VideoViewActivity.toolbar);
                        else
                            collapse(VideoViewAlbumActivity.toolbar1);
                    }
                }else{
                    expand(viewHolder.mBottomRL);
                    expand(viewHolder.mRotate);
                    if(isRotate){
                        if(from.equals("All"))
                            expand(VideoViewActivity.toolbar);
                        else
                            expand(VideoViewAlbumActivity.toolbar1);
                    }
                    bottomHandler(viewHolder);
                }
            }
        });

        bottomHandler(viewHolder);

    }

    public void bottomHandler(ViewHolder viewHolder){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                collapse(viewHolder.mBottomRL);
                collapse(viewHolder.mRotate);
                if(isRotate){
                    if(from.equals("All"))
                        collapse(VideoViewActivity.toolbar);
                    else
                        collapse(VideoViewAlbumActivity.toolbar1);
                }
            }
        }, 3000);
    }

    private Timer timer;
    private void timerCounter(ViewHolder viewHolder){
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(viewHolder);
                    }
                });
            }
        };
        timer.schedule(task, 0, 100);
    }

    private void updateUI(ViewHolder viewHolder){
//        if (viewHolder.seekbar.getProgress() >= 100) {
//            timer.cancel();
//        }
        int current = viewHolder.mVideo.getCurrentPosition();
        int progress = current * 100 / viewHolder.mVideo.getDuration();
        viewHolder.seekbar.setProgress(current);

        int duration = current/1000;
        int hours = duration / 3600;
        int minutes = (duration / 60) - (hours * 60);
        int seconds = duration - (hours * 3600) - (minutes * 60) ;
        String formatted;
        if(hours==0){
            formatted = String.format("%02d:%02d", minutes, seconds);
        }else{
            formatted = String.format("%d:%02d:%02d",hours,minutes,seconds);
        }
        viewHolder.startTime.setText(formatted+"");
        if(viewHolder.startTime.getText().equals(viewHolder.endTime.getText())){
            viewHolder.mPlay.setVisibility(View.VISIBLE);
            viewHolder.mPause.setVisibility(View.GONE);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        VideoView mVideo;
        ImageView mRotate,mBack10,mPlayBack,mPlay,mPlayforward,mForward10,mPause;
        TextView startTime,endTime;
        SeekBar seekbar;
        RelativeLayout mBottomRL,rootRL;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View

            mVideo = v.findViewById(R.id.mVideo);
            mRotate=v.findViewById(R.id.mRotate);
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


    public static void expand(final View v) {
        Util.isBottomPlayerExpanded=true;
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

// Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

// Expansion speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {

        Util.isBottomPlayerExpanded=false;
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

// Collapse speed of 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

}