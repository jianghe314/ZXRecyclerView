package test.szreach.com.test.mRecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by ZX on 2018/10/18
 * 需求分析
 *
 * 上拉加载更多：1.当处于加载更多时，此时下滑应可以取消加载更多状态，即取消请求队列，隐藏尾布局
 *               2.加载更多完成隐藏尾布局
 *               3.上拉未达到阈值时显示上拉加载更多，松开隐藏尾布局，当上拉距离达到阈值时，显示释放以加载更多，松开显示加载中
 * 下拉刷新：1.当处于下拉刷新时，此时上滑可以取消数据的加载，隐藏头布局
 *           2.下拉刷新完成时，隐藏头布局，刷新未完成时，保存正在刷新的状态
 *           3.是否能够设置打开页面自动刷新功能
 */
public class ZXRecylerView extends RecyclerView implements View.OnTouchListener{

    //下拉状态
    private static final int STATUS_PULL_TO_REFRESH=1;
    //释放立即刷新
    private static final int STATUS_REFRESH_TO_REFRESH=2;
    //正在刷新状态
    private static final int  STATUS_REFRESHING=3;
    //刷新完成或未刷新状态
    private static final int STATUS_REFRESH_END=4;

    //在加载更多中
    private static final int STATUS_LOADMORE_LOADING=5;
    //加载更多完成或未加载状态
    private static final int STATUS_LOADMORE_END=6;
    //上拉加载更多
    private static final int STATUS_LOADMORE_UP_LOAD=7;
    //释放以加载更多
    private static final int  STATUS_LOADMORE_TO_LOAD=8;


    //下拉头部回滚的速度
    private static final int SCROLL_SPEED=-20;

    //下拉刷新，上拉加载的滑动停留状态阈值
    private static final int SCROLL_VLUES=350;

    //用户设置的Adapter
    private Adapter mAdapter;
    private ArrayList<View> mHeaderView;
    private ArrayList<View> mFooterView;

    private ZXAdapter zxAdapter;
    //下拉头的高度
    private int headerHeight;
    //上拉尾的高度
    private int footerHeight;
    //当前头部的状态
    private int headCurrentStatus=STATUS_REFRESH_END;
    //当前尾部的状态
    private int footCurrentStatus=STATUS_LOADMORE_END;
    //手指按下时的纵坐标
    private float yDown;
    //判断为滚动之前可以移动的最大值
    private int touchSlop;
    //下拉头的布局参数
    private MarginLayoutParams headLayoutParams;
    //上拉尾布局参数
    private MarginLayoutParams footLayoutParams;
    //下拉头的View
    private ZXHeadView header;
    //上拉尾的View
    private ZXFootView footer;

    private RefreshDataListener refreshDataListener;

    //执行的任务
    private RefreshUpData refreshUpData;
    private RefreshLoadDate refreshLoadDate;

    public ZXRecylerView(Context context) {
        this(context,null);
    }

