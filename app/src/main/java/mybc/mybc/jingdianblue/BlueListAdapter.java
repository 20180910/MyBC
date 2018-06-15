package mybc.mybc.jingdianblue;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mybc.mybc.R;

/**
 * Created by Administrator on 2018/6/7.
 */

public class BlueListAdapter extends BaseAdapter{
    private Context mContext;
    private List<DeviceNameBean> list=new ArrayList<>();
    boolean showTitle;
    boolean showSecondTile;
    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    public BlueListAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public List<DeviceNameBean> getList() {
        return list;
    }

    public void  add(DeviceNameBean bean){
        this.list.add(bean);
    }

    public void setList(List<DeviceNameBean> list) {
        this.list = list;
    }

    @Override
    public DeviceNameBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.blue_item,null);
            holder=new Holder();
            holder.tv_blue_name=convertView.findViewById(R.id.tv_blue_name);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }

        DeviceNameBean bean = getItem(position);

        holder.tv_blue_name.setText(bean.name+"\n"+bean.address);

        Log.e("======","==="+bean.name);

        return convertView;
    }

    public class Holder{
//        TextView tv_blue_title;
        TextView tv_blue_name;
    }
}
