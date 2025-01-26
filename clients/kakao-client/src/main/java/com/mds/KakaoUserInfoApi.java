package com.mds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoUserInfoClient", url = "${spring.security.oauth2.client.provider.kakao.user-info-uri}")
interface KakaoUserInfoApi {

    @PostMapping(consumes = "application/x-www-form-urlencoded")
    String getUserInfo(@RequestHeader("Authorization") String authorization);
}
