- [语音识别接口（流式）](#----------)
  * [1.URL](#1url)
  * [2.Method](#2method)
  * [3.表单编码](#3----)
  * [4.表单内容](#4----)
  * [5.结果content-type](#5--content-type)
  * [6.结果内容](#6----)
  * [7.示例代码（Python）](#7-----python-)
- [语音识别接口（单包）](#----------)
  * [1.URL](#1url-1)
  * [2.Method](#2method-1)
  * [3.表单编码](#3-----1)
  * [4.表单内容](#4-----1)
  * [5.结果content-type](#5--content-type-1)
  * [6.结果内容](#6-----1)

# 语音识别接口（流式）

注意：大量的流式包调用，客户端应开启http的keep-alive选项以大幅提高接口性能，各语言版本开启方式不同，python版本的见本文档所附示例代码。   

完整音频单包识别请勿调用此接口，可使用后文描述的单包语音识别接口。

## 1.URL
https://smartedu.html5.qq.com/v1/asr  

## 2.Method
POST  

## 3.表单编码
application/x-www-form-urlencoded

## 4.表单内容
|字段|类型|描述|
|-|-|-|
|sSessionId|String|请求唯一标识字段，应传入一个uuid（备注1）|
|iSeq|Int|数据帧序列号，从0开始，每帧加1|
|cPosBits|Int|阶段标识，头包传1，过程传2， 尾包传4|
|sAudio|String|数据帧Base64编码的字符串 （备注2）|

备注1：  
异常情况下，调用方应将表单中的sSessionId与返回内容一同存储，反馈问题时需提供sSessionId的值   

备注2：  
目前只支持单通道、16kHz、16bit有符号的PCM音频

## 5.结果content-type

application/json

## 6.结果内容  

**字段描述**

|字段|类型|描述|
|-|-|-|
|iRet|Int|返回码，含义见下方描述|
|sMsg|String|错误信息|  
|results|Array< AsrResult \>|结果数组|    

结果数组中可能有多条结果对象，每个结果代表一个断句的结果。比如上传的句子中有三句话，那么结果数组中会包含三个AsrResult对象，如：  

```json  
{
    "iRet":0,
    "sMsg":"",
    "results":[{...}, {...}, {...}]
}
```

AsrResult字段  

|字段|类型|描述|
|-|-|-|
|text|String|识别的文本|
|begin_time|float|音频段的开始时间|  
|end_time|float|音频段的结束时间|   
|is_stable|boolean|是否稳定结果|  
|words|Array< String \>|识别出的单词数组|  
|words_begin_times|Array< float \>|单词开始时间|  
|words_end_times|Array< float \>|单词结束时间|  
|details|Array< Array < WordInfo \> \>|单词级别的nbest信息|  
|confidence|float|本段结果置信度|    

只有is_stable为true的时候，此结果对象中才会有内容。  

结果Array<AsrResult\>中，各个AsrResult对象的音频段开始时间、结束时间，是一个顺序的，非连续的区间，例如一个15秒的音频，返回了三个结果对象， 可能出现：  
`第一个结果在0.5s～7.3s，第二个结果在8.2s～10.1s，第三个结果在12.8s～14.4s`  

单词数组是识别出来的单词数组，如：  
```json
["how", "are", "you"]
```

单词级别的nbest信息是一个**二维数组**， 代表单词数组中每个单词的nbest候选集。候选集中可能出现符号`<eps>`，代表空白字符。   
这里要注意的是， details数组的长度可能比words数组的长度要长。例如，语音内容比较含糊，可能是`how are you`，也可能是`how old are you`，而asr系统认为前者的概率更大，那么单词数组(words)的长度是3，details长度为4。  
在details数组的第二个候选集中，包含了`<eps>`和`old`两个候选单词，其中`<eps>`的置信度比`old`要大，而空字符不出现在最终结果中，所以worlds数组长度为3，details数组长度为4。  
例如：  
```json
{
    "iRet":0,
    "sMsg":"",
    "results":[{
        "text":" HOW ARE YOU",
        ···
        "words":["HOW", "ARE", "YOU"],
        "details":[
            [{"word":"HOW","confidence":0.9},{"word":"HI","confidence":0.1}],
            [{"word":"OLD","confidence":0.4},{"word":"<eps>","confidence":0.6}]
            [{"word":"ARM","confidence":0.1},{"word":"ARE","confidence":0.95}],
            [{"word":"YOU","confidence":0.8},{"word":"YOUR","confidence":0.2}]
        ]
        ···
    }]
}
```   

以上示例省略了其它字段。 其中第二条，`<eps>`置信度最高的nbest结果，虽然没有出现在最终结果中，但是其返回也是有意义的。 业务可以从中知道，这句话除了可能是`how are you`，还有可能是`how old are you`，只是置信度较低。

WordInfo字段  

|字段|类型|描述|
|-|-|-|
|word|String|单词字符串|
|begin_time|float|单词开始时间|  
|end_time|float|单词结束时间|  
|confidence|float|单词置信度|   

错误码  

|错误码|描述|
|-|-|
|0|成功|
|100001、100002|内部接口错误|
|100003～100005|iSeq不合法|
|100006～100009|该sSessionId数据帧出错，乱序、超时、缺包等|
|990000|服务器未知错误|
|990001|接口鉴权失败|

**示例内容**    
```json
{
    "iRet":0,
    "sMsg":"",
    "results":[
        {
            "text":" MY FRIEND",
            "begin_time":1.1399999856948853,
            "end_time":2.0399999618530273,
            "is_stable":true,
            "words":[
                "MY",
                "FRIEND"
            ],
            "words_begin_times":[
                1140,
                1500
            ],
            "words_end_times":[
                1500,
                2040
            ],
            "details":[
                [
                    {
                        "word":"MY",
                        "begin_time":1.1399999856948853,
                        "end_time":1.5,
                        "confidence":1
                    }
                ],
                [
                    {
                        "word":"FRIEND",
                        "begin_time":1.5,
                        "end_time":2.0399999618530273,
                        "confidence":1
                    }
                ]
            ],
            "confidence":1
        }
    ]
}
```  

备注1:  
稳定结果是最终的识别结果, 只有在is_stable为true的时候才存在。   
备注2:  
nbest结果是一个二维数组，内容是句子中每个单词的候选集， 每个候选集中置信度最高的单词会出现在结果的words数组中。<eps>符号代表空字符，若一个单词候选集中置信度最高的选项是<eps>，则这个单词被认为是空文本， 不会出现在words中。

## 7.示例代码（Python）

Python版本：3.x   
依赖模块：requests、上一小节中的签名计算示例类   
安装：`pip3 install requests`

```python
import sys, time, uuid, base64
import requests
from Signer import Signer 

class AsrClient:
    def __init__(self, appId, appSecret, audioBuffer, session):
        self.appId = appId
        self.appSecret = appSecret
        self.iFrameSize = 6400
        self.audioBuffer = audioBuffer
        self.schema = "https"
        self.method = "post"
        self.host = "smartedu.html5.qq.com"
        self.path = "/v1/asr"
        self.session = session

    def send(self):
        iOffset, iSeq, cPosBits, uuidv1 = 0, 0, 2, uuid.uuid1()
        allLen = len(self.audioBuffer)
        while iOffset < allLen:
            timestamp = int(round(time.time() * 1000))
            frameBuffer = self.audioBuffer[iOffset:iOffset + self.iFrameSize]
            frameBase64 = base64.b64encode(frameBuffer).decode()
            if iOffset == 0:
                cPosBits = 1
            elif (iOffset+self.iFrameSize) < allLen:
                cPosBits = 2
            else:
                cPosBits = 4
            bodyParams = {
                "sAudio": frameBase64,
                "sSessionId": uuidv1,
                "iSeq": iSeq,
                "cPosBits": cPosBits
            }
            signer = Signer(self.appId, self.appSecret, timestamp, self.method, self.host, self.path, {}, bodyParams )
            authHeader = signer.getAuthorizationHeader()
            response = self.session.post(url="{}://{}{}".format(self.schema, self.host, self.path),data=bodyParams,headers={"Authorization": authHeader})
            now = int(round(time.time() * 1000))
            diff = now - timestamp
            print("{} {}/{}".format(diff, iOffset, allLen), bodyParams["iSeq"], response.text)
            iSeq += 1
            iOffset += self.iFrameSize
            if(diff < 200):
                time.sleep((200 - diff)/1000)

if __name__ == "__main__":
    # 注意：使用此方法以开启http的keep-alive选项，能避免多次建立、断开tcp连接的开销，大幅提升接口性能
    # http的keep-alive，各语言实现方式不同，但都应该开启
    session = requests.session()
    with open("./test.pcm", "rb") as audioBuffer:
         asrClient = AsrClient("分配的appid", "分配的secret", audioBuffer.read(), session)
         asrClient.send()
    with open("./test2.pcm", "rb") as audioBuffer:
         asrClient = AsrClient("分配的appid", "分配的secret", audioBuffer.read(), session)
         asrClient.send()
```  

# 语音识别接口（单包）  

语音识别的单包识别接口

## 1.URL
https://smartedu.html5.qq.com/v1/asr_once  

## 2.Method
POST  

## 3.表单编码
application/x-www-form-urlencoded

## 4.表单内容
|字段|类型|描述|
|-|-|-|
|sSessionId|String|请求唯一标识字段，应传入一个uuid（备注1）|
|sAudio|String|完整音频数据的Base64编码的字符串 （备注2）|

备注1：  
异常情况下，调用方应将表单中的sSessionId与返回内容一同存储，反馈问题时需提供sSessionId的值   

备注2：  
目前只支持单通道、16kHz、16bit有符号的PCM音频

## 5.结果content-type

application/json

## 6.结果内容  
与流式识别结果相同