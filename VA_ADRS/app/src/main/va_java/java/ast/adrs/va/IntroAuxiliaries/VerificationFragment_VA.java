package ast.adrs.va.IntroAuxiliaries;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;

import ast.adrs.va.AppConfig;
import ast.adrs.va.IntroActivity;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_SignUp_VA;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Verify_PinCode_VA;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.GPSTracker;
import ast.adrs.va.Utils.IWebCallback;
import ast.adrs.va.Utils.PinEntry;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;


public class VerificationFragment_VA extends Fragment implements View.OnClickListener, OTPListener {


    final static int resendTries = 3;
    private static final long TIME_COUNTDOWN = 20 * 1000;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    RelativeLayout rlLogin;
    int pinCodeTries = 1;
    TextView txvDesignation;
    TextView txvName, txvCNIC, txvPhone;
    double latitude, longitude;
    private TextView txvNtReceivedShown;
    private TextView txvOTP;
    private Dialog progressDialog;
    private String strEnteredPIN, strPIN, strPhone;
    private EditText edtCode;
    private Button btnConfirm;
    private TextView txvResend, txvCountDown;
    private PinEntry mPIN;
    private TextView txvNtReceived;
    private CountDownTimer mTimer;

    public VerificationFragment_VA() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_verification_va, container, false);

        initData();
        bindViews(frg);
        showSoftKeyboardForced();
        startCountDownTimer(TIME_COUNTDOWN);
        String strPhoneNumber = AppConfig.getInstance().mUserData.getPhone();

        strPhoneNumber = "" + strPhoneNumber.substring(1);
        strPhoneNumber = "0" + strPhoneNumber.substring(1);
        String str_changeTextColot = getColoredSpanned("", "#A0A0A0");
        str_changeTextColot = getColoredSpanned(strPhoneNumber, "#7DD92958");



        String str_firstText = getResources().getString(R.string.verification_code_send);
        String str_lastText = getResources().getString(R.string.verification_code_send_2);

        txvOTP.setText(Html.fromHtml(str_firstText + " " + str_changeTextColot + " " + str_lastText));


        txvDesignation.setText(AppConfig.getInstance().mUserData.getDesignation());
        txvName.setText(AppConfig.getInstance().mUserData.getName());


        txvPhone.setText(strPhoneNumber);
        String input = String.valueOf(AppConfig.getInstance().mUserData.getCNIC());     //input string
        String sixTewelveChars = "";     //substring containing first 4 characters
        String firstFiveChars = "";     //substring containing first 4 characters
        String lastChars = "";     //substring containing first 4 characters

        if (input.length() >= 10) {
            firstFiveChars = input.substring(0, 5);
            sixTewelveChars = input.substring(5, 12);
            lastChars = input.substring(12, 13);


            txvCNIC.setText(firstFiveChars + "-" + sixTewelveChars + "-" + lastChars);
        }

//        mPIN.setText(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()));
        return frg;
    }

    private void initData() {
        // Request the permission immediately here for the first time run
        requestPermissions(Manifest.permission.RECEIVE_SMS, PERMISSIONS_REQUEST_RECEIVE_SMS);
        strEnteredPIN = "";
        strPIN = "";
        strPhone = "";
        latitude = 0;
        longitude = 0;
        OtpReader.bind(this, "8700");

    }

    private void requestPermissions(String permission, int requestCode) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(getContext(), "Granting permission is necessary!", Toast.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permission},
                        requestCode);

                // requestCode is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("GPS_TRACKER", " : requestCode " + requestCode);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RECEIVE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    NotificationUtil.getInstance().show(this, NotificationUtil.CONTENT_TYPE.INFO,
//                            getResources().getString(R.string.app_name),
//                            "Permission granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

