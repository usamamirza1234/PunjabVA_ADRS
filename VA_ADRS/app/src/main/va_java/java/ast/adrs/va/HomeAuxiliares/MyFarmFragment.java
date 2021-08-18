package ast.adrs.va.HomeAuxiliares;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ast.adrs.va.AppConfig;
import ast.adrs.va.HomeAuxiliares.adapters.ItemClickListener;
import ast.adrs.va.HomeAuxiliares.adapters.Section;
import ast.adrs.va.HomeAuxiliares.adapters.SectionedExpandableLayoutHelper;
import ast.adrs.va.IntroAuxiliaries.AnimalPopulationRcvAdapter;
import ast.adrs.va.IntroAuxiliaries.DModel_Animals;
import ast.adrs.va.IntroAuxiliaries.DModel_District;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmID;
import ast.adrs.va.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmerID;
import ast.adrs.va.R;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.IBadgeUpdateListener;
import ast.adrs.va.Utils.IWebCallback;


public class MyFarmFragment extends Fragment implements ItemClickListener {
    RecyclerView rcv_populatin;
    AnimalPopulationRcvAdapter animalPopulationRcvAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    ArrayList<DModel_District> lstSpices = new ArrayList<>();
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    ArrayList<Integer> lstfarmId = new ArrayList<>();
    private ArrayList<DModel_Animals> lstPopulation;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    boolean isExpended= true;

    public MyFarmFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_my_farm, container, false);


        initData();
        bindViews(frg);

        requestFarmWisePop();
        return frg;
    }


//    private void populateFarmPop() {
//        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity(),
//                rcv_populatin, this, 1);
//        showProgDialog();
//        for (int i = 0; i < AppConfig.getInstance().getFarmPopList().size(); i++) {
//            lstfarmId.add(Integer.parseInt(AppConfig.getInstance().getFarmPopList().get(i).getId()));
//        }
//
//
//        List<Integer> numbers = lstfarmId;
//        Log.d("listing", "lstfarmId numbers" + numbers);
//
//
//        Set<Integer> hashSet = new LinkedHashSet(numbers);
//        ArrayList<Integer> removedDuplicates = new ArrayList(hashSet);
//
//
//        for (int i = 0; i < removedDuplicates.size(); i++) {
//            requestFarmsbyFarmID(String.valueOf(removedDuplicates.get(i)));
//
//        }
//
//
//    }

    void requestFarmWisePop() {
        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity(),
                rcv_populatin, this, 1);


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

//                        for (int i = 0; i < lstPopulation.size(); i++)
//                        {
//                            Log.d("listing", "lstfarmId size" + lstPopulation.size());
//
//                        }


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

