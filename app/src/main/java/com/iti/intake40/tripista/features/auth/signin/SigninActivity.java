package com.iti.intake40.tripista.features.auth.signin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;

public class SigninActivity extends AppCompatActivity implements Delegate {
    public final static String EMAIL_ARG = "Email";
    public final static String PHONE_ARG = "Phone";
    FragmentManager mgr;
    Fragment signIn;
    FragmentTransaction trns;
    String verificationId;
    private FirebaseAuth auth;
    private SigninContract.PresenterInterface presenterInterface;
    private FireBaseCore core;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        core = FireBaseCore.getInstance();
        mgr = getSupportFragmentManager();
        trns = mgr.beginTransaction();
        signIn = new SignInFragment();
        trns.replace(R.id.container, signIn, "signFragment");
        trns.commit();
    }

    @Override
    public void setData(String data) {
        Bundle bundle = new Bundle();
        bundle.putString(EMAIL_ARG, data);
        // set Fragmentclass Arguments
        PasswordFragment passwordFragment = new PasswordFragment();
        passwordFragment.setArguments(bundle);
        trns = mgr.beginTransaction();
        trns.replace(R.id.container, passwordFragment, "passwordFragment");
        trns.commit();
    }

    @Override
    public void changeFragment(String data) {
        Bundle bundle = new Bundle();
        bundle.putString(PHONE_ARG, data);
        PhoneVerficiation phoneVerficiation = new PhoneVerficiation();
        phoneVerficiation.setArguments(bundle);
        trns = mgr.beginTransaction();
        core = FireBaseCore.getInstance();
        presenterInterface = new SigninPresenter(phoneVerficiation, core);
        trns.replace(R.id.container, phoneVerficiation, "phoneFragment");
        trns.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = mgr.findFragmentByTag("signFragment");
        fragment.onActivityResult(requestCode, resultCode, data);
    }


}
