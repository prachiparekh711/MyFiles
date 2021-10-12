package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.LargeAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.LargeListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;

public class LargeActivity extends AppCompatActivity implements View.OnClickListener{


    public static ImageView view,mSelect,mDeselect,back,close;
    public static boolean select=false;
    public static LinearLayout titleLL,actionLL;
    public static CardView mDeleteCard;
    public static RelativeLayout mHeaderRL;
    public static TextView count;
    RecyclerView recyclerView;
    LargeAdapter adapter;
    LargeListAdapter listAdapter;
    String mFrom="";
    TextView textTitle;

    private FirebaseAnalytics mFirebaseAnalytics;
    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large);
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(LargeActivity.this);
    }

    public void init(){
        close=findViewById(R.id.ic_close);
        close.setOnClickListener(this);

        recyclerView = findViewById(R.id.rvImages);
        view = findViewById(R.id.view);
        view.setOnClickListener(this);

        mSelect = findViewById(R.id.img_select);
        mSelect.setOnClickListener(this);
        mDeselect = findViewById(R.id.img_unselect);
        mDeselect.setOnClickListener(this);

        titleLL=findViewById(R.id.l1);
        actionLL=findViewById(R.id.l3);
        mDeleteCard=findViewById(R.id.deleteCard);
        mDeleteCard.setOnClickListener(this);
        mHeaderRL=findViewById(R.id.header);
        count=findViewById(R.id.count);
        textTitle=findViewById(R.id.textTitle);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFrom=getIntent().getStringExtra("From");
        if(Util.VIEW_TYPE=="Grid"){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                }
            });
            final GridLayoutManager layoutManager = new GridLayoutManager(LargeActivity.this, Util.COLUMN_TYPE);
            recyclerView.setLayoutManager(layoutManager);
            if(mFrom.equals("Large")){
                textTitle.setText("Large Files");
                adapter = new LargeAdapter("Large", Util.mLargeFinalList, LargeActivity.this);
            }else if(mFrom.equals("WImage")){
                textTitle.setText("Whatsapp Images");
                adapter = new LargeAdapter("WImage", Util.mWhatsappImgList, LargeActivity.this);
            }else if(mFrom.equals("WVideo")){
                textTitle.setText("Whatsapp Video");
                adapter = new LargeAdapter("WVideo", Util.mWhatsappVideoList, LargeActivity.this);
            }
            recyclerView.setAdapter(adapter);

        }
        else{
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(LargeActivity.this,RecyclerView.VERTICAL,false));
            if(mFrom.equals("Large")){
                textTitle.setText("Large Files");
                listAdapter = new LargeListAdapter("Large", Util.mLargeFinalList, LargeActivity.this);
            }else if(mFrom.equals("WImage")){
                textTitle.setText("Whatsapp Images");
                listAdapter = new LargeListAdapter("WImage", Util.mWhatsappImgList, LargeActivity.this);
            }else if(mFrom.equals("WVideo")){
                textTitle.setText("Whatsapp Video");
                listAdapter = new LargeListAdapter("WVideo", Util.mWhatsappVideoList, LargeActivity.this);
            }
            recyclerView.setAdapter(listAdapter);
        }

    }

   
    @Override
    public void onBackPressed(){
        if(mFrom.equals("Large")){
            if(Util.mSelectedLargeList.size() == 0){
                super.onBackPressed();
            }else{
                changeToOriginalView();
            }
        }else if(mFrom.equals("WImage")){
            if(Util.mSelectedWImageList.size() == 0){
                super.onBackPressed();
            }else{
                changeToOriginalView();
            }
        }else if(mFrom.equals("WVideo")){
            if(Util.mSelectedWVideoList.size() == 0){
                super.onBackPressed();
            }else{
                changeToOriginalView();
            }
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.view:
                changeViewIcon();
                break;
            case R.id.img_unselect:
                changeView();
                if(mFrom.equals("Large")){
                    for(int i = 0; i < Util.mLargeFinalList.size(); i++){
                        Util.mSelectedLargeList.add(Util.mLargeFinalList.get(i).getPath());
                    }
                    count.setText(Util.mSelectedLargeList.size() + " Selected");
                }else if(mFrom.equals("WImage")){
                    for(int i = 0; i < Util.mWhatsappImgList.size(); i++){
                        Util.mSelectedWImageList.add(Util.mWhatsappImgList.get(i).getPath());
                    }
                    count.setText(Util.mSelectedWImageList.size() + " Selected");
                }else if(mFrom.equals("WVideo")){
                    for(int i = 0; i < Util.mWhatsappVideoList.size(); i++){
                        Util.mSelectedWVideoList.add(Util.mWhatsappVideoList.get(i).getPath());
                    }
                    count.setText(Util.mSelectedWVideoList.size() + " Selected");
                }
                if(adapter!=null)
                  adapter.notifyDataSetChanged();
                if(listAdapter!=null)
                    listAdapter.notifyDataSetChanged();

                break;
            case R.id.img_select:
            case R.id.ic_close:
                changeToOriginalView();
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
                if(listAdapter!=null)
                    listAdapter.notifyDataSetChanged();
                break;
            case R.id.deleteCard:
                int count=0;
                File file;
                ArrayList<String> selectedList=new ArrayList<>();
                if(mFrom.equals("Large")){
                    fireAnalytics("Delete", "Large");
                    selectedList=Util.mSelectedLargeList;
                }else if(mFrom.equals("WImage")){
                    fireAnalytics("Delete", "Whatsapp Image");
                    selectedList=Util.mSelectedWImageList;
                }else if(mFrom.equals("WVideo")){
                    fireAnalytics("Delete", "Whatsapp Video");
                    selectedList=Util.mSelectedWVideoList;
                }
                for(int i = 0;i < selectedList.size();i++){
                    boolean isDelete=Util.delete(LargeActivity.this,new File(selectedList.get(i)));
                    if(isDelete)
                        count++;
                    else{
                        file=new File(selectedList.get(i));
                        isDelete=file.delete();
                        if(isDelete)
                            count++;
                    }
                }
                if(count== selectedList.size()){
                    Toast.makeText(LargeActivity.this, "All selected files delete successfully!!! ",Toast.LENGTH_SHORT).show();
                    if(mFrom.equals("Large")){ Util.mLargeDelete=true; }
                    else if(mFrom.equals("WImage")){ Util.mWImgDelete=true;}
                    else if(mFrom.equals("WVideo")){ Util.mWVideoDelete=true;}
                    super.onBackPressed();
                }else{
                    Toast.makeText(LargeActivity.this, "Something went wrong!!! ",Toast.LENGTH_SHORT).show();
                }
                changeToOriginalView();
                break;

        }
    }

    public void changeViewIcon(){
        if(Util.VIEW_TYPE=="Grid"){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                }
            });
            Util.VIEW_TYPE="List";
        }
        else{
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                }
            });
            Util.VIEW_TYPE="Grid";
        }

        onResume();
    }

    public void changeToOriginalView(){
        select=false;
        Util.mSelectedLargeList.clear();
        Util.mSelectedWImageList.clear();
        Util.mSelectedWVideoList.clear();

        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        mHeaderRL.setBackgroundColor(getResources().getColor(R.color.header_color));
        view.setVisibility(View.VISIBLE);
        mDeselect.setVisibility(View.VISIBLE);
        mSelect.setVisibility(View.GONE);
        mDeleteCard.setCardBackgroundColor(getResources().getColor(R.color.delete_disable));
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
                if(listAdapter!=null)
                    listAdapter.notifyDataSetChanged();
            }
        });
    }

    public void changeView(){
        mDeselect.setVisibility(View.GONE);
        mSelect.setVisibility(View.VISIBLE);
        titleLL.setVisibility(View.GONE);
        actionLL.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        mDeleteCard.setCardBackgroundColor(getResources().getColor(R.color.themeColor));
        mHeaderRL.setBackgroundColor(getResources().getColor(R.color.themeColor));
        select=true;
    }
}

