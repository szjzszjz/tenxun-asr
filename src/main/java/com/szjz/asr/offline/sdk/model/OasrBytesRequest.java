package com.szjz.asr.offline.sdk.model;

import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import com.tencent.cloud.asr.realtime.sdk.config.AsrInternalConfig;
import com.tencent.cloud.asr.realtime.sdk.model.request.RasrBaseRequest;

/**
 * 录音文件识别（离线识别）请求对象。
 *
 * @author iantang
 * @version 1.0
 */
public class OasrBytesRequest extends RasrBaseRequest {

	/**
	 * 回调 URL，用户接受结果，长度大于 0，小于 2048
	 */
	private String callBackUrl;

	/**
	 * 语音声道数，仅在电话 8k 通用模型下，支持 1 和 2，其他模型仅支持 1
	 */
	private int channelNum = 1;

	/**
	 * 结果返回方式。0：同步返回；1：异步返回。目前只支持异步返回
	 */
	private int resType = 1;

	/**
	 * 语音数据来源。0：语音 URL；1：语音数据（post body）
	 */
	private int sourceType;

	/**
	 * 语音 URL，公网可下载。当 source_type 值为 0 时须填写该字段，为 1 时不填；URL 的长度大于 0，小于 2048
	 */
	private String sourceUrl;

	/**
	 * 语音文本的内容。sourceType为1时，由此变量携带语音内容。大小须小于5兆。
	 */
	private byte[] contents = new byte[0];

	/**
	 * 构造函数。创建请求对象，语音数据为用户的url，提供给服务器去下载。
	 */
	public OasrBytesRequest(String callBackUrl, String sourceUrl) {
		super();
		this.callBackUrl = callBackUrl;
		this.sourceType = 0;
		this.sourceUrl = sourceUrl;
		this.initTreeMap(); // 需要重新构建，因为上面3个属性是后面才赋值的。
	}

	/**
	 * 构造函数。创建请求对象，语音数据直接由参数传入。
	 */
	public OasrBytesRequest(String callBackUrl, byte[] contents) {
		super();
		this.callBackUrl = callBackUrl;
		this.sourceType = 1; // 语音内容直接传入进来
		this.contents = contents;
		this.initTreeMap(); // 需要重新构建，因为上面3个属性是后面才赋值的。
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public byte[] getContents() {
		return contents;
	}

	public int getChannelNum() {
		return channelNum;
	}

	public void setChannelNum(int channelNum) {
		this.channelNum = channelNum;
	}

	public int getResType() {
		return resType;
	}

	public int getSourceType() {
		return sourceType;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	/**
	 * 与实时SDK创建serverUrl的方式不同，离线这边需要对所有的字段值做urlencode编码。
	 */
	@Override
	protected String createServerUrl() {
		this.initTreeMap(); // 构建参数
		StringBuilder sb = new StringBuilder(AsrInternalConfig.REAL_ASR_URL);
		sb.append(this.getAppId()).append("?");
		// to make that all the parameters are sorted by ASC order
		TreeMap<String, String> sortedMap = new TreeMap<String, String>(super.treeMap);
		for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(this.encode(entry.getValue()));
			sb.append('&');
		}
		if (this.treeMap.size() > 0) {
			sb.setLength(sb.length() - 1); // 去掉最后面的 '&'号
		}
		System.out.println("Generated URL: " + sb.toString());
		return sb.toString();
	}

	/**
	 * 创建资料树，通常在对象创建时初始化，微弱提升后面的线程的处理速度。
	 */
	@Override
	protected void initTreeMap() {
		if (super.treeMap == null)
			super.treeMap = new TreeMap<String, String>();
		else
			super.treeMap.clear();
		super.treeMap.put("secretid", super.getSecretId());
		super.treeMap.put("projectid", "" + super.getProjectId());
		super.treeMap.put("sub_service_type", "" + super.getSubServiceType());
		super.treeMap.put("engine_model_type", super.getEngineModelTtype().getName());
		super.treeMap.put("callback_url", this.callBackUrl);
		super.treeMap.put("channel_num", "" + this.channelNum);
		super.treeMap.put("res_text_format", "" + super.getResponseEncode().getId());
		super.treeMap.put("res_type", "" + this.resType);
		super.treeMap.put("source_type", "" + this.sourceType);
		if (this.sourceType == 0)
			super.treeMap.put("url", this.sourceUrl);
		super.treeMap.put("timestamp", "" + super.getTimestamp());
		super.treeMap.put("expired", "" + super.getExpired()); // 1天后过期
		super.treeMap.put("nonce", "" + super.getNonce());
	}

	private Object encode(String value) {
		try {
			return URLEncoder.encode(value, "utf-8");
		} catch (Exception e) {
			System.err.println("Failed encode value: " + value);
			return value;
		}
	}
}
