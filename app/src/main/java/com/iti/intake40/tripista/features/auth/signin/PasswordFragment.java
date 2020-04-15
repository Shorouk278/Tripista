package com.iti.intake40.tripista.features.auth.signin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.features.auth.home.HomeActivity;

import static com.iti.intake40.tripista.features.auth.signin.SigninActivity.EMAIL_ARG;

public class PasswordFragment extends Fragment implements SigninContract.ViewInterface {
    private static final String NAME = "LOGGEDPREF" ;
    private String email;
    private String password;
    private FireBaseCore core;
    private SigninContract.PresenterInterface presenterInterface;
    private TextInputEditText et_password;
    private FloatingActionButton signIn;
    private TextInputLayout passwordLayout;

    public PasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        core = FireBaseCore.getInstance();
        presenterInterface = new SigninPresenter(this, core);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        et_password = view.findViewById(R.id.et_password_login);
        signIn = view.findViewById(R.id.sign_in);
        passwordLayout = view.findViewById(R.id.input_layout_password);
        // Inflate the layout for this fragment
        email = getArguments().getString(EMAIL_ARG);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password = et_password.getText().toString();
                if (!(password.length() < 6)) {
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                        presenterInterface.signIn(email, password);
                    }
                } else {
                    passwordLayout.setError(getResources().getString(R.string.password_small));
                }
            }
        });

        return view;
    }

    @Override
    public void sentMessage(int message) {
        Toast.makeText(getActivity(), getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    @Override
    public void sentError(int message) {
        Toast.makeText(getActivity(), getResources().getString(message), Toast.LENGTH_LONG).show();

    }

    @Override
    public void changeFragment() {
        Toast.makeText(getActivity(), "go home", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getActivity(), HomeActivity.class);
       intent.putExtra("firstLogin","first");
//        startActivity(intent);
//        SharedPreferences share= getActivity().getSharedPreferences(NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=share.edit();
//        editor.putBoolean("isLoggedBefore",true);
//        editor.commit();
    }

    @Override
    public void reciveCode(String code) {

    }

}
