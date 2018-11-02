package test.szreach.com.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

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
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL));
        adapter=new ItemAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.addFootView(new ZXFootView(this));
        recyclerView.addHeadView(new ZXHeadView(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //创建数据
        for (int i = 'A'; i <='z'; i++) {
            Bean bean=new Bean();
            bean.setTv((char)i+"");
            data.add(bean);
        }
        adapter.notifyDataSetChanged();
        recyclerView.setRefreshUpData(new ZXRecylerView.RefreshDataListener() {
            @Override
            public void RefreshUpData() {
                Log.e("RefreshData","-->下拉刷新");
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void RefreshLoadData() {
                Log.e("RefreshData","-->上拉加载更多");
                try {
                    Thread.sleep(1000*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void RefreshDataComplete() {
                Log.e("RefreshData","-->上拉，下拉完成");
            }

            @Override
            public void RefreshCancel() {
                Log.e("RefreshData","-->上拉，下拉取消");
            }
        });



    }


    @Override
    protected void onStop() {
        recyclerView.CancelRequestTask();
        super.onStop();
    }
}