//                    NotificationUtil.getInstance().show(this, NotificationUtil.CONTENT_TYPE.ERROR,
//                            getResources().getString(R.string.app_name),
//                            "Permission denied! App will not function correctly");
                }
                return;
            }


            case REQUEST_LOCATION_PERMISSION: {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
            }
            break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();

            if (GPSTracker.getInstance(getContext()).getLatitude() != 0) {

                longitude = GPSTracker.getInstance(getContext()).getLongitude();
                latitude = GPSTracker.getInstance(getContext()).getLatitude();

                Log.d("GPS_TRACKER", "Lat : " + GPSTracker.getInstance(getContext()).getLatitude());
                Log.d("GPS_TRACKER", "Long : " + GPSTracker.getInstance(getContext()).getLongitude());
                Log.d("GPS_TRACKER", "Loc : " + GPSTracker.getInstance(getContext()).getLocation());
            }

        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    private void startCountDownTimer(long mCountDownTime) {
        txvNtReceived.setOnClickListener(null);

        txvNtReceived.setTextColor(getResources().getColor(R.color.gray));

        txvCountDown.setVisibility(View.VISIBLE);
        if (mTimer != null)
            mTimer.cancel();

        mTimer = new CountDownTimer(mCountDownTime, 1000) {

            public void onTick(long millisUntilFinished) {
//                txvCountdown.setText(millisUntilFinished / 1000 + " ");
                if (txvCountDown != null)
                    if ((millisUntilFinished % 60000 / 1000) < 10) {
                        txvCountDown.setText("0" + (millisUntilFinished / 60000) + ":" + "0" + (millisUntilFinished % 60000 / 1000));
                    } else {
                        txvCountDown.setText("0" + (millisUntilFinished / 60000) + ":" + (millisUntilFinished % 60000 / 1000));
                    }

//                remMillis = millisUntilFinished;
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                txvNtReceived.setVisibility(View.VISIBLE);
                hideCountDownTimer();
            }

        }.start();
    }

    private void hideCountDownTimer() {
        try {
            if (mTimer != null)
                mTimer.cancel();

            txvNtReceived.setOnClickListener(this);

            txvNtReceived.setTextColor(getResources().getColor(R.color.black));

            txvCountDown.setVisibility(View.INVISIBLE);

            txvCountDown.setText("00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews(View frg) {


        mPIN = frg.findViewById(R.id.frg_sign_up_verifictn_pin_entry);
        txvNtReceived = frg.findViewById(R.id.frg_sign_up_verifictn_txv_nt_received);
        txvCountDown = frg.findViewById(R.id.frg_sign_up_verifictn_txv_countdown);
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
        txvNtReceivedShown = frg.findViewById(R.id.frg_sign_up_verifictn_txv_nt_received_showen);
        txvOTP = frg.findViewById(R.id.frg_varification_va_txv_txv_otp);

        txvDesignation = frg.findViewById(R.id.frg_varification_va_txv_txv_designation);
        txvName = frg.findViewById(R.id.frg_varification_va_txv_edt_name);
        txvPhone = frg.findViewById(R.id.frg_varification_va_txv_edt_phone);
        txvCNIC = frg.findViewById(R.id.frg_varification_va_txv_edt_cnic);

        rlLogin = frg.findViewById(R.id.frg_verifiacction_rl_login);


        txvNtReceived.setOnClickListener(this);
        rlLogin.setOnClickListener(this);

    }


    private void hideMyKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtCode.getWindowToken(), 0);
    }

    private void showSoftKeyboardForced() {
        try {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            edtCode.postDelayed(new Runnable() {
                @Override
                public void run() {
                    edtCode.requestFocus();
                    imm.showSoftInput(edtCode, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void otpReceived(String smsText) {
        //Do whatever you want to do with the text
        String number = smsText.replaceAll("\\D+", "");
        try {
            mPIN.setText(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.frg_sign_up_verifictn_txv_nt_received:
                requestCode();

                break;
            case R.id.frg_verifiacction_rl_login:

//                requestLocationPermission();
                requestLogin();


                break;
            default:
                break;
        }
    }

    private void requestLogin() {

        Log.d("LOG_AS", "postSignUp: getPinCode " + AppConfig.getInstance().mUserData.getPinCode());

        if (mPIN.getText().toString().equalsIgnoreCase(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()))) {
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


            String data = "{" +
                    "\"employeeName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                    "\"fatherName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                    "\"husbandName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                    "\"placeOfPosting\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
//                            "\"selfImage\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +

                    "\"employeeRole\"" + ":\"" + "VA" + "\"," +
                    "\"email\"" + ":\"" + AppConfig.getInstance().mUserData.getEmail() + "\"," +


                    "\"isDeleted\"" + ":" + true + "," +
                    "\"isVerified\"" + ":" + true + "," +


//                            "\"user\"" + ":" + jsonObject.toString() + "," +


                    "\"verificationCode\"" + ":" + 0 + "," +
                    "\"id\"" + ":" + 0 + "," +
                    "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC() + "," +
                    "\"pinCode\"" + ":" + AppConfig.getInstance().mUserData.getPinCode() + "," +
                    "\"employeeId\"" + ":" + 0 + "," +
                    "\"bps\"" + ":" + 0 + "," +
                    "\"mobileNo\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                    "\"gender\"" + ":" + AppConfig.getInstance().mUserData.getGenderID() + "," +
                    "\"district\"" + ":" + AppConfig.getInstance().mUserData.getDistrictID() + "," +
                    "\"teshil\"" + ":" + AppConfig.getInstance().mUserData.getTehsilID() + "," +
                    "\"designation\"" + ":" + AppConfig.getInstance().mUserData.getDesignationID() + "," +
                    "\"whatsappMobileNo\"" + ":" + AppConfig.getInstance().mUserData.getWhatsapp() + "," +
                    "\"preferedLanguage\"" + ":\"" + lang +
                    "\"}";


            Log.d("LOG_AS", "postSignUp: " + data);


            requestVerifyPin(data);

        } else
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_otp_that_send));

    }

    private void requestVerifyPin(String data) {
        showProgDialog();
        Intro_WebHit_Post_Verify_PinCode_VA intro_webHit_post_verify_pinCode_va = new Intro_WebHit_Post_Verify_PinCode_VA();

        intro_webHit_post_verify_pinCode_va.getVAVerified(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                dismissProgDialog();
                if (isSuccess) {
                    if (Intro_WebHit_Post_Verify_PinCode_VA.responseObject != null &&
                            Intro_WebHit_Post_Verify_PinCode_VA.responseObject.getResult() != null &&
                            Intro_WebHit_Post_Verify_PinCode_VA.responseObject.getResult().getUser() != null) {

                        AppConfig.getInstance().mUserData.setId(Intro_WebHit_Post_Verify_PinCode_VA.responseObject.getResult().getId());

                        if (Intro_WebHit_Post_Verify_PinCode_VA.responseObject.getResult().getUser() != null) {
                            AppConfig.getInstance().mUserData.setAuthToken(Intro_WebHit_Post_Verify_PinCode_VA.responseObject.getResult().getUser().getAuthToken());
                            AppConfig.getInstance().mUserData.setRefreshToken(Intro_WebHit_Post_Verify_PinCode_VA.responseObject.getResult().getUser().getRefreshToken());

                        }


                        AppConfig.getInstance().mUserData.setLoggedIn(true);    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                        AppConfig.getInstance().mUserData.setFarmer(false);
                        AppConfig.getInstance().mUserData.setVA(true);
                        AppConfig.getInstance().saveUserProfileData();
                        ((IntroActivity) getActivity()).navtoMainActivity();

                    } else {
                        AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                    }

                } else {
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);
                AppConfig.getInstance().showErrorMessage(getContext(), ex.getLocalizedMessage());
            }
        }, data);

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

    private void requestCode() {
        if (pinCodeTries < resendTries) {
            requestResendOTP();

        } else {

            showOTP();

        }
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

    private void showOTP() {
        String str_changeTextColot = getColoredSpanned("", "#A0A0A0");

        str_changeTextColot = getColoredSpanned(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()), "#E80F1F");

        txvNtReceived.setVisibility(View.GONE);

        String str_firstText = getResources().getString(R.string.technical_fault_otp);

        txvNtReceivedShown.setText(Html.fromHtml(str_firstText + " " + str_changeTextColot));


        mPIN.setText(Html.fromHtml(str_changeTextColot));

        txvNtReceivedShown.setVisibility(View.VISIBLE);
    }


    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }


    private void requestVARegister(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_SignUp_VA intro_webHit_post_signUp_va = new Intro_WebHit_Post_SignUp_VA();
        intro_webHit_post_signUp_va.postSignUpVA(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());

                    startCountDownTimer(TIME_COUNTDOWN);
                    pinCodeTries++;
                    Log.d("LOG_AS", "postSignUp: New OTP IS " + Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());
                    Log.d("LOG_AS", "postSignUp: pinCodeTries " + pinCodeTries);
                    AppConfig.getInstance().mUserData.setName(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getEmployeeName());
                    AppConfig.getInstance().mUserData.setCNIC(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getCnic());
                    AppConfig.getInstance().mUserData.setWhatsapp(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getWhatsappMobileNo());
                    AppConfig.getInstance().mUserData.setPhone(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getMobileNo());
                    AppConfig.getInstance().mUserData.setEmail(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getEmail());


                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());
                if (pinCodeTries < resendTries) {
                    AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
                    showOTP();
                }
            }
        }, _signUpEntity);
    }


}

