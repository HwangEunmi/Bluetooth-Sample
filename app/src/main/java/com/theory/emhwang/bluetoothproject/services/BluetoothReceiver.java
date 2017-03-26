package com.theory.emhwang.bluetoothproject.services;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theory.emhwang.bluetoothproject.activity.DeviceListActivity;
import com.theory.emhwang.bluetoothproject.common.Constants;

/**
 * Created by hwangem on 2017-03-23.
 */

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction(); // 어떤 방송이 수신되었는지 알아보기

        // 검색결과 기기를 찾았을때
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // BluetoothDevice 객체를 인텐트로부터 얻어옴
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.d("TEST", "device: " + device);
            // 이미 페어링 된 장비라면 무시
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                // 새로운 기기 검색 성공시
                Intent bleIntent = new Intent(context, DeviceListActivity.class);
                bleIntent.putExtra(Constants.NEW_DEVICE, Constants.RESULT_CODE.RESULT_TRUE);
                bleIntent.putExtra(Constants.NEW_DEVICE_INFO_NAME, device.getName());
                bleIntent.putExtra(Constants.NEW_DEVICE_INFO_ADDRESS, device.getAddress());
            }
        }
    }
}
