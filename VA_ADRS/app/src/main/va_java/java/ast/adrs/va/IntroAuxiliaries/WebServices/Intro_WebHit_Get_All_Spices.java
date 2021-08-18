package ast.adrs.va.IntroAuxiliaries.WebServices;

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


public class Intro_WebHit_Get_All_Spices {

    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void getSpices(final IWebCallback iWebCallback) {

        String myUrl;


        myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getSpices;
//        myUrl = "http://74.208.144.99:9090/api/FlatMouza/GetAllDistrict";

        Log.d("LOG_AS", "getSpices: " + myUrl);


//        if (AppConfig.getInstance().getGuestLogin()) {
//            mClient.addHeader(ApiMethod.HEADER.Authorization, AppConstt.guestAuth.guestAuth);
//        } else {
//  mClient.addHeader(ApiMethod.HEADER.Authorization, AppConfig.getInstance().mUserData.authToken);
//
//        }
//        mClient.addHeader(ApiMethod.HEADER.Authorization, AppConstt.guestAuth.guestAuth);
//        mClient.addHeader(ApiMethod.HEADER.Lang, AppConfig.getInstance().loadDefLanguage());
//        Log.d("currentLang", AppConfig.getInstance().loadDefLanguage());




        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);
        mClient.post(myUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getSpices onSuccess: "+statusCode);
                        String strResponse;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);



                            responseObject = gson.fromJson(strResponse, ResponseModel.class);


                            switch (responseObject.getStatusCode()){

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
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error) {
                        Log.d("LOG_AS", "getSpices onFailure: "+statusCode);
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


    public static final class ResponseModel {


        public class Result
        {
            private String valueMemeber;

            private String displayMember;

            public void setValueMemeber(String valueMemeber){
                this.valueMemeber = valueMemeber;
            }
            public String getValueMemeber(){
                return this.valueMemeber;
            }
            public void setDisplayMember(String displayMember){
                this.displayMember = displayMember;
            }
            public String getDisplayMember(){
                return this.displayMember;
            }
        }


            private String version;

            private int statusCode;

            private String errorMessage;

            private List<Result> result;

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
            public void setResult(List<Result> result){
                this.result = result;
            }
            public List<Result> getResult(){
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
