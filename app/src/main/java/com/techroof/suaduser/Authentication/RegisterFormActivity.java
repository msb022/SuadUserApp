package com.techroof.suaduser.Authentication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techroof.suaduser.HomeActivity;
import com.techroof.suaduser.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterFormActivity extends AppCompatActivity {

    //private ConstraintLayout imgCL;
    private TextInputLayout nameEt, emailEt,passwordEt,confirmPassEt,phoneEt;
    private Button submitBtn;
    private Uri imageUri;
    //private ImageView uploadIV;
    private String imageUrl,phoneNumber;
    private ProgressDialog pd;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        submitBtn = findViewById(R.id.submit_btn);
        //imgCL = findViewById(R.id.image_cl);
        nameEt = findViewById(R.id.full_name_et);
        emailEt = findViewById(R.id.email_et);
        //uploadIV = findViewById(R.id.upload_img);
        passwordEt=findViewById(R.id.password_et);
        confirmPassEt=findViewById(R.id.confirm_et);
        phoneEt=findViewById(R.id.phone_et);

        pd=new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        firebaseStorage=FirebaseStorage.getInstance();
        firestore=FirebaseFirestore.getInstance();

        phoneNumber=getIntent().getStringExtra("phoneNumber");

        if (phoneNumber!=null){
            phoneEt.getEditText().setText(phoneNumber);
            phoneEt.setFocusable(false);
            phoneEt.setEnabled(false);
        }

      /*  imgCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryResultLauncher.launch(galleryIntent);
            }
        });

       */

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=nameEt.getEditText().getText().toString();
                String email=emailEt.getEditText().getText().toString();
                String password=passwordEt.getEditText().getText().toString();
                String confirmPass=confirmPassEt.getEditText().getText().toString();
                String phone=phoneEt.getEditText().getText().toString();

                if (TextUtils.isEmpty(name)){
                    nameEt.setError("Enter Name");
                }
                if (TextUtils.isEmpty(email)){
                    emailEt.setError("Enter Email");
                }

                if (TextUtils.isEmpty(phone)){
                    phoneEt.setError("Enter Phone Number");
                }

                if (TextUtils.isEmpty(password)){
                    passwordEt.setError("Enter Password");
                }

                if (TextUtils.isEmpty(confirmPass)){
                    confirmPassEt.setError("Confirm Password");
                }

                if (TextUtils.isEmpty(imageUrl)){
                    Toast.makeText(RegisterFormActivity.this, "Please add image", Toast.LENGTH_SHORT).show();
                }

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)
                        && !TextUtils.isEmpty(phone)
                        && !TextUtils.isEmpty(imageUrl)
                        && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPass)){

                    if (password.equals(confirmPass)){

                        addUserData(name,email,phone,imageUrl,password);


                    }else{
                        confirmPassEt.setError("Password doesn't match");
                    }

                }

            }
        });
    }

    private void addUserData(String name, String email,String phone, String imageUrl,String password) {

        pd.setMessage("Please wait...");
        pd.show();

        String uid=firestore.collection("users").document().getId();

        Map<String, Object> usersMap=new HashMap<>();
        usersMap.put("uid",uid);
        usersMap.put("name",name);
        usersMap.put("email",email);
        usersMap.put("phone",phone);
        usersMap.put("password",password);
        usersMap.put("url",imageUrl);

        firestore.collection("users").document(uid).set(usersMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Intent home=new Intent(RegisterFormActivity.this, HomeActivity.class);
                            startActivity(home);
                            pd.dismiss();
                            finish();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(RegisterFormActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();

                    }
                });

    }

    /*ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK) {

                        imageUri = result.getData().getData();
                        uploadIV.setImageURI(imageUri);

                        uploadImage();

                    }

                }
            });

     */
    void uploadImage() {

        StorageReference storageReference=firebaseStorage.getReference()
                .child("UsersImages"+ UUID.randomUUID().toString());

        pd.show();

        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageUrl=taskSnapshot.getStorage().getDownloadUrl().toString();
                pd.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(RegisterFormActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100.0 * snapshot.getBytesTransferred()
                        / snapshot.getTotalByteCount());

                pd.setMessage((int)progress + "%"+" Uploaded");

            }
        });

    }
}