    public ZXRecylerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZXRecylerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        //获取最小滑动距离的阈值
        touchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        setOnTouchListener(this);
    }

    private void init() {
        mHeaderView=new ArrayList<>();
        mFooterView=new ArrayList<>();

    }

    //替换Adapter
    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter=adapter;
        zxAdapter=new ZXAdapter();
        super.setAdapter(zxAdapter);
    }

    //向外提供设置头布局和尾布局的方法
    public void addHeadView(View view){
        mHeaderView.clear();
        mHeaderView.add(view);
        header= (ZXHeadView) view;
    }

    public void addFootView(View view){
        mFooterView.clear();
        mFooterView.add(view);
        footer= (ZXFootView) view;
    }

    //初始化相关参数，将下拉头向上偏移进行隐藏
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        headerHeight=-header.getHeight();
        footerHeight=footer.getHeight();
        headLayoutParams= (MarginLayoutParams) header.getLayoutParams();
        headLayoutParams.topMargin=headerHeight;

        footLayoutParams= (MarginLayoutParams) footer.getLayoutParams();

    }

    //处理尾部
    @Override
    public void onScrollStateChanged(int state) {

    }

    //监听列表滑动事件
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int[] pos=getVisiblePos();
        Log.e("pos","pos[0]-->"+pos[0]+"pos[1]-->"+pos[1]+"-->getItemCount-->"+zxAdapter.getItemCount());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //手指按下时,获取手指在屏幕的位置
                yDown=event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float yMove=event.getRawY();
                int distance= (int) (yMove-yDown);
                Log.e("Distance","-->"+distance);
                if(Math.abs(distance) < touchSlop){
                    return false;
                }else {
                    /**
                     * distance>0即下拉否则上拉
                     * 1.当正在处于上拉加载更多状态时，此时下滑应该可以取消加载更多的状态。
                     * 2.当正在处于下拉刷新状态时，此时上滑应该可以取消刷新状态。
                     * 3.当数据不足一页时，不能上拉加载更多。
                     */
                    if(distance > 0){
                        if(pos[0] <= 1){
                            if(headCurrentStatus != STATUS_REFRESHING){
                                if(null == headLayoutParams){
                                    headLayoutParams= (MarginLayoutParams) header.getLayoutParams();
                                }
                                if(null != headLayoutParams){
                                    headLayoutParams.topMargin=(distance/2)+headerHeight;
                                    header.setLayoutParams(headLayoutParams);
                                    header.setStatus_To_Refresh(ZXHeadView.STATUS_REFRESH_PULL_RESHRESH);
                                    headCurrentStatus=STATUS_PULL_TO_REFRESH;
                                }
                                //当下拉达到阈值时，显示释放以刷新
                                if(Math.abs(distance) >= SCROLL_VLUES){
                                    header.setStatus_To_Refresh(ZXHeadView.STATUS_REFRESH_TO_REFRESH);
                                    headCurrentStatus=STATUS_REFRESH_TO_REFRESH;
                                }
                            }
                        }
                        //取消上拉加载更多
                        if(footCurrentStatus == STATUS_LOADMORE_LOADING){
                            if(null != footLayoutParams){
                                footLayoutParams.bottomMargin=-footer.getHeight();
                                footer.setLayoutParams(footLayoutParams);
                                footer.setStatus_To_Load(ZXFootView.STATUS_LOAD_DATA_END);
                                //取消任务
                                if(!refreshLoadDate.isCancelled()){
                                    if(refreshLoadDate.cancel(true)){
                                        refreshDataListener.RefreshCancel();
                                    }
                                }
                                footCurrentStatus=STATUS_REFRESH_END;
                            }
                        }
                    }else {
                        if(pos[1] >= zxAdapter.getItemCount()-2){
                            if(footCurrentStatus != STATUS_LOADMORE_LOADING){
                                if(null == footLayoutParams){
                                    footLayoutParams= (MarginLayoutParams) footer.getLayoutParams();
                                }
                                if(null != footLayoutParams){
                                    int lm=(-distance/2)-footer.getHeight();
                                    footLayoutParams.bottomMargin=lm;
                                    footer.setLayoutParams(footLayoutParams);
                                    footCurrentStatus=STATUS_LOADMORE_UP_LOAD;
                                }
                                if(Math.abs(distance) >= SCROLL_VLUES){
                                    footer.setStatus_To_Load(ZXFootView.STATUS_LOADMORE_TO_LOAD);
                                    footCurrentStatus=STATUS_LOADMORE_TO_LOAD;
                                }
                            }

                        }
                        //取消下拉刷新
                        if(headCurrentStatus == STATUS_REFRESHING){
                            if(null != headLayoutParams){
                                headLayoutParams.topMargin=-header.getHeight();
                                header.setLayoutParams(headLayoutParams);
                                header.setStatus_To_Refresh(ZXHeadView.STATUS_REFRESH_DATA_END);
                                //取消任务,true:已经取消任务，false,没有取消任务
                                if(!refreshUpData.isCancelled()){
                                    if(refreshUpData.cancel(true)){
                                        refreshDataListener.RefreshCancel();
                                    }
                                }
                                headCurrentStatus=STATUS_REFRESH_END;
                            }
                        }
                    }
                }
                break;
                //抬起手指的时候
            case MotionEvent.ACTION_UP:
                if(headCurrentStatus == STATUS_PULL_TO_REFRESH){
                    //下拉刷新未到达阈值
                    headLayoutParams.topMargin=-header.getHeight();
                    header.setLayoutParams(headLayoutParams);
                }
                if(footCurrentStatus == STATUS_LOADMORE_UP_LOAD){
                    //要加载更多 ,隐藏尾部
                    if(null != footLayoutParams){
                        footLayoutParams= (MarginLayoutParams) footer.getLayoutParams();
                        footLayoutParams.bottomMargin=-footer.getHeight();
                        footer.setLayoutParams(footLayoutParams);
                        footer.setStatus_To_Load(ZXFootView.STATUS_LOADMORE_TO_UP);
                        footCurrentStatus=STATUS_LOADMORE_END;
                    }

                }
                //下拉刷新到达阈值，显示释放以刷新
                if(headCurrentStatus == STATUS_REFRESH_TO_REFRESH){
                    //如果松开手指时是下拉状态
                    headLayoutParams.topMargin=0;
                    header.setLayoutParams(headLayoutParams);
                    header.setStatus_To_Refresh(ZXHeadView.STATUS_REFRESH_REFRESHING);
                    headCurrentStatus=STATUS_REFRESHING;
                    if(null == refreshUpData){
                        refreshUpData=new RefreshUpData();
                    }
                    AsyncTask.Status status=refreshUpData.getStatus();
                    //如果任务栈已经开始执行或者还没有执行，则执行
                    if(status== AsyncTask.Status.PENDING){
                        refreshUpData.execute();
                    }
                    if(status == AsyncTask.Status.FINISHED){
                        refreshUpData=null;
                        refreshUpData=new RefreshUpData();
                        refreshUpData.execute();
                    }

                }

                if(footCurrentStatus == STATUS_LOADMORE_TO_LOAD){
                    //当处于STATUS_LOADMORE_TO_LOAD停留bottomMargin的值，加载完成回调加载完成状态
                    if(null != footLayoutParams){
                        footer.setStatus_To_Load(ZXFootView.STATUS_LOADMORE_LOADING);
                        footLayoutParams= (MarginLayoutParams) footer.getLayoutParams();
                        footLayoutParams.bottomMargin=0;
                        footer.setLayoutParams(footLayoutParams);
                        footCurrentStatus=STATUS_LOADMORE_LOADING;
                        if(null == refreshLoadDate){
                            refreshLoadDate=new RefreshLoadDate();
                        }
                        AsyncTask.Status status=refreshLoadDate.getStatus();
                        //如果任务栈已经开始执行或者还没有执行，则执行
                        if(status== AsyncTask.Status.PENDING){
                            refreshLoadDate.execute();
                        }
                        if(status == AsyncTask.Status.FINISHED){
                            //因为AsyncTask方法一个任务只能执行一次，如果任务已经执行
                            refreshLoadDate=null;
                            refreshLoadDate=new RefreshLoadDate();
                            refreshLoadDate.execute();
                        }
                    }
                }
                break;
        }

        return false;
    }



    //这里的pos[0]和pos[1]是显示在屏幕(可视视图)的第一个item和最后一个item的位置，不是列表的
    private int[] getVisiblePos(){
        int[] pos=new int[2];
        LayoutManager layoutManager=getLayoutManager();
        LinearLayoutManager linearLayoutManager= (LinearLayoutManager) layoutManager;
        pos[0]=linearLayoutManager.findFirstVisibleItemPosition();
        pos[1]=linearLayoutManager.findLastVisibleItemPosition();
        return pos;
    }

    public void setRefreshUpData(RefreshDataListener refreshDataListener){
        this.refreshDataListener=refreshDataListener;
    }

    /**
     * 上拉加载更多工作线程
     * 用AsyncTask封装加载工作的好处是，数据加载完成不用回调隐藏尾布局
     */

    class RefreshLoadDate extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(null != refreshDataListener){
                refreshDataListener.RefreshLoadData();
            }
            return null;
        }

        //请求完回调加载结束
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(null == footLayoutParams){
                footLayoutParams= (MarginLayoutParams) footer.getLayoutParams();
            }else {
                footLayoutParams.bottomMargin=-footer.getHeight();
                footer.setLayoutParams(footLayoutParams);
                footer.setStatus_To_Load(ZXFootView.STATUS_LOAD_DATA_END);
                refreshDataListener.RefreshDataComplete();
                footCurrentStatus=STATUS_LOADMORE_END;

            }
        }
    }

    class RefreshUpData extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(null != refreshDataListener){
                refreshDataListener.RefreshUpData();
            }
            return null;
        }

        //请求完回调刷新结束
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(null == headLayoutParams){
                headLayoutParams= (MarginLayoutParams) header.getLayoutParams();
            }else {
                headLayoutParams.topMargin=-header.getHeight();
                header.setLayoutParams(headLayoutParams);
                header.setStatus_To_Refresh(ZXHeadView.STATUS_REFRESH_DATA_END);
                refreshDataListener.RefreshDataComplete();
                headCurrentStatus=STATUS_REFRESH_END;
            }
        }
    }


    class ZXAdapter extends Adapter{

        //刷新
        private static final int REFRESH=1;
        //加载更多
        private static final int LOADMORE=2;
        //正常内容
        private static final int NORMAL=3;

        @Override
        public int getItemViewType(int position) {
            if(isRefresh(position)){
                return REFRESH;
            }else if(isLoadMore(position)){
                return LOADMORE;
            }else {
                return NORMAL;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder=null;
            switch (viewType){
                case REFRESH:
                    holder=new WrapViewHolder(mHeaderView.get(0));
                    break;
                case LOADMORE:
                    holder=new WrapViewHolder(mFooterView.get(0));
                    break;
                case NORMAL:
                    holder=mAdapter.onCreateViewHolder(parent,viewType);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //如果是刷新或者加载更多就返回，否则调用调用者的
            if(isRefresh(position)||isLoadMore(position)){
                return;
            }
            mAdapter.onBindViewHolder(holder,realPosition(position));
        }

        @Override
        public int getItemCount() {
            return mAdapter.getItemCount()+mHeaderView.size()+mFooterView.size();
        }



        //是否刷新
        private boolean isRefresh(int position){
            return position==0;
        }
        //是否加载更多
        private boolean isLoadMore(int position){
            return position==getItemCount()-1;
        }

        //根据下标，获取用户设置的真实的下标
        private int realPosition(int position){
            return position - mHeaderView.size();
        }

        class WrapViewHolder extends RecyclerView.ViewHolder{

            public WrapViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 定义刷新加载更多接口
     */

    public interface RefreshDataListener{

        //下拉刷新
        void RefreshUpData();

        //上拉加载更多
        void RefreshLoadData();

        //上拉加载，下拉刷新完成是结束刷新状态
        void RefreshDataComplete();

        //取消加载更多和上拉刷新状态
        void RefreshCancel();
    }

}
