import com.alibaba.fastjson.JSON;
import com.bft.utils.HttpUtils;
import com.bft.utils.SignatureUtils;

import java.util.HashMap;

/**
 法币调整收银台Demo
 签名算法规则：
 1.参数名ASCII码从小到大排序（字典序）
 2.如果参数的值为空不参与签名
 3.参数名区分大小写
 4.验证调用返回或BFT主动通知签名时，传送的signature参数不参与签名，将生成的签名与该signature值作校验
 **/
public class FBDemo {

    /**
     * 商户ID，对应商户平台的“商户编码”。 (测试时，修改成自己UID)
     */
    private final static  String UID = "";

    /**
     * 接入私钥，对应商户后台“接入密钥”（测试时，修改成自己的Key)
     */
    private final static String KEY = "";

    /**
     * API 接口
     */
    private final static String API_URL = "https://api.bifutong.vip/coin/pay/order/pay/checkout/counter";


    /**
     * 测试入口
     *
     * @param args
     */
    public static void main(String[] args) {
        //1 跳转收银台
        getCheckoutCounter();

        //2 回调参数校验
//        callback();
    }


    /**
     * 1 跳转收银台
     */
    private static void getCheckoutCounter(){
        if("".equals(KEY)){
            throw new RuntimeException("APP_KEY 参数为空，请填写");
        }
        if("".equals(UID)){
            throw new RuntimeException("UID 参数为空，请填写");
        }

        HashMap<String, Object> params = new HashMap();
        //商户ID，对应商户平台的“商户编码”。
        params.put("uid", UID);
        //商户唯一ID，接入方自己根据自己平台设定。例如，接入方平台的用户ID
        params.put("uniqueCode",1112);
        //下单金额，RMB
        params.put("money", "10.00");
        //支付类型，默认是1，也仅支持1
        params.put("payType",1);
        //订单ID，接入方，自己生成
        params.put("orderId", "22222222565656");

        //生成签名
        String sign = SignatureUtils.sign(params,KEY);
        //设置签名参数
        params.put("signature",sign);
        System.out.println("FB sign result:"+sign);

        //转化成JSON串请求参数
        String jsonParams =  JSON.toJSONString(params);

        //调用跳转“收银台接口”
        String result = HttpUtils.sendPost(API_URL,jsonParams);

        //返回结果。如果成功，data数据为URL，拿到URL可以跳到收银台下单
        //{"code":1,"message":"成功","data":"https://www.bftong.vip/created/?data=NGbQ2jeA4zF3yA7%2FyVrbMVHGfBHB60E89xeklE19hzbHX5lXZWFVuJ%2Fj7ZRP4Ldf","success":true}

    }

    /**
     *  2 下单成功回调参数校验
     *
     *   参数是币富通平台返回给调用方，此处至模拟验签
     */
    private static void callback(){
        //币富通平台回调接口返回参数
        HashMap<String, Object> params = new HashMap();
        ////商户订单号
        params.put("apiOrderNo","22222222565656");
        //金额
        params.put("money","10");
        //交易状态。1：成功
        params.put("tradeStatus",1);
        //BFT 平台单号
        params.put("tradeId","bft-order00002");
        //唯一标识号
        params.put("uniqueCode","1112");
        //签名
        params.put("signature","eb0a0863bf0e93aec668a315b26aca01");

        //验签
        boolean isVerify = SignatureUtils.verify(params,KEY);
        System.out.println("验签结果："+isVerify);

        //TODO 如果true，继续处理自己平台业务逻辑

    }



}
