package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Adapter.TabsAppPageAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Fragment.APKFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Fragment.InstalledFragment;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Model.AppModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.CustomViewPager;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.TITLE;

public class BaseAppActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{

    TabsAppPageAdapter tabsPagerAdapter;
    public static TabLayout tabLayout;
    public static CustomViewPager viewPager;
    ImageView back;
    public static ImageView view,more,close;
    public static CardView card1,card2,card3;
    LinearLayout mSelectAllC1,mRefreshC1;
    LinearLayout mDeselectAllC2,mUninstallC2;
    LinearLayout mSelectAllC3,mInfoC3,mUninstallC3;
    ArrayList<AppModel> applicationModels = new ArrayList<>();
    public static ArrayList<BaseModel>  BaseModels=new ArrayList<>();
    List<Directory<BaseModel>> mAll = new ArrayList<>();
    MediaScannerConnection msConn;
    public static LinearLayout actionLL,titleLL;
    public static RelativeLayout headerRL;
    public static ImageView share;
    public static TextView count;
    int backPresse=0;
    AlertDialog alertadd1 ;
    RelativeLayout rootLayout;
    public static boolean viewClose=false;
    TextView actionApp,actionApp1;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    private String[] mSuffix;
    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 19;
    RelativeLayout rl_progress;
    AVLoadingIndicatorView avi;
    TextView successText;
    AlertDialog alertVault;

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
        setContentView(R.layout.activity_base_app);
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(BaseAppActivity.this);

