package com.theory.emhwang.bluetoothproject.common;

/**
 * Created by hwangem on 2017-03-21.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.theory.emhwang.bluetoothproject.activity.DeviceListActivity;

/**
 * Bluetooth와 관련된 모든 작업을 처리할 클래스
 */
public class BluetoothHelper {

    private static final String TAG = "BluetoothHelper";

    /**
     * 연결, 페어링, 검색 등 Bluetooth 연동을 위한 기본 클래스
     */
    private BluetoothAdapter mBTAdapter;

    private Activity mActivity;

    private Handler mHandler;

    private Intent mIntent;

    public BluetoothHelper(Activity activity, Handler handler) {
        mActivity = activity;
        mHandler = handler;

        // BluetoothAdapter 얻기
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 블루투스 활성화여부 확인하는 메소드
     */
    public void enableBluetooth() {
        if (mBTAdapter.isEnabled()) { // 단말의 Bluetooth 상태가 on인 경우

        } else { // off인 경우
            mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // 활성화를 위한 퍼미션을 요구하는 다이얼로그 띄움
            mActivity.startActivityForResult(mIntent, Constants.REQUEST_ENABLE_BT);
        }
    }

    /**
     * 단말의 블루투스 지원여부 확인하는 메소드
     */
    public boolean getDeviceState() {
        if (this == null) { // Bluetooth를 지원하지않는 단말로 연동이 불가능
            Toast.makeText(mActivity, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            return false;

        } else {
            Toast.makeText(mActivity, "Bluetooth is available", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * 검색된 기기에 접속하는 메소드
     */
    public void getDeviceInfo(Intent data) {
        // 기기의 Mac주소 얻기
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // BluetoothDevice 객체 얻기
//        BluetoothDevice btDevice = mBTAdapter.getRemoteDevice(address);

        Log.d("TEST", "Device Address: " + address);
//        connect(btDevice);
    }

    public void connect(BluetoothDevice device) {

    }

    public BluetoothAdapter getBTAdapter() {
        return mBTAdapter;
    }
}
