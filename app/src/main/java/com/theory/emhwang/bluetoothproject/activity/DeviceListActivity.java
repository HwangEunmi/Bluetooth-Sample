package com.theory.emhwang.bluetoothproject.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.theory.emhwang.bluetoothproject.R;
import com.theory.emhwang.bluetoothproject.adapter.DeviceListAdapter;
import com.theory.emhwang.bluetoothproject.common.Constants;

import java.util.Set;

/**
 * 장비를 찾기위한 다이얼로그 창
 */
public class DeviceListActivity extends Activity {

    /**
     * 연결할 기기의 MAC 어드레스
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * ListView
     */
    private ListView mLView;

    /**
     * 새로 발견한 기기
     */
    //    private DeviceListAdapter mNewDeviceAdapter;
    private ArrayAdapter<String> mNewDeviceAdapter;

    /**
     * 블루투스 Adapter
     */
    private BluetoothAdapter mBLEAdapter;

    private IntentFilter mIntentFilter;

    /**
     * 기기 스캔 버튼
     */
    private Button scanButton;

    /**
     * 페어링된 기기 스캔 버튼
     */
    private Button pairedListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // 사용자가 나갔을때(again)
        setResult(Activity.RESULT_CANCELED);
        setIntentFilter();

        // 페어링 된 기기목록 가져오기
        pairedListButton = (Button) findViewById(R.id.btn_paired_scan);
        pairedListButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getPairedDeviceList(v);
            }
        });

        // 새로운 기기 스캔
        scanButton = (Button) findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 타이틀바 제목 변경
                setTitle("디바이스 검색중 ...");
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        mLView = (ListView) findViewById(R.id.lv_list);
        mNewDeviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //        mNewDeviceAdapter = new DeviceListAdapter();
        mLView.setAdapter(mNewDeviceAdapter);

//        setIntentFilter();

        mBLEAdapter = BluetoothAdapter.getDefaultAdapter();

        /**
         * 기기 연결 리스너
         */
        mLView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 해당 기기에 연결하기 위해 현재 행하고 있는 기기검색을 취소
                mBLEAdapter.cancelDiscovery();

                Log.d("TEST", "deviceInfo : " + ((TextView) view).getContext().toString());
                // 기기의 Mac 어드레스를 얻어옴 (마지막 17글자)
                String deviceInfo = ((TextView) view).getContext().toString();
                String macAddress = deviceInfo.substring(deviceInfo.length() - 17);
                Log.d("TEST", "macAddress: " + macAddress);

                // 인텐트 객체에 연결할 장치의 Mac 어드레스 정보를 넣어줌
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_DEVICE_ADDRESS, macAddress);
                                setResult(Activity.RESULT_OK, intent);

                                finish();
            }
        });
        // TODO : 블루투스 활성화 상태인지 체크 추가
    }

    /**
     * 기기 검색하는 메소드
     */
    private void doDiscovery() {
        stopFindDiscovery();

        mBLEAdapter.startDiscovery(); // 검색 시작 (결과는 BroadcastReceiver로 받을 수 있음)
    }

    /**
     * 기기 검색 취소하는 메소드
     */
    private void stopFindDiscovery() {
        if (mBLEAdapter.isDiscovering()) { // 이미 검색하고 있었다면
            mBLEAdapter.cancelDiscovery(); // 취소
            // 리시버를 등록해제
            unregisterReceiver(mReceiver);
        }
    }

    /**
     * IntentFilter 메소드
     */
    private void setIntentFilter() {
        // 기기가 발견되었을때 방송을 수신할 BroadcastReceiver 등록
        mIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, mIntentFilter);

        // 기기 발견이 끝났을때 방송을 수신할 BroadcastReceiver 등록
        mIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, mIntentFilter);
    }

    /**
     * 페어링된 디바이스 목록 get 메소드
     */
    private void getPairedDeviceList(View view) {
        setTitle("페어링된 기기 목록");
        view.setVisibility(View.GONE);

        // 페어링 된 디바이스를 얻어옴
        Set<BluetoothDevice> pairedDevices = mBLEAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            mNewDeviceAdapter.clear();
            for (BluetoothDevice device : pairedDevices) {
                mNewDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.d("TEST", "device address: "+device.getAddress());
            }

        } else {
            mNewDeviceAdapter.clear();
            mNewDeviceAdapter.add("No Device!!");
        }
    }

    /**
     * 다른 기기에게 자신을 검색 허용
     */
    private void setDiscoverable() {
        // 현재 검색 허용상태라면 함수 탈출
        if(mBLEAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            return;
        }

        // 다른 기기에게 자신을 검색할 수 있도록 허용
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // 300초 동안
        startActivity(intent);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction(); // 어떤 방송이 수신되었는지 알아보기
            Log.d("TEST", "action: "+action);
            mNewDeviceAdapter.clear();
            scanButton.setVisibility(View.VISIBLE);
            setTitle("검색된 기기 목록");
            // 검색결과 기기를 찾았을때
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // BluetoothDevice 객체를 인텐트로부터 얻어옴
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("TEST", "device: " + device);

                // 이미 페어링 된 장비가 아니라면 (새로운 기기 검색 성공시)
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
                }

                // 검색결과 발견된 기기가 없으면
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (mNewDeviceAdapter.getCount() == 0) {
                    mNewDeviceAdapter.add("No Device!!");
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 작업 취소
        if (mBLEAdapter != null) {
            mBLEAdapter.cancelDiscovery();
        }

        // 리시버 해제
        unregisterReceiver(mReceiver);
    }
}
