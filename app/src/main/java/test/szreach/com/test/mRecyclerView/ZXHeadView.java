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
 * RecyclerView的头布局
 */
public class ZXHeadView extends RelativeLayout {

    /**
     * 下拉刷新有几种状态，下拉刷新，释放以刷新，正在刷新加载中
     */

    private ProgressBar progressBar;
    private TextView hintTxt;

    //下拉刷新
    public static final int STATUS_REFRESH_PULL_RESHRESH=1;
    //释放以刷新
    public static final int STATUS_REFRESH_TO_REFRESH=2;
    //正在刷新加载中
    public static final int STATUS_REFRESH_REFRESHING=3;
    //刷新完成
    public static final int STATUS_REFRESH_DATA_END=4;

    public ZXHeadView(Context context) {
        this(context,null);
    }

    public ZXHeadView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ZXHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view= LayoutInflater.from(context).inflate(R.layout.refresh_header_layout,null,true);
        progressBar=view.findViewById(R.id.head_progress_bar);
        hintTxt=view.findViewById(R.id.head_hint_txt);
        addView(view);
        progressBar.setVisibility(GONE);
        hintTxt.setText("下拉刷新");

    }

    public void setStatus_To_Refresh(int status){
        if(status == STATUS_REFRESH_PULL_RESHRESH){
            progressBar.setVisibility(GONE);
            hintTxt.setText("下拉刷新");
        }else if(status == STATUS_REFRESH_TO_REFRESH){
            progressBar.setVisibility(GONE);
            hintTxt.setText("释放以刷新");
        }else if(status == STATUS_REFRESH_REFRESHING){
            progressBar.setVisibility(VISIBLE);
            hintTxt.setText("正在刷新中");
        }else if(status == STATUS_REFRESH_DATA_END){
            progressBar.setVisibility(VISIBLE);
            hintTxt.setText("下拉刷新");
        }
    }
}
