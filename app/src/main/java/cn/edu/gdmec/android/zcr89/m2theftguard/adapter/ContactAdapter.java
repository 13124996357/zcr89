package cn.edu.gdmec.android.zcr89.m2theftguard.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.edu.gdmec.android.zcr89.R;
import cn.edu.gdmec.android.zcr89.m2theftguard.ContactSelectActivity;
import cn.edu.gdmec.android.zcr89.m2theftguard.entity.ContactInfo;


public class ContactAdapter extends BaseAdapter {
    private List<ContactInfo> contactInfos;
    private Context context;
    public ContactAdapter(List<ContactInfo> contactInfos, Context context) {
        super();
        this.contactInfos=contactInfos;
        this.context=context;
    }

    public ContactAdapter(List<ContactInfo> systemContacts, ContactSelectActivity context) {

    }

    @Override
    public int getCount() {
        return contactInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return contactInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            convertView = View.inflate(context, R.layout.item_list_contact_select,null);
            holder = new ViewHolder();
            holder.mNameTV=(TextView)convertView.findViewById(R.id.tv_name);
            holder.mPhoneTv=(TextView)convertView.findViewById(R.id.tv_phone);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.mNameTV.setText(contactInfos.get(position).name);
        holder.mPhoneTv.setText(contactInfos.get(position).phone);
        return convertView;
    }
    static class ViewHolder{
        TextView mNameTV;
        TextView mPhoneTv;


    }
}
