package mybc.mybc.a;

/**
 * Created by Administrator on 2018/6/14.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;

import com.inuker.bluetooth.library.BluetoothClient;

import java.io.InputStream;
import java.io.OutputStream;

import mybc.mybc.BaseActivity;
import mybc.mybc.Config;
import mybc.mybc.R;
import top.wuhaojie.bthelper.BtHelperClient;


/**
 * Created by Administrator on 2018/6/7.
 */

public class BlueConnectActivity extends BaseActivity implements DialogInter {
    private Context mContext;
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

    BlueHelper helper;
    SwitchCompat sc_led1,sc_led2,sc_led3;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.blue_connect);
        mHandler=new Handler(getMainLooper());
        helper=new BlueHelper(mContext,this);
        register();
        initView();
        initData();

        showDialog();

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        helper.connect(device);

    }
    String TAG=this.getClass().getSimpleName();

    private void initView() {
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        address = getIntent().getStringExtra("address");
        Log.i("===", "==address=" + address);

        sc_led1 = (SwitchCompat) findViewById(R.id.sc_led1);
        sc_led1.setOnCheckedChangeListener(getListener(1));

        sc_led2 = (SwitchCompat) findViewById(R.id.sc_led2);
        sc_led2.setOnCheckedChangeListener(getListener(2));


        sc_led3 = (SwitchCompat) findViewById(R.id.sc_led3);
        sc_led3.setOnCheckedChangeListener(getListener(3));


    }

    @NonNull
    private CompoundButton.OnCheckedChangeListener getListener(final int index) {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (index){
                    case 1:
                        sendMessage(isChecked?"1":"2");
                    break;
                    case 2:

                        sendMessage(isChecked?"3":"4");
                    break;
                    case 3:

                        sendMessage(isChecked?"5":"6");
                    break;
                }
            }
        };
    }


    private void initData() {

    }



    private void sendMessage(String message) {
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            helper.write(send);
        }
    }





    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Config.action_gatt_connected);
        intentFilter.addAction(Config.action_gatt_disconnected);
        intentFilter.addAction(Config.action_gatt_services_discovered);
        intentFilter.addAction(Config.action_data_available);

//        registerReceiver(mGattUpdateReceiver, intentFilter);
    }

    public void unRegister() {
//        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegister();
        if(helper!=null){
            helper.close();
        }
    }
    @Override
    public void dismiss(boolean isSuccess) {
        dismissDialog();
        if(isSuccess){
            showMsg("连接成功");
        }else{
            showMsg("连接失败");
            helper.close();
            finish();
        }
    }
}

