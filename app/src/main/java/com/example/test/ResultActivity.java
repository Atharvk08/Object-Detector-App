package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class ResultActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("photo");

        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewResult);
        imageView.setImageBitmap(bmp);

    }
    public void MoveToMainActivity(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}