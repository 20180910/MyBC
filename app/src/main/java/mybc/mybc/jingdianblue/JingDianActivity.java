package mybc.mybc.jingdianblue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.litesuits.bluetooth.LiteBluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mybc.mybc.BaseActivity;
import mybc.mybc.R;

/**
 * Created by Administrator on 2018/6/14.
 */

public class JingDianActivity extends BaseActivity implements View.OnClickListener {

    public final int requestEnableBt = 1000;
    private Context mContext;
    TextView tv_kai;
    TextView tv_guan;
    TextView tv_sao;
    TextView tv_status;
    TextView tv_duankai;
    TextView tv_blue_title;
    private BluetoothAdapter mBtAdapter;

    BlueListAdapter adapter;
    ListView listView;

    BlueListAdapter adapter2;
    ListView listView2;
    List<String> addressList = new ArrayList<>();

    Handler mHandler;

    private LiteBluetooth liteBluetooth;
    private boolean scan_flag = true;
    private boolean mScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        mHandler = new Handler(getMainLooper());

        initView();
        initBlue();
        initData();
    }

    private void initBlue() {
        tv_blue_title.setVisibility(View.GONE);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            tv_blue_title.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : bondedDevices) {
                DeviceNameBean deviceNameBean = new DeviceNameBean();
                deviceNameBean.name = device.getName();
                deviceNameBean.address = device.getAddress();
                adapter.add(deviceNameBean);
            }
            adapter.notifyDataSetChanged();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }

    private void initData() {

    }

    private void initView() {

        tv_blue_title = (TextView) findViewById(R.id.tv_blue_title);

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
        listView2 = (ListView) findViewById(R.id.listView2);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DeviceNameBean deviceNameBean = adapter2.getList().get(position);
                if (deviceNameBean == null)
                    return;
                if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                final Intent intent = new Intent(mContext, BlueConnectActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("address", deviceNameBean.address);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DeviceNameBean deviceNameBean = adapter.getList().get(position);
                if (deviceNameBean == null)
                    return;
                if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                final Intent intent = new Intent(mContext, BlueConnectActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("address", deviceNameBean.address);
                startActivity(intent);
            }
        });
        adapter = new BlueListAdapter(mContext);
        listView.setAdapter(adapter);

        adapter2 = new BlueListAdapter(mContext);
        listView2.setAdapter(adapter2);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String address = device.getAddress();
                    String name = device.getName();
                    if (!addressList.contains(address)) {
                        DeviceNameBean deviceNameBean = new DeviceNameBean();
                        deviceNameBean.address = address;
                        deviceNameBean.name = name;

                        addressList.add(address);
                        adapter2.add(deviceNameBean);
                        adapter2.notifyDataSetChanged();
                        Log("##===" + device.getAddress() + "---" + device.getName());
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                /*if (adapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    adapter.add(noDevices);
                }*/
                if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                tv_sao.setText("扫描设备");
                Log("===saomiao2222222");
            }
        }
    };

    private void saoMiao() {
        tv_sao.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                    tv_sao.setText("扫描设备");
                }
            }
        }, 10000);
        doDiscovery();
    }

    private void doDiscovery() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    private void open() {
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {//关闭状态
            //打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, requestEnableBt);
        }
    }

    private void close() {
        if (mBtAdapter.isEnabled()) {//关闭状态
            //关闭蓝牙
            mBtAdapter.disable();
        }
    }
    private void stop(){
        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
            tv_sao.setText("扫描设备");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_duankai:
                break;
            case R.id.tv_kai:
                open();
                break;
            case R.id.tv_guan:
                close();
                break;
            case R.id.tv_sao:
                if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
                    showMsg("请开启蓝牙之后再试");
                } else if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    stop();
                } else {
                    addressList.clear();
                    adapter2.getList().clear();
                    adapter2.notifyDataSetChanged();

                    tv_sao.setText("停止扫描");
                    saoMiao();
                }
                break;
        }
    }
}
