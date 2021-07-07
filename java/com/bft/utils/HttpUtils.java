package com.bft.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @program: bft-demo
 * @description: Http工具类
 * @author: bft
 **/
public class HttpUtils {


    /**
     * 发送POST请求
     * @param urlS
     * @param jsonStr
     * @return JSON或者字符串
     * @throws Exception
     */
    public static String sendPost(String urlS, String jsonStr) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlS); // url地址
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");// 设置请求方式为POST
            connection.setDoInput(true);// 允许读入
            connection.setDoOutput(true);// 允许写出
            connection.setUseCaches(false);// 不使用缓存
            connection.setConnectTimeout(5000);// 连接超时时间
            connection.setReadTimeout(10000);// 主机读取超时时间
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");// 设置参数类型是json格式
            connection.setRequestProperty("language","zh_cn");// 设置语言标识
            connection.connect();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(jsonStr);
            writer.close();

            int responseCode = connection.getResponseCode();
            InputStream inputStream = null;
            if(responseCode == HttpURLConnection.HTTP_OK){
                inputStream = connection.getInputStream();
            } else{

                JSONObject json = new JSONObject();
                json.put("code", responseCode);
                json.put("msg", connection.getResponseMessage());
                System.out.println("request error："+json.toString());
                return json.toString();
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine())!= null) {
                response.append(line);
            }
            System.out.println("response result:"+response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null!=reader){
                try{
                    reader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (null!=connection){
                connection.disconnect();
            }
        }
    }

}
