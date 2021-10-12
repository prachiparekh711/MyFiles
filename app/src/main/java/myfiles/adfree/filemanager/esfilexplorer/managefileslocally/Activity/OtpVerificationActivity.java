package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Preferences.SharedPreference;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.R;
import myfiles.adfree.filemanager.esfilexplorer.managefileslocally.databinding.ActivityOtpVerificationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {

    ActivityOtpVerificationBinding verificationBinding;
//    MyClickHandlers myClickHandlers;

    private String mVerificationId = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    TextView mSendOTP,mOTP;
    ImageView mBack;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.e("LLLL_Verify_Com: ", "onVerificationCompleted:" + credential.getSmsCode());
            dialog.setMessage("Verifying otp...");
            dialog.show();
            verificationBinding.etOtp.setText(credential.getSmsCode());
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
//            Log.e("LLLL_Verify_Failed: ", "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                onBackPressed();
//                Log.e("LLLL_Verify_Failed1: ", "onVerificationFailed" + e.getLocalizedMessage());
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                onBackPressed();
                Log.e("LLLL_Verify_Failed2: ", "onVerificationFailed" + e.getLocalizedMessage());
            }

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
//            verifyVerificationCode(verificationId);
            Log.e("LLLL_Code: ", "onCodeSe+nt:" + verificationId);
            if (dialog != null && dialog.isShowing())
                dialog.dismiss();
            verificationBinding.llSendOtp.setVisibility(View.GONE);
            verificationBinding.llVerifyOtp.setVisibility(View.VISIBLE);

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    private FirebaseAnalytics mFirebaseAnalytics;
    private void fireAnalytics(String arg1, String arg2) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, arg1);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, arg2);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verificationBinding = DataBindingUtil.setContentView(OtpVerificationActivity.this, R.layout.activity_otp_verification);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(OtpVerificationActivity.this);
        dialog = new ProgressDialog(OtpVerificationActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mSendOTP=findViewById(R.id.tvSendOTP);
        mOTP=findViewById(R.id.tvOTP);
        mBack=findViewById(R.id.imgBack);
        mSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationBinding.etContactNo.getText().length() > 0) {
//                    if (isValidPhoneNumber(verificationBinding.etContactNo.getText().toString())) {
                    if (verificationBinding.etContactNo.getText().toString().matches("^[+(00)][0-9]{6,14}$")) {
                        dialog.setMessage("Sending otp...");
                        dialog.show();
                        sendOTP();
                    } else {
                        Toast.makeText(OtpVerificationActivity.this, "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationBinding.tvOTP.getText().toString().length() == 6) {
                    dialog.setMessage("Verifying otp...");
                    dialog.show();
                    verifyVerificationCode(verificationBinding.tvOTP.getText().toString());
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Please enter valid otp.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        myClickHandlers = new MyClickHandlers(OtpVerificationActivity.this);
//        verificationBinding.setOnClick(myClickHandlers);

    }

    private void sendOTP() {
        fireAnalytics("Verification", "OTP Send");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(verificationBinding.etContactNo.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)// Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        fireAnalytics("Verification", "Verify OTP");
        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        fireAnalytics("Verification", "Verify user");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("LLLL_User: ", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Log.e("LLLL_Verify_User: ", user.getPhoneNumber());
                            if (dialog != null && dialog.isShowing())
                                dialog.dismiss();
                            SharedPreference.setPasswordProtect(OtpVerificationActivity.this, "");
                            onBackPressed();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.e("LLLL_Faild: ", "signInWithCredential:failure", task.getException());
                            Toast.makeText(OtpVerificationActivity.this, "Please enter valid otp.", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
//        if (!TextUtils.isEmpty(phoneNumber)) {
//            return Patterns.PHONE.matcher(phoneNumber).matches();
//        }
//        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    public class MyClickHandlers {
//        Context context;
//
//        public MyClickHandlers(Context context) {
//            this.context = context;
//        }
//
//        public void onSendOtp(View view) {
//            if (verificationBinding.etContactNo.getText().length() > 0) {
//                if (isValidPhoneNumber(verificationBinding.etContactNo.getText().toString())) {
//                    dialog.setMessage("Sending otp...");
//                    dialog.show();
//                    sendOTP();
//                } else {
//                    Toast.makeText(OtpVerificationActivity.this, "Please enter a valid mobile number.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
//        public void onOtpClick(View view) {
//            if (verificationBinding.tvOTP.getText().toString().length() == 6) {
//                dialog.setMessage("Verifying otp...");
//                dialog.show();
//                verifyVerificationCode(verificationBinding.tvOTP.getText().toString());
//            } else {
//                Toast.makeText(OtpVerificationActivity.this, "Please enter valid otp.", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        public void onBackClick(View view) {
//            onBackPressed();
//        }
//
//    }
}