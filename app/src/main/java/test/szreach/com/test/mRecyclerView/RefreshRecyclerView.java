package test.szreach.com.test.mRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ZX on 2018/10/12
 */
public class RefreshRecyclerView extends RecyclerView implements View.OnTouchListener{

    private String TAG="LOG";
    /** 功能模式-关闭 */
    public static final String REFRESH_MODE_NONE = "refresh_mode_none";
    /** 功能模式-全部开启 */
    public static final String REFRESH_MODE_BOTH = "refresh_mode_both";
    /** 功能模式-下拉刷新 */
    public static final String REFRESH_MODE_REFRESH = "refresh_mode_refresh";
    /** 功能模式-上拉加载更多 */
    public static final String REFRESH_MODE_LOADMORE = "refresh_mode_loadmore";


    //头布局集合
    private ArrayList<View> mHeaders;
    //脚布局集合
    private ArrayList<View> mFooter;
    //使用者的Adapter
    private Adapter mAdapter;
    //封装的Adapter
    private InsideAdapter insideAdapter;
    //标记 刷新类型
    private String mode;



    public RefreshRecyclerView(@NonNull Context context) {
        this(context,null);
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
        setOnTouchListener(this);
    }

    /**初始化相关操作*/
    private void init() {
        if(mHeaders==null){
            mHeaders=new ArrayList<>();
        }
        if(mFooter==null){
            mFooter=new ArrayList<>();
        }


    }


    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter=adapter;
        insideAdapter=new InsideAdapter();
        super.setAdapter(insideAdapter);
        
        //注册观察者
       // mAdapter.registerAdapterDataObserver();
    }

    /**触摸事件监听，处理下拉刷新，上拉加载更多*/
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int[] pos=getVisiblePos();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG,"-->ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG,"-->ACTION_UP");
                break;
               default:
                   break;
        }
        return false;
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
    }

    /**获取RecyclerView当前显示的第一个控件和最后一个控件的位置*/
    private int[] getVisiblePos(){
        int[] pos=new int[2];
        LayoutManager layoutManager=getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager){
            LinearLayoutManager linearLayoutManager= (LinearLayoutManager) layoutManager;
            //获取布局管理器中的第一个Item和获取最后一个Item的位置
            pos[0]=linearLayoutManager.findFirstVisibleItemPosition();
            pos[1]=linearLayoutManager.findLastVisibleItemPosition();
        }
        return pos;
    }






    /**对外提供添加头布局的方法*/
    public void addHeaderView(View headView){
        mHeaders.add(headView);
    }

    /**对外提供添加脚布局的方法*/
    public void addFooterView(View footView){
        mHeaders.add(footView);
    }


    //添加头布局和尾布局是通过adapter的getItemType的类型判断来来添加相关的布局实现的。
    class InsideAdapter extends Adapter{

        /**布局类型，刷新布局*/
        private static final int VIEW_TYPE_REFRESH=1;
        /**布局类型，头布局*/
        private static final int VIEW_TYPE_HEAD=2;
        /**布局类型，正常布局*/
        private static final int VIEW_TYPE_NORMAL=3;
        /**布局类型，加载更多布局*/
        private static final int VIEW_TYPE_LOADMORE=4;
        /**布局类型，脚布局*/
        private static final int VIEW_TYPE_FOOT=5;

        @Override
        public int getItemViewType(int position) {
            if(isRefresh(position)){
                return VIEW_TYPE_REFRESH;
            }else if(isHeader(position)){
                return VIEW_TYPE_HEAD;
            }else if(isLoadMore(position)){
                return VIEW_TYPE_LOADMORE;
            }else if(isFooter(position)){
                return VIEW_TYPE_FOOT;
            }else {
                return VIEW_TYPE_NORMAL;
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            ViewHolder holder = null;
            switch (viewType){
                case VIEW_TYPE_REFRESH:
                    holder = new SimpleViewHolder(mHeaders.get(0));
                    break;
                case VIEW_TYPE_HEAD:
                    holder=new SimpleViewHolder(mHeaders.get(1));
                    break;
                case VIEW_TYPE_LOADMORE:
                    holder=new SimpleViewHolder(mFooter.get(0));
                    break;
                case VIEW_TYPE_FOOT:
                    holder=new SimpleViewHolder(mFooter.get(1));
                    break;
                case VIEW_TYPE_NORMAL:
                    holder= mAdapter.onCreateViewHolder(parent,viewType);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if(isRefresh(position) || isLoadMore(position) || isHeader(position) || isFooter(position)){
                return;
            }
            mAdapter.onBindViewHolder(holder,realPosition(position));
        }

        @Override
        public int getItemCount() {
            if(REFRESH_MODE_BOTH.equals(mode)){
                return mAdapter.getItemCount()+mHeaders.size()+mFooter.size();
            }else if(REFRESH_MODE_REFRESH.equals(mode)){
                return mAdapter.getItemCount()+mHeaders.size();
            }else if(REFRESH_MODE_LOADMORE.equals(mode)){
                return mAdapter.getItemCount()+mFooter.size();
            }else {
                return mAdapter.getItemCount()+mHeaders.size()+mFooter.size();
            }
        }

        //
        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            RecyclerView.LayoutManager manager=recyclerView.getLayoutManager();
            if(manager instanceof GridLayoutManager){
                final GridLayoutManager gridLayoutManager= (GridLayoutManager) manager;
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isRefresh(position) || isLoadMore(position)
                                || isHeader(position) || isFooter(position))
                                ? gridLayoutManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
            super.onViewAttachedToWindow(holder);

        }

        class SimpleViewHolder extends RecyclerView.ViewHolder{
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }


        //是否开启刷新
        private boolean canRefresh(){
            return true;
        }

        //是否刷新布局
        private boolean isRefresh(int position){
            //如果开启刷新并且当前位置为0，即第一个Item
            boolean refresh=canRefresh()&&position==0;
            return refresh;
        }
        //是否是头布局
        private boolean isHeader(int position){
            boolean head=canRefresh()?(position>=1&&(position < mHeaders.size()+1)) : (position >=0 && (position < mHeaders.size()));
            return head;
        }
        //是否加载更多
        private boolean isLoadMore(int position){
            boolean loadMore=canRefresh()&&(position == getItemCount()-1);
            return loadMore;
        }
        //是否是尾布局
        private boolean isFooter(int position){
            boolean isfooter=canRefresh()?((position >= getItemCount() - mFooter.size()-1)&&(position < getItemCount() -1)):((position >= getItemCount()-mFooter.size())&&(position < getItemCount()));
            return isfooter;
        }
        //根据下标，获取用户设置的真实的下标
        private int realPosition(int position){
            int realposition=canRefresh()?(position - mHeaders.size()-1):(position - mHeaders.size());
            return realposition;
        }
    }











}
