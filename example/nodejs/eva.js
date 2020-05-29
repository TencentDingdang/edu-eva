const fsPromise = require("fs").promises
const uuidv1 = require("uuid").v1
const EduSigner = require("./Signer")
const request = require("request")

class EvaClient{
    constructor(appId, appSecret,audioBuffer, sText){
        this.appId = appId
        this.appSecret = appSecret
        this.iFrameSize = 6400
        this.iMaxSeq = 999999
        this.sText = sText
        this.audioBuffer = audioBuffer
        this.schema = "https"
        this.method = "post"
        this.host = "smartedu.html5.qq.com"
        this.path = "/v1/sentence_follow"
    }

    async sendFrame(){
        let iOffset = 0, iSeq = 0, cPosBits = 2
        let uuid = uuidv1(), finalRsp = {}
        while(iOffset < this.audioBuffer.length && iSeq <= this.iMaxSeq){
            let frameBuffer = this.audioBuffer.slice(iOffset, iOffset+this.iFrameSize)
            if(iOffset == 0){
                cPosBits = 1
            } else if (iOffset + this.iFrameSize >= this.audioBuffer.length || iSeq == this.iMaxSeq){
                cPosBits = 4
            } else {
                cPosBits = 2
            }
        
            let start = new Date().getTime()
            let stRsp = await new Promise((resolve, reject)=>{
                let body = {
                    sText:this.sText,
                    sAudio: frameBuffer.toString("base64"),
                    iAudioFormat: 0x92,
                    sSessionId: uuid,
                    iSeq:iSeq,
                    cPosBits: cPosBits,
                    iOffset:iOffset,
                    sGuid:"test-guid"
                }
                let timestamp = new Date().getTime()
                let signer = new EduSigner(this.appId,this.appSecret,timestamp, this.method,this.host,this.path,{},body)
                request.post(`${this.schema}://${this.host}${this.path}`, {
                    form: body,
                    headers:{
                        Authorization:`algorithm=sha256&appid=${this.appId}&timestamp=${timestamp}&sig=${signer.signature}`
                    },
                    json: true
                }, (err, response)=>{
                    if(err){
                        reject(err)
                    } else {
                        console.log(response.body)
                        resolve(response)
                    }
                })
            })
            if(stRsp.body.iEvaStatus == 0){
                finalRsp = stRsp.body
            }
            iSeq++
            iOffset += this.iFrameSize
            await this.sleep(200 - (new Date().getTime() - start))
        }
        return finalRsp
    }
    sleep(t){
        t = t>0?t:0
        return new Promise((resolve, reject)=>{
            setTimeout(resolve, t)
        })
    }
}

async function test(){
    try{
        const audioBuffer = await fsPromise.readFile("test.wav")
        asrClient = new EvaClient("your appid", "your appkey", audioBuffer,"how are you")
        let response = await asrClient.sendFrame()
        console.log(response)
    } catch(e){
        console.log("send error:", e)
    }
    
}

test()