package com.jd.h2.beans;

/**
 * @ClassName: CaloriesParams
 * @Description: java类作用描述
 * @Author: Wave.Zhang
 * @Email: zhanglang0504@gmail.com
 * @CreateDate: 2020-03-06 12:33
 * @Version: 1.0
 */
public class CaloriesParams {
    //sex=1,age=30,weight=55,hr=90,exercisetype=1,smo2=75,speed=6,slope=0

    public Integer sex = 1;

    public Integer age = 30;

    public Double weight = 55D;

    public Integer hr = 90;

    public Integer exerciseType = 1;

    public Integer smo2 = 75;

    public Integer speed = 6;

    public Integer slope = 0;

    public CaloriesParams(){}

    public CaloriesParams(int sex, int age, double weight, int hr, int exerciseType, int smo2, int speed, int slope) {
        this.sex = sex;
        this.age = age;
        this.weight = weight;
        this.hr = hr;
        this.exerciseType = exerciseType;
        this.smo2 = smo2;
        this.speed = speed;
        this.slope = slope;
    }

    public CaloriesParams(int sex, int age, double weight, int hr) {
        this.sex = sex;
        this.age = age;
        this.weight = weight;
        this.hr = hr;
    }

    public String[] toStringArray(){
        return new String[]{sex.toString(),age.toString(),weight.toString(),hr.toString(),exerciseType.toString(),smo2.toString(),speed.toString(),slope.toString()};
    }
}
