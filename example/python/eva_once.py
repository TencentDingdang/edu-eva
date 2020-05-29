import sys, time, uuid, base64
import requests
from Signer import Signer 

class EvaClient:
    def __init__(self, appId, appSecret, audioBuffer, sText, session):
        self.appId = appId
        self.appSecret = appSecret
        self.audioBuffer = audioBuffer
        self.schema = "https"
        self.method = "post"
        self.host = "smartedu.html5.qq.com"
        self.path = "/v1/paragraph_follow_once"
        self.sText = sText
        self.session = session

    def send(self):
        uuidv1 = uuid.uuid1()
        print(uuidv1)
        timestamp = int(round(time.time() * 1000))
        bodyParams = {
                "sAudio": base64.b64encode(self.audioBuffer).decode(),
                "sSessionId": uuidv1,
                "iAudioFormat": 146,#0x5000000,
                "sText":self.sText,
                "sGuid":"test-guid"
            }
        signer = Signer(self.appId, self.appSecret, timestamp, self.method, self.host, self.path, {}, bodyParams )
        authHeader = signer.getAuthorizationHeader()
        response = self.session.post(url="{}://{}{}".format(self.schema, self.host, self.path),data=bodyParams,headers={"Authorization": authHeader})
        now = int(round(time.time() * 1000))
        print(now - timestamp , response.elapsed.microseconds, response.text)

if __name__ == "__main__":
    # 注意：使用此方法以开启http的keep-alive选项，能避免多次建立、断开tcp连接的开销，大幅提升接口性能
    # http的keep-alive，各语言实现方式不同，但都应该开启
    session = requests.session()
    with open("./test.wav", "rb") as audioBuffer:
         asrClient = EvaClient("you appid", "your appsecret", audioBuffer.read(),"how are you", session)
         asrClient.send()