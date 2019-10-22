package com.szjz.asr.offline.sdk.model;

import com.tencent.cloud.asr.realtime.sdk.utils.JacksonUtil;

/**
 * json字符串解析类，多做了一点赋值。
 *
 * @author iantang
 * @version 1.0
 */
public class OasrResponseParser {

	public static OasrResponse parse(String response) {
		OasrResponse oasrResponse = JacksonUtil.parse(response, OasrResponse.class);
		oasrResponse.setOriginalText(response);
		return oasrResponse;
	}

}
