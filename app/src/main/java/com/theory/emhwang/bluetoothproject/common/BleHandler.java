package com.theory.emhwang.bluetoothproject.common;

import android.os.Handler;
import android.os.Message;

/**
 * Created by hwangem on 2017-03-24.
 */


/**
 * BluetoothService에서 처리된 과정은 Handler를 통해 여기로 전달됨 (메인 스레드와 서브 스레드간의 통신을 위한 Handler)
 */
public class BleHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
    }
}
