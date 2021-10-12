package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.collection.ArraySet;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model.BaseModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class    SharedPreference{
    public static final String MyPREFERENCES = "myFiles";
    public static String LOGIN = "LOGIN";
    public static String PROTECT_ = "Protect_file";
    public static String PROTECT_LIST = "Protect_file_list";
    public static String TRASH_LIST = "Trash_file_list";
    public static String PROTECT_FILE = "Protect_file";
    public static String RECENT_FILE = "Recent_file";
    public static String PASSWORD = "PasswordProtect";

    public static String getPasswordProtect(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ans = sharedpreferences.getString(PASSWORD, "");
        return ans;
    }

    public static void setPasswordProtect(Context c1, String value) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PASSWORD, value);
        editor.apply();
    }

    public static boolean getLogin(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean ans = sharedpreferences.getBoolean(LOGIN, false);
        return ans;
    }

    public static void setLogin(Context c1,boolean value) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(LOGIN, value);
        editor.apply();
    }

    public static ArrayList<String> getHideFileList(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet(PROTECT_LIST, new ArraySet<>());
        ArrayList<String> sample= new ArrayList<>(set);
        return sample;
    }

    public static void setHideFileList(Context c1, ArrayList<String> hideFileList) {
        SharedPreferences prefs= c1.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=prefs.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(hideFileList);
        edit.putStringSet(PROTECT_LIST, set);
        edit.commit();
    }

    public static void setTrashList(Context c1, ArrayList<String> trashFileList) {
        SharedPreferences prefs= c1.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=prefs.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(trashFileList);
        edit.putStringSet(TRASH_LIST, set);
        edit.commit();
    }

    public static ArrayList<String> getTrashList(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Set<String> set = sharedpreferences.getStringSet(TRASH_LIST, new ArraySet<>());
        ArrayList<String> sample= new ArrayList<>(set);
        return sample;
    }

    public static String getSharedPreferenceUri(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ans = sharedpreferences.getString(PROTECT_, "");
        return ans;
    }

    public static void setSharedPreferenceUri(Context c1, Uri hideFileList) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PROTECT_, String.valueOf(hideFileList));
        editor.apply();
    }

    public static String getSharedPreference(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String ans = sharedpreferences.getString(PROTECT_FILE, "");
        return ans;
    }

    public static void setSharedPreference(Context c1, String hideFileList) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PROTECT_FILE, String.valueOf(hideFileList));
        editor.apply();
    }

//    public static ArrayList<BaseModel> getRecentList(Context c1) {
//        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        Set<String> set = sharedpreferences.getStringSet(RECENT_FILE, new ArraySet<>());
//        ArrayList<BaseModel> sample= new ArrayList<>(set);
//        return sample;
//    }
//
//    public static void setRecentList(Context c1, ArrayList<BaseModel> hideFileList) {
//        SharedPreferences prefs= c1.getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit=prefs.edit();
//        Set<String> set = new HashSet<String>();
//        set.addAll(hideFileList);
//        edit.putStringSet(RECENT_FILE, set);
//        edit.commit();
//    }

    public static ArrayList<BaseModel> getRecentList(Context c1) {
        SharedPreferences sharedpreferences = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String response = sharedpreferences.getString(RECENT_FILE, "");
        ArrayList<BaseModel> lstArrayList = gson.fromJson(response,
                new TypeToken<List<BaseModel>>() {
                }.getType());
        if (lstArrayList == null)
            lstArrayList = new ArrayList<>();
        return lstArrayList;
    }

    public static void setRecentList(Context c1, ArrayList<BaseModel> hideFileList) {
        Gson gson = new Gson();
        String json = gson.toJson(hideFileList);

        SharedPreferences prefs = c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(RECENT_FILE, json);
        edit.commit();
    }
}
