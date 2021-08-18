package ast.adrs.va.ParentFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ast.adrs.va.AppConfig;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.PinEntry;

public class LoginbackFragment extends Fragment implements View.OnClickListener {

    RelativeLayout rlLogin;
    private String strEnteredPIN, strPIN, strPhone;
    private PinEntry mPIN;

    public LoginbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_login_back, container, false);


        initData();
        bindViews(frg);


        Log.d("LOG_AS", "Pin is: " + AppConfig.getInstance().mUserData.getPinCode());

        return frg;
    }


    private void initData() {


        strEnteredPIN = "";
        strPIN = "";
        strPhone = "";

    }


    private void bindViews(View frg) {
        rlLogin = frg.findViewById(R.id.frg_verifiacction_rl_login);
        mPIN = frg.findViewById(R.id.frg_sign_up_verifictn_pin_entry);


        mPIN.setOnPinEnteredListener(new PinEntry.OnPinEnteredListener() {
            @Override
            public void onPinEntered(String pin) {
                if (pin.length() == 4) {
                    strEnteredPIN = pin;
                    AppConfig.getInstance().closeKeyboard(getActivity());
                } else {
                    strEnteredPIN = "";
                }
            }
        });


        rlLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.frg_verifiacction_rl_login:
                if (mPIN.getText().toString().equalsIgnoreCase(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()))) {
                    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                    AppConfig.getInstance().saveUserProfileData();
                    navtoHomeFragmnet();
                } else
                    AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_otp__old));
        }
    }

    private void navtoHomeFragmnet() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft;
        Fragment frg = new HomeFragment();
        ft = fm.beginTransaction();
        ft.replace(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_HomeFragment);
//        ft.addToBackStack(AppConstt.FragTag.FN_HomeFragment);
//        hideLastStackFragment(ft);
        ft.commit();
    }


}
