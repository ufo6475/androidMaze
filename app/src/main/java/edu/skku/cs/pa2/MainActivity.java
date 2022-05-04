package edu.skku.cs.pa2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity {

    public static String EXT_name="Name";
    Button nameBtn;
    EditText nameEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameBtn=(Button) findViewById(R.id.signButton);
        nameEdit=(EditText) findViewById(R.id.nameEdit);
        nameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curName=nameEdit.getText().toString();
                OkHttpClient client=new OkHttpClient();

                nameModel nameData = new nameModel();
                nameData.setUsername(curName);

                Gson gson = new Gson();
                String json = gson.toJson(nameData,nameModel.class);

                HttpUrl.Builder urlBulider = HttpUrl.parse("http://115.145.175.57:10099/users").newBuilder();
                String url=urlBulider.build().toString();

                Request req = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json"),json)).build();

                client.newCall(req).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                               if(myResponse.contains("true")){
                                   Intent intent =new Intent(MainActivity.this,MapSelectionActivity.class);
                                   intent.putExtra(EXT_name,curName);
                                   startActivity(intent);
                               }
                               else{
                                   Toast.makeText(MainActivity.this, "Wrong User Name", Toast.LENGTH_SHORT).show();
                               }

                            }
                        });
                    }
                });


            }
        });
    }
}