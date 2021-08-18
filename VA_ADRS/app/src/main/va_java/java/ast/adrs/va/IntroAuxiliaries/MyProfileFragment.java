package ast.adrs.va.IntroAuxiliaries;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ast.adrs.va.AppConfig;
import ast.adrs.va.HomeAuxiliares.PerformanceRcvAdapter;
import ast.adrs.va.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_PerformanceEmployee;
import ast.adrs.va.IntroActivity;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmID;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmerID;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.CircleImageView;
import ast.adrs.va.Utils.IBadgeUpdateListener;
import ast.adrs.va.Utils.IWebCallback;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    public static final int PICK_IMAGE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int SELECT_PICTURE = 1;
    PerformanceRcvAdapter performanceRcvAdapter;
    TextView  txvTotalSum, txv_address;
    TextView txvEmpName, txvEmpCNIC, txvEmpDesignation;

    Bundle bundle;
    ArrayList<Integer> lstfarmId;
    boolean isComingFromMain;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    CircleImageView  civEmpImv;
    RecyclerView rcv_populatin, rcv_emp;
    AnimalPopulationRcvAdapter animalPopulationRcvAdapter;

    ArrayList<DModel_Animals> lstNewData;
    private ArrayList<DModel_Animals> lstPopulation;
    private ArrayList<DModel_Performance> lstPerformance;
    private ArrayList<String> lstdiseaseIntimationDiseases;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initData();
        bindViews(frg);
        requestEmpPerformance();


        String upperString = AppConfig.getInstance().mUserData.getName().substring(0, 1).toUpperCase() + AppConfig.getInstance().mUserData.getName().substring(1).toLowerCase();

        txvEmpName.setText(upperString);


        String input = String.valueOf(AppConfig.getInstance().mUserData.getCNIC());     //input string
        String sixTewelveChars = "";     //substring containing first 4 characters
        String firstFiveChars = "";     //substring containing first 4 characters
        String lastChars = "";     //substring containing first 4 characters

        if (input.length() >= 10) {
            firstFiveChars = input.substring(0, 5);
            sixTewelveChars = input.substring(5, 12);
            lastChars = input.substring(12, 13);


            txvEmpCNIC.setText(firstFiveChars + "-" + sixTewelveChars + "-" + lastChars);
        }
        txvEmpDesignation.setText(AppConfig.getInstance().mUserData.getDesignation());
        txv_address.setText( AppConfig.getInstance().mUserData.getDestrict().trim()
                + "\nT: " + AppConfig.getInstance().mUserData.getTehsil().trim());




        if (!AppConfig.getInstance().mUserData.getEncorededImage().isEmpty()) {
            byte[] decodedString = Base64.decode(AppConfig.getInstance().mUserData.getEncorededImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            civEmpImv.setImageBitmap(decodedByte);
        }

//        selectImage();
        return frg;
    }


    //region Employee

    //region EmployeePerformance
    void requestEmpPerformance() {
        showProgDialog();
        Home_WebHit_Post_Get_PerformanceEmployee home_webHit_post_get_performanceEmployee = new Home_WebHit_Post_Get_PerformanceEmployee();
        home_webHit_post_get_performanceEmployee.getEmployeePerformance(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    if (Home_WebHit_Post_Get_PerformanceEmployee.responseObject != null &&
                            Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult() != null) {


                        lstPerformance.clear();
                        lstdiseaseIntimationDiseases.clear();
                        for (int i = 0; i < Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationDiseases().size(); i++) {
                            lstdiseaseIntimationDiseases.add(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationDiseases().get(i));
                        }

                        find(lstdiseaseIntimationDiseases);


//             DModel_Performance dModel_performance = new DModel_Performance();
//
//
//                            DModel_Animals dModel_animals = new DModel_Animals(
//                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getId() + "",
//                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "");
//
//
//                            lstPopulation.add(dModel_animals);
//                        if (animalPopulationRcvAdapter == null) {
//
//                            animalPopulationRcvAdapter = new AnimalPopulationRcvAdapter(getActivity(), lstPopulation, (eventId, position) -> {
//
//
//                            });
//
//
//                            rcv_populatin.setLayoutManager(linearLayoutManager);
//                            rcv_populatin.setAdapter(animalPopulationRcvAdapter);
//
//                        } else {
//                            animalPopulationRcvAdapter.notifyDataSetChanged();
//                        }

                    }
                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "getFarmFarm_GetByFarmerID Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        });

    }

    void find(ArrayList<String> names) {


        int FMD = Collections.frequency(names, "Foot and Mouth Disease (FMD)");
        int BQ = Collections.frequency(names, "Black Quarter (BQ)");
        int Rabies = Collections.frequency(names, "Rabies");
        int ND = Collections.frequency(names, "Newcastle Disease (ND)");
        int Surra = Collections.frequency(names, "Surra (Trypanosoma evansi)");
        int CPP = Collections.frequency(names, "Caprine pleuropneumonia (CPP)");
        int Brucellosis = Collections.frequency(names, "Brucellosis");
        int Anthrax = Collections.frequency(names, "Anthrax");
        int Theileriosis = Collections.frequency(names, "Theileriosis");
        int bTB = Collections.frequency(names, "Bovine Tuberculosis (bTB)");
        int Jhone = Collections.frequency(names, "Paratuberculosis (Jhone's disease)");
        int HS = Collections.frequency(names, "Haemorrhagic Septicaemia (HS)");
        int PPR = Collections.frequency(names, "Peste des Petits Ruminants (PPR)");
        int ET = Collections.frequency(names, "Enterotoxaemia (ET)");
        int Glander = Collections.frequency(names, "Glander");
        int Bloat = Collections.frequency(names, "Bloat");
        int Enterotoxemia = Collections.frequency(names, "Enterotoxemia");
        int POX = Collections.frequency(names, "POX");


        if (FMD > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(FMD + "");
            dModel_performance.setName("Foot and Mouth Disease (FMD)");
            lstPerformance.add(dModel_performance);
        }
        if (BQ > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(BQ + "");
            dModel_performance.setName("Black Quarter BQ");
            lstPerformance.add(dModel_performance);
        }
        if (ND > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(ND + "");
            dModel_performance.setName("Newcastle Disease (ND)");
            lstPerformance.add(dModel_performance);
        }

        if (Rabies > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Rabies + "");
            dModel_performance.setName("Rabies");
            lstPerformance.add(dModel_performance);
        }
        if (Surra > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Surra + "");
            dModel_performance.setName("Surra (Trypanosoma evansi)");
            lstPerformance.add(dModel_performance);
        }
        if (CPP > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(CPP + "");
            dModel_performance.setName("Caprine pleuropneumonia (CPP)");
            lstPerformance.add(dModel_performance);
        }
        if (Brucellosis > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Brucellosis + "");
            dModel_performance.setName("Brucellosis");
            lstPerformance.add(dModel_performance);
        }
        if (Anthrax > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Anthrax + "");
            dModel_performance.setName("Anthrax");
            lstPerformance.add(dModel_performance);
        }
        if (Theileriosis > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Theileriosis + "");
            dModel_performance.setName("Theileriosis");
            lstPerformance.add(dModel_performance);
        }
        if (bTB > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(bTB + "");
            dModel_performance.setName("Bovine Tuberculosis (bTB)");
            lstPerformance.add(dModel_performance);
        }
        if (Jhone > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Jhone + "");
            dModel_performance.setName("Paratuberculosis (Jhone's disease)");
            lstPerformance.add(dModel_performance);
        }
        if (HS > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(HS + "");
            dModel_performance.setName("Haemorrhagic Septicaemia (HS)");
            lstPerformance.add(dModel_performance);
        }
        if (PPR > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(PPR + "");
            dModel_performance.setName("Peste des Petits Ruminants (PPR)");
            lstPerformance.add(dModel_performance);
        }
        if (ET > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(ET + "");
            dModel_performance.setName("Enterotoxaemia (ET)");
            lstPerformance.add(dModel_performance);
        }
        if (Glander > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Glander + "");
            dModel_performance.setName("Glander");
            lstPerformance.add(dModel_performance);
        }
        if (Bloat > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Bloat + "");
            dModel_performance.setName("Bloat");
            lstPerformance.add(dModel_performance);
        }
        if (Enterotoxemia > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(Enterotoxemia + "");
            dModel_performance.setName("Enterotoxemia");
            lstPerformance.add(dModel_performance);
        }
        if (POX > 0) {
            DModel_Performance dModel_performance = new DModel_Performance();
            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
            dModel_performance.setCount(POX + "");
            dModel_performance.setName("POX");
            lstPerformance.add(dModel_performance);
        }

