# h2-sdk
加动H2心率数据解析工具
Gradle
------
```
allprojects {
    repositories {
        ...
        maven {url 'https://jitpack.io'}
    }
}

dependencies {
    ...
    implementation 'com.github.igetwell:h2-sdk:0.0.2-Alpha'
}
```
Usage
-----
``` Java
        h2Bluetooth = new H2Bluetooth.Builder()
                .setContext(this)
                .setAge(20) // 年龄
                .setSex(1)  // 性别, 0 男, 1,女
                .setWeight(55) // 体重
                .setAddress(bleDevice.address) // 心率设备Mac地址
                .build();
        // 发起连接
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
         // 关闭连接       
         // h2Bluetooth.disconnect();       
```

Callback.class
-----
status 0 佩戴异常, 1-正常数据
hr 心率
kcal 卡路里 (千卡)
