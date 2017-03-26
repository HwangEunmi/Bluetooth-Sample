package com.theory.emhwang.bluetoothproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.theory.emhwang.bluetoothproject.R;
import com.theory.emhwang.bluetoothproject.common.BleHandler;
import com.theory.emhwang.bluetoothproject.common.BluetoothHelper;
import com.theory.emhwang.bluetoothproject.common.Constants;

import static com.theory.emhwang.bluetoothproject.activity.DeviceListActivity.EXTRA_DEVICE_ADDRESS;

public class MainActivity extends Activity {

    /**
     * Service 선언
     */
    private BluetoothHelper mBTService;

    /**
     * BluetoothService로부터 메시지를 받을 핸들러
     */
    private Handler mHandler;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new BleHandler();

        /**
         * 디바이스가 Bluetooth를 지원하는지 확인(Bluetooth 통신을 사용하기 전에)
         */
        if (mBTService == null) {
            mBTService = new BluetoothHelper(MainActivity.this, mHandler);
        }

        if (mBTService.getDeviceState()) { // true일 경우 블루투스 활성화 요청
            mBTService.enableBluetooth();
        }

        setIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT: // Bluetooth 꺼져있는 경우에만 다이얼로그 뜸
                if (resultCode == RESULT_OK) { // 확인 눌렀을때
                    Toast.makeText(MainActivity.this, "블루투스를 활성화하였습니다", Toast.LENGTH_SHORT).show();

                } else if (resultCode == RESULT_CANCELED) { // 취소 눌렀을때
                    Toast.makeText(MainActivity.this, "블루투스를 활성화 할 수 없습니다", Toast.LENGTH_SHORT).show();
                }
                break;

            case Constants.REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    mBTService.getDeviceInfo(data);

                } else if (resultCode == RESULT_CANCELED) {

                }
                break;
        }
    }

    private void setIntent() {

        /**
         * Bluetooth On
         */
        Button on = (Button)findViewById(R.id.btn_on);
        on.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
                mBTService.getBTAdapter().enable();
            }
        });
        /**
         * Bluetooth OFF
         */
        Button off = (Button)findViewById(R.id.btn_off);
        off.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                mBTService.getBTAdapter().disable();
            }
        });

        /**
         * Bluetooth 기기 검색 (BLE가 활성화 되어있는 상태에서)
         */
        Button search = (Button)findViewById(R.id.btn_search);
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mBTService.getBTAdapter().isEnabled()) { // 단말의 Bluetooth 상태가 on인 경우
                    mIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                    startActivityForResult(mIntent, Constants.REQUEST_CONNECT_DEVICE);

                } else {
                    Toast.makeText(MainActivity.this, "먼저 블루투스를 켜주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /**
         * 페어링 된 기기 목록 불러옴 (페어링된 상태 : 단말과 이미 한번 이상 연결된 기기)
         */
        Button list = (Button)findViewById(R.id.btn_call_list);
        list.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIntent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(mIntent);
            }
        });

        /**
         * Bluetooth 설정화면으로 이동
         */
        Button setting = (Button)findViewById(R.id.btn_setting);
        setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(mIntent);
            }
        });

        /**
         * Bluetooth 연결
         */
        Button connect = (Button)findViewById(R.id.btn_connect);
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mIntent = new Intent(MainActivity.this, ConnectActivity.class);
                startActivity(mIntent);
            }
        });

    }
}
