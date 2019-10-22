package com.szjz.asr.offline.sdk;

/**
 * author:szjz
 * date:2019/10/22
 */
public class Task {
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}

class Response {
    private String RequestId;

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String requestId) {
        RequestId = requestId;
    }

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        private String TaskId;

        public String getTaskId() {
            return TaskId;
        }

        public void setTaskId(String taskId) {
            TaskId = taskId;
        }
    }
}
