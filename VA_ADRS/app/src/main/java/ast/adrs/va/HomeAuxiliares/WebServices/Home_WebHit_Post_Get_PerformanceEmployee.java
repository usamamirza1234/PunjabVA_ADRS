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

public class Home_WebHit_Post_Get_PerformanceEmployee {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getEmployeePerformance(Context context, final IWebCallback iWebCallback) {

        mContext = context;

        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getEmpPerformance;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());


        Log.d("LOG_AS", "getEmployeePerformance  myUrl: " + myUrl);
        Log.d("LOG_AS", "getEmployeePerformance  Authorization_TOKEN: " + AppConfig.getInstance().mUserData.getAuthToken());

        mClient.post(myUrl, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getEmployeePerformance  onSuccess: " + statusCode);
                        String strResponse = null;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);
                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

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


                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "getEmployeePerformance  onFailure: " + statusCode);
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


        public class Result
        {
            private int diseaseIntimationCount;

            private List<String> diseaseIntimationDiseases;

            private int dieaseReportingCount;

            private List<String> diseaseReportingDiseases;

            private int sampleCollectionCount;

            public void setDiseaseIntimationCount(int diseaseIntimationCount){
                this.diseaseIntimationCount = diseaseIntimationCount;
            }
            public int getDiseaseIntimationCount(){
                return this.diseaseIntimationCount;
            }
            public void setDiseaseIntimationDiseases(List<String> diseaseIntimationDiseases){
                this.diseaseIntimationDiseases = diseaseIntimationDiseases;
            }
            public List<String> getDiseaseIntimationDiseases(){
                return this.diseaseIntimationDiseases;
            }
            public void setDieaseReportingCount(int dieaseReportingCount){
                this.dieaseReportingCount = dieaseReportingCount;
            }
            public int getDieaseReportingCount(){
                return this.dieaseReportingCount;
            }
            public void setDiseaseReportingDiseases(List<String> diseaseReportingDiseases){
                this.diseaseReportingDiseases = diseaseReportingDiseases;
            }
            public List<String> getDiseaseReportingDiseases(){
                return this.diseaseReportingDiseases;
            }
            public void setSampleCollectionCount(int sampleCollectionCount){
                this.sampleCollectionCount = sampleCollectionCount;
            }
            public int getSampleCollectionCount(){
                return this.sampleCollectionCount;
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
