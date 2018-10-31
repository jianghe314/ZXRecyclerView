package test.szreach.com.test.mRecyclerView;

/**
 * Created by ZX on 2018/10/12
 */
//定义RecyclerView刷新的一般方法
public interface RefreshRecyclerViewInterface {

    /**
     * 进入页面是否自动刷新
     * @param isAuto
     */
    void setAutoRefresh(boolean isAuto);

    /**
     * 是否刷新完成，隐藏head
     * @param isFinish
     */
    void setRefreshComplete(boolean isFinish);


}
