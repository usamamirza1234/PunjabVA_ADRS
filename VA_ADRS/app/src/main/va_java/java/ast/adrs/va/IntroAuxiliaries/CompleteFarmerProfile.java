package ast.adrs.va.IntroAuxiliaries;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ast.adrs.va.AppConfig;
import ast.adrs.va.IntroActivity;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Mozah;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Tehsil;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Save_Farmer;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Save_GetFarmerByCNIC;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Token;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.CircleImageView;
import ast.adrs.va.Utils.GPSTracker;
import ast.adrs.va.Utils.IBadgeUpdateListener;
import ast.adrs.va.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class CompleteFarmerProfile extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    Calendar calendar;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    DatePickerDialog.OnDateSetListener date;
    TextView txvName, txvCNIC, txvPhone, txvGender, txvTehsil, txvDistrict, txvMozah;
    EditText edtWhatsapp, edtFatherName;
    RelativeLayout rlRegisterVA;
    LinearLayout llTehsil, llMozuh;
    Spinner spinnerGender = null;
    Spinner spinnerDistrict = null;
    Spinner spinnerTehsil = null;
    Spinner spinnerMozah = null;
    SpinnerAdapter spinnerAdapter;
    boolean isLoadFirst;
    boolean isLoadFirst_Mozah;
    CircleImageView civFarmerImv, civEmpImv;
    ArrayList<DModel_District> lst_Tehsil = new ArrayList<>();
    ArrayList<DModel_District> lst_Mozah = new ArrayList<>();
    double latitude, longitude;
    private IBadgeUpdateListener mBadgeUpdateListener;


    //region init
    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_complete_profile, container, false);
        initData();
        bindViews(frg);
        requestData();
        requestLocationPermission();

        String upperString = AppConfig.getInstance().mUserData.getName().substring(0, 1).toUpperCase() + AppConfig.getInstance().mUserData.getName().substring(1).toLowerCase();
        txvName.setText(upperString);






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

        String strPhoneNumber = String.valueOf(AppConfig.getInstance().mUserData.getPhone());
        strPhoneNumber = "" + strPhoneNumber.substring(1);
        strPhoneNumber = "0" + strPhoneNumber.substring(1);
        edtWhatsapp.setText(strPhoneNumber);
        txvPhone.setText(strPhoneNumber);
