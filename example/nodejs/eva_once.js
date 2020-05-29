const fsPromise = require("fs").promises
const uuidv1 = require("uuid").v1
const EduSigner = require("./Signer")
const request = require("request")

class EvaClient{
    constructor(appId, appSecret,audioBuffer, sText){
        this.appId = appId
        this.appSecret = appSecret
        this.sText = sText
        this.audioBuffer = audioBuffer
        this.schema = "https"
        this.method = "post"
        this.host = "smartedu.html5.qq.com"
        this.path = "/v1/sentence_follow_once"
    }

    async sendOnce(){
        return new Promise((resolve, reject)=>{
            let body = {
                sGuid: "czzou_test",
                sText: this.sText,
                iAudioFormat: 0x92,
                sAudio: this.audioBuffer.toString("base64"),
                sSessionId: uuidv1()
            }
            let timestamp = new Date().getTime()
            
            let signer = new EduSigner(this.appId,this.appSecret,timestamp, this.method,this.host,this.path,{},body)
            request.post(`${this.schema}://${this.host}${this.path}`, {
                form:body,
                headers:{
                    Authorization:`algorithm=sha256&appid=${this.appId}&timestamp=${timestamp}&sig=${signer.signature}`
                },
                json: true
            }, (err, response)=>{
                if(err){
                    reject(err)
                } else {
                    resolve(response.body)
                }
            })
        })
    }

}

async function test(){
    try{
        const audioBuffer = await fsPromise.readFile("test.wav")
        asrClient = new EvaClient("your appid", "your appkey", audioBuffer,"how are you")
        let response = await asrClient.sendOnce()
        console.log(response)
    } catch(e){
        console.log("send error:", e)
    }
}

test()