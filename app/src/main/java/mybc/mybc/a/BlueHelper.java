package mybc.mybc.a;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Administrator on 2018/6/14.
 */

public class BlueHelper {
    private DialogInter inter;
    private final BluetoothAdapter mAdapter;
    private Context mContext;
    // 声明一个唯一的UUID
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");    //change by chongqing jinou
    private BluetoothSocket socket;
    private String TAG = this.getClass().getSimpleName();
    private ConnectedThread connectedThread;


    public BlueHelper(Context mContext, DialogInter inter) {
        this.mContext = mContext;
        this.inter = inter;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public synchronized void connect(BluetoothDevice device) {
        socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            connectedThread = new ConnectedThread(socket);
            connectedThread.start();
            inter.dismiss(true);
        } catch (IOException e) {
            Log("create() 失败");
            inter.dismiss(false);
        }
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "创建 ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // 得到BluetoothSocket输入和输出流
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            int bytes;
            String str1 = "";
            // 循环监听消息
            while (true) {
                try {
                    byte[] buffer = new byte[256];

                    bytes = mmInStream.read(buffer);
                    String readStr = new String(buffer, 0, bytes);
                    String str = bytes2HexString(buffer).replaceAll("00", "").trim();
                    if (bytes > 0) {
                        if (str.endsWith("0D")) {
                            byte[] buffer1 = (str1 + readStr).getBytes();
                            /*mHandler.obtainMessage(BluetoothChatActivity.MESSAGE_READ, buffer1.length, -1, buffer1)
                                    .sendToTarget();*/
                            str1 = "";
                        } else {
                            if (!str.contains("0A")) {
                                str1 = str1 + readStr;
                            } else {
                                if (!str.equals("0A") && str.endsWith("0A")) {
                                    /*mHandler.obtainMessage(BluetoothChatActivity.MESSAGE_READ, bytes, -1, buffer)
                                            .sendToTarget();*/
                                }
                            }
                        }

                    } else {
                        Log.e(TAG, "disconnected");
//                        connectionLost();

                       /* if (mState != STATE_NONE) {
                            Log.e(TAG, "disconnected");
//                            BluetoothChatService.this.start();
                        }*/
                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    inter.dismiss(false);
//                    connectionLost();
//
//                    if (mState != STATE_NONE) {
                        // 在重新启动监听模式启动该服务
//                        BluetoothChatService.this.start();
//                    }
                    break;
                }
            }
        }

        /**
         * 写入OutStream连接
         *
         * @param buffer 要写的字节
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

               /* // 把消息传给UI
                mHandler.obtainMessage(BluetoothChatActivity.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();*/
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }

    public void write(byte[] out) {
        if(connectedThread==null){
            return;
        }
        connectedThread.write(out);
    }
    public void showMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void Log(String str) {
        Log.i("Log", str);
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
}
