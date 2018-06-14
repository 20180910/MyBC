package mybc.mybc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.bluetooth.LiteBluetooth;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    //    String uuid="00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
//    String uuid="0000ffe1-0000-1000-8000-00805f9b34fb";   //SPP服务UUID号
    public final int requestEnableBt = 1000;
    private Context mContext;
    TextView tv_kai;
    TextView tv_guan;
    TextView tv_sao;
    TextView tv_status;
    TextView tv_duankai;
    ListView listView;
    private BluetoothAdapter mBluetoothAdapter;

    Handler mHandler;

    ArrayAdapter<String> adapter;
    List<BluetoothDevice> deviceList = new ArrayList();
    private LiteBluetooth liteBluetooth;
    private boolean scan_flag = true;
    private boolean mScanning;

    int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            // TODO Auto-generated method stub

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /* 讲扫描到设备的信息输出到listview的适配器 */
                    if(!deviceList.contains(device)){
                        deviceList.add(device);
                        String name=device.getName();
                        if(TextUtils.isEmpty(name)){
                            name="未知设备名称";
                        }
                        adapter.add(name + "\n" + device.getAddress());
                        adapter.notifyDataSetChanged();
                    }

                }
            });

            System.out.println("Address:" + device.getAddress());
            System.out.println("Name:" + device.getName());
            System.out.println("rssi:" + rssi);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        mHandler = new Handler(getMainLooper());

        initBlue();

        initView();
        initData();
    }

    private void initBlue() {
        // 手机硬件支持蓝牙
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Initializes Bluetooth adapter.
        // 获取手机本地的蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 打开蓝牙权限

    }



    private void initView() {

        tv_kai = (TextView) findViewById(R.id.tv_kai);
        tv_kai.setOnClickListener(this);

        tv_guan = (TextView) findViewById(R.id.tv_guan);
        tv_guan.setOnClickListener(this);

        tv_sao = (TextView) findViewById(R.id.tv_sao);
        tv_sao.setOnClickListener(this);

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_status.setOnClickListener(this);

        tv_duankai = (TextView) findViewById(R.id.tv_duankai);
        tv_duankai.setOnClickListener(this);


        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent intent=bl Intent(mContext,ConnectActivity.class);
                intent.putExtra("address",deviceList.get(position));
                startActivity(intent);*/
//                showDialog();

                final BluetoothDevice device = deviceList.get(position);
                if (device == null)
                    return;
//                intent.putExtra(ConnectActivity.EXTRAS_DEVICE_RSSI,rssis.get(position).toString());
                if (mScanning) {
                    /* 停止扫描设备 */
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    mScanning = false;
                }

                final Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
                intent.putExtra("name", device.getName());
                intent.putExtra("address", device.getAddress());
                startActivity(intent);
            }
        });
        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
    }


    private void initData() {
        setStatus(mBluetoothAdapter.isEnabled());
    }


    public void setStatus(boolean flag) {
        tv_status.setText(flag ? "蓝牙状态:开" : "蓝牙状态:关");
    }

    private void open() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {//关闭状态
            //打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, requestEnableBt);
        }
    }

    private void close() {
        if (mBluetoothAdapter.isEnabled()) {//关闭状态
            //关闭蓝牙
            mBluetoothAdapter.disable();
        }
    }

    @Override
    public void onClick(View v) {
       /* if(mBluetoothAdapter ==null){
            showMsg("此设备不支持蓝牙");
            return;
        }*/
        switch (v.getId()) {
            case R.id.tv_duankai:

                break;
            case R.id.tv_kai:
                if(mBluetoothAdapter!=null&&!mBluetoothAdapter.isEnabled()){
                    mBluetoothAdapter.enable();
                }
//                open();
                break;
            case R.id.tv_guan:
                if(mBluetoothAdapter!=null&&mBluetoothAdapter.isEnabled()){
                    mBluetoothAdapter.disable();
                }
                break;
            case R.id.tv_sao:
                if(mBluetoothAdapter==null||!mBluetoothAdapter.isEnabled()){
                    showMsg("请开启蓝牙之后再试");
                }else{
                    saoMiao();
                }
                break;
        }
    }

    private static int TIME_OUT_SCAN = 10000;

    private void saoMiao() {
        if (scan_flag) {
            deviceList.clear();
            adapter.clear();
            adapter.notifyDataSetChanged();

            scanLeDevice(true);
        } else {
            scanLeDevice(false);
            tv_sao.setText("扫描设备");
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    scan_flag = true;
                    tv_sao.setText("扫描设备");
                    Log.i("SCAN", "stop.....................");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
            /* 开始扫描蓝牙设备，带mLeScanCallback 回调函数 */
            Log.i("SCAN", "begin.....................");
            mScanning = true;
            scan_flag = false;
            tv_sao.setText("停止扫描");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            Log.i("Stop", "stoping................");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            scan_flag = true;
        }

    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
