package com.jd.h2;

/**
 * @ClassName: H2Parser
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020-03-06 17:33
 * @Version: 1.0
 */
public class H2Parser {

    static {
        System.loadLibrary("jd-h2-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native float calories(int sex, int age, float weight,int hr,int type,int smo2,int speed,int slope);

    public static native String getReadHeart(String value);
}
