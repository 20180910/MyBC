package mybc.mybc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import top.wuhaojie.bthelper.BtHelperClient;

/**
 * Created by Administrator on 2018/6/7.
 */

public class ConnectActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private ListAdapter adapter;

    ListView lv_content;
    TextView tv_connect;
    EditText et_content;
    TextView tv_send;
    private String address;

    BluetoothDevice device;
    Handler mHandler;

    String uuid = "0000ffe1-0000-1000-8000-00805f9b34fb";   //SPP服务UUID号
    private BluetoothSocket socket;
    private BluetoothAdapter bluetoothAdapter;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BtHelperClient client;
    private BluetoothClient mClient;
    private String name;

    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;

    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_connect);
        mHandler=new Handler(getMainLooper());
        helper=new Helper(mContext);
        register();
        initView();
        initData();

        showDialog();

        if (helper.initialize() == false) {
            dismissDialog();
            showMsg("连接失败");
            disconnected();
        }
        mBluetoothDeviceAddress=address;
        boolean connect = helper.connect(address);
        if(!connect){
            dismissDialog();
            showMsg("连接失败");
            disconnected();
        }

//        connect(address);
    }
    private String mBluetoothDeviceAddress;
    String TAG=this.getClass().getSimpleName();
    private int mConnectionState = 0;


    private void initView() {
        address = getIntent().getStringExtra("address");
        name = getIntent().getStringExtra("name");
        Log.i("===", name + "==address=" + address);

        lv_content = (ListView) findViewById(R.id.lv_content);

        tv_connect = (TextView) findViewById(R.id.tv_connect);
        tv_connect.setOnClickListener(this);

        et_content = (EditText) findViewById(R.id.et_content);

        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_send.setOnClickListener(this);


        adapter = new ListAdapter(mContext);
        lv_content.setAdapter(adapter);
    }


    private void initData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_connect:
//                initBlueAdapter();
                break;
            case R.id.tv_send:
                /*if(socket==null||!socket.isConnected()){
                    showMsg("请先连接设备");
                    return;
                }*/
//                sendText();
                if(TextUtils.isEmpty(et_content.getText())){
                    showMsg("请输入数据");
                    return;
                }
                sendText2(et_content.getText().toString());
