package com.mds;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static feign.FeignException.*;

@Component
public class KakaoClient {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoAuthApi kakaoAuthApi;
    private final KakaoUserInfoApi kakaoUserInfoApi;

    public KakaoClient(KakaoAuthApi kakaoAuthApi, KakaoUserInfoApi kakaoUserInfoApi) {
        this.kakaoAuthApi = kakaoAuthApi;
        this.kakaoUserInfoApi = kakaoUserInfoApi;
    }

    public String requestAccessToken(String code) {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("grant_type", "authorization_code");
        requestParams.put("client_id", clientId);
        requestParams.put("client_secret", clientSecret);
        requestParams.put("redirect_uri", redirectUri);
        requestParams.put("code", code);

        try {
            return kakaoAuthApi.requestAccessToken(requestParams);
        } catch (FeignClientException e) {
            throw new KakaoClientException("CLIENT");
        } catch (Exception e) {
            throw new KakaoClientException(e.getMessage());
        }
    }

    public String requestUserInfo(String token) {
        try {
            return kakaoUserInfoApi.requestUserInfo("Bearer " + token);
        } catch (FeignClientException e) {
            throw new KakaoClientException("CLIENT");
        } catch (Exception e) {
            throw new KakaoClientException(e.getMessage());
        }
    }
}
