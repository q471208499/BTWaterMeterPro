package cn.cb.btwatermeterpro.bean;

import java.text.DecimalFormat;

public class MainRunTest {
    public static void main(String[] args) {
        //System.out.println(String.format("%02d:00", 22));
        //System.out.println(String.format("%04d-%02d-%02d", 2021,2,3));
        //System.out.println(String.format("20%2s.%2s", "2104".substring(2), "2104".substring(2, 4)));
        double d = Double.parseDouble("331.");
        System.out.println(d);
        DecimalFormat df = new DecimalFormat("#.000");
        String s = df.format(d);
        System.out.println(s);
        System.out.println((int)(Double.parseDouble(s) * 1000));
    }
}
