package com.auth0.samples;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lbalmaceda on 5/10/17.
 */

public class MainActivity extends Activity {

    private TextView token;

    private String idToken;
    private TextView apiAnswer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        token = (TextView) findViewById(R.id.token);
        apiAnswer = (TextView) findViewById(R.id.apiAnswer);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Button apiCallButton = (Button) findViewById(R.id.ApiButton);
        apiCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeApiCall();
            }
        });
    }

    private void makeApiCall()
    {
        DevDayApi api = CreateApi("", idToken);

        api.getUserProfile().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                runOnUiThread(() -> {
                    apiAnswer.setText(response.body().Answer);
                });
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                apiAnswer.setText(t.getMessage());
            }
        });




    }

    private DevDayApi CreateApi(String baseUrl, String authToken)
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + authToken)//tokenProvider.getAuthToken())
                            .build();

                    return chain.proceed(newRequest);
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(DevDayApi.class);
    }

    private void login() {
        token.setText("Not logged in");
        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .withScope("openid email profile")
                .start(MainActivity.this, new AuthCallback() {

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                idToken = credentials.getIdToken();
                                Log.d("id token", credentials.getIdToken());
                                Log.d("access token", credentials.getAccessToken());
                                token.setText("Logged in: " + credentials.getIdToken());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull final Dialog dialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                });
    }
}
