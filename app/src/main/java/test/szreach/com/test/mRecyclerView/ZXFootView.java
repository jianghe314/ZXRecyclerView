package test.szreach.com.test.mRecyclerView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import test.szreach.com.test.R;

/**
 * Created by ZX on 2018/10/30
 * RecyclerView 的头布局
 */
public class ZXFootView extends RelativeLayout{

    /**
     * 有四种状态，上拉加载更多，释放以加载，正在加载中，没有更多啦
     */
    private ProgressBar progressBar;
    private TextView hintTxt;

    //上拉加载更多
    public static final int STATUS_LOADMORE_TO_UP=1;
    //释放手指以加载
    public static final int STATUS_LOADMORE_TO_LOAD=2;
    //正在加载中
    public static final int STATUS_LOADMORE_LOADING=3;
    //没有更多数据了
    public static final int STATUS_LOAD_NO_DATA=4;
    //载入数据完成
    public static final int STATUS_LOAD_DATA_END=5;

    public ZXFootView(Context context) {
        this(context,null);
    }

    public ZXFootView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZXFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view= LayoutInflater.from(context).inflate(R.layout.refresh_footer_layout,null,true);
        progressBar=view.findViewById(R.id.foot_progress_bar);
        hintTxt=view.findViewById(R.id.foot_hint_txt);
        progressBar.setVisibility(GONE);
        hintTxt.setText("上拉加载更多");
        addView(view);

    }

    //释放以加载
    public void setStatus_To_Load(int status){
        if(status == STATUS_LOADMORE_TO_UP){
            progressBar.setVisibility(GONE);
            hintTxt.setText("上拉加载更多");
        }else if(status == STATUS_LOADMORE_LOADING){
            progressBar.setVisibility(VISIBLE);
            hintTxt.setText("加载更多中");
        }else if(status == STATUS_LOADMORE_TO_LOAD){
            progressBar.setVisibility(GONE);
            hintTxt.setText("释放以加载");
        }else if(status == STATUS_LOAD_NO_DATA){
            progressBar.setVisibility(GONE);
            hintTxt.setText("没有更多啦");
        }else if(status == STATUS_LOAD_DATA_END){
            progressBar.setVisibility(GONE);
            hintTxt.setText("上拉加载更多");
        }

    }
}
