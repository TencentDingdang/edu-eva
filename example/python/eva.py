import sys, time, uuid, base64
import requests
from Signer import Signer 

class EvaClient:
    def __init__(self, appId, appSecret, audioBuffer, sText, session):
        self.appId = appId
        self.appSecret = appSecret
        self.iFrameSize = 6400
        self.audioBuffer = audioBuffer
        self.schema = "https"
        self.method = "post"
        self.host = "smartedu.html5.qq.com"
        self.path = "/v1/word_follow"
        self.sText = sText
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
                "cPosBits": cPosBits,
                "iOffset":iOffset,
                "iAudioFormat": 146,
                "sText":self.sText,
                "sGuid":"test-guid"
            }
            signer = Signer(self.appId, self.appSecret, timestamp, self.method, self.host, self.path, {}, bodyParams )
            authHeader = signer.getAuthorizationHeader()
            response = self.session.post(url="{}://{}{}".format(self.schema, self.host, self.path),data=bodyParams,headers={"Authorization": authHeader})
            now = int(round(time.time() * 1000))
            diff = now - timestamp
            print("{} {}/{}".format(diff, iOffset, allLen), len(frameBase64), bodyParams["iSeq"],cPosBits, response.elapsed.microseconds, response.text)
            iSeq += 1
            iOffset += self.iFrameSize
            gap = 60
            if(diff < gap):
                time.sleep((gap-diff)/1000)

if __name__ == "__main__":
    # 注意：使用此方法以开启http的keep-alive选项，能避免多次建立、断开tcp连接的开销，大幅提升接口性能
    # http的keep-alive，各语言实现方式不同，但都应该开启
    session = requests.session()
    with open("./test.wav", "rb") as audioBuffer:
         asrClient = EvaClient("you appid", "your appsecret", audioBuffer.read(),"how are you", session)
         asrClient.send()