//        showProgDialog();
        Intro_WebHit_POST_FarmFarmPop_GetByFarmID intro_webHit_post_farmFarmPop_getByFarmID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmID();
        intro_webHit_post_farmFarmPop_getByFarmID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size() > 0) {
                        int  noOfanimals = 0;
                        ArrayList<DModel_Animals> arrayList = new ArrayList<>();
                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); i++)
                        {
                            arrayList.add(new DModel_Animals(Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getSpecieID() + "",
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getNoOfAnimals() + ""));

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

//                        Log.d("Expandable","Pos: " + i);

                        if (isExpended){
                            sectionedExpandableLayoutHelper.addSection(name, noOfanimals,true, arrayList);
                            isExpended=false;
                        }
                        else
                            sectionedExpandableLayoutHelper.addSection(name, noOfanimals,false, arrayList);
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    }

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

    @Override
    public void itemClicked(DModel_Animals item) {
        Toast.makeText(getActivity(), "Item: " + item.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemClicked(Section section) {
        Toast.makeText(getActivity(), "Section: " + section.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    private void initData() {
        setBottomBar();
        lstPopulation = new ArrayList<>();

    }

    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);
            mBadgeUpdateListener.setHeaderTitle("My Farm");
        }

    }

    private void bindViews(View frg) {

        rcv_populatin = frg.findViewById(R.id.frg_complete_profile_rcv_populatin);

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


    //
//    void requestFarmWisePop() {
//        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity(),
//                rcv_populatin, this, 1);
//        showProgDialog();
//        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID intro_webHit_post_farmFarmPop_getByFarmerID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmerID();
//        intro_webHit_post_farmFarmPop_getByFarmerID.getFarmPop(getContext(), new IWebCallback() {
//            @Override
//            public void onWebResult(boolean isSuccess, String strMsg) {
//                if (isSuccess) {
//                    dismissProgDialog();
//
//                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject != null &&
//                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult() != null &&
//                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size() > 0) {
//
//                        ArrayList<String> lstFarmId = new ArrayList<>();
//                        ArrayList<DModel_Animals> lstAniamal;
//                        lstAniamal = new ArrayList<>();
//                        lstAniamal.clear();
//
//                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size(); i++) {
//                            lstFarmId.add(Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID() + "");
//                        }
//
//
//                        for (int i = 0; i < lstFarmId.size(); i++) {
//                            for (int j = 0; j < Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size(); j++) {
//
//                                int x = Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(j).getFarmID();
//                                int y = Integer.parseInt(lstFarmId.get(i));
//
////                                String id="";
////                                String tempid="";
//                                if ((x) == (y)) {
////                                    id= y+"";
//                                    //do something for not equals
//                                    Log.d("listing", "getFarmList equals " + y);
//
//                                    DModel_Animals dModel_animals = new DModel_Animals(
//                                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "",
//                                            "farmId  " + "");
//                                    lstAniamal.add(dModel_animals);
//
//                                } else {
//                                    Log.d("listing", "not equals" + y + "  " + x);
//                                }
//                            }
//                        }
//
//
////                        int farmId = 0;
////
//////                            do {
//////
//////                            }
//////                            while (true);
////
////
////                        if (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID() == farmId) {
////                            Log.d("LOG_AS", "farmId  " + farmId);
////                            DModel_Animals dModel_animals = new DModel_Animals(
////                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "",
////                                    "farmId  " + ""
////                            );
////                            lstAniamal.add(dModel_animals);
////                        }
////                            sectionedExpandableLayoutHelper.addSection("farmId  " + " ", lstAniamal);
////                            sectionedExpandableLayoutHelper.notifyDataSetChanged();
////
//////
//////                        lstPopulation.clear();
//////                        ArrayList<DModel_Animals> lstAniamal;
//////
//////                        lstPopulation.clear();
//////
//////                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size(); i++) {
//////                            lstAniamal = new ArrayList<>();
//////                            lstAniamal.clear();
//////
//////
//////                            int farmId = 0;
//////
//////                            String s_name = "";
//////
//////                            switch (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getSpecieID()) {
//////                                case 1:
//////                                    s_name = "Cattle";
//////                                    break;
//////
//////                                case 2:
//////                                    s_name = "Buffalo";
//////                                    break;
//////                                case 3:
//////                                    s_name = "Sheep";
//////                                    break;
//////                                case 4:
//////                                    s_name = "Goat";
//////                                    break;
//////                                case 5:
//////                                    s_name = "Camel";
//////                                    break;
//////                                case 6:
//////                                    s_name = "Horse";
//////                                    break;
//////                                case 7:
//////                                    s_name = "Donkey";
//////                                    break;
//////                                case 8:
//////                                    s_name = "Mule";
//////                                    break;
//////                                case 9:
//////                                    s_name = "Dog";
//////                                    break;
//////                                case 10:
//////                                    s_name = "Poultry";
//////                                    break;
//////                            }
//////
//////
//////                            if (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID() == farmId) {
//////                                Log.d("LOG_AS", "getName  " + AppConfig.getInstance().getSpicesList().get(i).getName());
//////                                DModel_Animals dModel_animals = new DModel_Animals(
//////                                        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "",
//////                                        s_name + ""
//////                                );
//////                                lstAniamal.add(dModel_animals);
//////
//////                                sectionedExpandableLayoutHelper.addSection(s_name + " ", lstAniamal);
//////                                sectionedExpandableLayoutHelper.notifyDataSetChanged();
//////
//////                            } else {
//////                                farmId = Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID();
//////                            }
//////
//////
////////                            if ( AppConfig.getInstance().getSpicesList().get(i).getId().equalsIgnoreCase(String.valueOf(Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getSpecieID())))
////////                            {
////////                                Log.d("LOG_AS", "getName  " + AppConfig.getInstance().getSpicesList().get(i).getName());
////////                                DModel_Animals dModel_animals = new DModel_Animals(
////////                                        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "",
////////                                        AppConfig.getInstance().getSpicesList().get(i).getName() +""
////////                                );
////////                                lstAniamal.add(dModel_animals);
////////
////////                                sectionedExpandableLayoutHelper.addSection(Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals()+" ", lstAniamal);
////////                                sectionedExpandableLayoutHelper.notifyDataSetChanged();
////////                            }
//////
//////
////////                            if (AppConfig.getInstance().getSpicesList().equals(Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getSpecieID())) {
////////
////////                                DModel_Animals dModel_animals = new DModel_Animals(
////////                                        Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getFarmID() +"",
////////                                        AppConfig.getInstance().getSpicesList().get(i).getName(),
////////                                        Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getNoOfAnimals() + "");
////////                                lstAniamal.add(dModel_animals);
////////
////////                                sectionedExpandableLayoutHelper.addSection(" fasd", lstAniamal);
////////                                sectionedExpandableLayoutHelper.notifyDataSetChanged();
////////                            }
//////
//////
//////                        }
//
//
//                    }
//                } else {
//                    dismissProgDialog();
//
//                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
//                }
//            }
//
//            @Override
//            public void onWebException(Exception e) {
//                dismissProgDialog();
//                Log.d("LOG_AS", "getFarmFarm_GetByFarmerID Exception: " + e.getMessage());
//
//                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
//            }
//        }, AppConfig.getInstance().mUserData.getId());
//
//    }
//
//    private void populateFarm() {
//
//
//        showProgDialog();
//        Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId intro_webHit_post_farmPopulation_getByFarmID_farmerId = new Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId();
//        intro_webHit_post_farmPopulation_getByFarmID_farmerId.getFarmPopulation_GetByFarmID(getContext(), new IWebCallback() {
//            @Override
//            public void onWebResult(boolean isSuccess, String strMsg) {
//                if (isSuccess) {
//                    dismissProgDialog();
//                    if (Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject != null &&
//                            Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult() != null &&
//                            Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().size() > 0) {
//
//
//                    }
//                } else {
//                    dismissProgDialog();
//
//                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
//                }
//            }
//
//            @Override
//            public void onWebException(Exception e) {
//                dismissProgDialog();
//                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());
//
//                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
//            }
//        }, AppConfig.getInstance().mUserData.getId());
//
//
//        //Animals
////        showProgDialog();
////        Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId intro_webHit_post_farmPopulation_getByFarmID = new Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId();
////        intro_webHit_post_farmPopulation_getByFarmID.getFarmPopulation_GetByFarmID(getContext(), new IWebCallback() {
////            @Override
////            public void onWebResult(boolean isSuccess, String strMsg) {
////                if (isSuccess) {
////                    dismissProgDialog();
////
////                    if (getActivity() != null) {
////                        if (isSuccess) {
////                            if (Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject != null &&
////                                    Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult() != null &&
////                                    Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().size() > 0) {
////
////
////                                lstPopulation.clear();
////
////                                Log.d("LOG_AS", "Size " + Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().size());
////
////                                for (int i = 0; i < Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().size(); i++) {
////
////
//////                                    if (String.valueOf(Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getSpecieID())
//////                                            .equalsIgnoreCase(lstSpices.get(i).getId())) {
//////
//////                                        DModel_Animals dModel_animals = new DModel_Animals(lstSpices.get(i).getName(),
//////                                                String.valueOf(Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getNoOfAnimals()));
//////
//////
//////                                        lstPopulation.add(dModel_animals);
//////                                    }
////                                    DModel_Animals dModel_animals = new DModel_Animals(lstSpices.get(i).getName(),
////                                            String.valueOf(Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getNoOfAnimals()));
////
////
////                                    lstPopulation.add(dModel_animals);
////
////                                }
////
////
////                                if (animalPopulationRcvAdapter == null) {
////
////                                    animalPopulationRcvAdapter = new AnimalPopulationRcvAdapter(getActivity(), lstPopulation, (eventId, position) -> {
////
////
////                                    });
////
////
////                                    rcv_populatin.setLayoutManager(linearLayoutManager);
////                                    rcv_populatin.setAdapter(animalPopulationRcvAdapter);
////
////                                } else {
////                                    animalPopulationRcvAdapter.notifyDataSetChanged();
////                                }
////
////                            }
////                        } else {
////
////
////                        }
////
////                    }
////                } else {
////                    dismissProgDialog();
////
////                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
////                }
////            }
////
////            @Override
////            public void onWebException(Exception e) {
////                dismissProgDialog();
////                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());
////
////                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
////            }
////        });
//
//
//    }
//
//    private void populateTableForFarms() {
//
//
////        for (int i=0;i<lstPopulation.size();i++)
////        {
////            sectionedExpandableLayoutHelper.addSection(lstPopulation.get(i).getId(), lstPopulation);
////        }
//
//
//        //        //random data
//
//
////        sectionedExpandableLayoutHelper.notifyDataSetChanged();
//    }

}

