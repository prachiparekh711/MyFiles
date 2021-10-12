package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.VaultAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.VaultListAdapter;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.MyMediaScannerConnectionClient;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Adapter.VaultAdapter.viewClose;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util.getSize;
import static myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Constant.Util.mSelectedVaultList;


public class VaultActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_OPEN_DOCUMENT_TREE = 42;
    private static final int INTENT_AUTHENTICATE = 101;
    @BindView(R.id.ll_password)
    LinearLayout ll_password;
    @BindView(R.id.tvEnterPass)
    TextView tvEnterPass;
    @BindView(R.id.anti_theft_t9_key_1)
    TextView anti_theft_t9_key_1;
    @BindView(R.id.anti_theft_t9_key_2)
    TextView anti_theft_t9_key_2;
    @BindView(R.id.anti_theft_t9_key_3)
    TextView anti_theft_t9_key_3;
    @BindView(R.id.anti_theft_t9_key_4)
    TextView anti_theft_t9_key_4;
    @BindView(R.id.anti_theft_t9_key_5)
    TextView anti_theft_t9_key_5;
    @BindView(R.id.anti_theft_t9_key_6)
    TextView anti_theft_t9_key_6;
    @BindView(R.id.anti_theft_t9_key_7)
    TextView anti_theft_t9_key_7;
    @BindView(R.id.anti_theft_t9_key_8)
    TextView anti_theft_t9_key_8;
    @BindView(R.id.anti_theft_t9_key_9)
    TextView anti_theft_t9_key_9;
    @BindView(R.id.anti_theft_t9_key_forgot)
    TextView anti_theft_t9_key_forgot;
    @BindView(R.id.anti_theft_t9_key_0)
    TextView anti_theft_t9_key_0;
    @BindView(R.id.anti_theft_t9_key_backspace)
    ImageView anti_theft_t9_key_backspace;
    @BindView(R.id.etPass1)
    AppCompatTextView etPass1;
    @BindView(R.id.etPass2)
    AppCompatTextView etPass2;
    @BindView(R.id.etPass3)
    AppCompatTextView etPass3;
    @BindView(R.id.etPass4)
    AppCompatTextView etPass4;

    String pass1 = "";
    String pass2 = "";
    String pass3 = "";
    String pass4 = "";
    String newPass1 = "";
    String newPass2 = "";
    String newPass3 = "";
    String newPass4 = "";
    Animation fadeout, fadein;

    private boolean isSet = false;
    ImageView lock;
    ArrayList<String> mVaultList=new ArrayList<>();
    RecyclerView recyclerView;
    VaultAdapter adapter;
    VaultListAdapter listAdapter;
    ImageView back;
    public static ImageView view,more,close,delete;
    public static LinearLayout actionLL,titleLL;
    public static RelativeLayout  mHeaderRL;
    public static TextView count;
    CardView card1,card2,card4;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ImageView proImage;
    TextView proName,proSize,proDate,proPath;
    MaterialCardView proOk;
    MediaScannerConnection msConn;
    LinearLayout mSelectAllC1,mRefreshC1;
    LinearLayout mDeselectAllC2,mVaultC2;
    LinearLayout mSelectAllC4,mVaultC4,mInfoC4,mDeselectAllC4;
    TextView cancelDel,okDel;
    private static boolean isDeleteImg = false;
    int backPresse=0;
    TextView successText,noData;
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
        setContentView(R.layout.activity_vault);

        ButterKnife.bind(this,VaultActivity.this);
        init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(VaultActivity.this);
        setAdapter();

        alertVault = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(VaultActivity.this);
        final View view = factory.inflate(R.layout.suceess_dialog,null);
        alertVault.setView(view);
        alertVault.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertVault.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successText=view.findViewById(R.id.success_text);

        anti_theft_t9_key_forgot.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

                if (km.isKeyguardSecure()) {
                    Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.app_name), "Confirm your screen lock.");
                    startActivityForResult(authIntent, INTENT_AUTHENTICATE);
                }else{
                    Intent intent = new Intent(getBaseContext(), OtpVerificationActivity.class);
                    startActivity(intent);

                }
            }
        });
        fadeout = AnimationUtils.loadAnimation(getBaseContext(), R.anim.stay);
        fadein = AnimationUtils.loadAnimation(getBaseContext(), R.anim.ripple_anim);

        anti_theft_t9_key_0.setOnClickListener(this);
        anti_theft_t9_key_1.setOnClickListener(this);
        anti_theft_t9_key_2.setOnClickListener(this);
        anti_theft_t9_key_3.setOnClickListener(this);
        anti_theft_t9_key_4.setOnClickListener(this);
        anti_theft_t9_key_5.setOnClickListener(this);
        anti_theft_t9_key_6.setOnClickListener(this);
        anti_theft_t9_key_7.setOnClickListener(this);
        anti_theft_t9_key_8.setOnClickListener(this);
        anti_theft_t9_key_9.setOnClickListener(this);
        anti_theft_t9_key_backspace.setOnClickListener(this);


        if (SharedPreference.getPasswordProtect(getBaseContext()).equals("")) {
            tvEnterPass.setText("Enter New Password");
        } else {
            tvEnterPass.setText("Enter Password");
            if (ll_password.getVisibility() == View.GONE)
            {
                ll_password.setVisibility(View.VISIBLE);
                lock.setVisibility(View.VISIBLE);
                mHeaderRL.setVisibility(View.GONE);
                noData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }
        }
        etPass4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!SharedPreference.getPasswordProtect(getBaseContext()).equals("")) {
                    String password = pass1 +
                            pass2 +
                            pass3 +
                            pass4;
                    if (password.equals(SharedPreference.getPasswordProtect(getBaseContext()))) {
                        if (!password.equals("****")) {
                            //   Log.e("LLLL_Password: ", password);
                            if (ll_password.getVisibility() == View.VISIBLE)
                            {
                                ll_password.setVisibility(View.GONE);
                                lock.setVisibility(View.GONE);

                                mHeaderRL.setVisibility(View.VISIBLE);
                                if(mVaultList!=null && mVaultList.size()>0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    noData.setVisibility(View.GONE);
                                }else{
                                    recyclerView.setVisibility(View.GONE);
                                    noData.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {

                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            if (!pass4.equals("")) {
                                etPass1.setText("");
                                etPass2.setText("");
                                etPass3.setText("");
                                etPass4.setText("");
                                etPass1.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                etPass2.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                etPass3.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                etPass4.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                pass1 = "";
                                pass2 = "";
                                pass3 = "";
                                pass4 = "";
                                Toast.makeText(getBaseContext(), "Password Incorrect...", Toast.LENGTH_SHORT).show();
                            }
                        }, 500);
                    }
                } else {
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        if (!isSet) {
                            if (!pass4.equals("")) {
                                isSet = true;
                                tvEnterPass.setText("Confirm Password");
                                etPass1.setText("");
                                etPass2.setText("");
                                etPass3.setText("");
                                etPass4.setText("");
                                etPass1.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                etPass2.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                etPass3.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                etPass4.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                            }
                        } else {
                            if (!newPass4.equals("")) {
                                String password = pass1 +
                                        pass2 +
                                        pass3 +
                                        pass4;

                                String password1 = newPass1 +
                                        newPass2 +
                                        newPass3 +
                                        newPass4;
                                if (password.equals(password1)) {
                                    if (!password.equals("****")) {
                                        //   Log.e("LLLL_Password: ", password);
                                        SharedPreference.setPasswordProtect(getBaseContext(), password);
                                        Toast.makeText(getBaseContext(), "Password set successfully...", Toast.LENGTH_SHORT).show();

                                        if(Util.VaultFromOther){
                                            Util.VaultFromOther=false;
                                            onBackPressed();
                                        }else {
                                            if (ll_password.getVisibility() == View.VISIBLE) {
                                                ll_password.setVisibility(View.GONE);
                                                lock.setVisibility(View.GONE);
                                                mHeaderRL.setVisibility(View.VISIBLE);
                                                if(mVaultList!=null && mVaultList.size()>0) {
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                    noData.setVisibility(View.GONE);
                                                }else{
                                                    recyclerView.setVisibility(View.GONE);
                                                    noData.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Password Incorrect...", Toast.LENGTH_SHORT).show();
                                    etPass1.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                    etPass2.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                    etPass3.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                    etPass4.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                                }
                            }
                        }
                    }, 500);
                }
            }
        });


    }

    public void init(){
        noData=findViewById(R.id.noData);
        lock=findViewById(R.id.lock);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        view=findViewById(R.id.view);
        actionLL=findViewById(R.id.l3);
        titleLL=findViewById(R.id.l1);
        mHeaderRL=findViewById(R.id.header);
        count=findViewById(R.id.count);
        delete=findViewById(R.id.delete);
        more=findViewById(R.id.more);


        recyclerView=findViewById(R.id.vaultRec);
        view.setOnClickListener(this);
        more.setOnClickListener(this);
        delete.setOnClickListener(this);

        View bottomSheet = findViewById(R.id.bottomProperty);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        proImage=findViewById(R.id.proImg);
        proDate=findViewById(R.id.proDate);
        proName=findViewById(R.id.proName);
        proPath=findViewById(R.id.proPath);
        proSize=findViewById(R.id.proSize);

        proOk=findViewById(R.id.proOk);
        proOk.setOnClickListener(this);

        card1=findViewById(R.id.card1);
        mSelectAllC1=findViewById(R.id.selectAllC1);
        mRefreshC1=findViewById(R.id.refreshC1);
        mSelectAllC1.setOnClickListener(this);
        mRefreshC1.setOnClickListener(this);

        card2=findViewById(R.id.card2);
        mDeselectAllC2=findViewById(R.id.deSelectAllC2);
        mVaultC2=findViewById(R.id.vaultC2);
        mDeselectAllC2.setOnClickListener(this);
        mVaultC2.setOnClickListener(this);


        card4=findViewById(R.id.card4);
        mSelectAllC4=findViewById(R.id.selectAllC4);
        mDeselectAllC4=findViewById(R.id.deSelectAllC4);
        mVaultC4=findViewById(R.id.vaultC4);
        mInfoC4=findViewById(R.id.infoC4);
        mSelectAllC4.setOnClickListener(this);
        mDeselectAllC4.setOnClickListener(this);
        mVaultC4.setOnClickListener(this);
        mInfoC4.setOnClickListener(this);

        close=findViewById(R.id.ic_close);
        close.setOnClickListener(this);
    }

    private void backSpaceCall() {
        if (!isSet) {
            String password = pass1 +
                    pass2 +
                    pass3 +
                    pass4;
            if (password.length() == 1) {
                etPass1.setText("");
                etPass1.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                pass1 = "";
            } else if (password.length() == 2) {
                etPass2.setText("");
                etPass2.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                pass2 = "";
            } else if (password.length() == 3) {
                etPass3.setText("");
                etPass3.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                pass3 = "";
            } else if (password.length() == 4) {
                etPass4.setText("");
                etPass4.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                pass4 = "";
            }
        } else {
            String password = newPass1 +
                    newPass2 +
                    newPass3 +
                    newPass4;
            if (password.length() == 1) {
                etPass1.setText("");
                etPass1.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                newPass1 = "";
            } else if (password.length() == 2) {
                etPass2.setText("");
                etPass2.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                newPass2 = "";
            } else if (password.length() == 3) {
                etPass3.setText("");
                etPass3.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                newPass3 = "";
            } else if (password.length() == 4) {
                etPass4.setText("");
                etPass4.setBackground(getResources().getDrawable(R.drawable.grey_pswd));
                newPass4 = "";
            }
        }
    }

    private void setText(TextView appCompatTextView) {
        if (etPass1.getText() == null || etPass1.getText().toString().trim().equals("")) {
            if (!isSet)
                pass1 = appCompatTextView.getText().toString().trim();
            else
                newPass1 = appCompatTextView.getText().toString().trim();

            etPass1.setText(appCompatTextView.getText().toString().trim());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable(){
                                    @Override
                                    public void run(){
                                        etPass1.setBackground(getResources().getDrawable(R.drawable.blue_pswd));
                                        etPass1.setText("*");
                                    }
                                },200);

        } else if (etPass2.getText() == null || etPass2.getText().toString().trim().equals("")) {
            if (!isSet)
                pass2 = appCompatTextView.getText().toString().trim();
            else
                newPass2 = appCompatTextView.getText().toString().trim();
            etPass2.setText(appCompatTextView.getText().toString().trim());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    etPass2.setBackground(getResources().getDrawable(R.drawable.blue_pswd));
                    etPass2.setText("*");
                }
            },200);
        } else if (etPass3.getText() == null || etPass3.getText().toString().trim().equals("")) {
            if (!isSet)
                pass3 = appCompatTextView.getText().toString().trim();
            else
                newPass3 = appCompatTextView.getText().toString().trim();
            etPass3.setText(appCompatTextView.getText().toString().trim());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    etPass3.setBackground(getResources().getDrawable(R.drawable.blue_pswd));
                    etPass3.setText("*");
                }
            },200);

        } else if (etPass4.getText() == null || etPass4.getText().toString().trim().equals("")) {
            if (!isSet)
                pass4 = appCompatTextView.getText().toString().trim();
            else
                newPass4 = appCompatTextView.getText().toString().trim();
            etPass4.setText(appCompatTextView.getText().toString().trim());
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    etPass4.setBackground(getResources().getDrawable(R.drawable.blue_pswd));
                    etPass4.setText("*");
                }
            },200);
        }

        fadeout = AnimationUtils.loadAnimation(getBaseContext(), R.anim.stay);
        fadein = AnimationUtils.loadAnimation( getBaseContext(), R.anim.ripple_anim);

        appCompatTextView.setBackground(getResources().getDrawable(R.drawable.light_blur_pswd));
        appCompatTextView.setTextColor(getResources().getColor(R.color.white));
        final Handler handler = new Handler(Looper.getMainLooper());
          handler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    appCompatTextView.setBackground(getResources().getDrawable(R.drawable.white_pswd));
                    appCompatTextView.setTextColor( getResources().getColor(R.color.black));
                }
            },200);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == INTENT_AUTHENTICATE){
            if(resultCode == RESULT_OK){
                //do something you want when pass the security
                SharedPreference.setPasswordProtect(getBaseContext(),"");
                isSet = false;
                if(SharedPreference.getPasswordProtect(getBaseContext()).equals("")){
                    tvEnterPass.setText("Enter New Password");
                }else{
                    tvEnterPass.setText("Enter Password");
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.anti_theft_t9_key_1:
                setText(anti_theft_t9_key_1);
                break;
            case R.id.anti_theft_t9_key_2:
                setText(anti_theft_t9_key_2);
                break;
            case R.id.anti_theft_t9_key_3:
                setText(anti_theft_t9_key_3);
                break;
            case R.id.anti_theft_t9_key_4:
                setText(anti_theft_t9_key_4);
                break;
            case R.id.anti_theft_t9_key_5:
                setText(anti_theft_t9_key_5);
                break;
            case R.id.anti_theft_t9_key_6:
                setText(anti_theft_t9_key_6);
                break;
            case R.id.anti_theft_t9_key_7:
                setText(anti_theft_t9_key_7);
                break;
            case R.id.anti_theft_t9_key_8:
                setText(anti_theft_t9_key_8);
                break;
            case R.id.anti_theft_t9_key_9:
                setText(anti_theft_t9_key_9 );
                break;
            case R.id.anti_theft_t9_key_0:
                setText(anti_theft_t9_key_0);
                break;
            case R.id.anti_theft_t9_key_backspace:
                backSpaceCall();
                break;

//                ****************************
            case R.id.view:
                changeViewIcon();
                break;
            case R.id.more:
                if(Util.isAllSelected){
                    card2.setVisibility(View.VISIBLE);
                    card1.setVisibility(View.GONE);
                    card4.setVisibility(View.GONE);
                }else  if(Util.mSelectedVaultList.size()==1){
                    card4.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    mInfoC4.setVisibility(View.VISIBLE);
                }else  if(Util.mSelectedVaultList.size()>1){
                    card4.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card1.setVisibility(View.GONE);
                    mInfoC4.setVisibility(View.GONE);
                } else{
                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.GONE);
                    card4.setVisibility(View.GONE);
                }
                break;
            case R.id.selectAllC1:
            case R.id.selectAllC4: {
                card1.setVisibility(View.GONE);

                Util.showCircle=true;
                viewClose=false;
                Util.mSelectedVaultList.clear();

                    for(int j = 0;j < mVaultList.size();j++){
                        if(!Util.mSelectedVaultList.contains(mVaultList.get(j))){
                            Util.mSelectedVaultList.add(mVaultList.get(j));
                        }
                    }
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        changeHomeView();
                        Util.isAllSelected=true;
                        if(adapter!=null)
                             adapter.notifyDataSetChanged();
                        if(listAdapter!=null)
                            listAdapter.notifyDataSetChanged();
                    }
                });

            }
            break;
            case R.id.refreshC1:
                card1.setVisibility(View.GONE);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        if(adapter!=null)
                            adapter.notifyDataSetChanged();
                        if(listAdapter!=null)
                            listAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case R.id.deSelectAllC4:{
                Util.clickEnable=true;
               card2Close();
            }

            break;
            case R.id.infoC4:
                File imgFile = new  File(Util.mSelectedVaultList.get(0));
                if(imgFile.exists()){
                    fileInfo();
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    Toast.makeText(VaultActivity.this,"Problem with file.",Toast.LENGTH_SHORT).show();
                }
                changeToOriginalView();
                break;
            case R.id.vaultC4:
            case R.id.vaultC2:
                fireAnalytics("Vault", "Unhide");
                moveFromVault();
                changeToOriginalView();
                setAdapter();
                break;
            case R.id.deSelectAllC2:
                Util.clickEnable=true;
                card2Close();
                break;
            case R.id.delete:
                ActionDelete();

                break;
            case R.id.ic_close:
                Util.clickEnable=true;
                card2Close();
               setAdapter();
                break;
            case R.id.proOk:
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }

    public void ActionDelete(){

        AlertDialog alertadd = new AlertDialog.Builder(this).create();
        LayoutInflater factory = LayoutInflater.from(VaultActivity.this);
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
                for(int j = 0;j < mSelectedVaultList.size();j++){
                    //   Log.e("package:",Util.mSelectedVaultList.get(j));
                    isDeleteImg=true;
                    boolean isDelete = false;

                    File sourceFile = new File(Util.mSelectedVaultList.get(j));

                    if(sourceFile.exists()){
//                            MoveToTrash(V.this,sourceFile);
                        isDelete = Util.delete(VaultActivity.this,sourceFile);
                        if(!isDelete)
                            isDelete=sourceFile.delete();
                        //   Log.e("LLLL_Del: ",String.valueOf(isDelete));
                    }
                    if(isDelete){
                        fireAnalytics("Vault", "Delete");
                        ArrayList<String> hideFileList =SharedPreference.getHideFileList(VaultActivity.this);
                        hideFileList.remove(sourceFile.getAbsolutePath());
                        SharedPreference.setHideFileList(VaultActivity.this, hideFileList);
                        count++;
                    }
                }

                if(count== mSelectedVaultList.size()){
                    successText.setText( "Delete successfully."  );
                    alertVault.show();

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            card2Close();
                            setAdapter();
                            alertVault.dismiss();
                        }
                    }, 2000);
                }else{
                    Toast.makeText(VaultActivity.this,"Something went wrong!!!",Toast.LENGTH_SHORT).show();
                }


                alertadd.dismiss();
            }
        });
        alertadd.show();
    }

    public void card2Close(){

        card2.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
        Util.showCircle=false;
        viewClose=true;
        mSelectedVaultList.clear();
        Util.isAllSelected=false;
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                changeToOriginalView();
                if(adapter!=null)
                    adapter.notifyDataSetChanged();
                if(listAdapter!=null)
                    listAdapter.notifyDataSetChanged();
            }
        });
    }

    void moveFromVault(){
        File sampleFile = Environment.getExternalStorageDirectory();
        for(int i = 0;i < mSelectedVaultList.size();i++){

            File file1 = new File(mSelectedVaultList.get(i));
            Uri uri = Uri.fromFile(file1);

            if(file1.getAbsolutePath().contains(sampleFile.getAbsolutePath())){
                Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
                SaveImage(bitmap,file1);
            }else{
                if(!SharedPreference.getSharedPreference(VaultActivity.this).contains(file1.getParentFile().getAbsolutePath())){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
                    intent.putExtra("android.content.extra.SHOW_ADVANCED",true);
                    startActivityForResult(intent,REQUEST_CODE_OPEN_DOCUMENT_TREE);
                }else{
                    getData(file1);
                }
            }
        }
        File[] files=new File[Util.mSelectedVaultList.size()];
        for(int i=0;i<Util.mSelectedVaultList.size();i++){
            files[i]=new File(Util.mSelectedVaultList.get(i));
        }
        scanPhoto(files);
    }

    private void SaveImage(Bitmap finalBitmap, File samplefile) {

        String[] parts = (samplefile.getAbsolutePath()).split("/");

        String string1 = parts[parts.length - 1].substring(0, parts[parts.length - 1].lastIndexOf('.'));
        String fileName = string1.replace(".", "");

        String extension = parts[parts.length - 1].substring(parts[parts.length - 1].lastIndexOf("."));

        File file = new File(samplefile.getParentFile().getAbsolutePath(),fileName + extension);
        File to=null;
        try {
//            if(samplefile.getName().contains(".jpg") || samplefile.getName().contains(".jpeg") || samplefile.getName().contains(".png")){
//                FileOutputStream out = new FileOutputStream(file);
//                finalBitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
//                out.flush();
//                out.close();
//            }else{
                File from = new File(samplefile.getAbsolutePath());
                File folder = new File(from.getParentFile().getAbsolutePath());
                if(!folder.exists())
                    folder.mkdir();
                 to = new File(folder.getAbsolutePath() + "/" + file.getName());
                from.renameTo(to);
//            }

            ArrayList<String> hideFileList = SharedPreference.getHideFileList(VaultActivity.this);
            hideFileList.remove(samplefile.getAbsolutePath());
            SharedPreference.setHideFileList(VaultActivity.this, new ArrayList<>());
            SharedPreference.setHideFileList(VaultActivity.this, hideFileList);

            Toast.makeText(this,   file.getName() + " Move out of vault successfully!!! ", Toast.LENGTH_SHORT).show();
            if (samplefile.exists()) {
                samplefile.delete();
            }


                MediaScannerConnection.MediaScannerConnectionClient client =
                        new MyMediaScannerConnectionClient(
                                getApplicationContext(), to, null);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void scanPhoto(File[] imageFileName) {
        msConn = new MediaScannerConnection(getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                if (msConn!=null)
                    msConn.connect();

                Log.i("msClient obj", "scan completed");
                for(int i=0;i<imageFileName.length;i++){
                    MediaScannerConnection.scanFile(getApplicationContext(),new String[]{ imageFileName[i].getPath() },new String[]{ "*/*" },null);
                }
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
            }
        });
        this.msConn.connect();
    }

    private void getData(File file1) {

        String[] parts = (file1.getAbsolutePath()).split("/");
        //   Log.e("LLL_Name: ", parts[parts.length - 1]);
        String string1 = parts[parts.length - 1].substring(0, parts[parts.length - 1].lastIndexOf('.'));
        String fileName = string1.replace(".", "");

        String extension = parts[parts.length - 1].substring(parts[parts.length - 1].lastIndexOf("."));
        //   Log.e("LLL_Name: ", extension);

        File file2 = new File(file1.getParentFile().getParentFile().getAbsolutePath(), fileName + extension);
        File file3 = new File(file1.getParentFile().getAbsolutePath(), fileName + extension);

        OutputStream fileOutupStream = null;
        DocumentFile targetDocument = getDocumentFile(file2, false);

        try {
            fileOutupStream = getContentResolver().openOutputStream(targetDocument.getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file1.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutupStream);
            fileOutupStream.flush();
            fileOutupStream.close();

            ArrayList<String> hideFileList = SharedPreference.getHideFileList(VaultActivity.this);
            hideFileList.remove(file1.getAbsolutePath());
            SharedPreference.setHideFileList(VaultActivity.this, new ArrayList<>());
            SharedPreference.setHideFileList(VaultActivity.this, hideFileList);

            if (file1.exists()) {
                boolean isDelete = Util.delete(VaultActivity.this, file1);
                //   Log.e("LLLL_Del: ", String.valueOf(isDelete));
            }

            successText.setText(  " File(s) moved out of vault."  );
            alertVault.show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertVault.dismiss();
                }
            }, 2000);
