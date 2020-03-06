package com.jd.h2;

import android.content.Context;

import com.jd.h2.beans.Callback;
import com.jd.h2.controllers.H2BluetoothController;

import io.reactivex.Observable;

/**
 * @ClassName: H2Bluetooth
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020-03-06 01:38
 * @Version: 1.0
 */
public class H2Bluetooth {

    H2BluetoothController controller;

    H2Bluetooth(Builder builder) {
        controller = new H2BluetoothController(builder.context,builder.age,builder.sex,builder.weight,builder.address);
    }

    public Observable<Callback> connect(){
        return controller.connect();
    }

    public void disconnect(){
        controller.disconnect();
    }

    public static final class Builder{

        int age = 20;

        int sex = 0;

        double weight = 55;

        String address;

        Context context;

        public Builder setAge(int age) {
            this.age = age;
            return this;
        }

        public Builder setSex(int sex) {
            this.sex = sex;
            return this;
        }

        public Builder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public H2Bluetooth build(){
            return new H2Bluetooth(this);
        }
    }
}