//
//        if (lstPerformance.size() > 0) {
//            DModel_Performance dModel_performance = new DModel_Performance();
//            dModel_performance.setTotal(Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount() + "");
//            dModel_performance.setCount("");
//            dModel_performance.setName("");
//            lstPerformance.add(dModel_performance);
//        }

        for (int i = 0; i < lstPerformance.size(); i++) {
            Log.d("findData", "lstPerformance: " + lstPerformance.get(i).getCount() + " " + lstPerformance.get(i).getName());
        }


        if (performanceRcvAdapter == null) {

            performanceRcvAdapter = new PerformanceRcvAdapter(getActivity(), lstPerformance);


            rcv_emp.setLayoutManager(linearLayoutManager);
            rcv_emp.setAdapter(performanceRcvAdapter);

            txvTotalSum.setText("Total: " + Home_WebHit_Post_Get_PerformanceEmployee.responseObject.getResult().getDiseaseIntimationCount());
        } else {
            performanceRcvAdapter.notifyDataSetChanged();
        }

        Log.d("findData", "FMD: " + FMD);
        Log.d("findData", "BQ: " + BQ);
        Log.d("findData", "Rabies: " + Rabies);
        Log.d("findData", "ND: " + ND);
        Log.d("findData", "CPP: " + CPP);
        Log.d("findData", "Surra: " + Surra);
        Log.d("findData", "Brucellosis: " + Brucellosis);
        Log.d("findData", "Anthrax: " + Anthrax);
        Log.d("findData", "Theileriosis: " + Theileriosis);
        Log.d("findData", "bTB: " + bTB);
        Log.d("findData", "Jhone: " + Jhone);
        Log.d("findData", "HS: " + HS);
        Log.d("findData", "PPR: " + PPR);
        Log.d("findData", "ET: " + ET);
        Log.d("findData", "Glander: " + Glander);

        Log.d("findData", "Bloat: " + Bloat);
        Log.d("findData", "Enterotoxemia: " + Enterotoxemia);
        Log.d("findData", "POX: " + POX);

