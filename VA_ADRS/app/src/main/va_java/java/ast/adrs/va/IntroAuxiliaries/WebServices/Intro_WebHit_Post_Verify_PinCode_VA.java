package ast.adrs.va.IntroAuxiliaries.WebServices;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import ast.adrs.va.AppConfig;
import ast.adrs.va.Utils.ApiMethod;
import ast.adrs.va.Utils.AppConstt;
import ast.adrs.va.Utils.IWebCallback;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Intro_WebHit_Post_Verify_PinCode_VA {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getVAVerified(Context context, final IWebCallback iWebCallback,
                              final String _signUpEntity) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.verifyVA;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        StringEntity entity = new StringEntity(_signUpEntity, "UTF-8");


        Log.d("LOG_AS", "getVAVerified: " + myUrl + " " + _signUpEntity);


        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse = null;


                        try {
                            Log.d("LOG_AS", "getVAVerified onSuccess: " + statusCode);

                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);
                            responseObject = gson.fromJson(strResponse, ResponseModel.class);
                            Log.d("LOG_AS", "getVAVerified  strResponse: " + strResponse);


                            switch (responseObject.getStatusCode()) {
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


                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "getVAVerified onFailure: " + statusCode);

                        String strResponse = null;

                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);
                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            Log.d("LOG_AS", "getVAVerified  onFailure strResponse: " + strResponse);

                            switch (responseObject.getStatusCode()) {
                                case AppConstt.ServerStatus.NETWORK_ERROR:
                                    iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                    break;
                                case AppConstt.ServerStatus.REFRESH_TOKEN:
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                                case AppConstt.ServerStatus.FORBIDDEN:
                                    break;
                                case AppConstt.ServerStatus.UNAUTHORIZED:
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;

                                case AppConstt.ServerStatus.BAD_REQUEST:

                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                                default:
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                            }

                        } catch (Exception e) {

                        }


                    }
                }

        );
    }


    public class ResponseModel {


        private String version;
        private int statusCode;
        private String errorMessage;
        private Result result;
        private String timestamp;
        private List<String> errors;

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public Result getResult() {
            return this.result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public List<String> getErrors() {
            return this.errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public class User {
            private String userName;

            private String authToken;

            private String refreshToken;

            private int id;

            public String getUserName() {
                return this.userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getAuthToken() {
                return this.authToken;
            }

            public void setAuthToken(String authToken) {
                this.authToken = authToken;
            }

            public String getRefreshToken() {
                return this.refreshToken;
            }

            public void setRefreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
            }

            public int getId() {
                return this.id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }

        public class Result {
            private long cnic;

            private String employeeName;

            private long mobileNo;

            private int gender;

            private String fatherName;

            private String husbandName;

            private String placeOfPosting;

            private int district;

            private int teshil;

            private String selfImage;

            private int designation;

            private int bps;

            private long whatsappMobileNo;

            private int pinCode;

            private User user;

            private String employeeRole;

            private int employeeId;

            private String email;

            private boolean isDeleted;

            private boolean isVerified;

            private int verificationCode;

            private int id;

            public long getCnic() {
                return this.cnic;
            }

            public void setCnic(long cnic) {
                this.cnic = cnic;
            }

            public String getEmployeeName() {
                return this.employeeName;
            }

            public void setEmployeeName(String employeeName) {
                this.employeeName = employeeName;
            }

            public long getMobileNo() {
                return this.mobileNo;
            }

            public void setMobileNo(long mobileNo) {
                this.mobileNo = mobileNo;
            }

            public int getGender() {
                return this.gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public String getFatherName() {
                return this.fatherName;
            }

            public void setFatherName(String fatherName) {
                this.fatherName = fatherName;
            }

            public String getHusbandName() {
                return this.husbandName;
            }

            public void setHusbandName(String husbandName) {
                this.husbandName = husbandName;
            }

            public String getPlaceOfPosting() {
                return this.placeOfPosting;
            }

            public void setPlaceOfPosting(String placeOfPosting) {
                this.placeOfPosting = placeOfPosting;
            }

            public int getDistrict() {
                return this.district;
            }

            public void setDistrict(int district) {
                this.district = district;
            }

            public int getTeshil() {
                return this.teshil;
            }

            public void setTeshil(int teshil) {
                this.teshil = teshil;
            }

            public String getSelfImage() {
                return this.selfImage;
            }

            public void setSelfImage(String selfImage) {
                this.selfImage = selfImage;
            }

            public int getDesignation() {
                return this.designation;
            }

            public void setDesignation(int designation) {
                this.designation = designation;
            }

            public int getBps() {
                return this.bps;
            }

            public void setBps(int bps) {
                this.bps = bps;
            }

            public long getWhatsappMobileNo() {
                return this.whatsappMobileNo;
            }

            public void setWhatsappMobileNo(long whatsappMobileNo) {
                this.whatsappMobileNo = whatsappMobileNo;
            }

            public int getPinCode() {
                return this.pinCode;
            }

            public void setPinCode(int pinCode) {
                this.pinCode = pinCode;
            }

            public User getUser() {
                return this.user;
            }

            public void setUser(User user) {
                this.user = user;
            }

            public String getEmployeeRole() {
                return this.employeeRole;
            }

            public void setEmployeeRole(String employeeRole) {
                this.employeeRole = employeeRole;
            }

            public int getEmployeeId() {
                return this.employeeId;
            }

            public void setEmployeeId(int employeeId) {
                this.employeeId = employeeId;
            }

            public String getEmail() {
                return this.email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public boolean getIsDeleted() {
                return this.isDeleted;
            }

            public void setIsDeleted(boolean isDeleted) {
                this.isDeleted = isDeleted;
            }

            public boolean getIsVerified() {
                return this.isVerified;
            }

            public void setIsVerified(boolean isVerified) {
                this.isVerified = isVerified;
            }

            public int getVerificationCode() {
                return this.verificationCode;
            }

            public void setVerificationCode(int verificationCode) {
                this.verificationCode = verificationCode;
            }

            public int getId() {
                return this.id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }


    }

}
