package com.mds;

public class KakaoClientException extends RuntimeException{

    public KakaoClientException(String errorCode) {
        super(errorCode);
    }

}
