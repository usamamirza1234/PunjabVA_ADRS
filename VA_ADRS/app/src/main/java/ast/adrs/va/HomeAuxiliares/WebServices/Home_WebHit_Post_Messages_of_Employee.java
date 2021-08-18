package ast.adrs.va.HomeAuxiliares.WebServices;

import android.content.Context;
import android.util.Log;

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

public class Home_WebHit_Post_Messages_of_Employee {
    public static ResponseModel responseObject = null;
    public Context mContext;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void getMsgofEmp(Context context, final IWebCallback iWebCallback) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getMsgofEmp+"employeeId="+AppConfig.getInstance().mUserData.getId();

        if (AppConfig.getInstance().mUserData.isFarmer())
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getMsgofEmp+"farmerId="+AppConfig.getInstance().mUserData.getId();
        else
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getMsgofFarm+"employeeId="+AppConfig.getInstance().mUserData.getId();

        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

//        RequestParams params = new RequestParams();
//
//
//        params.put("employeeId",AppConfig.getInstance().mUserData.getId());

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());


        Log.d("LOG_AS", " getMsgofEmp  myUrl: " + myUrl);
        mClient.post( myUrl , new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getMsgofEmp  onSuccess: " + statusCode);
                        String strResponse = null;


                        try {

                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            switch (statusCode) {

                                case AppConstt.ServerStatus.OK:
                                    iWebCallback.onWebResult(true, responseObject.errorMessage);
                                    break;

                                default:
                                    iWebCallback.onWebResult(false, responseObject.errorMessage);
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "getMsgofEmp  onFailure: " + statusCode);
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


    public class ResponseModel {


        private String version;
        private int statusCode;
        private String errorMessage;
        private List<Result> result;
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

        public List<Result> getResult() {
            return this.result;
        }

        public void setResult(List<Result> result) {
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

        public class Result {
            private String farmerId;

            private int employeeId;

            private String messageDesc;

            private String reportID;

            private int messageType;

            private int status;

            private String createdDate;

            private int createdBy;

            private String updatedDate;

            private String updatedBy;

            private boolean isDeleted;

            private int id;

            public String getFarmerId() {
                return this.farmerId;
            }

            public void setFarmerId(String farmerId) {
                this.farmerId = farmerId;
            }

            public int getEmployeeId() {
                return this.employeeId;
            }

            public void setEmployeeId(int employeeId) {
                this.employeeId = employeeId;
            }

            public String getMessageDesc() {
                return this.messageDesc;
            }

            public void setMessageDesc(String messageDesc) {
                this.messageDesc = messageDesc;
            }

            public String getReportID() {
                return this.reportID;
            }

            public void setReportID(String reportID) {
                this.reportID = reportID;
            }

            public int getMessageType() {
                return this.messageType;
            }

            public void setMessageType(int messageType) {
                this.messageType = messageType;
            }

            public int getStatus() {
                return this.status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getCreatedDate() {
                return this.createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public int getCreatedBy() {
                return this.createdBy;
            }

            public void setCreatedBy(int createdBy) {
                this.createdBy = createdBy;
            }

            public String getUpdatedDate() {
                return this.updatedDate;
            }

            public void setUpdatedDate(String updatedDate) {
                this.updatedDate = updatedDate;
            }

            public String getUpdatedBy() {
                return this.updatedBy;
            }

            public void setUpdatedBy(String updatedBy) {
                this.updatedBy = updatedBy;
            }

            public boolean getIsDeleted() {
                return this.isDeleted;
            }

            public void setIsDeleted(boolean isDeleted) {
                this.isDeleted = isDeleted;
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
