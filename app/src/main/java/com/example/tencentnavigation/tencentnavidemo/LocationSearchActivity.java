package com.example.tencentnavigation.tencentnavidemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


import com.google.gson.Gson;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 地点搜索
 * 输入提示
 */
public class LocationSearchActivity extends AppCompatActivity {

    private static final String TAG = "navisdk";
    private static final int CHANGE_UI = 1;

    private SearchView mSearchView;
    private ListView mListView;
    private ResultAdapter resultAdapter;

    private ArrayList<Searchlist> searchlists = new ArrayList<>();

    /**
     * 更新搜索结果列表
     */
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case CHANGE_UI:
                    resultAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_location_search);

        super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        View back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        //搜索框
        mSearchView =  findViewById(R.id.searchView);
        mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);

        mListView = findViewById(R.id.listView);
        resultAdapter = new ResultAdapter(this, R.id.listView, searchlists);
        mListView.setAdapter(resultAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Searchlist data = resultAdapter.getItem(position);
                if (data != null) {
                    Intent intent = new Intent();
                    intent.putExtra("address", data.title);
                    intent.putExtra("latitude", data.location.lat);
                    intent.putExtra("longitude", data.location.lng);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }



    //搜索框监听
    SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            //获取输入提示列表
            searchlists.clear();
            getResult(newText);
            return false;
        }
    };

    /**
     * 获取输入提示信息，解析
     * @param str
     */
    private void getResult(String str){

        new Thread(new Runnable() {
            @Override
            public void run() {
                //请求地址
                final StringBuilder path = new StringBuilder();
                path.append(getResources().getString(R.string.url))
                        .append("region=北京")
                        .append("&page_index=1")
                        .append("&page_size=10")
                        .append("&key=").append(NaviUtil.getAuthKey(getApplicationContext()))
                        .append("&keyword=").append(str);
                try {
                    URL url = new URL(path.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setRequestMethod("GET");
                    //获得结果码
                    int responseCode = connection.getResponseCode();
                    if(responseCode ==200){
                        //请求成功 获得返回的流
                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                is));
                        String result = "";
                        String line = "";
                        while (null != (line = reader.readLine()))
                        {
                            result += line;

                        }
                        Gson gson = new Gson();
                        SearchResult searchResult = gson.fromJson(result, SearchResult.class);
                        if(searchResult.status != 0 && searchResult.count<=0){
                            Log.e("search:","failed!!!!!");
                            return;
                        }
                        for(Searchlist searchlist:searchResult.data){
                            searchlists.add(searchlist);
                        }
                        Message message = new Message();
                        message.what = CHANGE_UI;
                        handler.sendMessage(message);

                    }else {
                        //请求失败
                        Log.e("search:","failed!!!!!!!!!");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 定义搜索结果类
     */
    private class Searchlist{

        public Location location;
        public String title;
        public String address;
        public String id;

    }

    private class SearchResult{
        public ArrayList<Searchlist> data;
        public int status;
        public int count;

    }
    private class Location{
        public float lng;
        public float lat;
    }

    /**
     * 定义适配器
     */
    private class ResultAdapter extends ArrayAdapter<Searchlist>{

        public ResultAdapter(Context context, int resourceId, ArrayList<Searchlist> searchlists){
            super(context, resourceId, searchlists);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Searchlist item = getItem(position);
            if(convertView == null){
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.search_item,null);
            }
            TextView title = convertView.findViewById(R.id.search_item_text);
            title.setText(item.title);
            return convertView;
        }
    }


}
