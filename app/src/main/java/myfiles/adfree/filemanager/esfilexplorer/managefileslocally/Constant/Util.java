package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.Directory;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class Util{

    public static ArrayList<String> mSelectedImgList=new ArrayList<>();
    public static ArrayList<String> mSelectedMusicList=new ArrayList<>();
    public static ArrayList<String> mSelectedVideoList=new ArrayList<>();
    public static ArrayList<String> mSelectedZipList=new ArrayList<>();
    public static ArrayList<String> mSelectedAppList=new ArrayList<>();
    public static ArrayList<String> mSelectedApkList=new ArrayList<>();
    public static ArrayList<String> mSelectedAppPackage=new ArrayList<>();
    public static ArrayList<String> mSelectedDocumentList=new ArrayList<>();
    public static ArrayList<String> mSelectedDownloadList=new ArrayList<>();
    public static ArrayList<String> mSelectedTrashList=new ArrayList<>();
    public static ArrayList<String> mOriginalPath=new ArrayList<>();
    public static ArrayList<String> mSelectedDuplicateList=new ArrayList<>();
    public static ArrayList<String> mSelectedLargeList=new ArrayList<>();
    public static ArrayList<String> mSelectedWImageList=new ArrayList<>();
    public static ArrayList<String> mSelectedWVideoList=new ArrayList<>();
    public static ArrayList<String> mSelectedVaultList=new ArrayList<>();

    public static String VIEW_TYPE = "Grid";
    public static int COLUMN_TYPE = 3;
    private static final DecimalFormat format = new DecimalFormat("#.##");
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;
    public static boolean clickEnable=true;
    public static boolean showCircle=false;
    public static boolean isAllSelected=false;
    public static boolean isBottomPlayerExpanded=true;

    public static HashMap<String,Long> mCommenlist;
    public static HashMap<String,Long> mPairlist;
    public static ArrayList<String> mTitleList;
    public static ArrayList<BaseModel> mAllFiles;
    public static ArrayList<BaseModel> mFinalList;
    public static ArrayList<BaseModel> mDuplicateFiles1;
    public static Map<String, List<String>> lists ;
    public static ArrayList<BaseModel> mLargeFileList=new ArrayList<>();
    public static ArrayList<BaseModel> mLargeFinalList=new ArrayList<>();
    public static ArrayList<BaseModel> mWhatsappImgList=new ArrayList<>();
    public static ArrayList<BaseModel> mWhatsappVideoList=new ArrayList<>();

    public static String docDirNamedir = Environment.getExternalStorageDirectory().toString();
    public static boolean mDuplicateDelete=false, mLargeDelete=false,mWImgDelete=false,mWVideoDelete=false;
    public static long mDupRealeaseSize=0, mLargeRealeaseSize=0,mWImgRelease=0,mWVidRelease=0;

    static long mImgLargeSize=0;
    static long mVidLargeSize=0;
    static long mMusicLargeSize=0;
    static long mDocLargeSize=0;

    public static boolean IsUpdate=false;
    public static boolean IsAPKUpdate=false;
    public static boolean IsAlbumUpdate=false;
    public static boolean VaultFromOther=false;

    static File whatsappMediaDirName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media");

    public static String convertTimeDateModifiedShown(long time) {
        Date date = new Date(time * 1000);
        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
        return format.format(date);
    }

    /**
     * Extract the file name in a URL
     * /storage/emulated/legacy/Download/sample.pptx = sample.pptx
     *
     * @param url String of a URL
     * @return the file name of URL with suffix
     */
    public static String extractFileNameWithSuffix(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


    /**
     * Extract the path in a URL
     * /storage/emulated/legacy/Download/sample.pptx = /storage/emulated/legacy/Download
     *
     * @param url String of a URL
     * @return the path of URL without the file separator
     */
    public static String extractPathWithoutSeparator(String url) {
        return url.substring(0, url.lastIndexOf("/"));
    }


    public static String convertTimeDateModified(long time) {
        Date date = new Date(time * 1000);
        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MMM.yyyy");
        return format.format(date);
    }

    public static String convertTimeDateModified1(long time) {

        @SuppressLint("SimpleDateFormat") Format format = new SimpleDateFormat("dd.MMM.yyyy");
        return format.format(time);
    }

    public static boolean delete(final Context context,final File file) {

        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[]{
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");
        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }

    public static String getFileSize(long file) {

        final double length = Double.parseDouble(String.valueOf(file));

        if (length > MiB) {
            return format.format(length / MiB) + " MB";
        }
        if (length > KiB) {
            return format.format(length / KiB) + " KB";
        }
        return format.format(length) + " B";
    }

    public static class ZipManager {
        private final int BUFFER_SIZE = 6 * 1024;

        public static Boolean unzip(String zipFile,String location,Context context) throws IOException{
            int count=0;
            boolean IsUnzip=false;
            try {
                File f = new File(location);
                if (!f.isDirectory()) {
                    f.mkdirs();
                }
                ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
                try {
                    ZipEntry ze = null;
                    while ((ze = zin.getNextEntry()) != null) {
                        count++;
                        String path = location + File.separator + ze.getName();

                        if (ze.isDirectory()) {
                            File unzipFile = new File(path);
                            if (!unzipFile.isDirectory()) {
                                unzipFile.mkdirs();
                            }
                        } else {
                            FileOutputStream fout = new FileOutputStream(path, false);

                            try {
                                for (int c = zin.read(); c != -1; c = zin.read()) {
                                    fout.write(c);
                                }
                                zin.closeEntry();
                            } finally {
                                fout.close();
                            }
                        }
                    }
                    Log.e("Zip","Done");
                    IsUnzip=true;
                    Toast.makeText(context,"Unzip " + count + " Files in " + location,Toast.LENGTH_SHORT).show();
                } finally {
                    zin.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Zip ex",e.getMessage());
            }
            return IsUnzip;
        }

    }

    public static void MoveToTrash(Context context,File sourceFile){
        mOriginalPath.add(sourceFile.getPath());

        SharedPreference.setTrashList(context, mOriginalPath);
        File from=new File(sourceFile.getAbsolutePath());
        File folder = new File(Environment.getExternalStorageDirectory(), ".TrashFiles");
        if(!folder.exists())
            folder.mkdir();
        File to=new File(folder.getAbsolutePath() + "/" + sourceFile.getName());
        from.renameTo(to);
        //   Log.e("From path",from.getPath());
        //   Log.e("To path",to.getPath());
    }

    public static String getSize(long size) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        long tera = giga * kilo;
        String s = "";
        double kb = (double)size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if(size < kilo) {
            s = size + " Bytes";
        } else if(size >= kilo && size < mega) {
            s =  String.format("%.2f", kb) + " KB";
        } else if(size >= mega && size < giga) {
            s = String.format("%.2f", mb) + " MB";
        } else if(size >= giga && size < tera) {
            s = String.format("%.2f", gb) + " GB";
        } else if(size >= tera) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }



//    ******************************************

    public static void DuplicateMainCall(){
        mCommenlist=new HashMap<>();
        mPairlist=new HashMap<>();
        mTitleList=new ArrayList<>();
        mAllFiles = new ArrayList<>();
        mFinalList = new ArrayList<>();
        mDuplicateFiles1 = new ArrayList<>();
         lists = new HashMap<String, List<String>>();

        mLargeFinalList.clear();
        mWhatsappImgList.clear();
        mWhatsappVideoList.clear();
        mAllFiles=findAllFiles(docDirNamedir,mAllFiles);


//        //   Log.e("Large files",String.valueOf(mLargeFileList.size()));
        for(int i = 0;i < mLargeFileList.size();i++){

            if(mLargeFileList.get(i).getSize()==mImgLargeSize || mLargeFileList.get(i).getSize()==mVidLargeSize
                    || mLargeFileList.get(i).getSize()==mMusicLargeSize || mLargeFileList.get(i).getSize()==mDocLargeSize){
//                   Log.e("Files i:" + i,"L : " + mLargeFileList.get(i));
                mLargeFinalList.add(mLargeFileList.get(i));
                mLargeRealeaseSize+=mLargeFileList.get(i).getSize();
            }
        }
        findDuplicateFiles(lists, new File(docDirNamedir));

        createDuplicateList();
    }

    private static final MessageDigest messageDigest;
    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("cannot initialize SHA-512 hash function", e);
        }
    }

public static void findDuplicateFiles(Map<String, List<String>> filesList,File directory) {
    for (File dirChild : directory.listFiles()) {
        if(!dirChild.getName().startsWith(".")){
            if(dirChild.isDirectory()){
                findDuplicateFiles(filesList,dirChild);

            }else{
                if(dirChild.getName().contains(".jpg")|| dirChild.getName().contains(".jpeg")|| dirChild.getName().contains(".png")||
                        dirChild.getName().contains(".mp3")||dirChild.getName().contains(".mkv")||dirChild.getName().contains(".mp4")||
                        dirChild.getName().contains(".doc")||dirChild.getName().contains(".pdf")||dirChild.getName().contains(".rtf")||
                        dirChild.getName().contains(".apk")||dirChild.getName().contains(".docx")||dirChild.getName().contains(".zip")||
                        dirChild.getName().contains(".ppt")||dirChild.getName().contains(".pptx")||dirChild.getName().contains(".xls")){
                    try{
                        // Read file as bytes
                        FileInputStream fileInput = new FileInputStream(dirChild);
                        byte[] fileData = new byte[(int)dirChild.length()];
                        fileInput.read(fileData);
                        fileInput.close();
                        // Create unique hash for current file
                        String uniqueFileHash = new BigInteger(1,messageDigest.digest(fileData)).toString(16);
                        List<String> identicalList = filesList.get(uniqueFileHash);
                        if(identicalList == null){
                            identicalList = new LinkedList<String>();
                        }
                        // Add path to list
                        identicalList.add(dirChild.getName());
                        // push updated list to Hash table
                        filesList.put(uniqueFileHash,identicalList);
                    }catch(IOException e){
                        throw new RuntimeException("cannot read file " + dirChild.getAbsolutePath(),e);
                    }
                }
            }
        }
    }
}

    public static ArrayList<BaseModel> findAllFiles(String filePath,ArrayList<BaseModel> docDataList) {

        File dir = new File(filePath);
        boolean success = true;
        if (success && dir.isDirectory()) {
            File[] listFile = dir.listFiles();
            if(listFile!=null && listFile.length>0){
                for (File file : listFile) {
                    if (file.isFile()) {
                        if (!file.getParentFile().getName().startsWith(".")) {
                            if (!file.getName().startsWith(".")) {

                                BaseModel BaseModel = new BaseModel();
                                BaseModel.setName(file.getName());
                                BaseModel.setBucketName(file.getParentFile().getName());
                                BaseModel.setPath(file.getAbsolutePath());
                                BaseModel.setSize(file.length());
                                BaseModel.setDate(convertTimeDateModified(file.lastModified()));

                                Directory<BaseModel> directory = new Directory<>();
                                directory.setId(BaseModel.getBucketId());
                                directory.setName(BaseModel.getDate());
                                directory.setPath(Util.extractPathWithoutSeparator(BaseModel.getPath()));

                                docDataList.add(BaseModel);

//                            ******************* Large file logic
                                if (file.getName().contains(".jpg") || file.getName().contains(".jpeg") || file.getName().contains(".png")) {
                                    if (mImgLargeSize < file.length()) {
                                        mImgLargeSize = file.length();
                                        mLargeFileList.add(BaseModel);
                                    }
                                } else if (file.getName().contains(".mp3")) {
                                    if (mMusicLargeSize < file.length()) {
                                        mMusicLargeSize = file.length();
                                        mLargeFileList.add(BaseModel);
                                    }
                                } else if (file.getName().contains(".mkv") || file.getName().contains(".mp4")) {
                                    if (mVidLargeSize < file.length()) {
                                        mVidLargeSize = file.length();
                                        mLargeFileList.add(BaseModel);
                                    }
                                } else if (file.getName().contains(".doc") || file.getName().contains(".pdf") || file.getName().contains(".rtf") ||
                                        file.getName().contains(".ppt") || file.getName().contains(".pptx") ||
                                        file.getName().contains(".docx") || file.getName().contains(".xls")) {
                                    if (mDocLargeSize < file.length()) {
                                        mDocLargeSize = file.length();
                                        mLargeFileList.add(BaseModel);
                                    }
                                }

//                            *******************  Whatsapp media logic
                                if (file.getAbsolutePath().contains(whatsappMediaDirName.getPath())) {
                                    if (file.getName().contains(".jpg") || file.getName().contains(".jpeg") || file.getName().contains(".png")) {
                                        mWhatsappImgList.add(BaseModel);
                                        mWImgRelease += file.length();
                                    }
                                    if (file.getName().contains(".mkv") || file.getName().contains(".mp4")) {
                                        mWhatsappVideoList.add(BaseModel);
                                        mWVidRelease += file.length();
                                    }
                                }
                            }
                        }

                    } else if (file.isDirectory()) {
                        findAllFiles(file.getAbsolutePath(), docDataList);
                    }
                }
            }
        }

        Collections.reverse(docDataList);
        return docDataList;
    }

    public static void createDuplicateList(){
        for (List<String> list : lists.values()) {
            if (list.size() > 1) {
                for (String file : list) {
//                   //   Log.e("File:",file);
                    mTitleList.add(file);
                }
            }
        }

        Collections.reverse(mAllFiles);
        for(int i = 0;i < mAllFiles.size();i++){
            for(int j = 0;j < mTitleList.size();j++){
                if(mAllFiles.get(i).getName().equals(mTitleList.get(j))){
                    mDuplicateFiles1.add(mAllFiles.get(i));
                }
            }
        }


        List<String> MainList=new ArrayList<>();
        for(int i=0;i<mDuplicateFiles1.size();i++){
            if(!MainList.contains(mDuplicateFiles1.get(i).getName())){
                MainList.add(mDuplicateFiles1.get(i).getName());
                mCommenlist.put(mDuplicateFiles1.get(i).getName(),mDuplicateFiles1.get(i).getSize());
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
            ChildItemList(key,value);
        }

    }

    public static List<BaseModel> ChildItemList(String model,Long size)
    {

//        //   Log.e("File Title:",model);

        for(int i=0;i<mDuplicateFiles1.size();i++){
            if(!mFinalList.contains(mDuplicateFiles1.get(i))){
//                if(DuplicateList.get(i).getName().equalsIgnoreCase(model) ){
                if(size==mDuplicateFiles1.get(i).getSize() && size!=0){
//                        //   Log.e("File content:",mDuplicateFiles1.get(i).getName());
                    mFinalList.add(mDuplicateFiles1.get(i));
                    mDupRealeaseSize+=mDuplicateFiles1.get(i).getSize();
                }
//                }
            }
        }
        return mFinalList;
    }




}
