package com.tencent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;


public class Signer {
	
	String mAppId;
	String mAppSecret;
	String mTimestamp;
	String mMethod;
	String mHost;
	String mPath;
	Map<String, String> mUrlParams;
	Map<String, String> mBodyParams;

	public Signer(String appId, String appSecret, String timestamp, String method, String host, String path) {		
		mAppId = appId;
		mAppSecret = appSecret;
		mTimestamp = timestamp;
		mMethod = method;
		mHost = host;
        mPath = path;
        mUrlParams = new HashMap<String, String>();
        mBodyParams = new HashMap<String, String>();
	}
	
	public Signer(String appId, String appSecret, String timestamp, String method, String host, String path,  Map<String, String> urlParams, Map<String, String> bodyParams) {
		
		mAppId = appId;
		mAppSecret = appSecret;
		mTimestamp = timestamp;
		mMethod = method;
		mHost = host;
        mPath = path;
        mUrlParams = urlParams;
        mBodyParams = bodyParams;
	}
	
    private String _createSignKey( ) {
    	Mac sha256_HMAC;
		try {
			sha256_HMAC = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secret_key = new SecretKeySpec(mAppSecret.getBytes(StandardCharsets.UTF_8),
	        		"HmacSHA256");
	        sha256_HMAC.init(secret_key);
	        String hash = String.format("%064x", new BigInteger(1, 
	        		sha256_HMAC.doFinal(String.valueOf(mTimestamp).getBytes(StandardCharsets.UTF_8))));
	        //System.out.println(hash);
	        return hash;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
    }

    private String _createSignString() {
    	List<String> arr = new ArrayList<>();
    	arr.add(mAppId.toLowerCase());
    	arr.add(String.valueOf(mTimestamp).toLowerCase());
    	arr.add(mMethod.toLowerCase());
    	arr.add(mHost.toLowerCase());
    	arr.add(mPath.toLowerCase());
    	arr.add(_createDictHash(mUrlParams));
    	arr.add(_createDictHash(mBodyParams));
    	
    	String join = StringUtils.join(arr,"\n");
		return join;
    }

    private String _createDictHash(Map<String, String>paramDict) {
    	if (paramDict.isEmpty())
    		return "";
    	List<String> keys=new ArrayList<String>(paramDict.keySet());
    	List<String> arr = new ArrayList<String>();
    	if (keys.isEmpty())
    		return "";
    	Collections.sort(keys);
    	for (String key : keys) {
    		String temp = key + "=" + paramDict.get(key);
    		arr.add(temp);
    	}
    	
    	try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(StringUtils.join(arr,"\n").getBytes(StandardCharsets.UTF_8));
			return String.format("%064x", new BigInteger(1, messageDigest.digest()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return "";
    }
    
    public String sign( ) {
    	String signKey = _createSignKey();
    	String signString = _createSignString();
    	System.out.println("signKey:" + signKey + ", length = " + signKey.length());
    	System.out.println("signStr:" + signString);
    	
    	Mac sha256_HMAC;
		try {
			sha256_HMAC = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secret_key = new SecretKeySpec(signKey.getBytes(StandardCharsets.UTF_8),
	        		"HmacSHA256");
	        sha256_HMAC.init(secret_key);
	        String hash = String.format("%064x", new BigInteger(1, 
	        		sha256_HMAC.doFinal(signString.getBytes(StandardCharsets.UTF_8))));
	        //System.out.println(hash);
	        return hash;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return "";
    }
    
    public String getAuthorizationHeader( ) {
        String sig = sign();
        String ret = "algorithm=sha256&timestamp=" + mTimestamp + "&appid=" + mAppId + "&sig=" + sig;
        //return "algorithm=sha256&timestamp={}&appid={}&sig={}".format(self.timestamp, self.appId, sig)
        return ret;
    }
	
	
	public static void main(String []args) {
    	String timestamp = new String("1572322787906"); //int(round(time.time() * 1000))
    	Map<String, String> urlParms = new HashMap<String, String>(); //urlParms = {}
    	Map<String, String> bodyParams = new HashMap<String, String>(); //urlParms = {}
    	bodyParams.put("sAudio", "base64 data");
    	bodyParams.put("sSessionId", "uuid");
    	bodyParams.put("iSeq", String.valueOf(0));
    	bodyParams.put("cPosBits", String.valueOf(2));
    	
    	Signer signer = new Signer("appid", "appsecret", 
    			timestamp, "post", "smartedu.html5.qq.com", "/v1/asr", urlParms, bodyParams);
    	signer.sign();
     }

}