        mainFunction();

        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseAppActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);
    }

    public void mainFunction(){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                applicationModels.clear();
                getApplications();
                mSuffix = new String[]{"apk"};
                new LoadAPK(BaseAppActivity.this).execute();
            }
        });

        setViewPager();
    }

    class LoadAPK extends AsyncTask<Void, Void, List<Directory<BaseModel>>>{

        @SuppressLint("StaticFieldLeak")
        Context fragmentActivity;

        public LoadAPK(FragmentActivity fragmentActivity) {
            this.fragmentActivity = fragmentActivity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Util.startAnim(rl_progress,avi);
        }

        @Override
        protected List<Directory<BaseModel>> doInBackground(Void... voids) {
            List<Directory<BaseModel>> directories = new ArrayList<>();
            String[] FILE_PROJECTION = {
                    //Base File
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_ADDED,

                    //Normal File
                    MediaStore.Files.FileColumns.MIME_TYPE};

            Cursor data = getContentResolver().query(MediaStore.Files.getContentUri("external"), FILE_PROJECTION, null, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

            while (data.moveToNext()) {
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                if (path != null && contains(path)) {
                    //Create a File instance
                    BaseModel file = new BaseModel();
                    file.setId(data.getLong(data.getColumnIndexOrThrow(_ID)));
                    file.setName(data.getString(data.getColumnIndexOrThrow(TITLE)));
                    file.setPath(data.getString(data.getColumnIndexOrThrow(DATA)));
                    file.setSize(data.getLong(data.getColumnIndexOrThrow(SIZE)));
                    file.setDate(Util.convertTimeDateModified(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED))));
                    //Create a Directory
                    Directory<BaseModel> directory = new Directory<>();
                    directory.setName(Util.extractFileNameWithSuffix(Util.extractPathWithoutSeparator(file.getPath())));
                    directory.setPath(Util.extractPathWithoutSeparator(file.getPath()));

                    if (!directories.contains(directory)) {
                        directory.addFile(file);
                        directories.add(directory);
                    } else {
                        directories.get(directories.indexOf(directory)).addFile(file);
                    }

                }
            }

            // Refresh folder list

            mAll = directories;
            ArrayList<BaseModel> list = new ArrayList<>();
            for (Directory<BaseModel> directory1 : directories) {
                list.addAll(directory1.getFiles());
            }

            BaseModels = list;

            return directories;
        }


        @Override
        protected void onPostExecute(List<Directory<BaseModel>> directories) {
            super.onPostExecute(directories);
//            Util.stopAnim(rl_progress,avi);
        }
    }

    private boolean contains(String path) {
        String mSuffixRegex;
        mSuffixRegex = obtainSuffixRegex(mSuffix);
        String name = Util.extractFileNameWithSuffix(path);
        Pattern pattern = Pattern.compile(mSuffixRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private String obtainSuffixRegex(String[] suffixes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < suffixes.length ; i++) {
            if (i ==0) {
                builder.append(suffixes[i].replace(".", ""));
            } else {
                builder.append("|\\.");
                builder.append(suffixes[i].replace(".", ""));
            }
        }
        return ".+(\\." + builder.toString() + ")$";
    }

    public void getApplications() {
        applicationModels.clear();
        // Get installed APK List and it's logo.
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if ((packageInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0) {
            } else {
                // It is installed by the user
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                }

                try {
                    Drawable icon = getPackageManager().getApplicationIcon(packageInfo.packageName);
                    Bitmap bitmap = getBitmapFromDrawable(icon);
                    String appName = "";
                    PackageManager packageManagers= getApplicationContext().getPackageManager();
                    try {
                        appName = (String) packageManagers.getApplicationLabel(packageManagers.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
//                           Log.e("LLL_PackageName: ",packageInfo.packageName);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    AppModel applicationModel = new AppModel();
                    applicationModel.setAppIcon(bitmap);
                    applicationModel.setAppName(appName);
                    applicationModel.setAppPath(packageInfo.sourceDir);
                    applicationModel.setPackageName(packageInfo.packageName);
                    applicationModels.add(applicationModel);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @NonNull
    static private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public void init(){
        rl_progress=findViewById(R.id.rl_progress);
        avi=findViewById(R.id.avi);

        tabLayout=findViewById(R.id.tab_imgFolder);
        viewPager=findViewById(R.id.viewPager);
        back=findViewById(R.id.back);
        view=findViewById(R.id.view);
        more=findViewById(R.id.more);

        card1=findViewById(R.id.card1);
        mSelectAllC1=findViewById(R.id.selectAllC1);
        mRefreshC1=findViewById(R.id.refreshC1);
        mSelectAllC1.setOnClickListener(this);
        mRefreshC1.setOnClickListener(this);

        card2=findViewById(R.id.card2);
        mDeselectAllC2=findViewById(R.id.deSelectAllC2);
        mUninstallC2=findViewById(R.id.uninstallC2);
        mDeselectAllC2.setOnClickListener(this);
        mUninstallC2.setOnClickListener(this);

        card3=findViewById(R.id.card3);
        mSelectAllC3=findViewById(R.id.selectAllC3);
        mInfoC3=findViewById(R.id.infoC3);
        mUninstallC3=findViewById(R.id.uninstallC3);
        mSelectAllC3.setOnClickListener(this);
        mInfoC3.setOnClickListener(this);
        mUninstallC3.setOnClickListener(this);

        view.setOnClickListener(this);
        more.setOnClickListener(this);
        view.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });

        actionLL=findViewById(R.id.l3);
        titleLL=findViewById(R.id.l1);
        headerRL=findViewById(R.id.header);
        share=findViewById(R.id.share);
        share.setOnClickListener(this);

        count=findViewById(R.id.count);
        alertadd1 = new AlertDialog.Builder(BaseAppActivity.this).create();
        rootLayout=findViewById(R.id.rootLayout);
        rootLayout.setOnTouchListener(this);

        close=findViewById(R.id.ic_close);
        close.setOnClickListener(this);

        actionApp=findViewById(R.id.actionApp);
        actionApp1=findViewById(R.id.actionApp1);

        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);

        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);

    }


    @Override
    protected void onResume(){
        super.onResume();
        if (msConn!=null)
            this.msConn.connect();
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

    public static void cardsVisibility(){
        if(Util.isAllSelected){
            card2.setVisibility(View.VISIBLE);
            card1.setVisibility(View.GONE);
            card3.setVisibility(View.GONE);
        }else  if(Util.mSelectedAppList.size()==1 || Util.mSelectedApkList.size()==1){
            card2.setVisibility(View.GONE);
            card1.setVisibility(View.GONE);
            card3.setVisibility(View.VISIBLE);
        }else  if(Util.mSelectedAppList.size()>1 || Util.mSelectedApkList.size()==1){
            card2.setVisibility(View.VISIBLE);
            card1.setVisibility(View.GONE);
            card3.setVisibility(View.GONE);
        } else{
            card1.setVisibility(View.VISIBLE);
            card3.setVisibility(View.GONE);
            card2.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.view:
                changeViewIcon();

                break;
            case R.id.more:
                cardsVisibility();
                break;
            case R.id.selectAllC1: {

                viewPager.setPagingEnabled(false);
                card1.setVisibility(View.GONE);
                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedAppList.clear();
                if(viewPager.getCurrentItem()==0){
                Util.mSelectedAppPackage.clear();
//                   Log.e("install directory size:",String.valueOf(applicationModels.size()));
                    for(int j = 0;j < applicationModels.size();j++){
//
                        if(!Util.mSelectedAppList.contains(applicationModels.get(j).getAppPath())){
                            Util.mSelectedAppList.add(applicationModels.get(j).getAppPath());
                            Util.mSelectedAppPackage.add(applicationModels.get(j).getPackageName());
                        }
                    }
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            changeHomeView();
                            Util.isAllSelected=true;
                            if(InstalledFragment.albumAdapter!=null)
                                InstalledFragment.albumAdapter.notifyDataSetChanged();
                            if(InstalledFragment.albumListAdapter!=null)
                                InstalledFragment.albumListAdapter.notifyDataSetChanged();
                        }
                    });
                }else{
//                    Log.e("apk directory size:",String.valueOf(BaseModels.size()));
                    for(int j = 0;j < BaseModels.size();j++){

                        if(!Util.mSelectedApkList.contains(BaseModels.get(j).getPath())){
                            Util.mSelectedApkList.add(BaseModels.get(j).getPath());
                        }
                    }
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            changeHomeView();
                            Util.isAllSelected=true;
                            if(APKFragment.albumAdapter!=null)
                                APKFragment.albumAdapter.notifyDataSetChanged();
                            if(APKFragment.albumListAdapter!=null)
                                APKFragment.albumListAdapter.notifyDataSetChanged();
                        }
                    });
                }


            }
            break;
            case R.id.refreshC1:
                viewPager.setPagingEnabled(true);
                card2Close();
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(APKFragment.albumAdapter!=null)
                            APKFragment.albumAdapter.notifyDataSetChanged();
                        if(APKFragment.albumListAdapter!=null)
                            APKFragment.albumListAdapter.notifyDataSetChanged();
                        if(InstalledFragment.albumAdapter!=null)
                            InstalledFragment.albumAdapter.notifyDataSetChanged();
                        if(InstalledFragment.albumListAdapter!=null)
                            InstalledFragment.albumListAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.deSelectAllC2:{
                viewPager.setPagingEnabled(true);
                card2Close();
            }
            break;
            case R.id.uninstallC2:{
               if(viewPager.getCurrentItem()==0){
                   fireAnalytics("App", "Uninstall");
                   for(int j = 0;j < Util.mSelectedAppList.size();j++){
                       //   Log.e("package:",Util.mSelectedAppPackage.get(j));
                       Intent in = new Intent(Intent.ACTION_DELETE);
                       in.setData(Uri.parse("package:" + Util.mSelectedAppPackage.get(j)));
                       startActivity(in);
                   }
                   
               }
               else{
                   ActionDelete();
               }
            }
            break;
            case R.id.selectAllC3:{
                viewPager.setPagingEnabled(false);
                card3.setVisibility(View.GONE);
                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedAppList.clear();
                if(viewPager.getCurrentItem()==0){
                    Util.mSelectedAppPackage.clear();
//                    Log.e("install directory size:",String.valueOf(applicationModels.size()));
                    for(int j = 0;j < applicationModels.size();j++){
//
                        if(!Util.mSelectedAppList.contains(applicationModels.get(j).getAppPath())){
                            Util.mSelectedAppList.add(applicationModels.get(j).getAppPath());
                            Util.mSelectedAppPackage.add(applicationModels.get(j).getPackageName());
                        }
                    }
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            changeHomeView();
                            Util.isAllSelected=true;
                            if(InstalledFragment.albumAdapter!=null)
                                InstalledFragment.albumAdapter.notifyDataSetChanged();
                            if(InstalledFragment.albumListAdapter!=null)
                                InstalledFragment.albumListAdapter.notifyDataSetChanged();
                        }
                    });
                }else{
//                    Log.e("apk directory size:",String.valueOf(BaseModels.size()));
                    for(int j = 0;j < BaseModels.size();j++){

                        if(!Util.mSelectedApkList.contains(BaseModels.get(j).getPath())){
                            Util.mSelectedApkList.add(BaseModels.get(j).getPath());
                        }
                    }
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            changeHomeView();
                            Util.isAllSelected=true;
                            if(APKFragment.albumAdapter!=null)
                                APKFragment.albumAdapter.notifyDataSetChanged();
                            if(APKFragment.albumListAdapter!=null)
                                APKFragment.albumListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            break;
            case R.id.infoC3:{
//                //   Log.e("List size:",Util.mSelectedAppPackage.size() + " ");
                if(viewPager.getCurrentItem()==0){
                    fireAnalytics("App", "Info");
                    if(Util.mSelectedAppPackage.size() == 1){
//                    //   Log.e("package:",Util.mSelectedAppPackage.get(0));
                        Intent in = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        in.addCategory(Intent.CATEGORY_DEFAULT);
                        in.setData(Uri.parse("package:" + Util.mSelectedAppPackage.get(0)));
                        startActivity(in);
                    }
                }else{
                    fireAnalytics("Apk", "Info");
                    File imgFile = new  File(Util.mSelectedApkList.get(0));
                    if(imgFile.exists()){
                        fileInfo();
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }else{
                        Toast.makeText(BaseAppActivity.this,"Problem with file.",Toast.LENGTH_SHORT).show();
                    }

                }
                card2Close();
            }
            break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.uninstallC3:


                if(Util.mSelectedAppList.size() == 1 || Util.mSelectedApkList.size() ==1){

                    if(viewPager.getCurrentItem() == 0){
                        fireAnalytics("App", "Uninstall");
                        Intent in = new Intent(Intent.ACTION_DELETE);
                        in.setData(Uri.parse("package:" + Util.mSelectedAppPackage.get(0)));
                        startActivity(in);
                        card2Close();
                    }else{
                        ActionDelete();
//                        boolean isDelete=Util.delete(BaseAppActivity.this,new File(Util.mSelectedAppList.get(0)));
//                        if(isDelete){
//                            Toast.makeText(BaseAppActivity.this,"Successfully Deleted",Toast.LENGTH_SHORT).show();
//                            APKFragment.albumAdapter.notifyDataSetChanged();
//                            APKFragment.albumListAdapter.notifyDataSetChanged();
//                        }
                    }
                }

                //for 1 app

            break;
            case R.id.share:

                ArrayList<Uri> uris = new ArrayList<>();
                String path;
                File file;
                Uri contentUri;
                if(viewPager.getCurrentItem() == 0) {
                    for (int i = 0; i < Util.mSelectedAppList.size(); i++) {
                        path = Util.mSelectedAppList.get(i);
                        file = new File(path);
                        contentUri = FileProvider.getUriForFile(BaseAppActivity.this, getPackageName() + ".provider", file);
                        uris.add(contentUri);
                    }
                }else{
                    for (int i = 0; i < Util.mSelectedApkList.size(); i++) {
                        path = Util.mSelectedApkList.get(i);
                        file = new File(path);
                        contentUri = FileProvider.getUriForFile(BaseAppActivity.this, getPackageName() + ".provider", file);
                        uris.add(contentUri);
                    }
                }

                //   Log.e("Uri list",String.valueOf(uris.size()));
                Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sharingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                sharingIntent.setType("*/*");
                sharingIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(sharingIntent,"Share Via"));
                card2Close();
                break;
            case R.id.ic_close:
                Util.clickEnable=true;
                card2Close();
                final Handler handler1 = new Handler(Looper.getMainLooper());
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Util.IsUpdate=true;
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BaseAppActivity.this);
                        Intent localIn = new Intent("TAG_REFRESH");
                        lbm.sendBroadcast(localIn);
                    }
                }, 1000);
                break;
        }
    }

    public void fileInfo(){
        File imgFile;
        if(viewPager.getCurrentItem() == 0) {
             imgFile = new  File(Util.mSelectedAppList.get(0));
        }else {

             imgFile = new File(Util.mSelectedApkList.get(0));
        }
        if(imgFile.exists()){
            RequestOptions options = new RequestOptions();
            try{
                String APKFilePath = imgFile.getPath();
                PackageInfo packageInfo = getPackageManager()
                        .getPackageArchiveInfo(APKFilePath, PackageManager.GET_ACTIVITIES);
                if(packageInfo != null){
                    ApplicationInfo appInfo = packageInfo.applicationInfo;
                    if(Build.VERSION.SDK_INT >= 8){
                        appInfo.sourceDir = APKFilePath;
                        appInfo.publicSourceDir = APKFilePath;
                    }
                    Drawable icon = appInfo.loadIcon(getPackageManager());
                    proImage.setImageDrawable(icon);
                }
            }catch(Exception e){
                Glide.with(getBaseContext())
                        .load(getResources().getDrawable(R.drawable.apps))
                        .apply(options.centerCrop()
                                .skipMemoryCache(true)
                                .priority(Priority.LOW)
                                .format(DecodeFormat.PREFER_ARGB_8888))

                        .into(proImage);
            }
            proName.setText(imgFile.getName());

            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
            String date = spf.format(lastModDate);
            proDate.setText(date);
            String s= Util.getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(imgFile.getPath());
        }
    }



    public void card2Close(){

        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);

        Util.clickEnable=true;
        Util.showCircle=false;
        viewClose=true;
        Util.mSelectedAppList.clear();
        Util.mSelectedApkList.clear();
        Util.mSelectedAppPackage.clear();

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                Util.isAllSelected=false;
                if(APKFragment.albumAdapter!=null)
                    APKFragment.albumAdapter.notifyDataSetChanged();
                if(APKFragment.albumListAdapter!=null)
                    APKFragment.albumListAdapter.notifyDataSetChanged();
                if(InstalledFragment.albumAdapter!=null)
                    InstalledFragment.albumAdapter.notifyDataSetChanged();
                if(InstalledFragment.albumListAdapter!=null)
                    InstalledFragment.albumListAdapter.notifyDataSetChanged();
            }
        });
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
        Util.IsUpdate=true;
        //   Log.e("LLLLView change:" , Util.VIEW_TYPE);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BaseAppActivity.this);
        Intent localIn = new Intent("TAG_REFRESH");
        lbm.sendBroadcast(localIn);
    }

    @Override
    public boolean onTouch(View v,MotionEvent event){
        //   Log.e("View ID:",String.valueOf(v.getId()));
//        if(v.getId()!=R.id.card1 || v.getId()!=R.id.card2 || v.getId()!=R.id.card3 || v.getId()!=R.id.card4)
//        {
        if(v.getId()!=R.id.more){
            disableCard();
        }
        return false;
    }

    @Override
    public void onBackPressed(){

        Util.showCircle = false;
        Util.clickEnable = true;
        if(Util.mSelectedAppList.size()==0 && Util.mSelectedApkList.size()==0){
            viewClose=false;

            super.onBackPressed();
        }

        if(!viewClose){
            changeToOriginalView();
            Util.IsUpdate=true;
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BaseAppActivity.this);
            Intent localIn = new Intent("TAG_REFRESH");
            lbm.sendBroadcast(localIn);

        }else{
            backPresse++;
            if(backPresse == 1){
                viewClose = false;
                finish();
            }
        }

    }

    public void changeToOriginalView(){
        viewPager.setPagingEnabled(true);
        Util.isAllSelected=false;
        Util.mSelectedAppList.clear();
        Util.mSelectedApkList.clear();
        Util.mSelectedAppPackage.clear();
        titleLL.setVisibility(View.VISIBLE);
        actionLL.setVisibility(View.GONE);
        headerRL.setBackgroundColor(getResources().getColor(R.color.header_color));
        tabLayout.setVisibility(View.VISIBLE);
        share.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
        disableCard();
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
        viewClose=true;
    }

    public void setViewPager() {

        tabsPagerAdapter = new TabsAppPageAdapter(getSupportFragmentManager(), getBaseContext());
        viewPager.setAdapter(tabsPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(tabsPagerAdapter.getTabView(i));
        }

        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        tabLayout.setTabIndicatorFullWidth(false);
        TabLayout.Tab tab=tabLayout.getTabAt(0);
        tabLayout.selectTab(tab);
        TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.img_tab);

        tv.setTextColor(getResources().getColor(R.color.themeColor));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition()==0){
                    actionApp.setText("Uninstall");
                    actionApp1.setText("Uninstall");
                }
                else{
                    actionApp.setText("Delete");
                    actionApp1.setText("Delete");
                }
                TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.img_tab);
                tv.setTextColor(getResources().getColor(R.color.themeColor));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tv = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.img_tab);

                tv.setTextColor(getResources().getColor(R.color.tabtextColor));

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setOffscreenPageLimit(2);
    }


    public void changeHomeView(){
        titleLL.setVisibility(View.GONE);
        actionLL.setVisibility(View.VISIBLE);
        headerRL.setBackgroundColor(getResources().getColor(R.color.themeColor));
        tabLayout.setVisibility(View.GONE);
        share.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(more, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        if(viewPager.getCurrentItem() == 0) {
            count.setText(Util.mSelectedAppList.size() + " Selected");
        }else{
            count.setText(Util.mSelectedApkList.size() + " Selected");
        }
    }

    public static void disableCard(){
        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
    }

    public void ActionDelete(){

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(BaseAppActivity.this);
        final View view = factory.inflate(R.layout.delete_dialog,null);
        alertadd.setView(view);
        alertadd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertadd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDel=view.findViewById(R.id.text4);
        okDel=view.findViewById(R.id.text5);

        cancelDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                alertadd.dismiss();
            }
        });

        okDel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int count=0;
                for(int j = 0;j < Util.mSelectedApkList.size();j++){
//                       Log.e("package:",Util.mSelectedApkList.get(j));
                   isDeleteImg=true;
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedApkList.get(j));

                    File sampleFile = Environment.getExternalStorageDirectory();
                    File file1 = new File(mAll.get(0).getPath());

                    if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                        if(sourceFile.exists()){
                            Util.MoveToTrash(BaseAppActivity.this,sourceFile);
                            isDelete = Util.delete(BaseAppActivity.this,sourceFile);
                            if(!isDelete)
                                sourceFile.delete();
//                               Log.e("LLLL_Del: ",String.valueOf(isDelete));
                        }
                    }else{
                        if(!SharedPreference.getSharedPreference(BaseAppActivity.this).contains(file1.getParentFile().getAbsolutePath())){
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                            intent.putExtra("android.content.extra.SHOW_ADVANCED",true);
                            startActivityForResult(intent,REQUEST_CODE_OPEN_DOCUMENT_TREE);
                        }else{
                            if(sourceFile.exists()){
                                isDelete =   Util.delete(BaseAppActivity.this,new File(Util.mSelectedApkList.get(j)));

                            }
                        }
                    }
                    if(isDelete){
                        count++;
                    }
                }

                if(count==Util.mSelectedApkList.size()){
                    fireAnalytics("Apk", "Delete");
                    successText.setText( "APK Delete successfully."  );
                    alertVault.show();
                    Util.IsAPKUpdate=true;
                    count=0;
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            card2Close();
                            alertVault.dismiss();

                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(BaseAppActivity.this);
                            Intent localIn = new Intent("TAG_REFRESH");
                            lbm.sendBroadcast(localIn);
                        }
                    }, 2000);
                }else{
                    Toast.makeText(BaseAppActivity.this,"Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }

                alertadd.dismiss();
            }
        });
        alertadd.show();
    }




}