package com.jd.h2.beans;

/**
 * @ClassName: Callback
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020-03-05 23:45
 * @Version: 1.0
 */
public class Callback {

    public int status;

    public Integer hr;

    public Float kcal;

    public Callback(){}

    public Callback(int status, Integer hr, Float kcal) {
        this.status = status;
        this.hr = hr;
        this.kcal = kcal;
    }
}
