package com.techroof.suaduser.Authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.techroof.suaduser.R;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private String login;
    private FirebaseAuth mAuth;
    private String phNumber, verificationId;
    private EditText edtNumberLogin,countryCodeEt;
    private Button btnOTP;
    private ProgressDialog pd;
    private ImageView imgBack,fbBtn,googleBtn;
    private int RC_SIGN_IN = 0;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imgBack = findViewById(R.id.back_btn);
        btnOTP = findViewById(R.id.btn_request_otp);
        edtNumberLogin = findViewById(R.id.edt_phone_number_login);
        countryCodeEt=findViewById(R.id.edt_country_code_login);
        fbBtn=findViewById(R.id.fb_btn);
        googleBtn=findViewById(R.id.google_btn);

        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Sending OTP...");

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                googleLogin();

            }
        });
/*
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","user_birthday"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                handleFacebookAccessToken(loginResult.getAccessToken());

                            }

                            @Override
                            public void onCancel() {
                                // App code
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                            }
                        });
            }
        });

 */

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });


        btnOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phNumber = countryCodeEt.getText().toString()+edtNumberLogin.getText().toString();

                if (TextUtils.isEmpty(edtNumberLogin.getText())){

                    edtNumberLogin.setError("Enter Phone Number");

                }else{

                    pd.show();
                    sendVerificationCode(phNumber);

                }

                //final FirebaseUser user = task.getResult().getUser();

            }
        });
    }

    //otp generator
    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(0L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks


            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            pd.dismiss();

            verificationId = s;

            Intent intent = new Intent(LoginActivity.this, OTPActivity.class);
            intent.putExtra("phoneNumber", phNumber);
            intent.putExtra("verificationId", verificationId);
            intent.putExtra("authenticationType", "login");
            startActivity(intent);
            pd.dismiss();

            Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            //mAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);


        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            pd.dismiss();
            Toast.makeText(getApplicationContext(), "Sorry Code Has Not Been Sent!" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void googleLogin() {

        pd.show();

        Intent signIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signIn, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            pd.dismiss();

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleLoginIntent(task);

        }else{

            callbackManager.onActivityResult(requestCode, resultCode, data);

        }

    }


    /*AccessTokenTracker tokenTracker= new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken==null){

                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }else{

                GraphRequest graphRequest= GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (object!=null){
                            try{

                                String email=object.getString("email");
                                String id=object.getString("id");
                                Toast.makeText(LoginActivity.this, ""+email+" "+id, Toast.LENGTH_SHORT).show();

                            }catch (Exception e){

                            }
                        }

                    }
                });

                Bundle parameters=new Bundle();
                parameters.putString("fields"," email,id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

            }

        }
    };



    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(LoginActivity.this, "success "+user, Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

     */

    private void handleGoogleLoginIntent(Task<GoogleSignInAccount> task) {

        pd.show();

        try {
            GoogleSignInAccount googleSignInAccount = task.getResult(ApiException.class);

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                String name = account.getDisplayName();
                String givenName = account.getGivenName();
                String familyName = account.getFamilyName();
                String email = account.getEmail();
                String id = account.getId();

            }
            //   startActivity(new Intent(LoginActivity.this,HomeActivity.class));

        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

    }
}