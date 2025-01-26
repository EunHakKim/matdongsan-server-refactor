package com.mds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "kakaoAuthClient", url = "${spring.security.oauth2.client.provider.kakao.token-uri}")
interface KakaoAuthApi {

    @PostMapping(consumes = "application/x-www-form-urlencoded")
    String getAccessToken(@RequestParam Map<String, ?> requestParams);

}
