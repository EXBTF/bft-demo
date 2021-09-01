using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Security.Cryptography;
using Newtonsoft.Json;

/**
*
*   法币调整收银台Demo

签名算法规则：
1.参数名ASCII码从小到大排序（字典序）
2.如果参数的值为空不参与签名
3.参数名区分大小写
4.验证调用返回或BFT主动通知签名时，传送的signature参数不参与签名，将生成的签名与该signature值作校验

*/
public class FBDemo
{
    /**
     *
     接入私钥，对应商户后台“接入密钥”（测试时，修改成自己的Key)
     */
    public static string APP_KEY = "";

     /**
     * 商户ID，对应商户平台的“商户编码”。 (测试时，修改成自己UID)
     */
    public static string UID = "";

    /**
    * API url
    **/
    public static string API_URL = "https://api.bifutong.vip/coin/pay/order/pay/checkout/counter";


/**
*
* HTTP 请求
*/
 public static string HttpRequest(string url, string parm, string method){

    if(method.Equals("GET")){

        url = url + "?" + parm;
        }
    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(url);
    request.Method = method;
    request.Timeout = 5 * 1000;
    request.AllowWriteStreamBuffering = true;
    request.AllowWriteStreamBuffering = true;
    request.ContentType = "application/json;charset=UTF-8";
    if (method.Equals("POST")){
        StreamWriter wr = new StreamWriter(request.GetRequestStream(), Encoding.UTF8);
        wr.Write(parm);
        wr.Flush();
        wr.Close();
        }

        HttpWebResponse respone = (HttpWebResponse)request.GetResponse();
        var stream = respone.GetResponseStream();
        StreamReader red = new StreamReader(stream, Encoding.UTF8);
        string str = red.ReadToEnd();
        red.Close();
        stream.Flush();
        stream.Close();
        return str;

    }

    /**
    * 获取加密串
    *
    */
      private static string getSignContent(IDictionary<string, string> parameters)
        {
            // 第一步：把字典按Key的字母顺序排序
            IDictionary<string, string> sortedParams = new SortedDictionary<string, string>(parameters,StringComparer.Ordinal);
            IEnumerator<KeyValuePair<string, string>> dem = sortedParams.GetEnumerator();

            // 第二步：把所有参数名和参数值串在一起
            StringBuilder query = new StringBuilder("");
            while (dem.MoveNext())
            {
                string key = dem.Current.Key;
                string value = dem.Current.Value;
                if (!string.IsNullOrEmpty(key) && !string.IsNullOrEmpty(value))
                {
                    query.Append(key).Append("=").Append(value).Append("&");
                }
            }
            string content = query.ToString().Substring(0, query.Length - 1);

            string contentResult =  content+"&key="+APP_KEY;

            Console.WriteLine("#before sign "+contentResult);

            return getEncryptString(contentResult);
        }



    /**
    * MD5 加密
    */
     public static string getEncryptString(string str)
        {
            MD5 md5 = MD5.Create();
            // 将字符串转换成字节数组
            byte[] byteOld = Encoding.UTF8.GetBytes(str);
            // 调用加密方法
            byte[] byteNew =  md5.ComputeHash(byteOld);
            // 将加密结果转换为字符串
            StringBuilder sb = new StringBuilder();
            foreach (byte b in byteNew)
            {
                // 将字节转换成16进制表示的字符串，
                sb.Append(b.ToString("x2"));
            }

            Console.WriteLine("#--sign result:"+sb.ToString());
            // 返回加密的字符串
            return sb.ToString();
        }




    /**
    测试类
    *
    */
    public static void Main(){
        if("" == UID){
           throw new Exception("uid 为空，请填写");
        }
        if("" == APP_KEY){
            throw new Exception("APP_KEY 为空，请填写");
        }

        IDictionary<string, string> dics = new Dictionary<string, string>();
        dics.Add("uid", UID);
        dics.Add("uniqueCode","1112");
        dics.Add("money","10.00");
        dics.Add("payType","1");
        dics.Add("orderId","22222222565656");

        String sign = getSignContent(dics);
        dics.Add("signature",sign);

        string jsonParams = JsonConvert.SerializeObject(dics);

      string requestResult = HttpRequest(API_URL,jsonParams,"POST");
      Console.WriteLine("#http result:"+requestResult);

    }
}