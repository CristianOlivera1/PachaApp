package com.example.pachaapp.clas;

import java.util.List;

public class ApiResponse<T> {
    private String type;
    private List<String> listMessage;
    private T data;

    public ApiResponse() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getListMessage() {
        return listMessage;
    }

    public void setListMessage(List<String> listMessage) {
        this.listMessage = listMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return "success".equals(type);
    }

    public boolean isError() {
        return "error".equals(type);
    }

    public String getFirstMessage() {
        if (listMessage != null && !listMessage.isEmpty()) {
            return listMessage.get(0);
        }
        return "";
    }
}