//        Log.d("findData","Finding duplicate elements in array using brute force method");
//
//        // First solution : finding duplicates using brute force method
//        System.out.println("Finding duplicate elements in array using brute force method");
//        for (int i = 0; i < names.size(); i++) {
//            for (int j = i + 1; j < names.size(); j++)
//            {
//                if (names.get(i).equals(names.get(j)))
//                {
//                    // got the duplicate element
//                    Log.d("findData", j + " My Findings : " + names.get(j) );
//                }
//
//            }
//        }
//
//        // Second solution : use HashSet data structure to find duplicates
//        System.out.println("Duplicate elements from array using HashSet data structure");
//        Log.d("findData","Duplicate elements from array using HashSet data structure");
//        Set<String> store = new HashSet<>();
//
//        for (String name : names) {
//            if (store.add(name) == false) {
//                System.out.println("found a duplicate element in array : "
//                        + name);
//                Log.d("findData","found a duplicate element in array : "
//                        + name);
//            }
//        }
//
//        // Third solution : using Hash table data structure to find duplicates
//        System.out.println("Duplicate elements from array using hash table");
//        Log.d("findData","Duplicate elements from array using hash table");
//        Map<String, Integer> nameAndCount = new HashMap<>();
//
//        // build hash table with count
//        for (String name : names) {
//            Integer count = nameAndCount.get(name);
//            if (count == null) {
//                nameAndCount.put(name, 1);
//            } else {
//                nameAndCount.put(name, ++count);
//            }
//        }
//
//        // Print duplicate elements from array in Java
//        Set<Map.Entry<String, Integer>> entrySet = nameAndCount.entrySet();
//        for (Map.Entry<String, Integer> entry : entrySet) {
//
//            if (entry.getValue() > 1) {
//                System.out.println("Duplicate element from array : "
//                        + entry.getKey());
//                Log.d("findData","Duplicate element from array : "
//                        + entry.getKey());
//            }
//        }
    }
    //endregion

    //endregion

    //region Farmer


    //region FarmerFarmList
    void requestFarmWisePop() {
        showProgDialog();
        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID intro_webHit_post_farmFarmPop_getByFarmerID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmerID();
        intro_webHit_post_farmFarmPop_getByFarmerID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {


                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size() > 0) {


                        lstPopulation.clear();
                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size(); i++) {


                            DModel_Animals dModel_animals = new DModel_Animals(
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID() + "",
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "");


                            lstPopulation.add(dModel_animals);
                        }


                        for (int i = 0; i < lstPopulation.size(); i++) {
                            lstfarmId.add(Integer.parseInt(lstPopulation.get(i).getId()));
                        }


                        List<Integer> numbers = lstfarmId;
                        Log.d("listing", "lstfarmId numbers" + numbers);


                        Set<Integer> hashSet = new LinkedHashSet(numbers);
                        ArrayList<Integer> removedDuplicates = new ArrayList(hashSet);


                        for (int i = 0; i < removedDuplicates.size(); i++) {
                            requestFarmsbyFarmID(String.valueOf(removedDuplicates.get(i)));
                        }
                    }
                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getActivity(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "getFarmFarm_GetByFarmerID Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getActivity(), e.toString());
            }
        }, AppConfig.getInstance().mUserData.getId());

    }

    private void requestFarmsbyFarmID(String s) {

        Intro_WebHit_POST_FarmFarmPop_GetByFarmID intro_webHit_post_farmFarmPop_getByFarmID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmID();
        intro_webHit_post_farmFarmPop_getByFarmID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size() > 0) {
                        int noOfanimals = 0;

                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); i++) {
                            noOfanimals = noOfanimals + Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getNoOfAnimals();
                        }


                        String name = "";

                        for (int i = 0; i < AppConfig.getInstance().getFarmList().size(); i++) {
                            for (int j = 0; j < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); j++) {

                                int x = (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(j).getFarmID());
                                int y = Integer.parseInt(AppConfig.getInstance().getFarmList().get(i).getId());

                                if (x == y) {
                                    //do something for not equals

                                    Log.d("listing", "getFarmList equals" + AppConfig.getInstance().getFarmList().get(i).getName());
                                    name = AppConfig.getInstance().getFarmList().get(i).getName();

                                }
                            }
                        }

                        lstNewData.add(new DModel_Animals(name, noOfanimals + ""));
                    }

                    populateData();
                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getActivity(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getActivity(), e.toString());
            }
        }, Integer.parseInt(s));


    }

    private void populateData() {
        if (animalPopulationRcvAdapter == null) {
            animalPopulationRcvAdapter = new AnimalPopulationRcvAdapter(getActivity(), lstNewData, (eventId, position) -> {
            });
            rcv_populatin.setLayoutManager(linearLayoutManager);
            rcv_populatin.setAdapter(animalPopulationRcvAdapter);
        } else {
            animalPopulationRcvAdapter.notifyDataSetChanged();
        }
    }
    //endregion


    //endregion

    //region init
    private void bindViews(View frg) {

        rcv_populatin = frg.findViewById(R.id.frg_complete_profile_rcv_populatin);
        civEmpImv = frg.findViewById(R.id.frg_complete_profile_emp_imv_profile);

        rcv_emp = frg.findViewById(R.id.frg_complete_profile_rcv_emp);


        txvTotalSum = frg.findViewById(R.id.lay_item_performance_txv_total);

        txv_address = frg.findViewById(R.id.frg_home_txv_address);



        txvEmpName = frg.findViewById(R.id.frg_my_profile_emp_txv_name);
        txvEmpCNIC = frg.findViewById(R.id.frg_my_profile_emp_txv_cnic);
        txvEmpDesignation = frg.findViewById(R.id.frg_my_profile_emp_txv_designation);


        civEmpImv.setOnClickListener(this);


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            Bitmap selectedImageBitmap = null;
            try {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            civEmpImv.setImageBitmap(selectedImageBitmap);
            civEmpImv.setVisibility(View.VISIBLE);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
            AppConfig.getInstance().mUserData.setEncorededImage(Base64.encodeToString(byteArrayImage, Base64.NO_WRAP));
            AppConfig.getInstance().saveUserProfileData();

        }

    }

    void setBottomBar() {
        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);
            mBadgeUpdateListener.setHeaderTitle(getString(R.string.my_profile));
        }

    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            setBottomBar();
        }
    }

    private void initData() {
        lstfarmId = new ArrayList<>();
        bundle = this.getArguments();
        if (bundle != null) {

            isComingFromMain = bundle.getBoolean("isComingFromMain", false);

            Log.d("LOG_AS", "isComingFromMain " + isComingFromMain);

        }
        setBottomBar();
        lstPopulation = new ArrayList<>();
        lstPerformance = new ArrayList<>();
        lstdiseaseIntimationDiseases = new ArrayList<>();
        lstNewData = new ArrayList();
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
    //endregion


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.frg_complete_profile_imv_profile:
            case R.id.frg_complete_profile_emp_imv_profile: {

                takePhoto();
            }
            break;
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


}

