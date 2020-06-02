- [句子跟读评测接口(流式)](#句子跟读评测接口(流式))
  * [1.URL](#1url)
  * [2.Method](#2method)
  * [3.表单编码](#3.表单编码)
  * [4.表单内容](#4.表单内容)
  * [5.结果content-type](#5--content-type)
  * [6.结果内容](#6.结果内容)
  * [7.示例代码](#7.示例代码)
- [句子跟读评测接口(单包)](#句子跟读评测接口(单包))
  * [1.URL](#1url-1)
  * [2.Method](#2method-1)
  * [3.表单编码](#3-----1)
  * [4.表单内容](#4-----1)
  * [5.结果content-type](#5--content-type-1)
  * [6.结果内容](#6-----1)
- [单词跟读评测接口(流式)](#单词跟读评测接口(流式))
  * [1.URL](#1url-2)
  * [2.其它](#2--)
- [单词跟读评测接口(单包)](#单词跟读评测接口(单包))
  * [1.URL](#1url-3)
  * [2.其它](#2---1)
- [段落跟读评测接口(单包)](#段落跟读评测接口(单包))
  * [1.URL](#1url-4)
  * [2.其它](#2---2)

# 句子跟读评测接口(流式)

## 1.URL
https://smartedu.html5.qq.com/v1/sentence_follow  

## 2.Method  

POST
## 3.表单编码
application/x-www-form-urlencoded  

## 4.表单内容
|字段|类型|描述|
|-|-|-|
|sText|String|参考文本|
|sAudio|String|数据帧Base64编码的字符串|
|iAudioFormat|Int|音频格式，0x92: pcm 0x1000792:speex 0x5000000:mp3|
|sSessionId|String|请求唯一标识字段，应传入一个uuid|
|iSeq|Int|数据帧序列号，从0开始，每帧加1|
|cPosBits|Int|阶段标识，头包传1，过程传2， 尾包传4，单次传7|
|iOffset|Int|音频数据偏移量，单位为字节|  

注意：所有格式均只支持单通道、16kHz、16bit的音频  

## 5.结果content-type

application/json

## 6.结果内容  

**字段描述**  

|字段|类型|描述|
|-|-|-|
|iRet|Int|返回码，含义见下方描述|
|sMsg|String|错误信息|  
|fScore|float|总得分，0～100，下同|  
|vLines|Array< SpeechEvaluateLines \>|每行的评测结果|  
|fFluency|float|流畅度|  
|fIntegrity|float|完整度|  
|fPronunciation|float|发音分数|  
|fRhythm|float|韵律度|  
|sSessionUuid|String|唯一标识|  
|iEvaStatus|Int|评测状态 1-评测中，0-评测完成|  
|iSeq|Int|序列号|  

SpeechEvaluateLines字段   

|字段|类型|描述|
|-|-|-|
|sText|String|该行对应的参考文本|
|fScore|float|得分|  
|vWords|Array< SpeechEvaluateWord \>|本行每个单词的评测详情|   

SpeechEvaluateWord字段    

|字段|类型|描述|
|-|-|-|
|sText|String|单词文本|
|fScore|float|得分|  
|vPhones|Array< EvaluatePhone \>|音素评测列表|   
|bIsUNK|boolean|UNKNOWN不支持的word|  
|bIsChinesePron|boolean|是否为中文发音|    

EvaluatePhone字段  

|字段|类型|描述|
|-|-|-|
|sPhone|String|音素文本|
|iStatus|Int|0 正确, 1错读, 2漏读|  
|iPos|Int|-1表示该发音不在支持列表|  
|iLen|Int|-1表示该发音不在支持列表|  
|fScore|float|音素分 0～1|   

错误码  

|字段|描述|
|-|-|
|0|成功|
|140001、140002|内部接口错误|
|140003～140007|音频数据错误|
|140008|参考文本为空|
|990000|服务器未知错误|
|990001|接口鉴权失败|

**示例内容**   

```json
{
	"iRet": 0,
	"sMsg": "",
	"fScore": 80.0199966430664,
	"vLines": [{
		"sText": "Do you",
		"fScore": 79.72953796386719,
		"vWords": [{
			"sText": "Do",
			"fScore": 99.85199737548828,
			"bIsBad": false,
			"vPhones": [{
				"sPhone": "ˈd",
				"iStatus": 0,
				"iPos": 0,
				"iLen": 1,
				"fScore": 0.9988825917243958,
				"bIsBad": false
			}, {
				"sPhone": "uː",
				"iStatus": 0,
				"iPos": 1,
				"iLen": 1,
				"fScore": 0.998039960861206,
				"bIsBad": false
			}],
			"bIsUNK": false,
			"bIsChinesePron": false
		}, {
			"sText": "you",
			"fScore": 97.91004943847656,
			"bIsBad": false,
			"vPhones": [{
				"sPhone": "ˈj",
				"iStatus": 0,
				"iPos": 0,
				"iLen": 1,
				"fScore": 0.9978115558624268,
				"bIsBad": false
			}, {
				"sPhone": "uː",
				"iStatus": 0,
				"iPos": 1,
				"iLen": 2,
				"fScore": 0.8279862999916077,
				"bIsBad": false
			}],
			"bIsUNK": false,
			"bIsChinesePron": false
		}]
	}],
	"fFluency": 88.36095428466797,
	"fIntegrity": 100,
	"fPronunciation": 79.72953796386719,
	"sSessionUuid": "7f6cd6e0-d91b-4085-bdfb-93c4a9a87e4f",
	"iEvaStatus": 0,
	"iSeq": 11,
	"fRhythm": 0
}
```    

## 7.示例代码  
请参考语音识别的示例代码， 调用方法一致， 跟读评测接口比语音识别接口多出sText、iAudioFormat、iOffset三个表单字。  
大量调用推荐开启http的keep-alive。 


# 句子跟读评测接口(单包)

## 1.URL
https://smartedu.html5.qq.com/v1/sentence_follow_once  

## 2.Method  

POST
## 3.表单编码
application/x-www-form-urlencoded  

## 4.表单内容
|字段|类型|描述|
|-|-|-|
|sText|String|参考文本|
|sAudio|String|数据帧Base64编码的字符串|
|iAudioFormat|Int|音频格式，0x92: pcm 0x1000792:speex 0x5000000:mp3|
|sSessionId|String|请求唯一标识字段，应传入一个uuid|

## 5.结果content-type

application/json

## 6.结果内容  
同句子跟读流式识别结果内容  

# 单词跟读评测接口(流式)

## 1.URL
https://smartedu.html5.qq.com/v1/word_follow  

## 2.其它
同句子跟读文档  

# 单词跟读评测接口(单包)

## 1.URL
https://smartedu.html5.qq.com/v1/word_follow_once  

## 2.其它
同句子跟读单包识别文档  

# 段落跟读评测接口(单包)

## 1.URL
https://smartedu.html5.qq.com/v1/paragraph_follow_once 

## 2.其它
同句子跟读单包识别文档  

