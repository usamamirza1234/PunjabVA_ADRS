package ast.adrs.va.IntroAuxiliaries.WebServices;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import ast.adrs.va.AppConfig;
import ast.adrs.va.Utils.ApiMethod;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.IWebCallback;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Intro_WebHit_Post_SignUp_VA {
    private AsyncHttpClient mClient = new AsyncHttpClient();
    public static ResponseModel responseObject = null;
    public Context mContext;

    public void postSignUpVA(Context context, final IWebCallback iWebCallback,
                           final String _signUpEntity) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.verifyEmploye;
        StringEntity entity = null;
        entity = new StringEntity(_signUpEntity, "UTF-8");

        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);



        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "postSignUpVA  onSuccess: " + statusCode);
                        String strResponse = null;


                        try {

                            Gson gson = new Gson();
                            strResponse = new String(responseBody, "UTF-8");

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            switch (responseObject.getStatusCode())
                            {

                                case AppConstt.ServerStatus.OK:
                                    iWebCallback.onWebResult(true, "");
                                    break;

                                default:
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "postSignUpVA  onFailure: " + statusCode);
                        switch (statusCode) {
                            case AppConstt.ServerStatus.NETWORK_ERROR:
                                iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                break;

                            case AppConstt.ServerStatus.FORBIDDEN:
                            case AppConstt.ServerStatus.UNAUTHORIZED:
                            default:
                                AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                break;
                        }
                    }
                }

        );
    }


    public class  ResponseModel{

        public class User
        {
            private String userName;

            private String authToken;

            private String refreshToken;

            private String id;

            public void setUserName(String userName){
                this.userName = userName;
            }
            public String getUserName(){
                return this.userName;
            }
            public void setAuthToken(String authToken){
                this.authToken = authToken;
            }
            public String getAuthToken(){
                return this.authToken;
            }
            public void setRefreshToken(String refreshToken){
                this.refreshToken = refreshToken;
            }
            public String getRefreshToken(){
                return this.refreshToken;
            }
            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
        }


        public class Result
        {
            private String cnic;

            private String employeeName;

            private String mobileNo;

            private int gender;

            private String fatherName;

            private String husbandName;

            private String placeOfPosting;

            private int district;

            private int teshil;

            private String selfImage;

            private int designation;

            private int bps;

            private String whatsappMobileNo;

            private int pinCode;

            private User user;

            private String employeeRole;

            private int employeeId;

            private String email;

            private int id;

            public void setCnic(String cnic){
                this.cnic = cnic;
            }
            public String getCnic(){
                return this.cnic;
            }
            public void setEmployeeName(String employeeName){
                this.employeeName = employeeName;
            }
            public String getEmployeeName(){
                return this.employeeName;
            }
            public void setMobileNo(String mobileNo){
                this.mobileNo = mobileNo;
            }
            public String getMobileNo(){
                return this.mobileNo;
            }
            public void setGender(int gender){
                this.gender = gender;
            }
            public int getGender(){
                return this.gender;
            }
            public void setFatherName(String fatherName){
                this.fatherName = fatherName;
            }
            public String getFatherName(){
                return this.fatherName;
            }
            public void setHusbandName(String husbandName){
                this.husbandName = husbandName;
            }
            public String getHusbandName(){
                return this.husbandName;
            }
            public void setPlaceOfPosting(String placeOfPosting){
                this.placeOfPosting = placeOfPosting;
            }
            public String getPlaceOfPosting(){
                return this.placeOfPosting;
            }
            public void setDistrict(int district){
                this.district = district;
            }
            public int getDistrict(){
                return this.district;
            }
            public void setTeshil(int teshil){
                this.teshil = teshil;
            }
            public int getTeshil(){
                return this.teshil;
            }
            public void setSelfImage(String selfImage){
                this.selfImage = selfImage;
            }
            public String getSelfImage(){
                return this.selfImage;
            }
            public void setDesignation(int designation){
                this.designation = designation;
            }
            public int getDesignation(){
                return this.designation;
            }
            public void setBps(int bps){
                this.bps = bps;
            }
            public int getBps(){
                return this.bps;
            }
            public void setWhatsappMobileNo(String whatsappMobileNo){
                this.whatsappMobileNo = whatsappMobileNo;
            }
            public String getWhatsappMobileNo(){
                return this.whatsappMobileNo;
            }
            public void setPinCode(int pinCode){
                this.pinCode = pinCode;
            }
            public int getPinCode(){
                return this.pinCode;
            }
            public void setUser(User user){
                this.user = user;
            }
            public User getUser(){
                return this.user;
            }
            public void setEmployeeRole(String employeeRole){
                this.employeeRole = employeeRole;
            }
            public String getEmployeeRole(){
                return this.employeeRole;
            }
            public void setEmployeeId(int employeeId){
                this.employeeId = employeeId;
            }
            public int getEmployeeId(){
                return this.employeeId;
            }
            public void setEmail(String email){
                this.email = email;
            }
            public String getEmail(){
                return this.email;
            }
            public void setId(int id){
                this.id = id;
            }
            public int getId(){
                return this.id;
            }
        }



            private String version;

            private int statusCode;

            private String errorMessage;

            private Result result;

            private String timestamp;

            private List<String> errors;

            public void setVersion(String version){
                this.version = version;
            }
            public String getVersion(){
                return this.version;
            }
            public void setStatusCode(int statusCode){
                this.statusCode = statusCode;
            }
            public int getStatusCode(){
                return this.statusCode;
            }
            public void setErrorMessage(String errorMessage){
                this.errorMessage = errorMessage;
            }
            public String getErrorMessage(){
                return this.errorMessage;
            }
            public void setResult(Result result){
                this.result = result;
            }
            public Result getResult(){
                return this.result;
            }
            public void setTimestamp(String timestamp){
                this.timestamp = timestamp;
            }
            public String getTimestamp(){
                return this.timestamp;
            }
            public void setErrors(List<String> errors){
                this.errors = errors;
            }
            public List<String> getErrors(){
                return this.errors;
            }


    }
}
