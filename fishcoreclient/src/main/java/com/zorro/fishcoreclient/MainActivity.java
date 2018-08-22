package com.zorro.fishcoreclient;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Zorro-Client";
    private TextView mTextMessage;
    private static final int FISH_MESSAGE_CODE = 0x0001;
    private static final int CHANGBA_MESSAGE_CODE = 0x0002;

    //用于启动RemoteService的Intent对应的action
    private final String SERVICE_ACTION = "com.zorro.action.RemoteService";

    //serviceMessenger表示的是Service端的Messenger，其内部指向了RemoteService的ServiceHandler实例
    //可以用serviceMessenger向RemoteService发送消息
    private Messenger serviceMessenger = null;

    //clientMessenger是客户端自身的Messenger，内部指向了ClientHandler的实例
    //RemoteService可以通过Message的replyTo得到clientMessenger，从而RemoteService可以向客户端发送消息，
    //并由ClientHandler接收并处理来自于Service的消息
    private Messenger clientMessenger = new Messenger(new ClientHandler());
    private LinkedList<Integer> list =  new LinkedList<>();

    //客户端用ClientHandler接收并处理来自于Service的消息
    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "ClientHandler -> handleMessage");
            if(msg.what == FISH_MESSAGE_CODE){
                Bundle data = msg.getData();
                if(data != null){
                    String str = data.getString("msg");
                    Log.i(TAG, "客户端收到Service的消息: " + str);
                }
            }
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //客户端与Service建立连接
            Log.i(TAG, "客户端 onServiceConnected");
            //我们可以通过从Service的onBind方法中返回的IBinder初始化一个指向Service端的Messenger
            serviceMessenger = new Messenger(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //客户端与Service失去连接
            serviceMessenger = null;
            Log.i(TAG, "客户端 onServiceDisconnected");
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Button bt1 = (Button)findViewById(R.id.button1);
        Button bt2 = (Button)findViewById(R.id.button2);
        Button bt3 = (Button)findViewById(R.id.button3);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                list.add(new Random().nextInt(100));
                Message msg = Message.obtain();
                msg.what = CHANGBA_MESSAGE_CODE;

                //此处跨进程Message通信不能将msg.obj设置为non-Parcelable的对象，应该使用Bundle
                //msg.obj = "你好，RemoteService，我是客户端";
                Bundle data = new Bundle();
                data.putString("msg", "你好，RemoteService，我是客户端");
                msg.setData(data);

                //需要将Message的replyTo设置为客户端的clientMessenger，
                //以便Service可以通过它向客户端发送消息
                msg.replyTo = clientMessenger;
                try {
                    Log.i(TAG, "客户端向service发送信息");
                    serviceMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(TAG, "客户端向service发送消息失败: " + e.getMessage());
                }
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //单击了unbindService按钮
                Log.i(TAG, "客户端调用unbindService方法");
                unbindService(conn);
            }
        });

    }
    private void insertSort(LinkedList<Integer> numbers) {
        int size = numbers.size();
        Integer temp = null;
        int j = 0;

        for (int i = 0; i < size; i++) {
            temp = numbers.get(i);
            //假如temp比前面的值小，则将前面的值后移
            for (j = i; j > 0 && temp< numbers.get(j - 1); j--) {
                numbers.add(j - 1,temp);
                numbers.remove(j + 1);
            }
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        }
    }
    private boolean hasContactsPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean canReadConnacts() {
        boolean has = false;
        try {
            Cursor cursor = this.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                has = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return has;
    }
    private void startService() {
        Intent intent = new Intent();
        intent.setAction(SERVICE_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        PackageManager pm = getPackageManager();
        //我们先通过一个隐式的Intent获取可能会被启动的Service的信息
        ResolveInfo info = pm.resolveService(intent, 0);
        if (info != null) {
            //如果ResolveInfo不为空，说明我们能通过上面隐式的Intent找到对应的Service
            //我们可以获取将要启动的Service的package信息以及类型
            String packageName = info.serviceInfo.packageName;
            String serviceNmae = info.serviceInfo.name;
            //然后我们需要将根据得到的Service的包名和类名，构建一个ComponentName
            //从而设置intent要启动的具体的组件信息，这样intent就从隐式变成了一个显式的intent
            //之所以大费周折将其从隐式转换为显式intent，是因为从Android 5.0 Lollipop开始，
            //Android不再支持通过通过隐式的intent启动Service，只能通过显式intent的方式启动Service
            //在Android 5.0 Lollipop之前的版本倒是可以通过隐式intent启动Service
            ComponentName componentName = new ComponentName(packageName, serviceNmae);
            intent.setComponent(componentName);
            try {
                Log.i(TAG, "客户端调用bindService方法");
                bindService(intent, conn, BIND_AUTO_CREATE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
