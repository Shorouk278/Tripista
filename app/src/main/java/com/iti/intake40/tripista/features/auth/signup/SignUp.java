package com.iti.intake40.tripista.features.auth.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.core.model.UserModel;
import com.iti.intake40.tripista.features.auth.signin.SigninActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import static com.iti.intake40.tripista.features.auth.signin.SigninActivity.EMAIL_ARG;

public class SignUp extends AppCompatActivity implements SignupContract.ViewInterface {
    private ImageView profileImage;
    private String userName;
    private String phoneNumber;
    private String password;
    private String repassword;
    private String email;
    private UserModel model;
    private FireBaseCore core;
    private Uri imageUri;
    private TextInputEditText etUserName;
    private TextInputLayout etUserNameLayout,emailLayout,phoneLayout,passwordLayout,confirmPassLayout;
    private TextInputEditText etPasword;
    private TextInputEditText etRePassword;
    private TextInputEditText etPhoneNumber;
    private TextInputEditText etEmail;
    private SignupContract.PresenterInterface presenterInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        profileImage = findViewById(R.id.profile_image);
        etUserName = findViewById(R.id.et_user_name);
        etUserNameLayout=findViewById(R.id.input_layout_user_name);
        etPasword = findViewById(R.id.et_password);
        etRePassword = findViewById(R.id.et_confirm_password);
        confirmPassLayout = findViewById(R.id.input_layout_confirm_password);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        phoneLayout = findViewById(R.id.input_layout_phone);
        passwordLayout=findViewById(R.id.input_layout_password);
        etEmail = findViewById(R.id.et_user_email);
        emailLayout=findViewById(R.id.input_layout_user_email);
        core = FireBaseCore.getInstance();
        model = new UserModel();
    }

    //save data on fire base
    public void saveProfileData(View view) {
        userName = etUserName.getText().toString();
        phoneNumber = etPhoneNumber.getText().toString();
        password = etPasword.getText().toString();
        repassword = etRePassword.getText().toString();
        email = etEmail.getText().toString();
        etUserNameLayout.setError("");
        emailLayout.setError("");
        phoneLayout.setError("");
        passwordLayout.setError("");
        confirmPassLayout.setError("");
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(password)) {
            if(!password.equals(repassword))
            {
                confirmPassLayout.setError(getResources().getString(R.string.password_not_match));
            }
            else  if(password.length()<6)
            {
                passwordLayout.setError(getResources().getString(R.string.password_small));
            }
            else {
                model.setName(userName);
                model.setPhone("+2" + phoneNumber);
                model.setPassWord(password);
                if (imageUri != null)
                    model.setImageUrl(imageUri.toString());
                model.setEmail(email);
                presenterInterface.signup(model);
            }
        }
        if(TextUtils.isEmpty(userName))
        {
            etUserNameLayout.setError(getResources().getString(R.string.user_name_empty));
        }
        if(TextUtils.isEmpty(email))
        {
            emailLayout.setError(getResources().getString(R.string.email_empty));
        }
        if(TextUtils.isEmpty(phoneNumber))
        {
            phoneLayout.setError(getResources().getString(R.string.phone_empty));
        }
        if(TextUtils.isEmpty(password))
        {
            passwordLayout.setError(getResources().getString(R.string.password_empty));
        }

    }

    //get image from galary
    public void getImgFromGalory(View view) {
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .start(SignUp.this);
    }

    //get croped image as result by activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImage.setImageURI(imageUri);
        }
    }

    @Override
    public void sentMessage(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    @Override
    public void sentError(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void changeActivity() {
        Intent goSignIn = new Intent(this, SigninActivity.class);
        goSignIn.putExtra(EMAIL_ARG, email);
        goSignIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goSignIn);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenterInterface = new SignupPresenter(this, core);
    }


}
