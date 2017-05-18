package net.gluu.erasmus.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIService {
    //Live
  public static final String API_BASE_URL = "https://erasmusdev.gluu.org/badge-mgr/";
    //Local
//  public static final String API_BASE_URL = "http://192.168.200.78:8080/badge-mgr/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {

//            httpClient.interceptors().add(new Interceptor() {
//                @Override
//                public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
//                    Request original = chain.request();
//
//                    Request.Builder requestBuilder = original.newBuilder();
//                    if (authToken != null) {
//                    // Request customization: add request headers
//                            requestBuilder.header("Authorization", authToken);
//                    }
//
//                    requestBuilder.method(original.method(), original.body());
//
//                    Request request = requestBuilder.build();
//                    return chain.proceed(request);
//                }
//            });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
