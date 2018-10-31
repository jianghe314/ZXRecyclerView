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
    }
}
