package com.mds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakao-userinfo-api", url = "${spring.security.oauth2.client.provider.kakao.user-info-uri}")
interface KakaoUserInfoApi {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String requestUserInfo(@RequestHeader("Authorization") String authorization);

}
