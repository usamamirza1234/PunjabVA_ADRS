package ast.adrs.va.ParentFragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ast.adrs.va.AppConfig;
import ast.adrs.va.HomeAuxiliares.IntimateDiseaseFragment;
import ast.adrs.va.HomeAuxiliares.MessageFragment;
import ast.adrs.va.HomeAuxiliares.MyFarmFragment;
import ast.adrs.va.HomeAuxiliares.SearchFarmerProfileByVA;
import ast.adrs.va.HomeAuxiliares.SuggestionFragment;
import ast.adrs.va.IntroAuxiliaries.DModel_Animals;
import ast.adrs.va.IntroAuxiliaries.MyProfileFragment;
import ast.adrs.va.MainActivity;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.CustomAlertConfirmationInterface;
import ast.adrs.va.Utils.CustomAlertDialog;
import ast.adrs.va.Utils.IBadgeUpdateListener;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public CustomAlertDialog customAlertDialog;
    public ArrayList<DModel_Animals> lstAnimal;
    ImageView imv_logo;
    TextView txv_Name;
    Button btnLogout;
    TextView txv_address;
    LinearLayoutManager linearLayoutManager;
    LinearLayout llSuggestion, llMyfarm, llDiesase, llMyProfile, llMsgBox, llsync;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_home, container, false);
        initData();
        bindViews(frg);


        Log.d("LOG_AS", "ID: " + AppConfig.getInstance().mUserData.getId());
        Log.d("LOG_AS", "ID: " + AppConfig.getInstance().mUserData.getAuthToken());
        txv_Name.setText(AppConfig.getInstance().mUserData.getName());


        if (AppConfig.getInstance().mUserData.isFarmer()) {


            txv_address.setText(
                    "M: " + AppConfig.getInstance().mUserData.getMozah().trim()
                            + " T: " + AppConfig.getInstance().mUserData.getTehsil().trim()
                            + "\nD: " + AppConfig.getInstance().mUserData.getDestrict().trim()
            );
            llMyfarm.setVisibility(View.VISIBLE);
        } else {
            txv_address.setText(AppConfig.getInstance().mUserData.getDesignation().trim() + ", " + AppConfig.getInstance().mUserData.getDestrict().trim()
                    + "\nT: " + AppConfig.getInstance().mUserData.getTehsil().trim());
            llMyfarm.setVisibility(View.GONE);
        }


        return frg;
    }


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
    private void initData() {
        setBottomBar();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        this.lstAnimal = new ArrayList<>();

    }

    void setBottomBar() {
        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.ToolbarState.TOOLBAR_BACK_TITLE_HIDDEN);
            mBadgeUpdateListener.setHeaderTitle("HOME");
        }
    }

    private void bindViews(View frg) {

        imv_logo = frg.findViewById(R.id.act_intro_rl_toolbar_logo);
        llSuggestion = frg.findViewById(R.id.frg_home_llSuggestion);
        llMsgBox = frg.findViewById(R.id.frg_home_llMsg);
        llMyfarm = frg.findViewById(R.id.frg_home_llMyFarm);
        llMyProfile = frg.findViewById(R.id.frg_home_llMyProfile);
        llDiesase = frg.findViewById(R.id.frg_home_llDisease);
        llsync = frg.findViewById(R.id.frg_home_ll_sync);

        btnLogout = frg.findViewById(R.id.frg_home_btn_logout);
        txv_Name = frg.findViewById(R.id.frg_home_txv_next);
        txv_address = frg.findViewById(R.id.frg_home_txv_address);


        btnLogout.setOnClickListener(this);
        llSuggestion.setOnClickListener(this);
        llMsgBox.setOnClickListener(this);
        llMyfarm.setOnClickListener(this);
        llMyProfile.setOnClickListener(this);
        llDiesase.setOnClickListener(this);
        llsync.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_home_llDisease:
                if (AppConfig.getInstance().mUserData.isFarmer()) {
                    navtoDieaseFragment();
                } else {
                    navtoFarmerPerticularsFragment();
                }

                break;

            case R.id.frg_home_llMyProfile:
                navtoMyProfileFragment();
                break;

            case R.id.frg_home_llMyFarm:
                navtoMyFarm();
                break;
            case R.id.frg_home_llSuggestion:
                navtoMySuggestion();
                break;

            case R.id.frg_home_llMsg:
                navtoMsgFragment();
                break;
            case R.id.frg_home_btn_logout:
                showLogoutMessage(getContext(), "Are you sure to logout?");

                break;
            case R.id.frg_home_ll_sync:
                ((MainActivity) getActivity()).requestDesignations();
                break;
        }
    }


    private void navtoFarmerPerticularsFragment() {
        Fragment frg = new SearchFarmerProfileByVA();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_CompleteFarmerProfileByVA);
        ft.addToBackStack(AppConstt.FragTag.FN_CompleteFarmerProfileByVA);
        ft.hide(this);
        ft.commit();
    }

    private void navtoMsgFragment() {
        Fragment frg = new MessageFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_MessageFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MessageFragment);
        ft.hide(this);
        ft.commit();
    }

    private void navtoMySuggestion() {
        Fragment frg = new SuggestionFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_SuggestionFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_SuggestionFragment);
        ft.hide(this);
        ft.commit();
    }

    private void navtoDieaseFragment() {
        IntimateDiseaseFragment frg = new IntimateDiseaseFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_IntimateDiseaseFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_IntimateDiseaseFragment);
        ft.hide(this);
        ft.commit();

    }

    private void navtoMyFarm() {
        Fragment frg = new MyFarmFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_MyFarmFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MyFarmFragment);
        ft.hide(this);
        ft.commit();

    }

    private void navtoMyProfileFragment() {
        Fragment frg = new MyProfileFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isComingFromMain", true);
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_MyProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MyProfileFragment);
        Log.d("LOG_AS", "isComingFromMain " + true);
        frg.setArguments(bundle);
        ft.hide(this);
        ft.commit();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setBottomBar();
        }
    }


    public void showLogoutMessage(Context context, String _errorMsg) {
        customAlertDialog = new CustomAlertDialog(context, "", _errorMsg, "Yes", "No", true, new CustomAlertConfirmationInterface() {
            @Override
            public void callConfirmationDialogPositive() {
//                AppConfig.getInstance().navtoLogin();
                AppConfig.getInstance().mUserData.setLoggedInTemp(false);
                AppConfig.getInstance().saveUserProfileData();
                getActivity().finishAffinity();
                customAlertDialog.dismiss();
            }

            @Override
            public void callConfirmationDialogNegative() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.show();
    }

}
