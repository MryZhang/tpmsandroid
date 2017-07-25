package com.android.morephone.data.network;

import android.content.Context;

import com.android.morephone.data.BaseUrl;
import com.android.morephone.data.entity.Response;
import com.android.morephone.data.entity.register.BindingRequest;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AnPEthan on 7/8/2016.
 */
public class ApiMorePhone {

    //    private static final String BASE_URL = "https://raw.githubusercontent.com/tungpham/tpmsservices/";

    private static ApiMorePhonePath mApiPath;

    private static volatile Retrofit mRetrofit;

    private static final String TAG = ApiMorePhone.class.getSimpleName();

    //Singleton for Retrofit
    private static Retrofit getRetrofit(final Context context) {
        if (mRetrofit == null) {
            synchronized (ApiMorePhone.class) {
                if (mRetrofit == null) {

                    //Set log
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    boolean isLog = true;
                    logging.setLevel(isLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

                    //Create cache
//                    File file = new File(context.getCacheDir(), "response");

                    //Add log and set time out
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                            .authenticator(new Authenticator() {
//                                @Override
//                                public Request authenticate(Route route, Response response) throws IOException {
////                                    System.out.println("Authenticating for response: " + response);
////                                    System.out.println("Challenges: " + response.challenges());
//                                    String credential = Credentials.basic("ACebd7d3a78e2fdda9e51239bad6b09f97", "8d2af0937ed2a581dbb19f70dd1dd43b");
//                                    return response.request().newBuilder()
//                                            .header("Authorization", credential)
//                                            .build();
//                                }
//                            })
//                            .addInterceptor(new Interceptor() {
//                                @Override
//                                public Response intercept(Chain chain) throws IOException {
//                                    Request.Builder ongoing = chain.request().newBuilder();
//                                    ongoing.addHeader("Accept", "application/json");
//                                    return chain.proceed(ongoing.build());
//                                }
//                            })
                            .readTimeout(60, TimeUnit.SECONDS)
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .build();
                    mRetrofit = new Retrofit.Builder()
                            .baseUrl(BaseUrl.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient).build();
                }
            }
        }
        return mRetrofit;
    }


    //Singleton for ApiPath
    private static ApiMorePhonePath getApiPath(Context context) {
        if (mApiPath == null) {
            synchronized (ApiMorePhone.class) {
                if (mApiPath == null) {
                    mApiPath = getRetrofit(context).create(ApiMorePhonePath.class);
                }
            }
        }
        return mApiPath;
    }

    public static void registerApplication(Context context,
                                           String incomingPhoneNumberSid,
                                           Callback<Response> callback) {
        Call<Response> call = getApiPath(context).registerApplication(incomingPhoneNumberSid);
        call.enqueue(callback);
    }

    public static void binding(Context context,
                               BindingRequest bindingRequest,
                               Callback<Response> callback) {
        Call<Response> call = getApiPath(context).binding(bindingRequest);
        call.enqueue(callback);
    }

}
