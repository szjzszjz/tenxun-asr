package com.szjz.asr.offline.sdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tencent.cloud.asr.offline.sdk.http.OasrRequesterSender;
import com.tencent.cloud.asr.offline.sdk.model.OasrBytesRequest;
import com.tencent.cloud.asr.offline.sdk.model.OasrResponse;
import com.tencent.cloud.asr.realtime.sdk.config.AsrBaseConfig;
import com.tencent.cloud.asr.realtime.sdk.config.AsrInternalConfig;
import com.tencent.cloud.asr.realtime.sdk.config.AsrPersonalConfig;
import com.tencent.cloud.asr.realtime.sdk.model.enums.EngineModelType;
import com.tencent.cloud.asr.realtime.sdk.model.enums.VoiceFormat;
import com.tencent.cloud.asr.realtime.sdk.utils.ByteUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

//import com.tencent.cloud.asr.realtime.sdk.utils.ByteUtils;

/**
 * 录音文件识别（即：以前的离线识别）请求实例，可运行。
 *
 * <pre>
 * 并发说明：
 * 用户可根据根据需要自行改成多线程版本： 每个线程都通过new OasrRequesterSender()创建出自己的sender对象，
 * 然后用自己的sender对象发送请求获得回执，线程间互不干扰。从而形成并发的效果，加快请求发出的速度。
 * 此代码写起来很简单，暂时就不写到本例子中了。如确有需要让我们提供并发实例，欢迎随时联络我们。
 * </pre>
 *
 * @author iantang
 * @version 1.0
 */
public class OasrRequestSample {


    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private OasrRequesterSender oasrRequesterSender = new OasrRequesterSender();

    private static ObjectMapper mapper = new ObjectMapper();
    static {
        initBaseParameters();
    }

    public static void main(String[] args) {
        OasrRequestSample rasrRequestSample = new OasrRequestSample();
        rasrRequestSample.start();
//        String  key = "Gu5t9xGARNpq86cd98joQYCN3EXAMPLE";
//        String str = "GETcvm.tencentcloudapi.com/?Action=DescribeInstances&InstanceIds.0=ins-09dx96dg&Limit=20&Nonce=11886&Offset=0&Region=ap-guangzhou&SecretId=AKIDz8krbsJ5yKBZQpn74WFkmLPx3EXAMPLE&Timestamp=1465185768&Version=2017-03-12";
//        String hmac = genHMAC(str, key);
//        System.err.println("hamc:"+hmac);
    }

