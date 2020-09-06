package com.jd.h2.controllers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.jd.h2.H2Parser;
import com.jd.h2.beans.Callback;
import com.jd.h2.beans.CaloriesParams;
import com.jd.h2.utils.WzLog;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @ClassName: H2BluetoothController
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020-03-05 23:45
 * @Version: 1.0
 */
public class H2BluetoothController {

    /**
     * 心率设备数据服务
     */
    final String H2_SERVICE_UUID_DATA = "0000180d-0000-1000-8000-00805f9b34fb";
    /**
     * 心率设备数据特征
     */
    final String H2_CHARACTERISTIC_UUID_DATA = "00002a37-0000-1000-8000-00805f9b34fb";
    /**
     * 读取心率设备电量服务
     */
    final String H2_SERVICE_UUID_BATTER = "0000180f-0000-1000-8000-00805f9b34fb";
    /**
     * 读取心率设备电量特征
     */
    final String H2_CHARACTERISTIC_UUID_BATTER = "00002a19-0000-1000-8000-00805f9b34fb";

    //write or read or notify
    final String CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    Context context;

    int age;

    int sex;

    double weight;

    String address;

    BluetoothGatt bluetoothGatt;

    ObservableEmitter<Callback> emitter;

    public H2BluetoothController(Context context,int age,int sex,double weight, String address) {
        this.context = context;
        this.address = address;
        this.age = age;
        this.sex = sex;
        this.weight = weight;
    }

    public Observable<Callback> connect(){
        return Observable.create(new ObservableOnSubscribe<Callback>() {
            @Override
            public void subscribe(ObservableEmitter<Callback> emitter) throws Exception {
                connect(emitter);
            }
        });
    }

    void connect(ObservableEmitter<Callback> emitter1){
        this.emitter = emitter1;
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(this.address);
        WzLog.d("开始连接H2-->"+this.address);
        bluetoothGatt = bluetoothDevice.connectGatt(this.context, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                WzLog.e("--->gatt----onConnectionStateChange==> status = " + status + ". newState = " + newState);
                if(status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED){
                    gatt.discoverServices();
                    //emitter.onComplete();
                    return;
                }
                WzLog.e("设备连接异常!!!");
                emitter.onError(new ConnectException("H2 设备连接异常!!!"));
                emitter.onComplete();
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                onReadHeartRateData(gatt,status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                onBatteryValueChanged(gatt,characteristic);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                onValueChanged(gatt,characteristic);
            }

        });
    }

    public void disconnect(){
        if(this.emitter != null){
            this.emitter.onComplete();
        }
        this.emitter = null;
        this.context = null;
        if(bluetoothGatt == null)return;
        bluetoothGatt.close();
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
    }

    void onReadHeartRateData(BluetoothGatt gatt, int status){
        if(status != BluetoothGatt.GATT_SUCCESS){
            emitter.onError(new ConnectException("H2 设备连接异常!!!"));
            emitter.onComplete();
            return;
        }
        BluetoothGattService service = gatt.getService(UUID.fromString(H2_SERVICE_UUID_DATA));
        if(service == null){

            emitter.onError(new ConnectException("非H2设备 无法获取心率数据服务"));
            emitter.onComplete();
            return;
        }
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(H2_CHARACTERISTIC_UUID_DATA));
        if(characteristic == null){
            emitter.onError(new ConnectException("非H2设备 无法获取到心率数据特征"));
            emitter.onComplete();
            return;
        }
        // openNotification
        gatt.setCharacteristicNotification(characteristic, true);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    void onReadBatteryData(BluetoothGatt gatt){
        if(gatt == null)return;
        BluetoothGattService service = gatt.getService(UUID.fromString(H2_SERVICE_UUID_BATTER));
        BluetoothGattCharacteristic batteryCharacteristic = service.getCharacteristic(UUID.fromString(H2_CHARACTERISTIC_UUID_BATTER));
        gatt.readCharacteristic(batteryCharacteristic);
    }

    String toHaxString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTmp;
        for (int i = 0; i < bytes.length; i++) {
            sTmp = Integer.toHexString(0xFF & bytes[i]);
            if (sTmp.length() < 2){
                sb.append(0);
            }
            sb.append(sTmp.toUpperCase());
        }
        return sb.toString();
    }

    boolean isReadHeart(String result){
        return result.startsWith("06") || result.startsWith("04");
    }

    int battery = 0;

    void onValueChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
        if(gatt == null || characteristic == null)return;
        // 获取电量数据
        //onReadBatteryData(gatt);
        // 解析数据
        byte[] bs = characteristic.getValue();
        if(bs == null || bs.length <= 0)return;
        String result = toHaxString(bs);
        //WzLog.e(" byte[] = "+result);
        if(!isReadHeart(result)){
            WzLog.e("无法识别的数据");
            return;
        }
        // 心率数据
        int status = result.startsWith("06") ? 1 : 0;
        String haxHr = result.substring(2,4);
        //WzLog.d(" haxHr = "+haxHr);
        int heartRate = Integer.parseInt(haxHr,16);
        // 计算
        CaloriesParams params = new CaloriesParams(age,sex,weight,heartRate);
        //double calories = caloriesParser.getCalories(params);
        float calories = H2Parser.calories(params.sex,params.age,params.weight.floatValue(),params.hr,params.exerciseType,params.smo2,params.speed,params.slope);
        //calories = calories * 2;
        BigDecimal b = new BigDecimal(calories);
        calories = b.setScale(4,BigDecimal.ROUND_HALF_UP).floatValue();
        WzLog.d("状态 : "+status+" 心率 : "+heartRate+" 卡路里 : "+calories);
        emitter.onNext(new Callback(status,heartRate,calories));
    }

    void onBatteryValueChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
        if(gatt == null || characteristic == null)return;
        byte[] bs = characteristic.getValue();
        String result = toHaxString(bs);
        if(result.length() == 2){
            battery = Integer.parseInt(result,16);
            return;
        }
    }

}
