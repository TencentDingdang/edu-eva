package com.tencent;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EvaDemo {
    public static void main(String []args) {
        if (args.length == 0) {
            System.out.println("You should pass an audio file path!");
            return;
        }
        Base64.Encoder encoder = Base64.getEncoder();
        Utils utils = new Utils();
        //TODO: 改成实际的文件地址
        byte[] bytes = utils.getFile(args[0]);
        if (bytes == null || bytes.length == 0) {
            System.out.println("File " + args[0] + " seems invalid or currupted!");
            return;
        }
        String encodedText = encoder.encodeToString(bytes);
        
        long currentTime=System.currentTimeMillis();
        String timeStamp = String.valueOf(currentTime);
        System.out.println(timeStamp);
        
        UUID uuid = UUID.randomUUID();
        
        Map<String, String> bodyParams = new HashMap<String, String>();
        bodyParams.put("sAudio", encodedText);
        bodyParams.put("sSessionId", uuid.toString());
        bodyParams.put("iAudioFormat", String.valueOf(146));
        bodyParams.put("sText", "This is my classmate. He is tall and thin. His student number is fifteen. He can play football with me. We are good friends.");
        bodyParams.put("sGuid", "test-guid");
        System.out.println("Session id = " + uuid.toString());
        
        Map<String, String> urlParams = new HashMap<String, String>();
        
        Signer signer = new Signer("your appid", "your appsecret", 
        		timeStamp, "post", "smartedu.html5.qq.com", "/v1/paragraph_follow_once", urlParams, bodyParams);
        String authHeader = signer.getAuthorizationHeader();
        
        System.out.println("authHeader = " + authHeader);
        
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", authHeader);
        
        
        String result = HttpUtil.sendPost("https://smartedu.html5.qq.com/v1/paragraph_follow_once", bodyParams, headers);
        System.out.println(result);
     }
}
