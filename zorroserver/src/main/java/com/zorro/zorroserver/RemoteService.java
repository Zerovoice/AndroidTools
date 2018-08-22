package com.zorro.zorroserver;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/6/27.
 */

public class RemoteService extends Service {
    // 用来处理客户端传过来的消息
    private static final String TAG = "Zorro-Server";
    private static final int FISH_MESSAGE_CODE = 0x0001;
    private static final int CHANGBA_MESSAGE_CODE = 0x0002;

    //serviceMessenger是Service自身的Messenger，其内部指向了ServiceHandler的实例
    //客户端可以通过IBinder构建Service端的Messenger，从而向Service发送消息，
    //并由ServiceHandler接收并处理来自于客户端的消息
    private Messenger serviceMessenger = new Messenger(new ServiceHandler());

    //RemoteService用ServiceHandler接收并处理来自于客户端的消息
    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "ServiceHandler -> handleMessage");
//            Messenger clientMessenger = AppMessengerManager.getMessenger(msg.what);
            if(msg.what == CHANGBA_MESSAGE_CODE){
                Bundle data = msg.getData();
                if(data != null){
                    String str = data.getString("msg");
                    Log.i(TAG, "RemoteService收到客户端如下信息: " + str);
                }
                //通过Message的replyTo获取到客户端自身的Messenger，
                //Service可以通过它向客户端发送消息
                //clientMessenger表示的是客户端的Messenger，可以通过来自于客户端的Message的replyTo属性获得，
                //其内部指向了客户端的ClientHandler实例，可以用clientMessenger向客户端发送消息
               Messenger clientMessenger = msg.replyTo;
                if(clientMessenger != null){
                    Log.i(TAG, "RemoteService向客户端回信");
                    Message msgToClient = Message.obtain();
                    msgToClient.what = FISH_MESSAGE_CODE;
                    //可以通过Bundle发送跨进程的信息
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", "你好，客户端，我是RemoteService");
                    msgToClient.setData(bundle);
                    try{
                        clientMessenger.send(msgToClient);
                    }catch (RemoteException e){
                        e.printStackTrace();
                        Log.e(TAG, "RemoteService向客户端发送信息失败: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "RemoteService -> onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "RemoteService -> onDestroy");
        super.onDestroy();
    }

       /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }
}
