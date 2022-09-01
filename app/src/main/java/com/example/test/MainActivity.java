package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imageView;
    Button button;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    Bitmap imageBitmap;
    String TAG="MAIN ACTIVITY";
//    AVD
    String url = "http://10.0.2.2:2000/predictObject";

//    Real Device
//    String url = "http://192.168.0.104:2000/predictObject";
//    String url = "http://192.168.43.52:2000/predictObject";

    boolean imageSelected=false;
    JSONObject obj;
    String encodedImg="";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

//    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView_container);
        button = (Button)findViewById(R.id.button_upload);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

//    public void detectImageButton(View view){
//        Log.d(TAG, "detectImage: ");
//        if(imageSelected) {
//            Intent intent =new Intent(this,ResultActivity.class);
//            ByteArrayOutputStream stream=new ByteArrayOutputStream();
//
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
//            byte[] byteArray=stream.toByteArray();
//            intent.putExtra("photo", byteArray);
//            startActivity(intent);
//        }
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            imageSelected=true;
            try {
                imageBitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            useImage();
        }

        //Capture an image via camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();

            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageSelected=true;
            useImage();
        }

    }
    private void useImage(){
        encodedImg =encodedImg+ encodeTobase64();
    }

//    OkHttpClient client =new OkHttpClient().newBuilder()
//            .connectTimeout(20, TimeUnit.SECONDS)
//            .readTimeout(20,TimeUnit.SECONDS)
//            .writeTimeout(20, TimeUnit.SECONDS)
//            .build();
    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
            return chain.proceed(request);
        }
    })
            .build();
    public void sendPostRequest(View view){
        if(!imageSelected)
            return;
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    //Your code goes here

        String key="imagefile";
        String data = encodedImg;
        try {
            JSONObject json = new JSONObject();
            json.put(key,data);
            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            System.out.println("resonse will be sent");
            System.out.println("response sent");
                try (Response response = client.newCall(request).execute()){
                    System.out.println("respoonse receiveddd");
                    String result = response.body().string();
                    if (!TextUtils.isEmpty(result)) {
                        obj = new JSONObject(result);
                        System.out.println("Response received");
                        nextActivity();
                    }else{

                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception = " + e);
                }

        }catch(Exception e){
//            System.out.println(e);
        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void nextActivity(){
        if(obj.has("resultant_image")){
            try {
                Bitmap resMap = decodeBase64(obj.getString("resultant_image"));

                Intent intent =new Intent(this,ResultActivity.class);
                ByteArrayOutputStream stream=new ByteArrayOutputStream();

                resMap.compress(Bitmap.CompressFormat.JPEG,50,stream);
                byte[] byteArray=stream.toByteArray();
                intent.putExtra("photo", byteArray);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String encodeTobase64()
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input)
    {
        input=input.substring(2);
        System.out.println(input);
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        System.out.println(decodedByte.length+" lebfjd");
        Bitmap b =BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        return b;
    }



    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
}