package com.Tharun.qrdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QrConfirm extends AppCompatActivity {

    ImageView icon;
    TextView txtRes;
    Button confirm;

    Retrofit retro;
    Api api;

    private String qrToken;

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_confirm);

        icon = findViewById(R.id.imageView);
        txtRes = findViewById(R.id.textView);
        confirm = findViewById(R.id.button);

        Bundle extras = getIntent().getExtras();
        qrToken = extras.getString("key");

        final String AUTH = "Basic " + Base64.encodeToString(("username:password").getBytes(), Base64.NO_WRAP);

        String BASE_URL = "http://192.../";


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @NonNull
                            @Override
                            public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
                                Request original = chain.request();

                                Request.Builder requestBuilder = original.newBuilder()
                                        .addHeader("Authorization", AUTH)
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        }
                ).build();

        retro = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        api = retro.create(Api.class);

        int id=Integer.parseInt(qrToken);
       // Toast.makeText(getApplicationContext(),"id: "+id,Toast.LENGTH_SHORT).show();
        Call<ReturnObject> call = api.getResponse(id);

        call.enqueue(new Callback<ReturnObject>() {
            @Override
            public void onResponse(Call<ReturnObject> call, Response<ReturnObject> response) {

                if(response.code() == 404){
                    icon.setImageResource(R.drawable.ic_error);
                    //icon.setBackgroundResource(R.color.cYelloe);
                    txtRes.setText("Ticket Not Found");
                }
                else if(response.body().getValid()){
                    icon.setImageResource(R.drawable.ic_ok);
                    //icon.setBackgroundResource(R.color.lGreen);
                    txtRes.setText("Authenticated");
                }else if (!response.body().getValid()){
                    icon.setImageResource(R.drawable.ic_error);
                    //icon.setBackgroundResource(R.color.cYelloe);
                    txtRes.setText("Duplicate");
                }
            }

            @Override
            public void onFailure(Call<ReturnObject> call, Throwable t) {
                icon.setImageResource(R.drawable.ic_cross);
                //icon.setBackgroundResource(R.color.red);
                Log.d("d", "onFailure: Failed :(");
                Log.d("d", "onResponseFailed: "+t.toString());
                txtRes.setText("Error");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

    }
}