    private void start() {
        try {
            this.sendUrlRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // this.sendBytesRequest();
        System.exit(0);
    }

    /**
     * 指定语音文件的Url，发出请求。建议使用此方法。
     */
    private void sendUrlRequest() throws Exception {

//        String taskUrl = "https://asr.tencentcloudapi.com/?Action=CreateRecTask" +
//                "&ChannelNum=1" +
//                "&EngineModelType=8k_0" +
//                "&ResTextFormat=0" +
//                "&SourceType=0" +
//                "&Version=2019-06-14" +
//                "&Nonce="+new Random().nextInt(Integer.MAX_VALUE)+
//                "&Timestamp="+ System.currentTimeMillis() / 1000+
//                "&SecretId=AKID4vwf7KQyQnu9qM9gTzGI5f9PP9csMa6V"+
//                "&Url=https%3a%2f%2fruskin-1256085166.cos.ap-guangzhou.myqcloud.com%2ftest.wav";
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(); // TreeMap可以自动排序
        // 实际调用时应当使用随机数，例如：
        treeMap.put("Nonce", new Random().nextInt(Integer.MAX_VALUE));// 公共参数
        // 实际调用时应当使用系统当前时间，例如：
        treeMap.put("Timestamp", System.currentTimeMillis() / 1000);
        treeMap.put("ChannelNum",1);
        treeMap.put("EngineModelType","8k_0");
        treeMap.put("ResTextFormat","0");
        treeMap.put("SourceType","0");
        treeMap.put("Url","https://ruskin-1256085166.cos.ap-guangzhou.myqcloud.com/test.wav");
        treeMap.put("SecretId", "AKID4vwf7KQyQnu9qM9gTzGI5f9PP9csMa6V"); // 公共参数
        treeMap.put("Action", "CreateRecTask"); // 公共参数
        treeMap.put("Version", "2019-06-14"); // 公共参数
        treeMap.put("Region", "ap-guangzhou"); // 公共参数
        treeMap.put("Signature", sign(getStringToSign(treeMap), "JM9gy0wiUv74oLbcQ8bsqY2MQH4dx1oM", "HmacSHA1")); // 公共参数
        String taskUrl = getUrl(treeMap);
        System.out.println("taskUrl:"+taskUrl);
        String[] taskSplit = taskUrl.split("\\?");
        String  json = sendGET(taskSplit[0], taskSplit[1]);

        Task task = mapper.readValue(json, Task.class);
        System.err.println("response:" + task.getResponse().getData().getTaskId());



        TreeMap<String, Object> params = new TreeMap<String, Object>(); // TreeMap可以自动排序
        // 实际调用时应当使用随机数，例如：
        params.put("Nonce", new Random().nextInt(Integer.MAX_VALUE));// 公共参数
        // 实际调用时应当使用系统当前时间，例如：
        params.put("Timestamp", System.currentTimeMillis() / 1000);
        params.put("SecretId", "AKID4vwf7KQyQnu9qM9gTzGI5f9PP9csMa6V"); // 公共参数
        params.put("Action", "DescribeTaskStatus"); // 公共参数
        params.put("Version", "2019-06-14"); // 公共参数
        params.put("Region", "ap-guangzhou"); // 公共参数
        params.put("TaskId", "1396667"); // 业务参数
        params.put("Signature", sign(getStringToSign(params), "JM9gy0wiUv74oLbcQ8bsqY2MQH4dx1oM", "HmacSHA1")); // 公共参数
        String url = getUrl(params);
        System.out.println(url);

        OasrBytesRequest oasrBytesRequest = new OasrBytesRequest(
                url,
                "https://xuhai2-1255824371.cos.ap-chengdu.myqcloud.com/test.wav");
        oasrBytesRequest.setChannelNum(2); //设置为2声道语音，默认为1声道。目前仅8K语音支持2声道。
        OasrResponse oasrResponse = this.oasrRequesterSender.send(oasrBytesRequest);
        this.printReponse(oasrResponse);
        String[] split = url.split("\\?");
        String taskResponse = sendGET(split[0], split[1]);
        System.err.println(taskResponse);
    }

    /**
     * 从文件中读取语音数据，通过HttpBody发出请求。语音文件大小需小于5兆才可使用此方法。
     */
    private void sendBytesRequest() {
        byte[] content = ByteUtils.inputStream2ByteArray("test_wav/8k/8k.wav");
        OasrBytesRequest oasrBytesRequest = new OasrBytesRequest("http://xxx.xx.xxx", content);
        // oasrBytesRequest.setChannelNum(2); //特别设置为2声道，默认为1声道。目前仅8K语音支持2声道。
        OasrResponse oasrResponse = this.oasrRequesterSender.send(oasrBytesRequest);
        this.printReponse(oasrResponse);
    }

    private void printReponse(OasrResponse oasrResponse) {
        if (oasrResponse != null)
            System.out.println("Result is: " + oasrResponse.getOriginalText());
        else
            System.out.println("Result is null.");
    }

    /**
     * 初始化基础参数, 请将下面的参数值配置成你自己的值。
     * <p>
     * 参数获取方法可参考： <a href="https://cloud.tencent.com/document/product/441/6203">签名鉴权 获取签名所需信息</a>
     */
    private static void initBaseParameters() {
        // required, 必须配置
        AsrBaseConfig.appId = "1300494328";
        AsrBaseConfig.secretId = "AKID4vwf7KQyQnu9qM9gTzGI5f9PP9csMa6V";
        AsrBaseConfig.secretKey = "JM9gy0wiUv74oLbcQ8bsqY2MQH4dx1oM";
        AsrInternalConfig.SUB_SERVICE_TYPE = 0; // 0表示离线识别

        // optional，根据自身需求配置值
        AsrPersonalConfig.engineModelType = EngineModelType._8k_0;
        AsrPersonalConfig.voiceFormat = VoiceFormat.wav;

        // optional 可忽略
        // AsrBaseConfig.PRINT_CUT_REQUEST = true; // 是否打印中间的每个分片请求到控制台
        // AsrBaseConfig.PRINT_CUT_RESPONSE = true; // 是否打印中间的每个分片请求的结果到控制台
        // 默认使用自定义连接池，连接数可在AsrGlobelConfig中修改，更多细节参数，可直接修改源码HttpPoolingManager.java,然后自行打Jar包。
        // AsrGlobelConfig.USE_CUSTOM_CONNECTION_POOL = true;
    }

    public static String sign(String s, String key, String method) throws Exception {
        Mac mac = Mac.getInstance(method);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(CHARSET), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(s.getBytes(CHARSET));
        return DatatypeConverter.printBase64Binary(hash);
    }

    public static String getStringToSign(TreeMap<String, Object> params) {
        StringBuilder s2s = new StringBuilder("GETasr.tencentcloudapi.com/?");
        // 签名时要求对参数进行字典排序，此处用TreeMap保证顺序
        for (String k : params.keySet()) {
            s2s.append(k).append("=").append(params.get(k).toString()).append("&");
        }
        return s2s.toString().substring(0, s2s.length() - 1);
    }

    private final static String CHARSET = "UTF-8";

    public static String getUrl(TreeMap<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder("https://asr.tencentcloudapi.com/?");
        // 实际请求的url中对参数顺序没有要求
        for (String k : params.keySet()) {
            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
            url.append(k).append("=").append(URLEncoder.encode(params.get(k).toString(), CHARSET)).append("&");
        }
        return url.toString().substring(0, url.length() - 1);
    }


    /**
     * 使用 HMAC-SHA1 签名方法对data进行签名
     *
     * @param data 被签名的字符串
     * @param key  密钥
     * @return 加密后的字符串
     */
    public static String genHMAC(String data, String key) {
        byte[] result = null;
        try {
            //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
            SecretKeySpec signinKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            //生成一个指定 Mac 算法 的 Mac 对象
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            //用给定密钥初始化 Mac 对象
            mac.init(signinKey);
            //完成 Mac 操作
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = Base64.encodeBase64(rawHmac);

        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        } catch (InvalidKeyException e) {
            System.err.println(e.getMessage());
        }
        if (null != result) {
            return new String(result);
        } else {
            return null;
        }
    }


    public static String sendGET(String url, String param) {
        String result = "";//访问返回结果
        BufferedReader read = null;//读取访问结果
        try {
            //创建url
            URL realurl = new URL(url + "?" + param);
            //打开连接
            URLConnection connection = realurl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //建立连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段，获取到cookies等
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            read = new BufferedReader(new InputStreamReader(
                    connection.getInputStream(), "UTF-8"));
            String line;//循环读取
            while ((line = read.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (read != null) {//关闭流
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }
}
