package test.szreach.com.test;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

public class NoHttpUtils {
    private static NoHttpUtils instence;
    private RequestQueue requestQueue;

    public NoHttpUtils() {
        this.requestQueue = NoHttp.newRequestQueue(4);
    }

    public static NoHttpUtils getInstence(){
        if(instence==null){
            synchronized (NoHttpUtils.class){
                if(instence==null){
                    instence=new NoHttpUtils();
                }
            }
        }
        return instence;
    }

    public <T> void add(int what, Request<T> request, OnResponseListener listener){
        requestQueue.add(what,request,listener);
    }

    //取消请求
    public void cancelBySign(Object sign){
        requestQueue.cancelBySign(sign);
    }

    //取消所有请求
    public void cancelAll(){
        requestQueue.cancelAll();
    }


}