//            Toast.makeText(this, "Unhide " + file1.getName(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public DocumentFile getDocumentFile(final File file, final boolean isDirectory) {
        String baseFolder = getExtSdCardFolder(file);

        if (baseFolder == null) {
            return null;
        }

        String relativePath = null;
        try {
            String fullPath = file.getCanonicalPath();
            relativePath = fullPath.substring(baseFolder.length() + 1);
        } catch (IOException e) {
            return null;
        }

        Uri treeUri = Uri.parse(SharedPreference.getSharedPreferenceUri(VaultActivity.this));

        if (treeUri == null) {
            return null;
        }

        // start with root of SD card and then parse through document tree.
        DocumentFile document = DocumentFile.fromTreeUri(VaultActivity.this, treeUri);

        String[] parts = relativePath.split("\\/");
        for (int i = 0; i < parts.length; i++) {
            DocumentFile nextDocument = document.findFile(parts[i]);

            if (nextDocument == null) {
                if ((i < parts.length - 1) || isDirectory) {
                    nextDocument = document.createDirectory(parts[i]);
                } else {
                    nextDocument = document.createFile("image", parts[i]);
                }
            }
            document = nextDocument;
        }

        return document;
    }

    public String getExtSdCardFolder(final File file) {
        String[] extSdPaths = getExtSdCardPaths();
        try {
            for (int i = 0; i < extSdPaths.length; i++) {
                if (file.getCanonicalPath().startsWith(extSdPaths[i])) {
                    return extSdPaths[i];
                }
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String[] getExtSdCardPaths() {
        List<String> paths = new ArrayList<>();
        for (File file : getExternalFilesDirs("external")) {
            if (file != null && !file.equals(getExternalFilesDir("external"))) {
                int index = file.getAbsolutePath().lastIndexOf("/Android/data");
                if (index < 0) {
                    Log.w("TAG", "Unexpected external file dir: " + file.getAbsolutePath());
                } else {
                    String path = file.getAbsolutePath().substring(0, index);
                    try {
                        path = new File(path).getCanonicalPath();
                    } catch (IOException e) {
                        // Keep non-canonical path.
                    }
                    paths.add(path);
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }



    public void fileInfo(){
        File imgFile = new  File(Util.mSelectedVaultList.get(0));
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            proImage.setImageBitmap(myBitmap);
            proName.setText(imgFile.getName());

            Date lastModDate = new Date(imgFile.lastModified());
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
            String date = spf.format(lastModDate);
            proDate.setText(date);
            String s=getSize(imgFile.length());
            proSize.setText(s);
            proPath.setText(imgFile.getPath());
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

       setAdapter();
    }
    @Override
    public void onBackPressed(){

        Util.showCircle = false;
        Util.clickEnable = true;
        if(Util.mSelectedAppList.size()==0){
            viewClose=false;

            super.onBackPressed();
        }

        if(!viewClose){
            changeToOriginalView();

           setAdapter();

        }else{
            backPresse++;
            if(backPresse == 1){
                viewClose = false;
                finish();
            }
        }

    }
    void getVaultData(){
        mVaultList=SharedPreference.getHideFileList(VaultActivity.this);

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


    public void setAdapter(){

        getVaultData();


            if (Util.VIEW_TYPE == "Grid") {
                final GridLayoutManager layoutManager = new GridLayoutManager(getBaseContext(), Util.COLUMN_TYPE);
                recyclerView.setLayoutManager(layoutManager);

                adapter = new VaultAdapter(mVaultList, VaultActivity.this);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false));
                listAdapter = new VaultListAdapter(mVaultList, VaultActivity.this);
                recyclerView.setAdapter(listAdapter);
            }

    }

    public void changeToOriginalView(){
        Util.isAllSelected=false;
        Util.mSelectedVaultList.clear();
        VaultActivity.titleLL.setVisibility(View.VISIBLE);
        VaultActivity.actionLL.setVisibility(View.GONE);
        VaultActivity.mHeaderRL.setBackgroundColor(getResources().getColor(R.color.header_color));
        VaultActivity.delete.setVisibility(View.GONE);
        VaultActivity.view.setVisibility(View.VISIBLE);
        ImageViewCompat.setImageTintList(VaultActivity.more, ColorStateList.valueOf(getResources().getColor(R.color.themeColor)));
        viewClose=true;
        disableCard();
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

    public void changeHomeView(){
        VaultActivity.titleLL.setVisibility(View.GONE);
        VaultActivity.actionLL.setVisibility(View.VISIBLE);
        VaultActivity.mHeaderRL.setBackgroundColor(getResources().getColor(R.color.themeColor));
        VaultActivity.delete.setVisibility(View.VISIBLE);
        VaultActivity.view.setVisibility(View.GONE);
        ImageViewCompat.setImageTintList(VaultActivity.more, ColorStateList.valueOf(getResources().getColor(R.color.white)));
        VaultActivity.count.setText(Util.mSelectedVaultList.size() + " Selected");

    }

    public void disableCard(){
        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
    }

}