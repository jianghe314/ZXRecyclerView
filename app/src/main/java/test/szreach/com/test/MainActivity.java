package test.szreach.com.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import test.szreach.com.test.mRecyclerView.RefreshRecyclerView;
import test.szreach.com.test.mRecyclerView.ZXFootView;
import test.szreach.com.test.mRecyclerView.ZXHeadView;
import test.szreach.com.test.mRecyclerView.ZXRecylerView;

public class MainActivity extends AppCompatActivity {

    private String api="http://api.ybolo.com/rest/WeatherRestService/weather";
    private Gson gson=new Gson();
    private ZXRecylerView recyclerView;
    private List<Bean> data=new ArrayList<>();
    private ItemAdapter adapter;

    //migrate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new ItemAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.addFootView(new ZXFootView(this));
        recyclerView.addHeadView(new ZXHeadView(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //创建数据
        for (int i = 'A'; i <='Z'; i++) {
            Bean bean=new Bean();
            bean.setTv((char)i+"");
            data.add(bean);
        }
        adapter.notifyDataSetChanged();
        recyclerView.setRefreshUpData(new ZXRecylerView.RefreshDataListener() {
            @Override
            public void RefreshUpData() {

            }

            @Override
            public void RefreshLoadData() {
                Log.e("RefreshLoadData","-->上拉加载更多");
            }

            @Override
            public void RefreshDataComplete() {

            }
        });


    }

}
