# 自由表达接口(单包)

## 1.URL
https://smartedu.html5.qq.com/v1/presentation_once  

## 2.Method
POST  

## 3.表单编码
application/x-www-form-urlencoded

## 4.表单内容
|字段|类型|描述|
|-|-|-|
|sSessionId|String|请求唯一标识字段，应传入一个uuid（备注1）|
|sAudio|String|数据帧Base64编码的字符串 （备注2）|
|sRefText|String|参考文本 **一维数组** 序列化字符串（备注3）|

备注1：  
异常情况下，调用方应将表单中的sSessionId与返回内容一同存储，反馈问题时需提供sSessionId的值   

备注2：  
目前只支持单通道、16kHz、16bit有符号的PCM音频  

备注3:  
与问答、情景提问接口一致,均传入其序列化后的字符串。  

样例：   

```
题目
1. Where do you want to go? 
2. What activities would you like to do? 

参考答案列表
1. I want to go to Beijing during the summer holiday. I will go with my friends. We will take the train to get to Beijing. We will go to the Great Wall, the Palace Museum and the Summer Palace. I think we will have a good time there.
2. I want to go to Tokyo. I will go with my parents. We will get there by plane. There are many famous hotels and shopping malls in Tokyo. I want to buy a camera and a mobile phone. I think we will have a great time there.
3. I want to go to Bangkok. I will go there by air. I will go to visit the famous palaces there. And I will also go to the beaches there. They are very beautiful. 

则请求时，传入的sRefText为 
"[\"I want to go to Beijing during the summer holiday. I will go with my friends. We will take the train to get to Beijing. We will go to the Great Wall, the Palace Museum and the Summer Palace. I think we will have a good time there.\",\"I want to go to Tokyo. I will go with my parents. We will get there by plane. There are many famous hotels and shopping malls in Tokyo. I want to buy a camera and a mobile phone. I think we will have a great time there.\",\"I want to go to Bangkok. I will go there by air. I will go to visit the famous palaces there. And I will also go to the beaches there. They are very beautiful.\"]"

```

## 5.结果content-type

application/json

## 6.结果内容  

**字段描述**

|字段|类型|描述|
|-|-|-|
|iRet|Int|返回码，含义见下方描述|
|sMsg|String|错误信息|  
|fScore|Float|得分，分值分布在0～100（目前是0、20、40、60、80、100分段分）|  
|sSessionUuid|String|请求ID|    

错误码  

|错误码|描述|
|-|-|
|0|成功|
|160001~160002|音频或参考文本为空|
|990000|服务器未知错误|
|990001|接口鉴权失败|