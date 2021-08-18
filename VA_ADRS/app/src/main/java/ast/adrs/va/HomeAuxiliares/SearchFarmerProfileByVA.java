package ast.adrs.va.HomeAuxiliares;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ast.adrs.va.AppConfig;
import ast.adrs.va.IntroAuxiliaries.DModel_District;
import ast.adrs.va.IntroAuxiliaries.SpinnerAdapter;
import ast.adrs.va.IntroAuxiliaries.SpinnerDistrictAdapter;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_District;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Mozah;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Tehsil;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Save_Farmer;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Save_GetFarmerByCNIC;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.CustomToast;
import ast.adrs.va.Utils.GPSTracker;
import ast.adrs.va.Utils.IBadgeUpdateListener;
import ast.adrs.va.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class SearchFarmerProfileByVA extends Fragment implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    private final int REQUEST_LOCATION_PERMISSION = 1;
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;
    TextView txvGender, txvTehsil, txvDistrict, txvMozah;
    TextView txv_FarmerGender, txv_FarmerTehsil, txv_FarmerDistrict, txv_FarmerMozah;
    TextView txv_FarmerName, txv_FarmerPhone;
    EditText edtFarmerName, edtCNIC, edtPhone;
    RelativeLayout rlRegisterVA, rl_search;
    LinearLayout llTehsil, llMozuh;
    Spinner spinnerGender = null;
    Spinner spinnerDistrict = null;
    Spinner spinnerTehsil = null;
    Spinner spinnerMozah = null;
    SpinnerAdapter spinnerAdapter;
    boolean isLoadFirst;
    boolean isLoadFirst_Mozah;
    ArrayList<DModel_District> lst_District = new ArrayList<>();
    ArrayList<DModel_District> lst_Tehsil = new ArrayList<>();
    ArrayList<DModel_District> lst_Mozah = new ArrayList<>();
    String mozahID, Mozah, districtID, Destrict, tehsilID, tehsil;
    LinearLayout llFarmerNotFounded, llFarmerFound;
    double latitude = 0, longitude = 0;
    boolean isCnicFounded;
    //region init
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_searchfarmer_profile_by_va, container, false);
        initData();
        bindViews(frg);
        checkForSmsPermission();
        requestLocationPermission();
        return frg;
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


        txvGender = frg.findViewById(R.id.frg_complete_profile_va_txv_gender);


        txv_FarmerName = frg.findViewById(R.id.frg_complete_profile_va_txv_name);
        txv_FarmerPhone = frg.findViewById(R.id.frg_complete_profile_va_txv_phone);
        txv_FarmerGender = frg.findViewById(R.id.frg_complete_profile_va_txv_farmer_gender);
        txv_FarmerDistrict = frg.findViewById(R.id.frg_complete_profile_va_txv_farmer_district);
        txv_FarmerTehsil = frg.findViewById(R.id.frg_complete_profile_va_txv_farmer_tehsil);
        txv_FarmerMozah = frg.findViewById(R.id.frg_complete_profile_va_txv_farmer_mozuh);

        txvDistrict = frg.findViewById(R.id.frg_complete_profile_va_txv_district);
        txvTehsil = frg.findViewById(R.id.frg_complete_profile_va_txv_tehsil);

        edtFarmerName = frg.findViewById(R.id.frg_complete_profile_vaedt_name);
        edtPhone = frg.findViewById(R.id.frg_complete_profile_vaedt_phone);
        edtCNIC = frg.findViewById(R.id.frg_complete_profile_vaedt_cnic);

        txvMozah = frg.findViewById(R.id.frg_complete_profile_va_txv_mozuh);

        llTehsil = frg.findViewById(R.id.frg_complete_profile_va_ll_tehsil);
        llMozuh = frg.findViewById(R.id.frg_complete_profile_va_ll_mozuh);
        rlRegisterVA = frg.findViewById(R.id.frg_signin_rl_login);
        rl_search = frg.findViewById(R.id.frg_complete_profile_va_rl_search);
        llFarmerNotFounded = frg.findViewById(R.id.frg_complete_profile_va_continue);
        llFarmerFound = frg.findViewById(R.id.frg_complete_profile_va_farmer);


        llFarmerNotFounded.setVisibility(View.GONE);
        llFarmerFound.setVisibility(View.GONE);
        rlRegisterVA.setVisibility(View.GONE);


        spinnerGender = frg.findViewById(R.id.frg_complete_profile_va_spiner_gender);
        spinnerMozah = frg.findViewById(R.id.frg_complete_profile_va_va_spinner_mozuh);
        spinnerDistrict = frg.findViewById(R.id.frg_complete_profile_va_spinner_district);
        spinnerTehsil = frg.findViewById(R.id.frg_complete_profile_va_spinner_tehsil);

        populateSpinners();


        llTehsil.setOnClickListener(this);
        rl_search.setOnClickListener(this);
        rlRegisterVA.setOnClickListener(this);


    }

    //endregion

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            setBottomBar();
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


