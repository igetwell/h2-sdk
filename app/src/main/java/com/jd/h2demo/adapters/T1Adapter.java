package com.jd.h2demo.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.jd.h2.utils.WzLog;
import com.jd.h2demo.R;
import com.jd.h2demo.beans.BleDevice;
import com.jd.h2demo.databinding.ItemDeviceBinding;
import com.jd.h2demo.listeners.OnItemT1DeviceListener;

import java.util.ArrayList;


/**
 * @author Wave
 * @date 2019/7/31
 */
public class T1Adapter extends BaseArrayAdapter<BleDevice, ItemDeviceBinding>{

    OnItemT1DeviceListener listener;

    public void setListener(OnItemT1DeviceListener listener) {
        this.listener = listener;
    }

    public T1Adapter() {
        this.clearArray();
//        this.addArray(createDevice("8C:DE:52:C5:1F:7F","Wz-001"));
//        this.addArray(createDevice("8C:DE:52:C5:1F:7F","Wz-002"));
//        this.addArray(createDevice("8C:DE:52:C5:1F:7F","Wz-003"));
    }

    BleDevice createDevice(String address,String name){
        BleDevice bluetoothDevice = new BleDevice();
        bluetoothDevice.address = address;
        bluetoothDevice.name = name;
        return bluetoothDevice;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_device;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if(listener != null)viewHolder.getViewDataBinding().setListener(new OnItemT1DeviceListener() {
            @Override
            public void onItemClick(View view, int position) {
                BleDevice bleDevice = getArray().get(position);
                bleDevice.selected = !bleDevice.selected;
                getArray().set(position,bleDevice);
                notifyItemChanged(position);
                WzLog.d(" bleDevice.selected = "+bleDevice.selected);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewDataBinding(ItemDeviceBinding viewDataBinding, int position) {
        viewDataBinding.setPosition(position);
        viewDataBinding.setDev(getArray().get(position));
    }

    public ArrayList<BleDevice> getSelected(){
        ArrayList<BleDevice> bleDevices = new ArrayList<>();
        for (int i = 0; i < getArray().size(); i++) {
            if(getArray().get(i).selected == true){
                bleDevices.add(getArray().get(i));
            }
        }
        return bleDevices;
    }

}
