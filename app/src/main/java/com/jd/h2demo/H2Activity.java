package com.jd.h2demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jd.h2.H2Bluetooth;
import com.jd.h2.beans.Callback;
import com.jd.h2demo.beans.BleDevice;
import com.jd.h2demo.databinding.ActivityH2Binding;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName: H2Activity
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020-03-06 00:42
 * @Version: 1.0
 */
public class H2Activity extends AppCompatActivity {

    public static final String EXTRA_T1 = "extra_t1_info";

    ActivityH2Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_h2);
        onAfter();
    }

    H2Bluetooth h2Bluetooth;

    void onAfter(){
        //Blead
        BleDevice bleDevice = getIntent().getParcelableExtra(EXTRA_T1);

        h2Bluetooth = new H2Bluetooth.Builder()
                .setContext(this)
                .setAge(20) // 年龄
                .setSex(1)  // 性别, 0 男, 1,女
                .setWeight(55) // 体重
                .setAddress(bleDevice.address) // 心率设备Mac地址
                .build();

        h2Bluetooth.connect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Callback>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Callback callback) {
                        binding.setCallback(callback);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(h2Bluetooth != null)h2Bluetooth.disconnect();
    }
}
