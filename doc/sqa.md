# 情景提问接口（单包）  

## 1.URL
https://smartedu.html5.qq.com/v1/situation_qa_once  

## 2.Method
POST  

## 3.表单编码
application/x-www-form-urlencoded

## 4.表单内容
|字段|类型|描述|
|-|-|-|
|sSessionId|String|请求唯一标识字段，应传入一个uuid（备注1）|
|sAudio|String|数据帧Base64编码的字符串 （备注2）|
|sRefText|String|参考文本数组序列化字符串（备注3）|

备注1：  
异常情况下，调用方应将表单中的sSessionId与返回内容一同存储，反馈问题时需提供sSessionId的值   

备注2：  
目前只支持单通道、16kHz、16bit有符号的PCM音频  

备注3:  
参考文本应该传入一个序列化成字符串的数组,文本内容为该题的所有答案列表。  

例如  
```
题目  
You have some problems in Maths. Ask your teacher two questions for help.  

参考答案列表  
1. Can you help me with my Maths?  
2. How can I improve my Maths?  
3. I don't know how to improve my Maths. Can you help me?  

则请求时，传入的sRefText为  
"[\"Can you help me with my Maths?\",\"How can I improve my Maths?\",\"I don't know how to improve my Maths. Can you help me?\"]  
```

## 5.结果content-type

application/json

## 6.结果内容  

**字段描述**

|字段|类型|描述|
|-|-|-|
|iRet|Int|返回码，含义见下方描述|
|sMsg|String|错误信息|  
|fScore|Float|得分，分值分布在0～100（目前是0、50、100分段分）|  
|sSessionUuid|String|请求ID|    

错误码  

|错误码|描述|
|-|-|
|0|成功|
|150001~150013|内部系统错误|
|150004~150016|音频或参考文本为空|
|990000|服务器未知错误|
|990001|接口鉴权失败|