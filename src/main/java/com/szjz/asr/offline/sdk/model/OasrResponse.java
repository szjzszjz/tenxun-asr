package com.szjz.asr.offline.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 离线识别回复类。
 *
 * @author iantang
 * @version 1.0
 */
public class OasrResponse {

	@JsonProperty
	private String code;

	@JsonProperty
	private String message;

	@JsonProperty
	private String requestId;

	@JsonIgnore
	private String originalText;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@JsonIgnore
	public String getOriginalText() {
		return originalText;
	}

	@JsonIgnore
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

}
