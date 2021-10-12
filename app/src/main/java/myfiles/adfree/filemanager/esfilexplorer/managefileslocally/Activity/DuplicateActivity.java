package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.DuplicateParentAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.ParentModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DuplicateActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView parentRec;
    DuplicateParentAdapter parentAdapter;
    List<String> MainList=new ArrayList<>();
    List<BaseModel> DuplicateList=new ArrayList<>();
    HashMap<String,Long> mPairlist=new HashMap<>();
    HashMap<String,Long> mCommenlist=new HashMap<>();
    public static ImageView view,mSelect,mDeselect,back,close;
    public static boolean select=false;
    public static LinearLayout titleLL,actionLL;
    public static CardView mDeleteCard;
    public static RelativeLayout mHeaderRL;
    public static TextView count;
    List<ParentModel> itemList  = new ArrayList<>();

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
        setContentView(R.layout.activity_duplicate);
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(DuplicateActivity.this);

        DuplicateList.addAll(Util.mFinalList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DuplicateActivity.this, RecyclerView.VERTICAL, false);
        parentRec.setLayoutManager(linearLayoutManager);
        parentRec.setScrollContainer(true);
        parentRec.setLayoutAnimation(null);
        parentRec.setItemAnimator(null);

        parentAdapter = new DuplicateParentAdapter(ParentItemList(), DuplicateActivity.this);
        parentRec.setAdapter(parentAdapter);
        
    }

    public void init(){
        close=findViewById(R.id.ic_close);
        close.setOnClickListener(this);

        parentRec = findViewById(R.id.rvImages);
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
        if(Util.VIEW_TYPE=="Grid"){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
                }
            });

        }
        else{
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    view.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
                }
            });

        }
    }

    private List<ParentModel> ParentItemList()
    {

        for(int i=0;i<DuplicateList.size();i++){
//            //   Log.e("Duplicate List:",DuplicateList.get(i).getName());
            if(!MainList.contains(DuplicateList.get(i).getName())){
                MainList.add(DuplicateList.get(i).getName());
                mCommenlist.put(DuplicateList.get(i).getName(),DuplicateList.get(i).getSize());
            }
        }

        for(int i = 0;i < MainList.size();i++){

            if(!mPairlist.containsKey(MainList.get(i))){    //match title
                Long size=mCommenlist.get(MainList.get(i));

                if(!mPairlist.containsValue(size)){
                    mPairlist.put(MainList.get(i),size);
                }
            }
        }


        for (String name: mPairlist.keySet()) {
            String key = name;
            Long value = mPairlist.get(name);
//            //   Log.e("Pair ",key + " " + value);
            ParentModel item= new ParentModel(key,ChildItemList(key,value));
            itemList.add(item);
        }

        return itemList;
    }


    private List<BaseModel> ChildItemList(String model,Long size)
    {
        List<BaseModel> ChildItemList = new ArrayList<>();
//        //   Log.e("File Title:",model);

        for(int i=0;i<DuplicateList.size();i++){
            if(!ChildItemList.contains(DuplicateList.get(i))){
//                if(DuplicateList.get(i).getName().equalsIgnoreCase(model) ){
                    if(size==DuplicateList.get(i).getSize()){
//                        //   Log.e("File content:",DuplicateList.get(i).getName());
                        ChildItemList.add(DuplicateList.get(i));
                    }
//                }
            }
        }
        return ChildItemList;
    }

    @Override
    public void onBackPressed(){
        if(Util.mSelectedDuplicateList.size()==0)
            super.onBackPressed();
        else
            changeToOriginalView();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {

            case R.id.view:
                changeViewIcon();
                break;
            case R.id.img_unselect:
                changeView();
                for(int i = 0;i < itemList.size();i++){
                    List<BaseModel> childList=new ArrayList<>();
                    childList=itemList.get(i).getChildItemList();
                    for(int j = 0;j < childList.size();j++){
                        if(j>0){
                            Util.mSelectedDuplicateList.add(childList.get(j).getPath());
                        }
                    }
                }
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(parentAdapter!=null)
                        parentAdapter.notifyDataSetChanged();
                    }
                });
                count.setText(Util.mSelectedDuplicateList.size() + " Selected");
                break;
            case R.id.img_select:
            case R.id.ic_close:
                changeToOriginalView();

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(parentAdapter!=null)
                        parentAdapter.notifyDataSetChanged();
                    }
                });

                break;
            case R.id.deleteCard:

                fireAnalytics("Duplicate", "Delete");
                int count=0;
                for(int i = 0;i < Util.mSelectedDuplicateList.size();i++){
                    boolean isDelete=Util.delete(DuplicateActivity.this,new File(Util.mSelectedDuplicateList.get(i)));
                    if(isDelete)
                        count++;
                }

                    Toast.makeText(DuplicateActivity.this, "All selected files delete successfully!!! ",Toast.LENGTH_SHORT).show();
                    Util.mDuplicateDelete=true;
                    super.onBackPressed();


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
        if(parentAdapter!=null)
            parentAdapter.notifyDataSetChanged();
    }

    public void changeToOriginalView(){
        select=false;
        Util.mSelectedDuplicateList.clear();
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
                if(parentAdapter!=null)
                parentAdapter.notifyDataSetChanged();
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

