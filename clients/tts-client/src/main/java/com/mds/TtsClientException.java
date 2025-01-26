package com.mds;

public class TtsClientException extends RuntimeException {

    public TtsClientException(String errorCode) {
        super(errorCode);
    }

}
