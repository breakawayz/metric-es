package com.zhangyx.util;

import java.net.InetAddress;

public class IpUtil {
    public static String getLocalIp() {
        InetAddress addr = null;
        String ip = "";
        try {
            addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress().toString();//获得本机IP　　
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }
}
