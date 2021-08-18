package ast.adrs.va.IntroAuxiliaries;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ast.adrs.va.AppConfig;
import ast.adrs.va.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.va.IntroActivity;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Designations;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_District;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Spices;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Tehsil;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_Post_SignUp_VA;
import ast.adrs.va.MyApplication;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.IBadgeUpdateListener;
import ast.adrs.va.Utils.IWebCallback;


public class SignInVAFragment extends Fragment implements View.OnClickListener {
    Calendar calendar;
    DatePickerDialog.OnDateSetListener date;

    LinearLayout rlDOB;
    TextView txvDob, txvGender, txvDesignation, txvTehsil, txvDistrict;
    EditText edtName, edtCNIC, edtPhone, edtWhatsapp, edtEmail;


    RelativeLayout rlRegisterVA;

    LinearLayout llTehsil;
    Spinner spinnerGender = null;
    Spinner spinnerDesignation = null;
    Spinner spinnerDistrict = null;
    Spinner spinnerTehsil = null;
    SpinnerAdapter spinnerAdapter;
    boolean isLoadFirst;
    private LinearLayout llSync, llNotSync;

    ArrayList<DModel_District> lst_Tehsil = new ArrayList<>();

    private RelativeLayout rlSwitchLang, rlContinue;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    ArrayList<DModel_District> lstDesgnation;
    ArrayList<DModel_District> lstSpices;
    List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel> lst_AllDiseaseDefination;
    ArrayList<DModel_District> lst_District;
    public SignInVAFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_signin_va, container, false);
        initData();
        bindViews(frg);



        Log.d("OFFLINE_DB", "getDesignationList()  " + AppConfig.getInstance().getDesignationList().size());
        Log.d("OFFLINE_DB", "getDistrictList()  " + AppConfig.getInstance().getDistrictList().size());
        Log.d("OFFLINE_DB", "getDiseasesList()  " + AppConfig.getInstance().getDiseasesList().size());
        Log.d("OFFLINE_DB", "getSpicesList()  " + AppConfig.getInstance().getSpicesList().size());


        if (AppConfig.getInstance().getDesignationList().size() <= 0) {
            requestDesignations();
        } else if (AppConfig.getInstance().getDistrictList().size() <= 0) {
            requestDistrict();
        } else if (AppConfig.getInstance().getDiseasesList().size() <= 0) {
            requestDiseaseDefinition();
        } else if (AppConfig.getInstance().getSpicesList().size() <= 0) {
            requestSpices();
        }
        requestData();

        return frg;
    }

    private void requestSpices() {

        Intro_WebHit_Get_All_Spices intro_webHit_get_all_spices = new Intro_WebHit_Get_All_Spices();

        intro_webHit_get_all_spices.getSpices(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
//                dismissProgDialog();
                if (isSuccess) {
                    llSync.setVisibility(View.GONE);
                    llNotSync.setVisibility(View.VISIBLE);

                    if (Intro_WebHit_Get_All_Spices.responseObject != null &&
                            Intro_WebHit_Get_All_Spices.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_Spices.responseObject.getResult().size() > 0) {

                        lstSpices.clear();
                        for (int i = 0; i < Intro_WebHit_Get_All_Spices.responseObject.getResult().size(); i++) {


                            DModel_District dModel_spices = new DModel_District(Intro_WebHit_Get_All_Spices.responseObject.getResult().get(i).getValueMemeber()
                                    , Intro_WebHit_Get_All_Spices.responseObject.getResult().get(i).getDisplayMember());

                            lstSpices.add(dModel_spices);
                        }

                        DModel_District dModel_spices = new DModel_District("0", getString(R.string.select_spices));
                        lstSpices.add(dModel_spices);


                        if (AppConfig.getInstance().getSpicesList() != null) {
                            AppConfig.getInstance().getSpicesList().clear();
                        }
                        AppConfig.getInstance().saveSpicesList(lstSpices);


                    }
                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }
    private void requestDesignations() {
        llSync.setVisibility(View.VISIBLE);
        llNotSync.setVisibility(View.GONE);
//        showProgDialog();
        Intro_WebHit_Get_All_Designations intro_webHit_get_all_designations = new Intro_WebHit_Get_All_Designations();

        intro_webHit_get_all_designations.getDesignations(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
//                dismissProgDialog();
                if (isSuccess) {

                    llSync.setVisibility(View.GONE);
                    llNotSync.setVisibility(View.VISIBLE);
                    Log.d("OFFLINE_DB", "getDistrictList()  " + AppConfig.getInstance().getDistrictList().size());
                    Log.d("OFFLINE_DB", "getDiseasesList()  " + AppConfig.getInstance().getDiseasesList().size());
                    Log.d("OFFLINE_DB", "getSpicesList()  " + AppConfig.getInstance().getSpicesList().size());

                    if (AppConfig.getInstance().getDistrictList().size() <= 0) {
                        llSync.setVisibility(View.VISIBLE);
                        llNotSync.setVisibility(View.GONE);
                        requestDistrict();
                    } else if (AppConfig.getInstance().getDiseasesList().size() <= 0) {
                        llSync.setVisibility(View.VISIBLE);
                        llNotSync.setVisibility(View.GONE);
                        requestDiseaseDefinition();
                    } else if (AppConfig.getInstance().getSpicesList().size() <= 0) {
                        llSync.setVisibility(View.VISIBLE);
                        llNotSync.setVisibility(View.GONE);
                        requestSpices();
                    } else {
                        llSync.setVisibility(View.GONE);
                        llNotSync.setVisibility(View.VISIBLE);
                    }


                    if (Intro_WebHit_Get_All_Designations.responseObject != null &&
                            Intro_WebHit_Get_All_Designations.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_Designations.responseObject.getResult().size() > 0) {


                        for (int i = 0; i < Intro_WebHit_Get_All_Designations.responseObject.getResult().size(); i++) {


                            Log.d("LOG_AS", "getDesignations: getValueMemeber " + Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember());

                            if (Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember().equalsIgnoreCase("va") ||
                                    Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember().equalsIgnoreCase("ait")
                                    ||  Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember().equalsIgnoreCase("labasst.")


                            ) {
                                DModel_District dModel_designation = new DModel_District(Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getValueMemeber()
                                        , Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember());

                                lstDesgnation.add(dModel_designation);
                            }

//                            DModel_District dModel_designation = new DModel_District(Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getValueMemeber()
//                                    , Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember());
//
//                            lstDesgnation.add(dModel_designation);
                        }

                        DModel_District dModel_designation = new DModel_District("0", getString(R.string.select_Designation));
                        lstDesgnation.add(dModel_designation);

                        if (AppConfig.getInstance().getDesignationList().size() >= 0) {
                            AppConfig.getInstance().getDesignationList().clear();
                        }
                        AppConfig.getInstance().saveDesignationList(lstDesgnation);

                        populateSpinnerDesignation();
                    }

                } else {
                    requestDesignations();
                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }

    private void requestDistrict() {

        Intro_WebHit_Get_All_District intro_webHit_get_all_district = new Intro_WebHit_Get_All_District();

        intro_webHit_get_all_district.getDistrict(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {

                if (isSuccess) {

                    llSync.setVisibility(View.GONE);
                    llNotSync.setVisibility(View.VISIBLE);

                    Log.d("OFFLINE_DB", "getDiseasesList()  " +
                            AppConfig.getInstance().getDiseasesList().size());
                    Log.d("OFFLINE_DB", "getSpicesList()  " +
                            AppConfig.getInstance().getSpicesList().size());
                    if (AppConfig.getInstance().getDiseasesList().size() <= 0) {
                        llSync.setVisibility(View.VISIBLE);
                        llNotSync.setVisibility(View.GONE);
                        requestDiseaseDefinition();
                    } else if (AppConfig.getInstance().getSpicesList().size() <= 0) {
                        llSync.setVisibility(View.VISIBLE);
                        llNotSync.setVisibility(View.GONE);
                        requestSpices();
                    } else {
                        llSync.setVisibility(View.GONE);
                        llNotSync.setVisibility(View.VISIBLE);
                    }


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

                        if (AppConfig.getInstance().getDistrictList().size() >=0) {
                            AppConfig.getInstance().getDistrictList().clear();
                        }
                        AppConfig.getInstance().saveDistrictList(lst_District);
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

    private void requestDiseaseDefinition() {
//        showProgDialog();
        Home_WebHit_Post_Get_DiseaseDefinationDetails home_webHit_post_get_diseaseDefinationDetails = new Home_WebHit_Post_Get_DiseaseDefinationDetails();
        home_webHit_post_get_diseaseDefinationDetails.getDiseaseDefinationDetails(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    llSync.setVisibility(View.GONE);
                    llNotSync.setVisibility(View.VISIBLE);
                    Log.d("OFFLINE_DB", "getSpicesList()  " +
                            AppConfig.getInstance().getSpicesList().size());
                    if (AppConfig.getInstance().getSpicesList().size() <= 0) {
                        llSync.setVisibility(View.VISIBLE);
                        llNotSync.setVisibility(View.GONE);
                        requestSpices();
                    } else {
                        llSync.setVisibility(View.GONE);
                        llNotSync.setVisibility(View.VISIBLE);
                    }


                    if (Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject != null &&
                            Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject.getResult() != null) {
                        for (int i = 0; i < Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject.getResult().size(); i++) {
                            Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel dModel = Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject;
                            lst_AllDiseaseDefination.add(dModel);


                            if (AppConfig.getInstance().getDiseasesList() != null) {
                                AppConfig.getInstance().getDiseasesList().clear();
                            }
                            AppConfig.getInstance().saveDiseasesList(lst_AllDiseaseDefination);
                        }
                    }
//                    dismissProgDialog();
                } else {
//                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
//                dismissProgDialog();
                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        });
    }






    private void requestData() {

        if (AppConfig.getInstance().getDesignationList().size()>0)
            populateSpinnerDesignation();
        if (AppConfig.getInstance().getDistrictList().size() >0)
            populateSpinnerDistrict();

    }

    //region init
    private void initializeDate() {
        final Calendar todayCalender = Calendar.getInstance(Locale.ENGLISH);


        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                if (todayCalender.after(calendar)) {

                    txvDob.setText(sdf.format(calendar.getTime()));
                }
            }

        };


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
        llSync = frg.findViewById(R.id.frg_choose_ll_when_sync);
        llNotSync = frg.findViewById(R.id.frg_choose_ll_when_not_sync);
        rlSwitchLang = frg.findViewById(R.id.frg_sgnin_rl_switchlang);
        txvDob = frg.findViewById(R.id.frg_sigin_va_edt_date_of_birth);
        txvGender = frg.findViewById(R.id.frg_sigin_va_txv_gender);
        txvDesignation = frg.findViewById(R.id.frg_sigin_va_txv_designation);
        txvDistrict = frg.findViewById(R.id.frg_sigin_va_txv_district);
        txvTehsil = frg.findViewById(R.id.frg_sigin_va_txv_tehsil);

        edtName = frg.findViewById(R.id.frg_sigin_va_edt_name);
        edtPhone = frg.findViewById(R.id.frg_sigin_va_edt_phone);
        edtCNIC = frg.findViewById(R.id.frg_sigin_va_edt_cnic);
        edtWhatsapp = frg.findViewById(R.id.frg_sigin_va_edt_whatsappphone);
        edtEmail = frg.findViewById(R.id.frg_sigin_va_edt_email);

        rlDOB = frg.findViewById(R.id.frg_sigin_va_rl_dob);


        llTehsil = frg.findViewById(R.id.frg_sigin_va_ll_tehsil);
        rlRegisterVA = frg.findViewById(R.id.frg_signin_rl_login);
        rlSwitchLang.setOnClickListener(this);
        spinnerGender = frg.findViewById(R.id.frg_sigin_va_spinner_gender);
        spinnerDesignation = frg.findViewById(R.id.frg_sigin_va_spinner_designation);
        spinnerDistrict = frg.findViewById(R.id.frg_sigin_va_spinner_district);
        spinnerTehsil = frg.findViewById(R.id.frg_sigin_va_spinner_tehsil);
        populateSpinners();

        rlDOB.setOnClickListener(this);
        llTehsil.setOnClickListener(this);
        rlRegisterVA.setOnClickListener(this);


    }

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
    }

    private void showCalender() {

        new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, date, 2001, 1,
                1).show();
    }

    //endregion

    private void initData() {
        setBottomBar();
        calendar = Calendar.getInstance();
        isLoadFirst = false;
        this.lstDesgnation = new ArrayList<>();
        this.lst_District = new ArrayList<>();
        this.lst_AllDiseaseDefination = new ArrayList<>();
        this.lstSpices = new ArrayList<>();

        initializeDate();
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

    //region  APICALLS

    private void populateSpinnerDesignation() {
        SpinnerDistrictAdapter spinnerDesignationAdapter = null;


        spinnerDesignationAdapter = new SpinnerDistrictAdapter(getContext(), AppConfig.getInstance().getDesignationList());


        spinnerDesignation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvDesignation.setText(AppConfig.getInstance().getDesignationList().get(position).getName());

                AppConfig.getInstance().mUserData.setDesignationID(Integer.valueOf(AppConfig.getInstance().getDesignationList().get(position).getId()));
                AppConfig.getInstance().mUserData.setDesignation((AppConfig.getInstance().getDesignationList().get(position).getName()));

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDesignation.setAdapter(spinnerDesignationAdapter);
        spinnerDesignation.setSelection(spinnerDesignationAdapter.getCount());
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.frg_sigin_va_rl_dob:
                showCalender();
                break;
            case R.id.frg_signin_rl_login:

                checkErrorConditions();
                break;
            case R.id.frg_sgnin_rl_switchlang:
                switchLang();
                break;
        }
    }

    private void switchLang() {
        Log.d("Locale", "mLanguage: " + AppConfig.getInstance().mLanguage);

        if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            MyApplication.getInstance().setAppLanguage(AppConstt.AppLang.LANG_EN);
        } else {
            MyApplication.getInstance().setAppLanguage(AppConstt.AppLang.LANG_UR);
        }

        reStartActivity();
    }

    private void reStartActivity() {
        if (getActivity() != null) {
//            getActivity().recreate();
            AppConfig.getInstance().shouldSkipSplash = true;
            Intent i = new Intent(getActivity(), IntroActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }
    private void navToVerificationFragment()
    {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new VerificationFragment_VA();
        ft.add(R.id.act_intro_content_frg, frag, AppConstt.FragTag.FN_VerificationFragment_VA);
        ft.addToBackStack(AppConstt.FragTag.FN_VerificationFragment_VA);
        ft.hide(this);
        ft.commit();
    }

    private void checkErrorConditions() {
        if (checkNameErrorCondition() && checkCNICErrorCondition() && checkNumberErrorCondition() && checkDobErrorCondition() && checkGenderErrorCondition() && checkDesignationErrorCondition() && checkDestrictErrorCondition() && checkTehsilErrorCondition()) {


            String strPhoneNumber = edtPhone.getText().toString();
            strPhoneNumber = "92" + strPhoneNumber.substring(1);


            String lang = "";

            if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
                lang = "u";
            } else {
                lang = "e";
            }

            if (txvGender.getText().toString().equalsIgnoreCase("Male")) {
                AppConfig.getInstance().mUserData.setGenderID(1);
            } else {
                AppConfig.getInstance().mUserData.setGenderID(2);
            }

            AppConfig.getInstance().mUserData.setPhone(strPhoneNumber);


            String strPhoneNumberW = edtWhatsapp.getText().toString();
            strPhoneNumberW = "92" + strPhoneNumberW.substring(1);

            AppConfig.getInstance().mUserData.setWhatsapp(strPhoneNumberW);
            AppConfig.getInstance().mUserData.setEmail((edtEmail.getText().toString()));


            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userName", "");
            jsonObject.addProperty("authToken", "");
            jsonObject.addProperty("refreshToken", "");


            String data = "{" +
                    "\"employeeName\"" + ":\"" + edtName.getText().toString() + "\"," +
                    "\"fatherName\"" + ":\"" + edtName.getText().toString() + "\"," +
                    "\"husbandName\"" + ":\"" + edtName.getText().toString() + "\"," +
                    "\"placeOfPosting\"" + ":\"" + edtName.getText().toString() + "\"," +
//                    "\"selfImage\"" + ":\"" + edtName.getText().toString() + "\"," +
                    "\"employeeRole\"" + ":\"" + "VA" + "\"," +
                    "\"email\"" + ":\"" + AppConfig.getInstance().mUserData.getEmail() + "\"," +
                    "\"isDeleted\"" + ":" + true + "," +
                    "\"isVerified\"" + ":" + true + "," +
                    "\"user\"" + ":" + jsonObject.toString() + "," +
                    "\"verificationCode\"" + ":" + 0 + "," +
                    "\"id\"" + ":" + 0 + "," +
                    "\"cnic\"" + ":" + edtCNIC.getText().toString().replaceFirst("^0+(?!$)", "") + "," +
                    "\"employeeId\"" + ":" + 0 + "," +
                    "\"bps\"" + ":" + 0 + "," +
                    "\"mobileNo\"" + ":" + strPhoneNumber + "," +
                    "\"gender\"" + ":" + AppConfig.getInstance().mUserData.getGenderID() + "," +
                    "\"district\"" + ":" + AppConfig.getInstance().mUserData.getDesignationID() + "," +
                    "\"teshil\"" + ":" + AppConfig.getInstance().mUserData.getTehsilID() + "," +
                    "\"designation\"" + ":" + AppConfig.getInstance().mUserData.getDesignationID() + "," +
                    "\"whatsappMobileNo\"" + ":" + strPhoneNumberW + "," +
                    "\"preferedLanguage\"" + ":\"" + lang +
                    "\"}";

            Log.d("LOG_AS", "postSignUp: " + data);

            requestVARegister(data);


        }
    }

    private void requestVARegister(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_SignUp_VA intro_webHit_post_signUp_va = new Intro_WebHit_Post_SignUp_VA();
        intro_webHit_post_signUp_va.postSignUpVA(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    AppConfig.getInstance().mUserData.setName(edtName.getText().toString());
                    AppConfig.getInstance().mUserData.setCNIC(edtCNIC.getText().toString().replaceFirst("^0+(?!$)", ""));

                    AppConfig.getInstance().mUserData.setEmail(edtEmail.getText().toString());
                    AppConfig.getInstance().mUserData.setDesignation(txvDesignation.getText().toString());
                    AppConfig.getInstance().mUserData.setDestrict(txvDistrict.getText().toString());
                    AppConfig.getInstance().mUserData.setTehsil(txvTehsil.getText().toString());
                    AppConfig.getInstance().mUserData.setDOB(txvDob.getText().toString());
                    AppConfig.getInstance().mUserData.setGender(txvGender.getText().toString());


//                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());
//                    Log.d("LOG_AS", "postSignUp: bef " + Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());


//                                        if (Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getUser().getAuthToken()!=null)
//                    AppConfig.getInstance().mUserData.setAuthToken(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getUser().getAuthToken());
//                    if (Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getUser().getRefreshToken()!=null)
//                    AppConfig.getInstance().mUserData.setRefreshToken(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getUser().getRefreshToken());

                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());
                    Log.d("LOG_AS", "postSignUp: bef " + Intro_WebHit_Post_SignUp_VA.responseObject.getResult().getPinCode());

                    navToVerificationFragment();
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

    //region  Check Validations
    private boolean checkNumberErrorCondition() {
        if (edtPhone.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_mobile_number));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkNameErrorCondition() {
        if (edtName.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_name));
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

    private boolean checkDobErrorCondition() {
        if (txvDob.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_dob));
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
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_Designation));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDestrictErrorCondition() {
        if (txvDistrict.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_district));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTehsilErrorCondition() {
        if (txvTehsil.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_tehsil));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    //endregion

}



