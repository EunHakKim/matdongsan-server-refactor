package com.mds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "kakao-auth-api", url = "${spring.security.oauth2.client.provider.kakao.token-uri}")
interface KakaoAuthApi {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String requestAccessToken(@RequestParam Map<String, ?> requestParams);

}
