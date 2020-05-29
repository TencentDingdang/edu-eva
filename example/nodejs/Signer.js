const crypto = require("crypto")

module.exports =  class EduSigner{
    constructor(appId,appSecret, timestamp, method, host, path, urlParams = {}, bodyParams = {}){
        this.appId = appId
        this.appSecret = appSecret
        this.timestamp = timestamp
        this.method = method
        this.host = host
        this.path = path
        this.urlParams = urlParams
        this.bodyParams = bodyParams
    }
    get signature() {
        if(this._sig) return this._sig
        let signKey = this.createSignKey(),signString = this.createSignString(), hmacsha256 = crypto.createHmac("sha256", signKey)
        hmacsha256.update(signString)
        this._sig = hmacsha256.digest("hex")
        return this._sig
    }

    createSignKey(){
        if(!this.timestamp || !this.appSecret) throw new Error("auth param error")
        let hmacsha256 = crypto.createHmac("sha256", this.appSecret)
        hmacsha256.update(this.timestamp.toString())
        return hmacsha256.digest("hex")
    }

    createSignString(){
        if(!this.appId || !this.timestamp) throw new Error("auth param error")
        let arr = []
        arr.push(this.appId.toLowerCase())
        arr.push(this.timestamp.toString().toLowerCase())
        arr.push(this.method.toLowerCase())
        arr.push(this.host.toLowerCase())
        arr.push(this.path.toLowerCase())
        arr.push(this.createKVHash(this.urlParams))
        arr.push(this.createKVHash(this.bodyParams))
        return arr.join("\n")
    }

    createKVHash(kv){
        let keys = Object.keys(kv), arr = [], hash256 = crypto.createHash("sha256")
        if(!keys.length) return ""
        keys.sort().forEach((key)=>{
            arr.push(`${key}=${kv[key]}`)
        })
        hash256.update(arr.join("\n"))
        return hash256.digest("hex")
    }
}
