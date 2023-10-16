package com.my.afarycode.ratrofit;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;
    private static AfaryCode apiInterface = null;

    public static Retrofit getClient(Context context) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(100, TimeUnit.SECONDS) // connect timeout
                .writeTimeout(100, TimeUnit.SECONDS) // write timeout
                .readTimeout(100, TimeUnit.SECONDS); // read timeout

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static AfaryCode loadInterface(Context context) {
        if (apiInterface == null) {
            apiInterface = ApiClient.getClient(context).create(AfaryCode.class);
        }
        return apiInterface;
    }
}