//        ArrayList<String> lstDesgnation = new ArrayList<>();
//
//        lstDesgnation.add(getResources().getString(R.string.va));
//        lstDesgnation.add(getResources().getString(R.string.ait));
//        lstDesgnation.add(getResources().getString(R.string.select_Designation));
//
//
//        spinnerAdapter = null;
//        spinnerAdapter = new SpinnerAdapter(getContext(), lstDesgnation);
//        spinnerDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedItem = parent.getItemAtPosition(position).toString();
//
//                int Pos = Integer.parseInt(selectedItem);
//                txvDesignation.setText(lstDesgnation.get(position));
//
//
//            } // to close the onItemSelected
//
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        spinnerDesignation.setAdapter(spinnerAdapter);
//        spinnerDesignation.setSelection(spinnerAdapter.getCount());


    }

    //endregion

    private void initData() {
        isCnicFounded = false;
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

    //region  APICALLS
    private void requestGetFarmerByCNIC() {
        showProgDialog();
        Intro_WebHit_Post_Save_GetFarmerByCNIC intro_webHit_post_save_getFarmerByCNIC = new Intro_WebHit_Post_Save_GetFarmerByCNIC();
        intro_webHit_post_save_getFarmerByCNIC.getFarmerByCNIC(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    if (Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject != null && Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().size() > 0) {

//                        CustomToast.showToastMessage(getActivity(), "CNIC Founded", Toast.LENGTH_SHORT);
                        llFarmerNotFounded.setVisibility(View.GONE);
                        isCnicFounded = true;


                        {
                            AppConfig.getInstance().mUserData.setFarmerID(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getId() + "");
                        }


                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setName(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getFarmerName() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setFarmerID(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getId() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMouzaID(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getMouzaID() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setCnic(edtCNIC.getText().toString().replaceFirst("^0+(?!$)", "") + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setGender(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getGender() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMobileNumber(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getMobileNumber() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setTehsil(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getTehsil() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setDestrict(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getDistrict() + "");
                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMozah(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getMouza() + "");
                        llFarmerFound.setVisibility(View.VISIBLE);
                        rlRegisterVA.setVisibility(View.VISIBLE);


                        Log.d("LOG_AS", "getFarmFarm_GetByFarmerID search: " + Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getId() + " ");

                        String strPhoneNumber = String.valueOf(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getMobileNumber());
                        strPhoneNumber = "" + strPhoneNumber.substring(1);
                        strPhoneNumber = "0" + strPhoneNumber.substring(1);


                        txv_FarmerPhone.setText(strPhoneNumber + "");
                        txv_FarmerName.setText(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getFarmerName() + "");


                        if (Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getGender() == 1)
                            txv_FarmerGender.setText(getResources().getString(R.string.male));
                        else
                            txv_FarmerGender.setText(getResources().getString(R.string.female));


                        txv_FarmerDistrict.setText(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getDistrict() + "");
                        txv_FarmerTehsil.setText(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getTehsil() + "");
                        txv_FarmerMozah.setText(Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getMouza() + "");


//                        navtoDieaseFragment();

                    } else {
                        requestDistrict();
                        rlRegisterVA.setVisibility(View.VISIBLE);
                        llFarmerNotFounded.setVisibility(View.VISIBLE);
                        llFarmerFound.setVisibility(View.GONE);
                        isCnicFounded = false;
                    }

                } else {
                    dismissProgDialog();
                    requestDistrict();
                    rlRegisterVA.setVisibility(View.VISIBLE);
                    llFarmerNotFounded.setVisibility(View.VISIBLE);
                    llFarmerFound.setVisibility(View.GONE);
                    isCnicFounded = false;
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, edtCNIC.getText().toString().replaceFirst("^0+(?!$)", ""));
    }

    private void requestFarmerSave() {

        String strPhoneNumber = edtPhone.getText().toString();
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


        int genderId = 0;
        if (txvGender.getText().toString().equalsIgnoreCase(getResources().getString(R.string.male)))
            genderId = 1;
        else if (txvGender.getText().toString().equalsIgnoreCase(getResources().getString(R.string.female)))
            genderId = 2;
        Date date = new Date();
        String str_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);

        String no_data = "nofarm";
        String data = "{" +
//                "\"id\"" + ":" + 0 + "," +
                "\"latitude\"" + ":" + latitude + "," +
                "\"longitude\"" + ":" + longitude + "," +
                "\"cnic\"" + ":" + edtCNIC.getText().toString().replaceFirst("^0+(?!$)", "") + "," +
                "\"farmerName\"" + ":\"" + edtFarmerName.getText().toString() + "\"," +
                "\"FarmName\"" + ":\"" + no_data + "\"," +

                "\"FarmerID\"" + ":" + 1 + "," +

                "\"fatherName\"" + ":\"" + edtFarmerName.getText().toString() + "\"," +
                "\"mobileNumber\"" + ":" + strPhoneNumber + "," +
                "\"gender\"" + ":" + genderId + "," +
                "\"createdDate\"" + ":\"" + str_DATE + "\"," +
                "\"district\"" + ":\"" + Destrict + "\"," +
                "\"teshil\"" + ":\"" + tehsil + "\"," +
                "\"mouza\"" + ":\"" + Mozah + "\"," +
                "\"designation\"" + ":\"" + 1 + "\"," +
                "\"mouzaID\"" + ":" + mozahID + "," +
                "\"whatsAppMobileNumber\"" + ":" + strPhoneNumber + "," +
                "\"preferedLanguage\"" + ":\"" + lang +
                "\"}";

        //  "cnic": -9784999,
        //     "farmerName": "UsamaMirza",
        //    "fatherName": "ad",
        //    "gender": 1,
        //    "mobileNumber": 923315671991,
        //    "mouzaID": 32431960,
        //    "whatsAppMobileNumber": 18965010,
        //    "id": 34100169,
        //    "latitude": 5085536.769037008,
        //    "longitude": -8380184.085884094,
        //    "createdDate": "1987-01-09T22:23:12.474Z",
        //    "district": "qui do",
        //    "tehsil": "ea est incididunt",
        //    "mouza": "velit nisi Excepteur sit"

        Log.d("LOG_AS", "requestFarmerUpdate: " + data);


        showProgDialog();
        Intro_WebHit_Post_Save_Farmer intro_webHit_post_save_farmer = new Intro_WebHit_Post_Save_Farmer();
        intro_webHit_post_save_farmer.postFarmerSave(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    AppConfig.getInstance().mUserData.setFarmerID(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getId() + "");

                    Log.d("LOG_AS", "getFarmFarm_GetByFarmerID add: " + Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getId() + " ");

                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setName(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getFarmerName() + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setFarmerID(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getId() + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMouzaID(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getMouzaID() + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMouzaID(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getMouzaID() + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMozah(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getMouza() + "");

                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setCnic(edtCNIC.getText().toString().replaceFirst("^0+(?!$)", "") + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setGender(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getGender() + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setMobileNumber(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getMobileNumber() + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setTehsil(tehsil + "");
                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setDestrict(Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getDistrict() + "");

                    navtoDieaseFragment();


                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, data);
    }

    private void requestDistrict() {
        showProgDialog();
        Intro_WebHit_Get_All_District intro_webHit_get_all_district = new Intro_WebHit_Get_All_District();

        intro_webHit_get_all_district.getDistrict(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                dismissProgDialog();
                if (isSuccess) {
                    if (Intro_WebHit_Get_All_District.responseObject != null &&
                            Intro_WebHit_Get_All_District.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_District.responseObject.getResult().size() > 0) {


                        for (int i = 0; i < Intro_WebHit_Get_All_District.responseObject.getResult().size(); i++) {


                            DModel_District dModel_district = new DModel_District(Intro_WebHit_Get_All_District.responseObject.getResult().get(i).getValueMemeber()
                                    , Intro_WebHit_Get_All_District.responseObject.getResult().get(i).getDisplayMember());

                            lst_District.add(dModel_district);
                        }

                        DModel_District dModel_district = new DModel_District("0", getString(R.string.select_district));
                        lst_District.add(dModel_district);
                        populateSpinnerDistrict();
                    }

                } else {
                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }

    private void populateSpinnerDistrict() {
        SpinnerDistrictAdapter spinnerDistrictAdapter = null;
        spinnerDistrictAdapter = new SpinnerDistrictAdapter(getContext(), lst_District);
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvDistrict.setText(lst_District.get(position).getName());
                districtID = lst_District.get(position).getId();
                Destrict = (lst_District.get(position).getName());
                requestTehsil(lst_District.get(position).getId());

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
                tehsilID = lst_Tehsil.get(position).getId();
                tehsil = ((lst_Tehsil.get(position).getName()));
                requestMozuh(String.valueOf(AppConfig.getInstance().mUserData.getTehsilID()));

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTehsil.setAdapter(spinnerTehsilAdapter);
        spinnerTehsil.setSelection(spinnerTehsilAdapter.getCount());


    }

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
    //endregion

    private void navtoDieaseFragment() {
        IntimateDiseaseFragment frg = new IntimateDiseaseFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_IntimateDiseaseFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_IntimateDiseaseFragment);
        ft.hide(this);
        ft.commit();

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
//                txvMozah.setText(lst_Mozah.get(position).getName());
                Mozah = (lst_Mozah.get(position).getName());
                mozahID = lst_Mozah.get(position).getId();

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

                if (isCnicFounded)
                    navtoDieaseFragment();
                else
                    checkErrorConditions();
                break;
            case R.id.frg_complete_profile_va_rl_search:

                if (!edtCNIC.getText().toString().isEmpty()) {
                    if (edtCNIC.getText().toString().length() == 13) {
                        if (AppConfig.getInstance().isInternetEnabled())
                            requestGetFarmerByCNIC();
                        else checkForSmsPermission();
                    } else
                        AppConfig.getInstance().showErrorMessage(getActivity(), getString(R.string.enter_valid_cnic));
                } else
                    AppConfig.getInstance().showErrorMessage(getActivity(), getString(R.string.cnic_number_not_null));
                break;

        }
    }

    //region  Check Run Time Permissions
    private void requestSendSms(String toSender, String Message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toSender, null, Message, null, null);
            CustomToast.showToastMessage(getActivity(), getContext().getResources().getString(R.string.message_sent), Toast.LENGTH_LONG);

        } catch (Exception ex) {
            CustomToast.showToastMessage(getActivity(), ex.getMessage(), Toast.LENGTH_LONG);
        }
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("LOG_AS", "getString(R.string.permission_not_granted)");
            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            // Permission already granted. Enable the SMS button.
            if (!edtCNIC.getText().toString().isEmpty())
                requestSendSms("8700", edtCNIC.getText().toString().replaceFirst("^0+(?!$)", ""));
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("GPS_TRACKER", " : requestCode " + requestCode);
        switch (requestCode) {


            case REQUEST_LOCATION_PERMISSION: {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
            }
            break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    //endregion


    //region  Check Validations

    private void checkErrorConditions() {
        if (checkNameErrorCondition() && checkCNICErrorCondition() && checkGenderErrorCondition() &&
                checkDestrictErrorCondition() && checkTehsilErrorCondition()) {


            requestFarmerSave();


        }
    }

    private boolean checkNameErrorCondition() {
        if (edtFarmerName.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.empty_farmer_field));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkCNICErrorCondition() {
        if (edtCNIC.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.empty_cnic_field));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean checkGenderErrorCondition() {
        if (txvGender.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.empty_gender_field));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean checkDestrictErrorCondition() {
        if (txvDistrict.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.empty_district_field));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTehsilErrorCondition() {
        if (txvTehsil.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.empty_tehsil_field));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    //endregion


}
