package test.szreach.com.test.mRecyclerView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by ZX on 2018/10/19
 * 定义下拉刷新，上拉加载更多的基类
 */
public abstract class RecyclerViewStatus extends FrameLayout{

    public RecyclerViewStatus(@NonNull Context context) {
        this(context,null);
    }

    public RecyclerViewStatus(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecyclerViewStatus(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
}
