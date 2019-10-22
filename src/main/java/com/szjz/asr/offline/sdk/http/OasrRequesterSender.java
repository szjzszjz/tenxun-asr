package com.szjz.asr.offline.sdk.http;

import com.tencent.cloud.asr.offline.sdk.model.OasrBytesRequest;
import com.tencent.cloud.asr.offline.sdk.model.OasrResponse;
import com.tencent.cloud.asr.offline.sdk.model.OasrResponseParser;
import com.tencent.cloud.asr.realtime.sdk.http.base.HttpRequester;

/**
 * 录音文件识别（离线识别）Http请求工具类。提供请求方法给用户调用，已对回复做好了解析。
 *
 * @author iantang
 * @version 1.0
 */
public class OasrRequesterSender {

	private HttpRequester httpRequester = new HttpRequester();

	public OasrResponse send(OasrBytesRequest oasrBytesRequest) {
		String responseStr = (String)this.httpRequester.send(oasrBytesRequest, oasrBytesRequest.getContents());
		OasrResponse oasrResponse = null;
		if (responseStr != null && responseStr.contains("code") && responseStr.contains("requestId")) {
			oasrResponse = OasrResponseParser.parse(responseStr); // 解析Json会消耗1~2ms时间，已测试。
		} else
			System.err.println("Unexpected oasr http response: " + responseStr);
		return oasrResponse;
	}

}
