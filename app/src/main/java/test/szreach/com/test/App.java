package test.szreach.com.test;

import android.app.Application;

import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.cache.DBCacheStore;

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //配置nohttp链接超时和缓存到数据库
        InitializationConfig.Builder builder=InitializationConfig.newBuilder(this);
        builder.connectionTimeout(10*1000)
                .readTimeout(10*1000)
                .cacheStore(new DBCacheStore(this).setEnable(true)).build();
        InitializationConfig config= builder.build();
        NoHttp.initialize(config);


    }
}
