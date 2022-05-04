package edu.skku.cs.pa2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MazeActivity extends AppCompatActivity {


    private GridViewAdapter gridViewAdapter;

    public int turn =0;
    public int hint =1;
    public int size;

    public int[] userLoc= {0,0};
    public int[] goalLoc= {0,0};
    ArrayList<cellInfo>items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiiy_maze);


        Intent intent = getIntent();
        String name =intent.getStringExtra(MapSelectionActivity.EXT_level);
        OkHttpClient client=new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://115.145.175.57:10099/maze/map").newBuilder();
        urlBuilder.addQueryParameter("name",name);
        String url =urlBuilder.build().toString();
        Request req = new Request.Builder().url(url).build();


        Button hintButton=(Button)findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hint==1) {
                    hint -= 1;

                    int hintidx=0;
                    Queue<int[]> queue = new LinkedList<>();
                    int[][]visited = new int[size][size];
                    visited[userLoc[1]][userLoc[0]]=1;

                    cellInfo curCell=items.get(userLoc[1]*size+userLoc[0]);
                    if(!curCell.UP){
                        int tmp[] = new int[3];
                        tmp[0]=userLoc[0];
                        tmp[1]=userLoc[1]-1;
                        tmp[2]=1;
                        visited[tmp[1]][tmp[0]]=1;
                        queue.add(tmp);
                    }
                    if(!curCell.DOWN){
                        int tmp[] = new int[3];
                        tmp[0]=userLoc[0];
                        tmp[1]=userLoc[1]+1;
                        tmp[2]=2;
                        visited[tmp[1]][tmp[0]]=1;
                        queue.add(tmp);
                    }
                    if(!curCell.LEFT){
                        int tmp[] = new int[3];
                        tmp[0]=userLoc[0]-1;
                        tmp[1]=userLoc[1];
                        tmp[2]=3;
                        visited[tmp[1]][tmp[0]]=1;
                        queue.add(tmp);
                    }
                    if(!curCell.RIGHT){
                        int tmp[] = new int[3];
                        tmp[0]=userLoc[0]+1;
                        tmp[1]=userLoc[1];
                        tmp[2]=4;
                        visited[tmp[1]][tmp[0]]=1;
                        queue.add(tmp);
                    }

                    while (!queue.isEmpty()) {
                        int cur[] = queue.poll();
                        Log.d("su",cur[0]+" "+cur[1]+" "+cur[2]);
                        cellInfo tmpCell=items.get(cur[1]*size+cur[0]);
                        if(!tmpCell.UP){
                            int tmp[] = new int[3];
                            tmp[0]=cur[0];
                            tmp[1]=cur[1]-1;
                            tmp[2]=cur[2];
                            if(tmp[0]==goalLoc[0] && tmp[1]==goalLoc[1]){
                                hintidx=tmp[2];
                                break;
                            }
                            if(visited[tmp[1]][tmp[0]]!=1) {
                                visited[tmp[1]][tmp[0]]=1;
                                queue.add(tmp);
                            }
                        }
                        if(!tmpCell.DOWN){
                            int tmp[] = new int[3];
                            tmp[0]=cur[0];
                            tmp[1]=cur[1]+1;
                            tmp[2]=cur[2];
                            if(tmp[0]==goalLoc[0] && tmp[1]==goalLoc[1]){
                                hintidx=tmp[2];
                                break;
                            }
                            if(visited[tmp[1]][tmp[0]]!=1) {
                                visited[tmp[1]][tmp[0]]=1;
                                queue.add(tmp);
                            }
                        }
                        if(!tmpCell.LEFT){
                            int tmp[] = new int[3];
                            tmp[0]=cur[0]-1;
                            tmp[1]=cur[1];
                            tmp[2]=cur[2];
                            if(tmp[0]==goalLoc[0] && tmp[1]==goalLoc[1]){
                                hintidx=tmp[2];
                                break;
                            }
                            if(visited[tmp[1]][tmp[0]]!=1) {
                                visited[tmp[1]][tmp[0]]=1;
                                queue.add(tmp);
                            }
                        }
                        if(!tmpCell.RIGHT){
                            int tmp[] = new int[3];
                            tmp[0]=cur[0]+1;
                            tmp[1]=cur[1];
                            tmp[2]=cur[2];
                            if(tmp[0]==goalLoc[0] && tmp[1]==goalLoc[1]){
                                hintidx=tmp[2];
                                break;
                            }
                            if(visited[tmp[1]][tmp[0]]!=1) {
                                visited[tmp[1]][tmp[0]] = 1;
                                queue.add(tmp);
                            }
                        }

                    }

                    if(hintidx==1){
                        cellInfo hintCell =items.get((userLoc[1]-1)*size+userLoc[0]);
                        hintCell.isHint=true;
                    }
                    else if(hintidx==2){
                        cellInfo hintCell =items.get((userLoc[1]+1)*size+userLoc[0]);
                        hintCell.isHint=true;
                    }
                    else if(hintidx==3){
                        cellInfo hintCell =items.get((userLoc[1])*size+userLoc[0]-1);
                        hintCell.isHint=true;
                    }
                    else if(hintidx==4){
                        cellInfo hintCell =items.get((userLoc[1])*size+userLoc[0]+1);
                        hintCell.isHint=true;
                    }
                    GridView gridView = findViewById(R.id.gridView);
                    GridViewAdapter gv = (GridViewAdapter) gridView.getAdapter();
                    gv.notifyDataSetChanged();

                    gridView.setAdapter(gv);
                }
            }
        });

        Button leftButton =(Button)findViewById(R.id.leftButton);
        Button rightButton =(Button)findViewById(R.id.rightButton);
        Button upButton =(Button)findViewById(R.id.upButton);
        Button downButton =(Button)findViewById(R.id.downButton);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cellInfo curCell=items.get(userLoc[1]*size+userLoc[0]);
                if(!curCell.LEFT){
                    turn+=1;
                    userLoc[0]-=1;

                    cellInfo nextCell=items.get(userLoc[1]*size+userLoc[0]);
                    curCell.isStart=false;
                    nextCell.isStart=true;
                    nextCell.isHint=false;
                    nextCell.angle=270;
                    GridView gridView = findViewById(R.id.gridView);
                    GridViewAdapter gv = (GridViewAdapter) gridView.getAdapter();
                    gv.notifyDataSetChanged();

                    gridView.setAdapter(gv);
                    TextView turnText= findViewById(R.id.turnText);
                    turnText.setText("Turn : "+turn);
                    
                    if(userLoc[0]==goalLoc[0] && userLoc[1]==goalLoc[1]){
                        Toast.makeText(MazeActivity.this, "Finish!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cellInfo curCell=items.get(userLoc[1]*size+userLoc[0]);
                if(!curCell.RIGHT){
                    turn+=1;
                    userLoc[0]+=1;

                    cellInfo nextCell=items.get(userLoc[1]*size+userLoc[0]);
                    curCell.isStart=false;
                    nextCell.isStart=true;
                    nextCell.isHint=false;
                    nextCell.angle=90;
                    GridView gridView = findViewById(R.id.gridView);
                    GridViewAdapter gv = (GridViewAdapter) gridView.getAdapter();
                    gv.notifyDataSetChanged();

                    gridView.setAdapter(gv);
                    TextView turnText= findViewById(R.id.turnText);
                    turnText.setText("Turn : "+turn);
                    if(userLoc[0]==goalLoc[0] && userLoc[1]==goalLoc[1]){
                        Toast.makeText(MazeActivity.this, "Finish!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cellInfo curCell=items.get(userLoc[1]*size+userLoc[0]);
                if(!curCell.UP){
                    turn+=1;
                    userLoc[1]-=1;

                    cellInfo nextCell=items.get(userLoc[1]*size+userLoc[0]);
                    curCell.isStart=false;
                    nextCell.isStart=true;
                    nextCell.isHint=false;
                    nextCell.angle=0;
                    GridView gridView = findViewById(R.id.gridView);
                    GridViewAdapter gv = (GridViewAdapter) gridView.getAdapter();
                    gv.notifyDataSetChanged();

                    gridView.setAdapter(gv);
                    TextView turnText= findViewById(R.id.turnText);
                    turnText.setText("Turn : "+turn);
                    if(userLoc[0]==goalLoc[0] && userLoc[1]==goalLoc[1]){
                        Toast.makeText(MazeActivity.this, "Finish!", Toast.LENGTH_SHORT).show();
                    }
                    
                    
                }
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cellInfo curCell=items.get(userLoc[1]*size+userLoc[0]);
                if(!curCell.DOWN){
                    turn+=1;
                    userLoc[1]+=1;
                    cellInfo nextCell=items.get(userLoc[1]*size+userLoc[0]);
                    curCell.isStart=false;
                    nextCell.isStart=true;
                    nextCell.isHint=false;
                    nextCell.angle=180;
                    GridView gridView = findViewById(R.id.gridView);
                    GridViewAdapter gv = (GridViewAdapter) gridView.getAdapter();
                    gv.notifyDataSetChanged();

                    gridView.setAdapter(gv);
                    TextView turnText= findViewById(R.id.turnText);
                    turnText.setText("Turn : "+turn);
                    if(userLoc[0]==goalLoc[0] && userLoc[1]==goalLoc[1]){
                        Toast.makeText(MazeActivity.this, "Finish!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String myResponse = response.body().string();
                Gson gson = new GsonBuilder().create();
                final mazeModel maze = gson.fromJson(myResponse,mazeModel.class);



                String[] tmp = maze.getMaze().split(" |\n|  ");

                items = new ArrayList<cellInfo>();
                size =Integer.parseInt(tmp[0]);

                goalLoc[0]=size-1;
                goalLoc[1]=size-1;

                for (int i=1;i<tmp.length;i++){
                    if(tmp[i].length()>0) {
                        if(i==1) {
                            cellInfo tt = new cellInfo(Integer.parseInt(tmp[i]),true,false);
                            items.add(tt);
                        }
                        else if(i==tmp.length-1){
                            cellInfo tt = new cellInfo(Integer.parseInt(tmp[i]),false,true);
                            items.add(tt);
                        }
                        else{
                            cellInfo tt = new cellInfo(Integer.parseInt(tmp[i]),false,false);
                            items.add(tt);
                        }
                    }
                }

                gridViewAdapter = new GridViewAdapter(items,size);
                GridView gridView = findViewById(R.id.gridView);


                MazeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        gridView.setAdapter(gridViewAdapter);
                        gridView.setNumColumns(size);
                    }
                });
            }
        });


    }

    class cellInfo{
        public boolean UP;
        public boolean DOWN;
        public boolean LEFT;
        public boolean RIGHT;
        public boolean isStart;
        public boolean isEnd;
        public boolean isHint;
        public int angle;


        public cellInfo(Integer num,boolean isStart,boolean isEnd){
            if(num/8==1)
                UP=true;
            else
                UP=false;
            num=num%8;
            if(num/4==1)
                LEFT=true;
            else
                LEFT=false;
            num=num%4;
            if(num/2==1)
                DOWN=true;
            else
                DOWN=false;
            num=num%2;
            if(num==1)
                RIGHT=true;
            else
                RIGHT=false;

            this.isStart=false;
            this.isEnd=false;
            if(isStart){
                this.isStart=true;
            }
            if(isEnd){
                this.isEnd=true;
            }
            angle=0;
            isHint=false;

        }
    }


    class GridViewAdapter extends BaseAdapter{

        ArrayList<cellInfo> items;
        int size;

        public Bitmap rotateImage(Bitmap src, float degree){

            Matrix matrix = new Matrix();

            matrix.postRotate(degree);

            return Bitmap.createBitmap(src,0,0,src.getWidth(),src.getHeight(),matrix,true);
        }

        public GridViewAdapter(ArrayList<cellInfo> items,int size)
        {
            this.items=items;
            this.size=size;

        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final cellInfo curCell = items.get(i);

            if(view==null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.cell_grid,viewGroup,false);



                ImageView whiteBox = view.findViewById(R.id.imageCell);

                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams)whiteBox.getLayoutParams();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


                int dpi=displayMetrics.densityDpi;
                float density = displayMetrics.density;

                int mg=3;
                int px= (int) Math.round((float)mg*density+0.5);

                int tmp[] = new int[4];
                int tpx=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) ((350+0.5)/size),context.getResources().getDisplayMetrics());
                int width =tpx;
                int height =tpx;


                if(items.get(i).UP) {
                    tmp[1] = px;
                    height-=px;
                }

                if(items.get(i).LEFT){
                    tmp[0]=px;
                    width-=px;
                }

                if(items.get(i).DOWN){
                    tmp[3]=px;
                    height-=px;
                }

                if(items.get(i).RIGHT){
                    tmp[2]=px;
                    width-=px;
                }

                lp.height=height;
                lp.width=width;


                lp.setMargins(tmp[0],tmp[1],tmp[2],tmp[3]);


                whiteBox.setLayoutParams(lp);
                ImageView unitView = view.findViewById(R.id.unitCell);
                if(curCell.isStart){
                    unitView.setVisibility(View.VISIBLE);
                    unitView.setImageBitmap(rotateImage(BitmapFactory.decodeResource(getResources(),R.drawable.user),curCell.angle));
                    if(size>=10){
                        unitView.setScaleX(0.7f);
                        unitView.setScaleY(0.7f);
                    }

                }
                else if(curCell.isEnd){
                    unitView.setVisibility(View.VISIBLE);
                    unitView.setImageResource(R.drawable.goal);
                    if(size>=10){
                        unitView.setScaleX(0.7f);
                        unitView.setScaleY(0.7f);
                    }
                }
                else if(curCell.isHint){
                    unitView.setVisibility(View.VISIBLE);
                    unitView.setImageResource(R.drawable.hint);
                    unitView.setScaleX(0.5f);
                    unitView.setScaleY(0.5f);
                    if(size>=10){
                        unitView.setScaleX(0.3f);
                        unitView.setScaleY(0.3f);
                    }
                }
                else{
                    unitView.setVisibility(View.INVISIBLE);
                }


            }
            return view;
        }
    }
}
