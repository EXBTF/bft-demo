"""
BFT 调整收银台

签名算法规则：
1.参数名ASCII码从小到大排序（字典序）
2.如果参数的值为空不参与签名
3.参数名区分大小写
4.验证调用返回或BFT主动通知签名时，传送的signature参数不参与签名，将生成的签名与该signature值作校验
"""

import hashlib
import requests
import json
import sys


# 接入私钥，对应商户后台“接入密钥”（测试时，务必修改成自己的Key)
APP_KEY=""
#商户ID，对应商户平台的“商户编码”。 (测试时，务必修改成自己UID)
UID = ""
#API 接口
URL = "https://api.bifutong.vip/coin/pay/order/pay/checkout/counter";

class BFTCheckoutCounter():

    #对参数按照key=value的格式，并按照参数名ASCII字典序排序拼接成字符串stringA，最后拼接上key，返回拼接API密钥。
    def getSignString(self,signData):
        list=[]
        stringA=""

        #循环遍历字典数据的键值，取出存放到列表中
        for key in signData.keys():
            list.append(key)

        #对列表的对象进行排序，默认升序，即按照ASCII码从小到大排序
        list.sort()

        #循环遍历排序后的列表，根据键值取出字典键对应的值
        for i in list:
            stringA += i+"="+signData[i]+"&"

        stringA += "key"+"="+APP_KEY
        return stringA


    # 获取Md5 加密串
    def getSignMD5Data(self,data):
        #1 去除参数的值为空或者参数名为signature的数据
        signData={}
        for key, value in data.items():
            if value != "" and key != "signature":
                signData[key] = value

        #拼接参数
        signStr = self.getSignString(signData);

        #MD5 加密
        md=hashlib.md5()
        md.update(signStr.encode('utf-8'))
        signValue=md.hexdigest()
        return signValue


    '''
       http 请求
    '''
    def httpRequest(self,url, paramsJson):
        data = json.dumps(paramsJson).encode()

        #注意，一定是json
        headers = {"Content-Type":'application/json'}
        response = requests.post(url=url, headers=headers, data=data)
        print('response result:',response.json())

        return response





# 测试调用入口
if __name__ == "__main__":
    # 请求数据（根据实际情况，替换）
    if len(UID) == 0:
        print('UID 为空，请填写')

    elif len(APP_KEY) == 0:
        print('APP_KEY 为空，请填写')
    else:
        data={
            'uid':UID,
            'uniqueCode':'1112',
            'money':'10.00',
            'payType':'1',
            'orderId':'22222222565656'
        }

        api = BFTCheckoutCounter();
        #1 生成签名串
        sign = api.getSignMD5Data(data)
        print('sign result:',sign)

        #2 拼接发送参数
        data.update({"signature": sign})
        print('request data:',data)

        #3 发送请求
        api.httpRequest('https://api.bifutong.vip/coin/pay/order/pay/checkout/counter',data)
