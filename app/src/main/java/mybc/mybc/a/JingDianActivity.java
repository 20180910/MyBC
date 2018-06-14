package mybc.mybc.a;

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
import android.widget.ArrayAdapter;
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
    ListView listView;
    private BluetoothAdapter mBtAdapter;

    Handler mHandler;

    ArrayAdapter<String> adapter;
    List<String> deviceList = new ArrayList();
    private LiteBluetooth liteBluetooth;
    private boolean scan_flag = true;
    private boolean mScanning;

    int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;

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
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                deviceList.add(device.getAddress());
                adapter.add(device.getName() + "\n" + device.getAddress());
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
                final String address = deviceList.get(position);
                if (address == null)
                    return;
                if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
                final Intent intent = new Intent(mContext, BlueConnectActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    deviceList.add(device.getAddress());
                    adapter.add(device.getName() + "\n" + device.getAddress());
                    adapter.notifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                /*if (adapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    adapter.add(noDevices);
                }*/
                if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
                    mBtAdapter.cancelDiscovery();
                }
            }
        }
    };

    private void saoMiao() {
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
                } else {
                    deviceList.clear();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    saoMiao();
                }
                break;
        }
    }
}
