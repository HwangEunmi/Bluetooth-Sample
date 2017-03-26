package com.theory.emhwang.bluetoothproject.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.theory.emhwang.bluetoothproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hwangem on 2017-03-22.
 */

public class DeviceListAdapter extends BaseAdapter {

    private List<String> mItems = new ArrayList<>();

    public void add(String data) {
        mItems.add(data);
        notifyDataSetChanged();
    }

    public void addAll(List<String> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceListVieHolder view;

        if (convertView == null) {
            view = new DeviceListVieHolder(parent.getContext());
            convertView = view;

        } else {
            view = (DeviceListVieHolder)convertView;
        }

        view.setDeviceListItem(mItems.get(position));
        view.setTag(mItems.get(position));
        return view;
    }

    /**
     * 뷰홀더
     */
    public class DeviceListVieHolder extends FrameLayout {

        /**
         * 기기
         */
        private TextView tvDeviceItem;

        public DeviceListVieHolder(Context context) {
            super(context);
            init();
        }

        private void init() {
            inflate(getContext(), R.layout.view_device_list, this);
            tvDeviceItem = (TextView)findViewById(R.id.tv_list_item);
        }

        private void setDeviceListItem(String deviceItem) {
            tvDeviceItem.setText(deviceItem);
        }
    }

}
