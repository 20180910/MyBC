package mybc.mybc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by Administrator on 2018/6/8.
 */

public   class BaseActivity extends AppCompatActivity   {

    protected Dialog dialog;
    protected void showDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(getLayoutInflater().inflate(R.layout.load_layout, null));
        dialog.show();
        if(dialog.findViewById(android.R.id.title)!=null){
            dialog.findViewById(android.R.id.title).setVisibility(View.GONE);
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    return false;
                } else {
                    return false;
                }
            }
        });
    }
    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
