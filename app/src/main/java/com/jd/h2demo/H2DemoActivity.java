package com.jd.h2demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jd.h2.H2Bluetooth;
import com.jd.h2.H2Parser;
import com.jd.h2.beans.Callback;
import com.jd.h2.utils.WzLog;
import com.jd.h2demo.databinding.ActivityH22Binding;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName: H2DemoActivity
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020/8/31 11:25 PM
 * @Version: 1.0
 */
public class H2DemoActivity extends AppCompatActivity {

    ActivityH22Binding binding;

    H2Bluetooth h2Bluetooth2;

    long startTime = 0L;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_h2_2);
        //float calories = H2Parser.calories(params.sex,params.age,params.weight.floatValue(),params.hr,params.exerciseType,params.smo2,params.speed,params.slope);
        startTime = System.currentTimeMillis();
        h2(1,28,110,55f,1,75,0,0);

        String address = "F1:DB:25:D4:EB:18";
        h2Bluetooth2 = new H2Bluetooth.Builder()
                .setContext(this)
                .setAge(28) // 年龄
                .setSex(0)  // 性别, 0 男, 1,女
                .setWeight(55) // 体重
                .setAddress(address) // 心率设备Mac地址
                .build();

        connect();
    }

    void connect(){
        h2Bluetooth2.connect()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Callback>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Callback callback) {
                        //binding.setCallback(callback);
                        if(binding.getTotal2() == null){
                            binding.setTotal2(0f);
                        }
                        float total = binding.getTotal2() + (callback.kcal == null ? 0 : callback.kcal);
                        binding.setTotal2(total);
                        binding.setValue2(callback.kcal);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onRxTimer();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    void onRxTimer(){
        Observable.timer(5,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        WzLog.d("正在重新连接...");
                        connect();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public String getGapTime(long time){
        long hours = time / (1000 * 60 * 60);
        long minutes = (time-hours*(1000 * 60 * 60 ))/(1000* 60);
        String diffTime="";
        if(minutes<10){
            diffTime=hours+":0"+minutes;
        }else{
            diffTime=hours+":"+minutes;
        }
        return diffTime;
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

    Disposable disposable;

    void h2(int sex,int age,int hr,float weight,int type,int som2,int speed,int slop){
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        float calories = H2Parser.calories(sex,age,weight,hr,type,som2,speed,slop);
                        if(binding.getTotal() == null){
                            binding.setTotal(0f);
                        }
                        float total = binding.getTotal() + calories;
                        binding.setTotal(total);
                        binding.setValue(calories);

                        binding.setTime(simpleDateFormat.format(System.currentTimeMillis() - startTime));
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
    public void finish() {
        super.finish();
        if(disposable != null && !disposable.isDisposed())disposable.dispose();
    }
}
