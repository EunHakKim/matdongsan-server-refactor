package com.mds;

public class OpenAiClientException extends RuntimeException {

    public OpenAiClientException(String errorCode) {
        super(errorCode);
    }

}
