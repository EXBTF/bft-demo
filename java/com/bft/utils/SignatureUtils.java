package com.bft.utils;

import com.sun.xml.internal.ws.util.StringUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * @program: bft-demo
 * @description: 签名工具类
 * @author: bft
 **/
public class SignatureUtils {

    /**
     * 验证签名
     *
     * @param params 请求
     * @param signKey 私钥
     * @return
     */
    public static boolean verify(Map<String, Object> params, String signKey) {

        if (!params.containsKey("signature")) {
            return false;
        } else {
            //获取当前key
            String key = params.remove("signature").toString();
            //对参数进行加密
            String currentKey =  sign(params, signKey);
            //对比
            return key.equals(currentKey);
        }
    }

    /**
     * 生成签名
     *
     * @param params 请求参数
     * @param key 私钥
     * @return
     */
    public static String sign(Map<String, Object> params, String key) {
        if("".equals(key)){
            throw new RuntimeException("APP_KEY 参数为空，请填写");
        }
        // 1. 参数名按照ASCII码表升序排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 2. 按照排序拼接参数名与参数值
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
                sb.append(k).append("=").append(params.get(k)).append("&");
        }

        // 3. 将secretKey拼接到最后
        sb.append("key").append("=").append(key);
        String signResult = MD5Utils.getMD5(sb.toString().getBytes());
        System.out.println("验签str:"+sb.toString()+"结果"+signResult);
        return signResult;
    }

}
