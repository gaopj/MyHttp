package com.gpj.httplib.net;

/**
 * Created by v-pigao on 5/15/2018.
 */

public class Response {
    private boolean error;
    private int errorType; // 1 为Cookie失效
    private String errorMessage;
    private String result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "error=" + error +
                ", errorType=" + errorType +
                ", errorMessage='" + errorMessage + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
