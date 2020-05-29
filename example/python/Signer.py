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
        signKey, signString = self._createSignKey(), self._createSignString()
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
    timestamp = 1572322787906 #int(round(time.time() * 1000))
    urlParms = {}
    bodyParams = {
        "sAudio": "base64 data",
        "sSessionId": "uuid",
        "iSeq":0,
        "cPosBits": 2
    }
    signer = Signer("appid", "appsecret", timestamp, "post", "smartedu.html5.qq.com", "/v1/asr", urlParms, bodyParams )
    print(signer.sign())
    #print(signer.getAuthorizationHeader())