//                sendText2(convertStringToHex("AT+LED=1").toUpperCase());
                break;
        }
    }

    private void Log(String str) {
        Log.i("Log", str);
    }
    public String convertStringToHex(String str){
//把字符串转换成char数组
        char[] chars = str.toCharArray();
//新建一个字符串缓存类
        StringBuffer hex = new StringBuffer();
//循环每一个char
        for(int i = 0; i < chars.length; i++){
//把每一个char都转换成16进制的，然后添加到字符串缓存对象中
            hex.append(Integer.toHexString((int)chars[i]));
        }
//最后返回字符串就是16进制的字符串
        return hex.toString();
    }
    private void sendText2(final String msg) {
//        ManyBlue.blueWriteDataStr2Hex("0a0a01", "tag"); //例如 "0a0a01"
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                byte[] buff = null;
                try {
                    buff = msg.getBytes(Config.format);
                    System.out.println("buff len:" + buff.length);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                int[] sendDatalens = ConnectActivity.this.dataSeparate(buff.length);
                for (i = 0; i < sendDatalens[0]; i++) {
                    byte[] dataFor20 = new byte[20];
                    for (int j = 0; j < 20; j++) {
                        dataFor20[j] = buff[(i * 20) + j];
                    }
                    System.out.println("here1");
                    System.out.println("here1:" + new String(dataFor20));//dataFor20
                    if(ConnectActivity.target_chara==null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showMsg("没有发现服务,无法发送数据");
                            }
                        });
                        return;
                    }
                    ConnectActivity.target_chara.setValue(dataFor20);
                    helper.writeCharacteristic(ConnectActivity.target_chara);
                }
                if (sendDatalens[1] != 0) {
                    System.out.println("here2");
                    byte[] lastData = new byte[20];
                    for (i = 0; i < sendDatalens[1]; i++) {
                        lastData[i] = buff[(sendDatalens[0] * 20) + i];
                    }
                    String str = null;
                    try {
                        str = new String(lastData, 0, sendDatalens[1], Config.format);
                    } catch (UnsupportedEncodingException e2) {
                        e2.printStackTrace();
                    }
                    System.out.println("here2:" + str + "-------len:" + str.length());//lastData
//                    ConnectActivity.target_chara.setValue(new byte[]{0x41,0x54,0x2B,0x4C,0x45,0x44,0x3D,0x31});//convertStringToHex("AT+LED=1").toUpperCase().getBytes()
//                    ConnectActivity.target_chara.setValue(new byte[]{0x41,0x54,0x4C,0x45,0x44,0x3D,0x31});//ATLED=1转16进制: 41 54 4C 45 44 3D 31
                    if(ConnectActivity.target_chara==null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showMsg("没有发现服务,无法发送数据");
                            }
                        });
                        return;
                    }
                    ConnectActivity.target_chara.setValue(lastData);//ATLED=1转16进制: 41 54 4C 45 44 3D 31
                    helper.writeCharacteristic(ConnectActivity.target_chara);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter==null){
                                return;
                            }
                            ListBean bean=new ListBean();
                            bean.isSend=true;
                            bean.text=msg;
                            adapter.getList().add(bean);
                            adapter.notifyDataSetChanged();
                            et_content.setText(null);
                        }
                    });
                }
            }
        }).start();
    }

    public int[] dataSeparate(int len) {
        return new int[]{len / 20, len % 20};
    }
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.action_gatt_connected);
        intentFilter.addAction(Config.action_gatt_disconnected);
        intentFilter.addAction(Config.action_gatt_services_discovered);
        intentFilter.addAction(Config.action_data_available);

        registerReceiver(mGattUpdateReceiver, intentFilter);
    }

    public void unRegister() {
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
        if(helper!=null){
            helper.disconnect();
            helper.close();
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Config.action_gatt_connected.equals(action)) {
//                Ble_Activity.this.mConnected = true;
//                Ble_Activity.this.status = "connected";
//                Ble_Activity.this.updateConnectionState(Ble_Activity.this.status);
                System.out.println("BroadcastReceiver :device connected");
                dismissDialog();
                showMsg("连接成功");
            } else if (Config.action_gatt_disconnected.equals(action)) {
//                Ble_Activity.this.mConnected = false;
//                Ble_Activity.this.status = "disconnected";
//                Ble_Activity.this.updateConnectionState(Ble_Activity.this.status);
                dismissDialog();
                showMsg("连接失败");
                disconnected();
                System.out.println("BroadcastReceiver :device disconnected");
            } else if (Config.action_gatt_services_discovered.equals(action)) {
                ConnectActivity.this.displayGattServices(ConnectActivity.this.getSupportedGattServices());
                System.out.println("BroadcastReceiver :device SERVICES_DISCOVERED");
            } else if (Config.action_data_available.equals(action)) {
                ConnectActivity.this.displayData(intent.getExtras().getString(Config.EXTRA_DATA), intent);
                System.out.println("BroadcastReceiver onData:" + intent.getStringExtra(Config.EXTRA_DATA));
            }
        }
    };

    public void disconnected(){
        finish();
    }
    private String rev_str = "";
    private void displayData(String rev_string, Intent intent) {
        try {
            byte[] data = intent.getByteArrayExtra("BLE_BYTE_DATA");
            if (data == null) {
                System.out.println("data is null!!!!!!");
            }
            rev_string = new String(data, 0, data.length, Config.format);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log("==="+rev_string);
        this.rev_str += rev_string;
        showMsg(rev_str);
        final String finalRev_string = rev_string;
        runOnUiThread(new Runnable() {
            public void run() {
                if(adapter==null){
                    return;
                }
                ListBean bean=new ListBean();
                bean.isSend=false;
                bean.text= finalRev_string;
                adapter.getList().add(bean);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private static BluetoothGattCharacteristic target_chara = null;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList();
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices != null) {
            String unknownServiceString = "unknown_service";
            String unknownCharaString = "unknown_characteristic";
            ArrayList<HashMap<String, String>> gattServiceData = new ArrayList();
            ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList();
            this.mGattCharacteristics = new ArrayList();
            for (BluetoothGattService gattService : gattServices) {
                HashMap<String, String> currentServiceData = new HashMap();
                String uuid = gattService.getUuid().toString();
                gattServiceData.add(currentServiceData);
                System.out.println("Service uuid:" + uuid);
                ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList();
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas = new ArrayList();
                for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap();
                    uuid = gattCharacteristic.getUuid().toString();
                    if (gattCharacteristic.getUuid().toString().equals(Config.uuid)) {
                        ConnectActivity.this.mHandler.postDelayed(new Runnable() {
                            public void run() {
                                ConnectActivity.this.helper.readCharacteristic(gattCharacteristic);
                            }
                        }, 200);
                        helper.setCharacteristicNotification(gattCharacteristic, true);
                        target_chara = gattCharacteristic;
                    }
                    for (BluetoothGattDescriptor descriptor : gattCharacteristic.getDescriptors()) {
                        System.out.println("---descriptor UUID:" + descriptor.getUuid());
                        helper.getCharacteristicDescriptor(descriptor);
                    }
                    gattCharacteristicGroupData.add(currentCharaData);
                }
                this.mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }
        }
    }
    public List<BluetoothGattService> getSupportedGattServices() {
        if (helper.mBluetoothGatt == null) {
            return null;
        }
        return helper.mBluetoothGatt.getServices();
    }

}
