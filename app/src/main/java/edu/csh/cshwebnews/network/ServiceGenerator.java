package edu.csh.cshwebnews.network;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass, String baseUrl,
                                      final String accessToken, final String tokenType) {

        /* Add a StethoInterceptor for debugging */
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.networkInterceptors().add(new StethoInterceptor());

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setClient(new OkClient(okHttpClient));

        //Non OAuth request, add headers
        if (accessToken != null && tokenType != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Content-Type", "application/json");
                    request.addHeader("Accept", "application/vnd.csh.webnews.v1+json");
                    request.addHeader("Authorization", tokenType + " " +
                            accessToken);
                }
            });
        }

        return builder.build().create(serviceClass);
    }
}
