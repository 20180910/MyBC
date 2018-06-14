package mybc.mybc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/7.
 */

public class ListAdapter extends BaseAdapter{
    private Context mContext;
    private List<BlueNameBean> list=new ArrayList<>();
    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    public ListAdapter(Context mContext) {
        this.mContext = mContext;
    }
    public List<BlueNameBean> getList() {
        return list;
    }

    public void setList(List<BlueNameBean> list) {
        this.list = list;
    }

    @Override
    public BlueNameBean getItem(int position) {
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
            convertView= LayoutInflater.from(mContext).inflate(R.layout.list_item,null);
            holder=new Holder();
            holder.tv_receive=convertView.findViewById(R.id.tv_receive);
            holder.tv_send=convertView.findViewById(R.id.tv_send);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }

        BlueNameBean bean = getItem(position);

        if(bean.isSend){
            holder.tv_receive.setVisibility(View.GONE);
            holder.tv_send.setVisibility(View.VISIBLE);
            holder.tv_send.setText(bean.text);
        }else{
            holder.tv_receive.setVisibility(View.VISIBLE);
            holder.tv_send.setVisibility(View.GONE);
            holder.tv_receive.setText(bean.text);
        }
        return convertView;
    }

    public class Holder{
        TextView tv_receive;
        TextView tv_send;
    }
}
