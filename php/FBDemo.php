<?php

/**
*
*  法币调整收银台Demo
* 签名算法规则：
* 1.参数名ASCII码从小到大排序（字典序）
* 2.如果参数的值为空不参与签名
* 3.参数名区分大小写
* 4.验证调用返回或BFT主动通知签名时，传送的signature参数不参与签名，将生成的签名与该signature值作校验
*/


/** 接入私钥，对应商户后台“接入密钥”（测试时，务必修改成自己的Key)  */
define("APP_KEY", "");
/** 商户ID，对应商户平台的“商户编码”。 (测试时，务必修改成自己UID) */
define("UID", "");
/** 易盾短信服务发送接口地址 */
define("API_URL", "https://api.bifutong.vip/coin/pay/order/pay/checkout/counter");


/**
 *  生成加密串
 *
 */
 function genSignature($param = [])
     {
        unset($param['sign']);
         ksort($param);
        $signstr = '';
        if (is_array($param)) {
            foreach ($param as $key => $value) {
               if ($value == '' || $value == 'signature') {
                     continue;
                }
                $signstr .= $key . '=' . $value . '&';
            }
            $signstr = rtrim($signstr, '&');
        }
        $signstr .= '&key='.APP_KEY;

        echo("signstr:".$signstr);
        echo "\r\n";

      return md5($signstr);;
     }


/**
*
* HTTP 请求
*/
function post($url,$data = [],$json = false)
{
    if($json){
        $str = 'application/json';
        $data = json_encode($data);
    }else{
        $str = 'application/x-www-form-urlencoded';
        $data = http_build_query($data);
    }

    $options['http'] = array(
        'timeout' => 5,
        'method'  => 'POST',
        'header'  => "Content-Type: $str;charset=utf-8",
        'content' => $data,
    );
    $context = stream_context_create($options);
    $result =  file_get_contents($url, false, $context);
    if ($result === FALSE) {
        return array("code" => 500, "msg" => "file_get_contents failed.");
    } else {
        return json_decode($result, true);
    }

}


/**
 *  测试入口
 */
 function main(){

    if(empty(UID)){
       echo('#--------UID 为空，请填写');
       return;
    }
    if(empty(APP_KEY)){
        echo('#--------APP_KEY 为空，请填写');
        return;
    }

       $params = array(
        "uid" => UID,
        "money" => "10",
		"uniqueCode" => "1112",
		"payType" => "1",
	    "orderId" => "22222222565656"
    );

    // 生成签名
    $signstr = genSignature($params);
     echo('md5 result:'.$signstr);
     echo "\r\n";

    $params["signature"] = $signstr;


    $paramJSON = json_encode($params);
    echo("request data:".$paramJSON);
    echo "\r\n";

  	$responResult = post1($API_URL,$params,true);
    echo 'reponse result:'.$responResult;

}

 main();

?>