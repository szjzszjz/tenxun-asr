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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

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
@Slf4j
public class OasrRequestSample {


    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private final static String CHARSET = "UTF-8";
    private static final String APPID = "1300494328";
    private static final String SECRETID = "AKID4vwf7KQyQnu9qM9gTzGI5f9PP9csMa6V";
    private static final String SECRETKEY = "JM9gy0wiUv74oLbcQ8bsqY2MQH4dx1oM";
    private static final String DOMAIN = "https://asr.tencentcloudapi.com/?";
    private static final int NONCE = new Random().nextInt(Integer.MAX_VALUE);
    private static final Long TIMESTAMP = System.currentTimeMillis() / 1000;
    //腾讯本身的中文测试语音
//    private static final String TESTURL = "https://ruskin-1256085166.cos.ap-guangzhou.myqcloud.com/test.wav";
    //其他网站测试语音
    //中英混合
//    private static final String TESTURL = "http://www.luyin.com/upload/zhongyingwenkejian003.mp3";
    //英文单词
    private static final String TESTURL = "http://www.luyin.com/upload/yingwennvtong0408009j.mp3";
    //英文语句
//    private static final String TESTURL = "http://www.luyin.com/upload/yingwenyuyin0122012.mp3";
    //中文
//    private static final String TESTURL = "http://www.luyin.com/upload/zhuanyeweixiu0927088j.mp3";


    private OasrRequesterSender oasrRequesterSender = new OasrRequesterSender();

    static {
        initBaseParameters();
    }

    public static void main(String[] args) {
        OasrRequestSample rasrRequestSample = new OasrRequestSample();
        rasrRequestSample.start();
    }

    private void start() {

//            this.sendUrlRequest();
        this.sendBytesRequest();
        System.exit(0);
    }

    /**
     * 指定语音文件的Url，发出请求。建议使用此方法。
     */
    private void sendUrlRequest() {
        String taskId = null;
        while (true) {
            taskId = getTaskId();
            if (taskId != null) {
                break;
            }
        }

        String callbackUrl = genCallbackUrl(taskId);

        OasrBytesRequest oasrBytesRequest = new OasrBytesRequest(callbackUrl, TESTURL);
        oasrBytesRequest.setChannelNum(2); //设置为2声道语音，默认为1声道。目前仅8K语音支持2声道。
        OasrResponse oasrResponse = this.oasrRequesterSender.send(oasrBytesRequest);
        this.printReponse(oasrResponse);

        getResult(callbackUrl);
    }


    /**
     * 从文件中读取语音数据，通过HttpBody发出请求。语音文件大小需小于5兆才可使用此方法。
     */
    private void sendBytesRequest() {
        String callbackUrl = genCallbackUrl(getTaskId());
        byte[] content = ByteUtils.inputStream2ByteArray("E:\\tencen-asr\\test01.mp3");
        OasrBytesRequest oasrBytesRequest = new OasrBytesRequest(callbackUrl, content);
        // oasrBytesRequest.setChannelNum(2); //特别设置为2声道，默认为1声道。目前仅8K语音支持2声道。
        OasrResponse oasrResponse = this.oasrRequesterSender.send(oasrBytesRequest);
        this.printReponse(oasrResponse);

        getResult(callbackUrl);
    }


    /**
     * 轮询回调接口 查询结果
     */
    public void getResult(String callbackUrl) {
        if (callbackUrl != null) {
            String[] split = callbackUrl.split("\\?");
            String taskResponse = null;

            // {"Response":{"RequestId":"827b0029-8c7c-42af-83ca-b42c32112c17","Data":{"TaskId":565804952,"Status":1,"StatusStr":"doing","Result":"","ErrorMsg":""}}}
            while (true) {
                taskResponse = sendGET(split[0], split[1]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String statusStr = taskResponse.split("\"StatusStr\":\"")[1].split("\",\"Result\"")[0];
                log.debug("result:{}", taskResponse);
                if (taskResponse != null && statusStr.equals("success")) {
                    break;
                }
            }
        }
    }

    /**
     * 生成回调URL 用于获取识别结果
     */
    public String genCallbackUrl(String taskId) {
        TreeMap<String, Object> params = new TreeMap<String, Object>(); // TreeMap可以自动排序
        // 实际调用时应当使用随机数，例如：
        params.put("Nonce", NONCE);// 公共参数
        // 实际调用时应当使用系统当前时间，例如：
        params.put("Timestamp", TIMESTAMP);
        params.put("SecretId", SECRETID); // 公共参数
        params.put("Action", "DescribeTaskStatus"); // 公共参数
        params.put("Version", "2019-06-14"); // 公共参数
//        params.put("Region", "ap-guangzhou"); // 公共参数
        params.put("TaskId", taskId); // 业务参数
        String url = null;
        try {
            params.put("Signature", sign(getStringToSign(params), SECRETKEY, HMAC_SHA1_ALGORITHM)); // 公共参数
            url = getUrl(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("CallbackUrl:{}", url);
        return url;
    }

    /**
     * 获取taskID
     */
    public String getTaskId() {
        TreeMap<String, Object> treeMap = new TreeMap<String, Object>(); // TreeMap可以自动排序
        // 实际调用时应当使用随机数，例如：
        treeMap.put("Nonce", NONCE);// 公共参数
        // 实际调用时应当使用系统当前时间，例如：
        treeMap.put("Timestamp", TIMESTAMP);
        treeMap.put("ChannelNum", 1);
        treeMap.put("EngineModelType", "8k_0");
        treeMap.put("ResTextFormat", "0");
        treeMap.put("SourceType", "0");//0:语音url or 1:语音数据bodydata
        treeMap.put("Url", TESTURL);
        treeMap.put("SecretId", SECRETID); // 公共参数
        treeMap.put("Action", "CreateRecTask"); // 公共参数
        treeMap.put("Version", "2019-06-14"); // 公共参数
        treeMap.put("Region", "ap-guangzhou"); // 公共参数
        String taskUrl = null;
        try {
            treeMap.put("Signature", sign(getStringToSign(treeMap), SECRETKEY, HMAC_SHA1_ALGORITHM)); // 公共参数
            taskUrl = getUrl(treeMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("taskUrl:" + taskUrl);
        String[] taskSplit = taskUrl.split("\\?");
        String json = sendGET(taskSplit[0], taskSplit[1]);
        if (json.contains("\"TaskId\":")) {
            String taskId = json.split("\"TaskId\":")[1].split("}")[0];
            System.err.println("response:" + json);
            System.err.println("taskId:" + taskId);
            return taskId;
        }
        return null;
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
        AsrBaseConfig.appId = APPID;
        AsrBaseConfig.secretId = SECRETID;
        AsrBaseConfig.secretKey = SECRETKEY;
        AsrInternalConfig.SUB_SERVICE_TYPE = 0; // 0表示离线识别

        // optional，根据自身需求配置值
        AsrPersonalConfig.engineModelType = EngineModelType._16k_0;
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


    public static String getUrl(TreeMap<String, Object> params) throws UnsupportedEncodingException {
        StringBuilder url = new StringBuilder(DOMAIN);
        // 实际请求的url中对参数顺序没有要求
        for (String k : params.keySet()) {
            // 需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
            url.append(k).append("=").append(URLEncoder.encode(params.get(k).toString(), CHARSET)).append("&");
        }
        return url.toString().substring(0, url.length() - 1);
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
//            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段，获取到cookies等
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
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
