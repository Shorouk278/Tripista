package com.iti.intake40.tripista.features.auth.signin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iti.intake40.tripista.R;
import com.iti.intake40.tripista.core.FireBaseCore;
import com.iti.intake40.tripista.features.auth.home.HomeActivity;

import static com.iti.intake40.tripista.features.auth.signin.SigninActivity.PHONE_ARG;

public class PhoneVerficiation extends Fragment implements SigninContract.ViewInterface {
    public static final String PREF_NAME = "tripista";
    private PinEntryEditText etPhoneCode;
    private FloatingActionButton nextBtn;
    private String phone;
    private FireBaseCore core;
    private SigninContract.PresenterInterface presenterInterface;
    private String code;


    public PhoneVerficiation() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phone = getArguments().getString(PHONE_ARG);
        core = FireBaseCore.getInstance();
        presenterInterface = new SigninPresenter(this, core);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_verficiation, container, false);
        etPhoneCode = view.findViewById(R.id.et_phone_verfy_code);
        nextBtn = view.findViewById(R.id.sign_in_phone);
        presenterInterface.signInWithMobile(phone);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code = etPhoneCode.getText().toString();
                presenterInterface.checKCode(code);
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
        //go to home
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SharedPreferences preferences = getActivity().getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PHONE_ARG, phone);
        editor.commit();
        startActivity(intent);
    }


    @Override
    public void reciveCode(String code) {
        etPhoneCode.setText(code);
        changeFragment();
    }
}
