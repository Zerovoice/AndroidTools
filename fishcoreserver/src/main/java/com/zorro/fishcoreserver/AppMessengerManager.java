package com.zorro.fishcoreserver;

/*
 *
 * Title: .
 * Description: .
 * Created by Zorro(zeroapp@126.com) on 2018/6/27.
 */

import android.os.Messenger;

import java.util.HashMap;

public class AppMessengerManager {
    private static HashMap<String, Messenger> appMessengerManager = null;

    private static synchronized HashMap<String, Messenger> get(){
        if(appMessengerManager == null){
            appMessengerManager = new HashMap<>();
        }
        return appMessengerManager;
    }
    public static Messenger getMessenger(String key){
        if(get().containsKey(key)){
            return get().get(key);
        }else{
            return null;
        }
    }

    public static void addMessenger(String key, Messenger messenger){
        get().put(key, messenger);
    }
}

