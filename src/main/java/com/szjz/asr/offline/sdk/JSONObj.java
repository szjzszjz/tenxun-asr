package com.szjz.asr.offline.sdk;

import org.springframework.boot.configurationprocessor.json.JSONObject;

/**
 * author:szjz
 * date:2019/10/23
 */
public class JSONObj {
    public static void main(String[] args) throws Exception {
//        String str = "{\"a\":\"b\", \"c\":\"d\"}";
        String str = "{\"PronAccuracy\":57.016747,\"PronFluency\":0.8546912,\"PronCompletion\":1.0,\"Words\":[{\"MemBeginTime\":10,\"MemEndTime\":630,\"PronAccuracy\":57.016747,\"PronFluency\":0.8546912,\"Word\":\"bike\",\"MatchTag\":0,\"PhoneInfos\":[{\"MemBeginTime\":20,\"MemEndTime\":140,\"PronAccuracy\":21.924858,\"DetectedStress\":false,\"Phone\":\"b\",\"Stress\":false,\"ReferencePhone\":\"\",\"MatchTag\":5},{\"MemBeginTime\":140,\"MemEndTime\":250,\"PronAccuracy\":49.780277,\"DetectedStress\":false,\"Phone\":\"ay\",\"Stress\":false,\"ReferencePhone\":\"\",\"MatchTag\":5},{\"MemBeginTime\":250,\"MemEndTime\":570,\"PronAccuracy\":99.3451,\"DetectedStress\":false,\"Phone\":\"k\",\"Stress\":false,\"ReferencePhone\":\"\",\"MatchTag\":5}],\"ReferenceWord\":\"\"}],\"SessionId\":\"62f2d2de7c294b7abf357a4c27564f7f\",\"AudioUrl\":\"https://soe-1255701415.cos.ap-beijing.myqcloud.com/1257940798/default/20191023/audio/dcd9b744-e088-4a9a-9bdc-53d7cb85d870.mp3\",\"SentenceInfoSet\":[{\"SentenceId\":-1,\"Words\":[{\"MemBeginTime\":10,\"MemEndTime\":630,\"PronAccuracy\":57.016747,\"PronFluency\":0.8546912,\"Word\":\"bike\",\"MatchTag\":0,\"PhoneInfos\":[{\"MemBeginTime\":20,\"MemEndTime\":140,\"PronAccuracy\":21.924858,\"DetectedStress\":false,\"Phone\":\"b\",\"Stress\":false,\"ReferencePhone\":\"\",\"MatchTag\":5},{\"MemBeginTime\":140,\"MemEndTime\":250,\"PronAccuracy\":49.780277,\"DetectedStress\":false,\"Phone\":\"ay\",\"Stress\":false,\"ReferencePhone\":\"\",\"MatchTag\":5},{\"MemBeginTime\":250,\"MemEndTime\":570,\"PronAccuracy\":99.3451,\"DetectedStress\":false,\"Phone\":\"k\",\"Stress\":false,\"ReferencePhone\":\"\",\"MatchTag\":5}],\"ReferenceWord\":\"\"}],\"PronAccuracy\":57.016747,\"PronFluency\":0.8546912,\"PronCompletion\":1.0,\"SuggestedScore\":0.0}],\"Status\":\"Finished\",\"SuggestedScore\":57.016747,\"RequestId\":\"45e99dbf-15c5-484a-bdcd-d28b3a201306\"}";
        JSONObject jsonObject = new JSONObject(str);
        System.out.println(jsonObject); // {"c":"d","a":"b"}
        System.out.println(jsonObject.get("PronAccuracy"));
        System.out.println(jsonObject.get("PronFluency"));
        System.out.println(jsonObject.get("PronCompletion"));
        System.out.println(jsonObject.get("SuggestedScore"));
//将json对象中的值遍历出来再add到list里面就可以了。
    }
}
