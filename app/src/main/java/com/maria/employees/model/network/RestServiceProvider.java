package com.maria.employees.model.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestServiceProvider {
    private static final RestServiceProvider INSTANCE = new RestServiceProvider();

    private RestService mRestService;

    private RestServiceProvider() {
    }

    public static RestServiceProvider newInstance() {
        return INSTANCE;
    }

    public synchronized final RestService getRestService() {
        if (mRestService == null) {
            mRestService = createRestService();
        }
        return mRestService;
    }

    private RestService createRestService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestService.API_URL)
                .client(provideClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RestService.class);
    }

    private OkHttpClient provideClient() {
        Interceptor authInterceptor = chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };

        HttpLoggingInterceptor logging = (new HttpLoggingInterceptor())
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build();
    }
}
