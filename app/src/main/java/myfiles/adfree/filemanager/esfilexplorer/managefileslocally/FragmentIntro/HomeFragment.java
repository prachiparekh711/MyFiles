package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.FragmentIntro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.RecentAdapter1;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseApp.Activity.BaseAppActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDocument.Activity.BaseDocumentActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload.BaseDownloadActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseImage.Activity.BaseImageActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseMusic.Activity.BaseMusicActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseVideo.Activity.BaseVideoActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseZip.Activity.BaseZipActivity;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseTrash.BaseTrashActivity;
import com.intrusoft.scatter.ChartData;
import com.intrusoft.scatter.PieChart;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.BaseDownload.BaseDownloadActivity.dirNamedir;

public class HomeFragment extends BaseFragment{


    LinearLayout imgLayout,vidLayout,musicLayout,zipLayout,appLayout,documentLayout,trashLayout,downloadLayout;
    View view;
    RecyclerView rv_recent;
    ImageView mNullRecent;
    ArrayList<BaseModel> allDataList = new ArrayList<>();
    ArrayList<BaseModel> recentList = new ArrayList<>();
    RecentAdapter1 recentAdapter;
    PieChart pieChart1,pieChart2;
    LinearLayout layer2,layer1,layer3;
    TextView txtFree1,txtTotal1,txtFree2,txtTotal2,txtFree3,txtTotal3;
    public static RelativeLayout rl_progress;
    public static AVLoadingIndicatorView avi;

    @Override
    void permissionGranted(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        view= inflater.inflate(R.layout.fragment_home,container,false);
        init();


        calculateInternal();
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if(isSDSupportedDevice && isSDPresent)
        {
            layer3.setVisibility(View.GONE);
            layer2.setVisibility(View.VISIBLE);
            calculateExternal();
        }
        else
        {
            layer1.setVisibility(View.GONE);
            layer2.setVisibility(View.GONE);
            layer3.setVisibility(View.VISIBLE);
        }

        rv_recent.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rv_recent.setNestedScrollingEnabled(false);


        return view;

    }

