# 统一接口鉴权  

所有调用接口之前，需要联系腾讯接口人申请具有对应接口权限的 appid、 appsecret，计算签名串并附加到http请求头的Authorization属性中

## 1.鉴权请求头格式

接口鉴权方式为在http请求的标准请求头 Authorization 中添加签名等鉴权属性  

`Authorization = algorithm=sha256&timestamp=${timestamp}&appid=${appid}&sig=${sig}`  

(由${}包裹起来的代表一个变量)   

- **algorithm** 固定字符串 sha256
- **timestamp**  从 UTC（GMT 格林威治时间）1970年1月1日0时0分0秒起至现在的总毫秒数，签名有效期五分钟   
- **appid** 申请的appid
- **sig** 签名字符串

## 2.计算SignKey   

`SignKey = hex( hmac-sha256( timestamp,  appsecret) )`  

- timestamp 第1节中生成的同一个时间戳
- appsecret 申请的appsecret

## 3.计算SignString   

待签名字符串SignString的计算分为下面几个步骤

### 计算Url参数hash  

若请求无任何url中传递的query参数，则 `urlhash=""`  

将url参数的key按assci码顺序排序，按 `a=b\nc=d\e=f` 格式组装  

`urlhash=hex( sha-256( "a=b\nc=d\e=f" ) )`

### 计算Body参数hash  

若请求无任何body表单参数，则 `bodyhash=""`  

将表单参数的key按assci码顺序排序，按 `a=b\nc=d\e=f` 格式组装  

`bodyhash=hex( sha-256( "a=b\nc=d\e=f" ) )`

### 计算SignString

`SignString = appid\ntimestamp\nmethod\nhost\npath\nurlhash\nbodyhash`   
- appid 申请的appid
- timestamp 第1节中生成的同一个时间戳
- method http请求方法，如post、get等，小写
- host 请求域名，小写，如`smartedu.sparta.html5.qq.com`
- path 请求路径，小写，如`/v1/asr`
- urlhash 3.1中计算的url参数hash
- bodyhash 3.2中计算的body参数hash

## 4.计算签名串sig    

根据第2、3两节中计算的SignString和SignKey计算签名串sig  

`sig = hex( hmac-sha256( ${SignString}, ${SignKey} ) )`    

## 5.最终的鉴权字符串

`Authorization = algorithm=sha256&timestamp=${timestamp}&appid=${appid}&sig=${sig}`   

调用接口时将Authorization的值写入http request header的Authorization属性中。  

## 6.示例代码（Python）  

Python版本：3.x  

```python  
import hashlib, hmac
import time

class Signer:
    def __init__(self, appId,appSecret, timestamp, method, host, path, urlParams = {}, bodyParams = {}):
        self.appId = appId
        self.appSecret = appSecret
        self.timestamp = timestamp
        self.method = method
        self.host = host
        self.path = path
        self.urlParams = urlParams
        self.bodyParams = bodyParams

    def sign(self):
        '''  签名计算方法 '''
        signKey, signString = self._createSignKey(), self._createSignString(), 
        hmacsha256 = hmac.new(signKey.encode(), digestmod=hashlib.sha256)
        hmacsha256.update(signString.encode())
        return hmacsha256.hexdigest()
    def getAuthorizationHeader(self):
        sig = self.sign()
        return "algorithm=sha256&timestamp={}&appid={}&sig={}".format(self.timestamp, self.appId, sig)
    
    def _createSignKey(self):
        '''  计算signKey '''
        if self.timestamp is None or self.appSecret is None:
            raise Exception("no timestamp or appSecret")
        hmacsha256 = hmac.new(self.appSecret.encode(), digestmod=hashlib.sha256)
        hmacsha256.update(str(self.timestamp).encode())
        return hmacsha256.hexdigest()

    def _createSignString(self):
        '''  计算signString '''
        if self.appId is None or self.timestamp is None:
            raise Exception("no timestamp or appId")
        arr = []
        arr.append(self.appId.lower())
        arr.append(str(self.timestamp).lower())
        arr.append(str(self.method).lower())
        arr.append(str(self.host).lower())
        arr.append(str(self.path).lower())
        arr.append(self._createDictHash(self.urlParams))
        arr.append(self._createDictHash(self.bodyParams))
        return "\n".join(arr)

    def _createDictHash(self, paramDict):
        ''' 计算url/body的dict的hash256 '''
        if paramDict is None:
            return ""
        keys, arr, hash256 = list(paramDict.keys()), [], hashlib.sha256()
        if len(keys) == 0:
            return ""
        keys.sort()
        for key in keys:
            arr.append("{}={}".format(key, str(paramDict[key])))
        hash256.update("\n".join(arr).encode())
        return hash256.hexdigest()
        
if __name__ == '__main__':
    timestamp = int(round(time.time() * 1000))
    urlParms = {}
    bodyParams = {
        "sAudio": "base64 data",
        "sSessionId": "uuid",
        "iSeq":0,
        "cPosBits": 2
    }
    signer = Signer("appid", "appsecret", timestamp, "post", "smartedu.html5.qq.com", "/v1/asr", urlParms, bodyParams )
    print(signer.sign())
    print(signer.getAuthorizationHeader())
```