//        txvCNIC.setText(strCnic);
        Log.d("LOG_AS", "auth token: " + AppConfig.getInstance().mUserData.getAuthToken());
        Log.d("LOG_AS", "refresh token: " + AppConfig.getInstance().mUserData.getRefreshToken());


        return frg;
    }

    private void requestData() {

        populateSpinnerDistrict();

    }

    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded())
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);

    }

    private void bindViews(View frg) {


        txvGender = frg.findViewById(R.id.frg_complete_profile_txv_gender);

        txvDistrict = frg.findViewById(R.id.frg_complete_profile_txv_district);
        txvTehsil = frg.findViewById(R.id.frg_complete_profile_txv_tehsil);
        civFarmerImv = frg.findViewById(R.id.frg_complete_profile_imv_profile);
        txvName = frg.findViewById(R.id.frg_complete_profile_edt_name);
        txvPhone = frg.findViewById(R.id.frg_complete_profile_edt_phone);
        txvCNIC = frg.findViewById(R.id.frg_complete_profile_edt_cnic);
        txvMozah = frg.findViewById(R.id.frg_complete_profile_txv_mozuh);
        edtWhatsapp = frg.findViewById(R.id.frg_complete_profile_edt_whatsappphone);
        edtFatherName = frg.findViewById(R.id.frg_complete_profile_edt_email);


        llTehsil = frg.findViewById(R.id.frg_complete_profile_ll_tehsil);
        llMozuh = frg.findViewById(R.id.frg_complete_profile_ll_mozuh);
        rlRegisterVA = frg.findViewById(R.id.frg_signin_rl_login);

        spinnerGender = frg.findViewById(R.id.frg_complete_profile_spinner_gender);
        spinnerMozah = frg.findViewById(R.id.frg_complete_profile_spinner_mozuh);
        spinnerDistrict = frg.findViewById(R.id.frg_complete_profile_spinner_district);
        spinnerTehsil = frg.findViewById(R.id.frg_complete_profile_spinner_tehsil);

        populateSpinners();


        llTehsil.setOnClickListener(this);
        rlRegisterVA.setOnClickListener(this);
        civFarmerImv.setOnClickListener(this);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            setBottomBar();
        }
    }

    //endregion

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            civFarmerImv.setImageBitmap(photo);


            civFarmerImv.setVisibility(View.VISIBLE);


            try {
                Log.d("imv_selctedAnimal", "data: " + data.getExtras().get("data"));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
//                if (photo != null && !photo.isRecycled()) {
//                    photo.recycle();
//                    photo = null;
//                }

//                photo.recycle();
                Log.d("imv_selctedAnimal", "byteArray: " + byteArray);
            } catch (Exception e) {
                Log.d("imv_selctedAnimal", "Exception: Failed to convert img to byte " + e.getMessage());
            }


        }

    }

    private void populateSpinners() {
        ArrayList<String> lstGender = new ArrayList<>();
        lstGender.add(getResources().getString(R.string.male));
        lstGender.add(getResources().getString(R.string.female));
        lstGender.add(getResources().getString(R.string.select_gender));
        spinnerAdapter = new SpinnerAdapter(getContext(), lstGender);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvGender.setText(lstGender.get(position));


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerGender.setAdapter(spinnerAdapter);
        spinnerGender.setSelection(spinnerAdapter.getCount());


    }

    //endregion

    //region  APICALLS

    private void initData() {

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

    private void populateSpinnerDistrict() {
        SpinnerDistrictAdapter spinnerDistrictAdapter = null;
        spinnerDistrictAdapter = new SpinnerDistrictAdapter(getContext(), AppConfig.getInstance().getDistrictList());
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvDistrict.setText(AppConfig.getInstance().getDistrictList().get(position).getName());
                AppConfig.getInstance().mUserData.setDistrictID(Integer.valueOf(AppConfig.getInstance().getDistrictList().get(position).getId()));
                AppConfig.getInstance().mUserData.setDestrict((AppConfig.getInstance().getDistrictList().get(position).getName()));
                requestTehsil(AppConfig.getInstance().getDistrictList().get(position).getId());

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDistrict.setAdapter(spinnerDistrictAdapter);
        spinnerDistrict.setSelection(spinnerDistrictAdapter.getCount());

    }

    private void populateSpinnerTehsil() {

        SpinnerDistrictAdapter spinnerTehsilAdapter = null;
        spinnerTehsilAdapter = new SpinnerDistrictAdapter(getContext(), lst_Tehsil);
        spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvTehsil.setText(lst_Tehsil.get(position).getName());
                AppConfig.getInstance().mUserData.setTehsilID(Integer.valueOf(lst_Tehsil.get(position).getId()));
                AppConfig.getInstance().mUserData.setTehsil((lst_Tehsil.get(position).getName()));
                requestMozuh(String.valueOf(AppConfig.getInstance().mUserData.getTehsilID()));

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTehsil.setAdapter(spinnerTehsilAdapter);
        spinnerTehsil.setSelection(spinnerTehsilAdapter.getCount());


    }
    //endregion

    private void requestTehsil(String _id) {

        if (isLoadFirst) {
            llTehsil.setVisibility(View.VISIBLE);
            showProgDialog();
            Intro_WebHit_Get_All_Tehsil intro_webHit_get_all_tehsil = new Intro_WebHit_Get_All_Tehsil();

            intro_webHit_get_all_tehsil.getTehsil(new IWebCallback() {
                @Override
                public void onWebResult(boolean isSuccess, String strMsg) {
                    dismissProgDialog();
                    if (isSuccess) {
                        if (Intro_WebHit_Get_All_Tehsil.responseObject != null &&
                                Intro_WebHit_Get_All_Tehsil.responseObject.getResult() != null &&
                                Intro_WebHit_Get_All_Tehsil.responseObject.getResult().size() > 0) {

                            lst_Tehsil.clear();


                            for (int i = 0; i < Intro_WebHit_Get_All_Tehsil.responseObject.getResult().size(); i++) {


                                DModel_District dModel_tehsil = new DModel_District(Intro_WebHit_Get_All_Tehsil.responseObject.getResult().get(i).getValueMemeber()
                                        , Intro_WebHit_Get_All_Tehsil.responseObject.getResult().get(i).getDisplayMember());

                                lst_Tehsil.add(dModel_tehsil);
                            }

                            DModel_District dModel_tehsil = new DModel_District("0", getString(R.string.select_tehsil));
                            lst_Tehsil.add(dModel_tehsil);


                            populateSpinnerTehsil();
                        }

                    } else {
                    }
                }

                @Override
                public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

                }
            }, _id);


        }

        isLoadFirst = true;

    }

    private void requestMozuh(String _id) {

        if (isLoadFirst_Mozah) {
            llMozuh.setVisibility(View.VISIBLE);
            showProgDialog();
            Intro_WebHit_Get_All_Mozah intro_webHit_get_all_mozah = new Intro_WebHit_Get_All_Mozah();

            intro_webHit_get_all_mozah.getMouza(new IWebCallback() {
                @Override
                public void onWebResult(boolean isSuccess, String strMsg) {
                    dismissProgDialog();
                    if (isSuccess) {
                        if (Intro_WebHit_Get_All_Mozah.responseObject != null &&
                                Intro_WebHit_Get_All_Mozah.responseObject.getResult() != null &&
                                Intro_WebHit_Get_All_Mozah.responseObject.getResult().size() > 0) {

                            lst_Mozah.clear();


                            for (int i = 0; i < Intro_WebHit_Get_All_Mozah.responseObject.getResult().size(); i++) {


                                DModel_District dModel_tehsil = new DModel_District(Intro_WebHit_Get_All_Mozah.responseObject.getResult().get(i).getValueMemeber()
                                        , Intro_WebHit_Get_All_Mozah.responseObject.getResult().get(i).getDisplayMember());

                                lst_Mozah.add(dModel_tehsil);
                            }

                            DModel_District dModel_tehsil = new DModel_District("0", getString(R.string.select_mozah));
                            lst_Mozah.add(dModel_tehsil);


                            populateSpinnerMozah();
                        }

                    } else {
                    }
                }

                @Override
                public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

                }
            }, _id);


        }

        isLoadFirst_Mozah = true;

    }

    private void populateSpinnerMozah() {

        SpinnerDistrictAdapter spinnerMozahAdapter = null;
        spinnerMozahAdapter = new SpinnerDistrictAdapter(getContext(), lst_Mozah);
        spinnerMozah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvMozah.setText(lst_Mozah.get(position).getName());
                AppConfig.getInstance().mUserData.setMozah(lst_Mozah.get(position).getName());
                AppConfig.getInstance().mUserData.setMozahID(Integer.valueOf(lst_Mozah.get(position).getId()));


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMozah.setAdapter(spinnerMozahAdapter);
        spinnerMozah.setSelection(spinnerMozahAdapter.getCount());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.frg_signin_rl_login:

                checkErrorConditions();
                break;

            case R.id.frg_complete_profile_imv_profile: {

                takePhoto();
            }
            break;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void takePhoto() {
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }


    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
//            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();

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

    private void checkErrorConditions() {
        if ( checkCNICErrorCondition() && checkNumberErrorCondition() && checkGenderErrorCondition() && checkDesignationErrorCondition() && checkDestrictErrorCondition() && checkTehsilErrorCondition()) {


            if (txvGender.getText().toString().equalsIgnoreCase(getString(R.string.male))) {
                AppConfig.getInstance().mUserData.setGenderID(1);
            } else {
                AppConfig.getInstance().mUserData.setGenderID(2);
            }


            String strPhoneNumber = edtWhatsapp.getText().toString();
            try {
                strPhoneNumber = "92" + strPhoneNumber.substring(1);
            } catch (Exception e) {

            }

            String lang = "";

            if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
                lang = "u";
            } else {
                lang = "e";
            }


            AppConfig.getInstance().mUserData.setWhatsapp(strPhoneNumber);
            Date date = new Date();
            String str_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);

            String no_data = "nofarm";
            String data = "{" +
//                "\"id\"" + ":" + 0 + "," +
                    "\"latitude\"" + ":" + latitude + "," +
                    "\"longitude\"" + ":" + longitude + "," +
                    "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC() + "," +
                    "\"farmerName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                    "\"FarmName\"" + ":\"" + no_data + "\"," +

                    "\"FarmerID\"" + ":" + 1 + "," +

                    "\"fatherName\"" + ":\"" + edtFatherName.getText().toString() + "\"," +
                    "\"mobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                    "\"gender\"" + ":" + AppConfig.getInstance().mUserData.getGenderID() + "," +
                    "\"createdDate\"" + ":\"" + str_DATE + "\"," +
                    "\"district\"" + ":\"" + AppConfig.getInstance().mUserData.getDestrict() + "\"," +
                    "\"teshil\"" + ":\"" + AppConfig.getInstance().mUserData.getTehsil() + "\"," +
                    "\"mouza\"" + ":\"" + AppConfig.getInstance().mUserData.getMozah() + "\"," +
                    "\"designation\"" + ":\"" + AppConfig.getInstance().mUserData.getDesignation() + "\"," +
                    "\"mouzaID\"" + ":" + AppConfig.getInstance().mUserData.getMozahID() + "," +
                    "\"whatsAppMobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getWhatsapp() + "," +
                    "\"preferedLanguage\"" + ":\"" + lang +
                    "\"}";


            requestGetFarmerByCNIC(data);

        }
    }

    private void requestGetFarmerByCNIC(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Save_GetFarmerByCNIC intro_webHit_post_save_getFarmerByCNIC = new Intro_WebHit_Post_Save_GetFarmerByCNIC();
        intro_webHit_post_save_getFarmerByCNIC.getFarmerByCNIC(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    if (Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject != null && Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().size() > 0) {
                        AppConfig.getInstance().mUserData.setId(
                                Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getId());
                        AppConfig.getInstance().mUserData.setLoggedIn(true);
                        AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                        AppConfig.getInstance().mUserData.setFarmer(true);
                        AppConfig.getInstance().mUserData.setVA(false);
                        AppConfig.getInstance().saveUserProfileData();
                        ((IntroActivity) getActivity()).navtoMainActivity();

                    } else {
                        requestFarmerSave(_signUpEntity);
                    }

                } else {
                    dismissProgDialog();
                    requestFarmerSave(_signUpEntity);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, AppConfig.getInstance().mUserData.getCNIC());
    }

    private void requestFarmerSave(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Save_Farmer intro_webHit_post_save_farmer = new Intro_WebHit_Post_Save_Farmer();
        intro_webHit_post_save_farmer.postFarmerSave(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    AppConfig.getInstance().mUserData.setId(
                            Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getId());
                    AppConfig.getInstance().mUserData.setLoggedIn(true);    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                    AppConfig.getInstance().mUserData.setFarmer(true);
                    AppConfig.getInstance().mUserData.setVA(false);
                    AppConfig.getInstance().saveUserProfileData();
                    navToAddFarmDetailFragment();
                } else {
                    dismissProgDialog();


                    if (strMsg.equalsIgnoreCase("510")) {
                        Log.d("LOG_AS", "requestFarmerUpdate 510: " + strMsg);
                        requestrefressToken();

                    } else
                        AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, _signUpEntity);
    }


    private void requestrefressToken() {
        showProgDialog();
        Intro_WebHit_Post_Token intro_webHit_post_token = new Intro_WebHit_Post_Token();
        intro_webHit_post_token.refreshToken(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    Log.d("LOG_AS", "refreshToken isSuccess: " + strMsg);

                    navToAddFarmDetailFragment();
                } else {
                    Log.d("LOG_AS", "refreshToken isfail: " + strMsg);
                    dismissProgDialog();

                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "refreshToken Exception: " + e.getMessage());

            }
        }, AppConfig.getInstance().mUserData.getRefreshToken());
    }

    private void navToAddFarmDetailFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new FarmProfileFragment();
        AppConfig.getInstance().isComingfromIntro = true;
        ft.add(R.id.act_intro_content_frg, frag, AppConstt.FragTag.FN_FarmProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_FarmProfileFragment);
        ft.hide(this);
        ft.commit();
    }


    //region  Check Validations
    private boolean checkNumberErrorCondition() {
        if (edtWhatsapp.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_mobile_whatsapp));
            return false;
        } else {
            return true;
        }
    }


    private boolean checkCNICErrorCondition() {
        if (txvCNIC.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_cnic));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean checkGenderErrorCondition() {
        if (txvGender.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_gender));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDesignationErrorCondition() {
        if (txvGender.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(),  getString(R.string.select_Designation));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDestrictErrorCondition() {
        if (txvDistrict.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(),  getString(R.string.select_district));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTehsilErrorCondition() {
        if (txvTehsil.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(),  getString(R.string.select_tehsil));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    //endregion


}