    class Sortbyroll implements Comparator<BaseModel>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(BaseModel a, BaseModel b)
        {
            return a.getRecentDate().compareToIgnoreCase( b.getRecentDate());
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        allDataList.clear();
        recentList.clear();
        allDataList = SharedPreference.getRecentList(getActivity());
        Collections.sort(allDataList, new Sortbyroll());
        Collections.reverse(allDataList);
        for (int i = 0; i < allDataList.size(); i++) {
            File file=new File(allDataList.get(i).getPath());
            if(file.exists()){
               recentList.add(allDataList.get(i));
            }
        }

//        //   Log.e("LLLLL_Date_Log file: ", allDataList.size() + " !" );
        if(recentList.size()>0){
            rv_recent.setVisibility(View.VISIBLE);
            mNullRecent.setVisibility(View.GONE);
            recentAdapter = new RecentAdapter1( recentList,getActivity(),(view1, position) -> {
            });
            rv_recent.setAdapter(recentAdapter);
        }else{
            rv_recent.setVisibility(View.GONE);
            mNullRecent.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void calculateInternal(){
        File[] roots = getActivity().getExternalFilesDirs(null);
        String phoneMemory = roots[0].getAbsolutePath(); // PhoneMemory
//        String SDMemory = roots[1].getAbsolutePath(); // SCCard (if available)
        long totalMemory = StatUtils.totalMemory(phoneMemory);
        long freeMemory = StatUtils.freeMemory(phoneMemory);

        final String totalSpace = StatUtils.humanize(totalMemory, true);
        final String usableSpace = StatUtils.humanize(freeMemory, true);

//        //   Log.e("Total external :" + totalSpace,"Total Internal : " + usableSpace);
        String x=totalSpace.substring(0,totalSpace.lastIndexOf(" ") + 1);
        String y=usableSpace.substring(0,usableSpace.lastIndexOf(" ") + 1);

        float total=Float.valueOf(x);
        float free=Float.valueOf(y);
        float used1=total-free;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float used = Float.valueOf(decimalFormat.format(used1));

//        //   Log.e("total :" +total,"free :"+ free + " used :" + used );

        float freeP=(100*free)/total;
        float usedP=(100*used)/total;

        freeP = Float.valueOf(decimalFormat.format(freeP));
        usedP = Float.valueOf(decimalFormat.format(usedP));

//        Log.e("Free % :" +freeP,"used % :" + usedP );

        List<ChartData> data = new ArrayList<>();
        data.add(new ChartData("",freeP, Color.WHITE, Color.parseColor("#CE9FFC")));
        data.add(new ChartData("", usedP, Color.WHITE, Color.parseColor("#E8C15A")));
        pieChart1.setChartData(data);

        txtFree1.setText(usableSpace);
        txtTotal1.setText(" / " + totalSpace);
        txtFree3.setText(usableSpace);
        txtTotal3.setText(" / " + totalSpace);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void calculateExternal(){
        File[] roots = getActivity().getExternalFilesDirs(null);
//        String phoneMemory = roots[0].getAbsolutePath(); // PhoneMemory
        String SDMemory = roots[1].getAbsolutePath(); // SCCard (if available)
        long totalMemory = StatUtils.totalMemory(SDMemory);
        long freeMemory = StatUtils.freeMemory(SDMemory);

        final String totalSpace = StatUtils.humanize(totalMemory, true);
        final String usableSpace = StatUtils.humanize(freeMemory, true);

//        //   Log.e("Total external :" + totalSpace,"Total Internal : " + usableSpace);
        String x=totalSpace.substring(0,totalSpace.lastIndexOf(" ") + 1);
        String y=usableSpace.substring(0,usableSpace.lastIndexOf(" ") + 1);

        float total=Float.valueOf(x);
        float free=Float.valueOf(y);
        float used1=total-free;
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        float used = Float.valueOf(decimalFormat.format(used1));

//        //   Log.e("total :" +total,"free :"+ free + " used :" + used );

        float freeP=(100*free)/total;
        float usedP=(100*used)/total;
        freeP = Float.valueOf(decimalFormat.format(freeP));
        usedP = Float.valueOf(decimalFormat.format(usedP));
//        //   Log.e("Free % :" +freeP,"used % :" + usedP );

        List<ChartData> data = new ArrayList<>();
        data.add(new ChartData("", freeP, Color.WHITE, Color.parseColor("#8FD878")));
        data.add(new ChartData("", usedP, Color.WHITE, Color.parseColor("#DC5B69")));
        pieChart2.setChartData(data);

        txtFree2.setText(usableSpace);
        txtTotal2.setText(" / " + totalSpace);
    }

    public static ArrayList<BaseModel> getAllRecentData(ArrayList<BaseModel> allDataList) {

        File dir = new File(dirNamedir);
        boolean success = true;

        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();


            if (listFile != null) {
                for (int i = 0; i < listFile.length; i++) {

                    SimpleDateFormat df = new SimpleDateFormat("MMM-yyyy", Locale.getDefault());

                    Date c = Calendar.getInstance().getTime();
                    String formattedDate = df.format(c);

                    Date date1 = new Date(listFile[i].lastModified());
                    String formattedDate1 = df.format(date1);

//                    //   Log.e("LLLLL_Date_Log: ", formattedDate + "        " + formattedDate1);

//                    if (formattedDate.equals(formattedDate1)) {
                        if(!listFile[i].getName().startsWith(".")){
                            BaseModel BaseModel = new BaseModel();
                            BaseModel.setName(listFile[i].getName());
                            BaseModel.setSize(listFile[i].length());
                            BaseModel.setPath(listFile[i].getAbsolutePath());
                            allDataList.add(BaseModel);
//                            //   Log.e("LLLLL_Date_Log file: ", listFile[i].getName());
                        }
//                    }
                }
            }

        }

        Collections.reverse(allDataList);
        return allDataList;
    }

    public  void init(){

        rl_progress=view.findViewById(R.id.rl_progress);
        avi=view.findViewById(R.id.avi);

        layer2=view.findViewById(R.id.layer2);
        layer1=view.findViewById(R.id.layer1);
        layer3=view.findViewById(R.id.layer3);
        rv_recent = view.findViewById(R.id.rv_recent);
        pieChart1 = view.findViewById(R.id.piechart1);
        pieChart2 = view.findViewById(R.id.piechart2);

        txtFree1=view.findViewById(R.id.txtFree1);
        txtFree2=view.findViewById(R.id.txtFree2);
        txtTotal1=view.findViewById(R.id.txtTotal1);
        txtTotal2=view.findViewById(R.id.txtTotal2);
        txtFree3=view.findViewById(R.id.txtFree3);
        txtTotal3=view.findViewById(R.id.txtTotal3);

        imgLayout=view.findViewById(R.id.ll_image);
        vidLayout=view.findViewById(R.id.ll_vidoes);
        musicLayout=view.findViewById(R.id.ll_audios);
        zipLayout=view.findViewById(R.id.ll_zipFiles);
        appLayout=view.findViewById(R.id.ll_Apps);
        documentLayout=view.findViewById(R.id.ll_document);
        trashLayout=view.findViewById(R.id.ll_trash);
        downloadLayout=view.findViewById(R.id.ll_download);
        imgLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(), BaseImageActivity.class);
                startActivity(in);
            }
        });

        vidLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(),BaseVideoActivity.class);
                startActivity(in);
            }
        });

        musicLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(),BaseMusicActivity.class);
                startActivity(in);
            }
        });

        zipLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(),BaseZipActivity.class);
                startActivity(in);
            }
        });

        appLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(), BaseAppActivity.class);
                startActivity(in);
            }
        });

        documentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(),BaseDocumentActivity.class);
                startActivity(in);
            }
        });

        trashLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(),BaseTrashActivity.class);
                startActivity(in);
            }
        });

        downloadLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent in=new Intent(getActivity(),BaseDownloadActivity.class);
                startActivity(in);
            }
        });

        mNullRecent=view.findViewById(R.id.mRecentNull);

    }


    public static final class StatUtils {

        public static long totalMemory(String path) {
            StatFs statFs = new StatFs(path);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //noinspection deprecation
                return (statFs.getBlockCount() * statFs.getBlockSize());
            } else {
                return (statFs.getBlockCountLong() * statFs.getBlockSizeLong());
            }
        }

        public static long freeMemory(String path) {
            StatFs statFs = new StatFs(path);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                //noinspection deprecation
                return (statFs.getAvailableBlocks() * statFs.getBlockSize());
            } else {
                return (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
            }
        }

        public static long usedMemory(String path) {
            long total = totalMemory(path);
            long free = freeMemory(path);
            return total - free;
        }

        public static String humanize(long bytes, boolean si) {
            int unit = si ? 1000 : 1024;
            if (bytes < unit) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
            return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
        }
    }




}