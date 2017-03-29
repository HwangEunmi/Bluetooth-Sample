package com.theory.emhwang.bluetoothproject.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.theory.emhwang.bluetoothproject.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class BLESearchActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static int BLUETOOTH_STATE_UNKNOWN = -1;

    ListView listDevicesFound;
    Button btnSandDevice;
    TextView stateBluetooth;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<BluetoothDevice> btDeviceList = new ArrayList<>();
    StringBuilder stringBuilder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면의 타이틀바를 프로그레스바로 만들어 작업 진행을 알림
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_setting);

        btnSandDevice = (Button) findViewById(R.id.scandevice);
        stateBluetooth = (TextView) findViewById(R.id.bluetoothstate);

        listDevicesFound = (ListView) findViewById(R.id.devicesfound);
        btArrayAdapter = new ArrayAdapter<String>(BLESearchActivity.this,
                android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(btArrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            stateBluetooth.setText("단말기는 블루투스를 지원하지 않습니다.");
            return;
        }

        if (bluetoothAdapter.isEnabled()) {
            // 블루투스가 활성화되어 있다면, 현재 검색을 진행중인지 확인한다.
            // 다른 어플리케이션에서 검색 작업을 수행할 수 있다.
            if (bluetoothAdapter.isDiscovering()) {
                stateBluetooth.setText("Bluetooth is currently in device discovery process.");

            } else { // 블루투스를 검색할 수 있도록 검색 버튼을 활성화한다.
                stateBluetooth.setText("Bluetooth is Enabled.");
                btnSandDevice.setEnabled(true);
            }
        } else { // 블루투스를 활성화시킨다.
            stateBluetooth.setText("Bluetooth is NOT Enabled!");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        btnSandDevice.setOnClickListener(ScanDevice);
//        btnSandDevice.setOnClickListener(pairedDevice);

        // 액션별로 수신할 브로드캐스트리시버를 등록한다.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(BluetoothStateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        // 액티비티가 종료하면 브로드캐스트리시버도 등록을 취소한다.
        unregisterReceiver(BluetoothStateReceiver);
    }

    private final Button.OnClickListener ScanDevice = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            // 현재 블루투스를 검색중이라면 추가 검색을 취소한다.
            if (bluetoothAdapter.isDiscovering())
                return;

            btArrayAdapter.clear();
            // 화면의 타이틀바에 스핀휠을 사용하여 작업이 진행중이란 사실을 알린다
            setProgressBarIndeterminateVisibility(true);
            bluetoothAdapter.startDiscovery();
            /*
            if(!bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.startDiscovery();
            }
            */
        }
    };

    private final Button.OnClickListener pairedDevice = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            getBondedDevices();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(BLESearchActivity.this, "블루투스가 활성화되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    // 사용자가 활성화 요청을 취소하였거나 활성화 작업중 예외가 발생했다.
                    Toast.makeText(BLESearchActivity.this, "활성화 요청이 취소되었거나 예외가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private final BroadcastReceiver BluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 주변에서 활성화되어있는 블루투스 디바이스를 발견하였다.
                // 어댑터를 사용하여 블루투스 디바이스를 화면에 추가한다.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btArrayAdapter.notifyDataSetChanged();
                btDeviceList.add(device);
                return;
            } else {
                // 나머지는 BluetoothAdapter.ACTION_STATE_CHANGED 액션이다.
                // 상태변화를 감시하고 그 내용을 토스트로 출력한다.
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BLUETOOTH_STATE_UNKNOWN);

                String message = null;

                // 블루투스 상태 변화에 따라 화면에 토스트를 뿌려준다.
                switch (state) {
                    case BluetoothAdapter.STATE_CONNECTED:
                        message = "STATE_CONNECTED";
                        break;

                    case BluetoothAdapter.STATE_CONNECTING:
                        message = "STATE_CONNECTING";
                        break;

                    case BluetoothAdapter.STATE_DISCONNECTED:
                        message = "STATE_DISCONNECTED";
                        break;

                    case BluetoothAdapter.STATE_DISCONNECTING:
                        message = "STATE_DISCONNECTING";
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        message = "STATE_OFF";
                        break;

                    case BluetoothAdapter.STATE_ON:
                        message = "STATE_ON";
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        message = "STATE_TURNING_OFF";
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        message = "STATE_TURNING_ON";
                        break;

                    default:
                }
                Toast.makeText(BLESearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            // TODO : UUID 리시버에서 말고 기기선택시 UUID알아오는것 추가
            // TODO : 리시버 -> 스캐너로 변경(버전별 두가지 모두)
            // TODO : UUID 단말 한개에서 왜 여러개 나오지?
            // TODO : 단말과 단말 연결하기
            // ACTION_UUID 액션이 브로드캐스트되었다.
            // 기기의 UUID를 알아냄
            if (BluetoothDevice.ACTION_UUID.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                for (int i = 0; i < uuidExtra.length; i++) {
//                    Toast.makeText(BLESearchActivity.this, "device: " + device.getName() +
//                            " , UUID: " + uuidExtra[i].toString(), Toast.LENGTH_SHORT).show();
                    btArrayAdapter.add(device.getName() + "\n" + device.getAddress() + " uuid: " + uuidExtra[i].toString());
                    stringBuilder.append(uuidExtra[i].toString()).append("  +  ");
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                setProgressBarIndeterminateVisibility(true);
                Toast.makeText(BLESearchActivity.this, "Discovery ing...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 검색이 종료되면, 종료되었음을 알린다. 그에 따라 타이틀바에 진행중인
                // 상태를 나타내는 스핀휠을 종료한다.
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(BLESearchActivity.this, "검색 완료", Toast.LENGTH_SHORT).show();

                // 검색이 완료되었다면, 검색하여 수집한 블루투스 디바이스를 사용하여
                // 외부 블루투스 디바이스로부터 UUID를 요청한다.
                Iterator<BluetoothDevice> itr = btDeviceList.iterator();
                while (itr.hasNext()) {
                    // Get Services for paired devices
                    BluetoothDevice device = itr.next();
                    Log.d("TEST", "Getting Services for " + device.getName() + ", " + device);
                    if (!device.fetchUuidsWithSdp()) {
                        Log.d("TEST", "Failed for " + device.getName());
                    }
                }
            }
        }
    };

    // 단말과 페어링된 모든 디바이스를 반환받는다
    private void getBondedDevices() {
        btArrayAdapter.clear();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // pairedDevices.size()이 0이 아니면, 이미 페어링되어 있는 블루투스 디바이스가 존재
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                // ArrayAdapter 객체에 블루투스 디바이스의 MAC 주소를 추가한다
                // 블루투스 연결을 시작하기 위해, 최소한의 필요정보는
                // 디바이스 이름과 디바이스의 MAC 주소
                btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            // 만약 기기가 없을 경우
            String noDevies = "기기가 존재하지 않습니다";
            btArrayAdapter.add(noDevies);
        }
    }
}
