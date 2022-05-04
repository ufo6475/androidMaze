package edu.skku.cs.pa2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapSelectionActivity extends AppCompatActivity {


    private mapViewAdapter mapViewAdapter;

    public static String EXT_level="level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);

        Intent intent = getIntent();
        String name =intent.getStringExtra(MainActivity.EXT_name);

        TextView nameView =findViewById(R.id.textName);
        nameView.setText(name);

        OkHttpClient client=new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://115.145.175.57:10099/maps").newBuilder();
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<Map<String, String>>>() {}.getType();
                ArrayList<Map<String, String>> data = gson.fromJson(myResponse, type);
                mapViewAdapter=new mapViewAdapter(getApplicationContext(),data);
                ListView mapList = findViewById(R.id.mapList);
                MapSelectionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mapList.setAdapter(mapViewAdapter);
                    }
                });
            }
        });
    }
    private class mapViewAdapter extends BaseAdapter{

        private Context mContext;
        private ArrayList<Map<String,String>> mapList;

        public mapViewAdapter(Context mContext, ArrayList<Map<String,String>>mapList){
            this.mContext=mContext;
            this.mapList=mapList;
        }
        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int i) {
            return mapList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null){
                LayoutInflater layoutInflater= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view =layoutInflater.inflate(R.layout.element_list,viewGroup,false);
            }
            TextView mazeName=view.findViewById(R.id.textMazeName);
            TextView mazeLevel=view.findViewById(R.id.textMazeLevel);
            mazeName.setText(mapList.get(i).get("name"));
            mazeLevel.setText(mapList.get(i).get("size"));

            Button startButton=view.findViewById(R.id.startButton);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MapSelectionActivity.this, MazeActivity.class);
                    intent.putExtra(EXT_level,mapList.get(i).get("name"));
                    startActivity(intent);
                }
            });

            return view;
        }
    }
}
