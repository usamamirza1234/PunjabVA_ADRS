package ast.adrs.va.ParentFragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonObject;

import ast.adrs.va.AppConfig;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_SignUp_VA;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.IBadgeUpdateListener;
import ast.adrs.va.Utils.IWebCallback;
import ast.adrs.va.Utils.PinEntry;

public class LoginbackFragment extends Fragment implements View.OnClickListener {

    RelativeLayout rlLogin;
    private String strEnteredPIN, strPIN, strPhone;
    private PinEntry mPIN;

    TextView txvNtReceived;
    private IBadgeUpdateListener mBadgeUpdateListener;
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

        setBottomBar();

        strEnteredPIN = "";
        strPIN = "";
        strPhone = "";

    }
    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded())
            mBadgeUpdateListener.setToolbarState(AppConstt.ToolbarState.TOOLBAR_HIDDEN);

    }



    private void requestResendOTP() {


        String lang = "";

        if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            lang = "u";
        } else {
            lang = "e";
        }


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userName", "");
        jsonObject.addProperty("authToken", "");
        jsonObject.addProperty("refreshToken", "");

        Log.d("LOG_AS", "Mobile : " + AppConfig.getInstance().mUserData.getPhone());
        Log.d("LOG_AS", "getWhatsapp : " + AppConfig.getInstance().mUserData.getWhatsapp());
        String data = "{" +
                "\"employeeName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                "\"fatherName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                "\"husbandName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                "\"placeOfPosting\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
//                    "\"selfImage\"" + ":\"" + edtName.getText().toString() + "\"," +
                "\"employeeRole\"" + ":\"" + "VA" + "\"," +
                "\"email\"" + ":\"" + AppConfig.getInstance().mUserData.getEmail() + "\"," +
                "\"isDeleted\"" + ":" + true + "," +
                "\"isVerified\"" + ":" + true + "," +
                "\"user\"" + ":" + jsonObject.toString() + "," +
                "\"verificationCode\"" + ":" + 0 + "," +
                "\"id\"" + ":" + 0 + "," +
                "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC().replaceFirst("^0+(?!$)", "") + "," +
                "\"employeeId\"" + ":" + 0 + "," +
                "\"bps\"" + ":" + 0 + "," +
                "\"mobileNo\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                "\"gender\"" + ":" + AppConfig.getInstance().mUserData.getGenderID() + "," +
                "\"district\"" + ":" + AppConfig.getInstance().mUserData.getDesignationID() + "," +
                "\"teshil\"" + ":" + AppConfig.getInstance().mUserData.getTehsilID() + "," +
                "\"designation\"" + ":" + AppConfig.getInstance().mUserData.getDesignationID() + "," +
                "\"whatsappMobileNo\"" + ":" + AppConfig.getInstance().mUserData.getWhatsapp() + "," +
                "\"preferedLanguage\"" + ":\"" + lang +
                "\"}";

        Log.d("LOG_AS", "postSignUp: " + data);

        requestVARegister(data);


    }
    //region  functions for Dialog
    private void dismissProgDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgDialog() {
        progressDialog = new Dialog(getActivity(), R.style.AppTheme);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_progress);

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //endregion
    private Dialog progressDialog;

    private void requestVARegister(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_SignUp_VA intro_webHit_post_signUp_va = new Intro_WebHit_Post_SignUp_VA();
        intro_webHit_post_signUp_va.postSignUpVA(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());
                    AppConfig.getInstance().mUserData.setName(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getEmployeeName());
                    AppConfig.getInstance().mUserData.setCNIC(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getCnic());
                    AppConfig.getInstance().mUserData.setWhatsapp(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getWhatsappMobileNo());
                    AppConfig.getInstance().mUserData.setPhone(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getMobileNo());
                    AppConfig.getInstance().mUserData.setEmail(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getEmail());

                    AppConfig.getInstance().saveUserProfileData();
                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());

            }
        }, _signUpEntity);
    }

    private void bindViews(View frg) {
        rlLogin = frg.findViewById(R.id.frg_verifiacction_rl_login);
        mPIN = frg.findViewById(R.id.frg_sign_up_verifictn_pin_entry);




        txvNtReceived = frg.findViewById(R.id.frg_sign_up_verifictn_txv_nt_received);


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
        txvNtReceived.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.frg_sign_up_verifictn_txv_nt_received:
                requestResendOTP();


                break;


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
