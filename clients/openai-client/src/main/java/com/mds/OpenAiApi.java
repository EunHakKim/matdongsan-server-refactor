package com.mds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "openai-api", url = "${openai.api.url}")
interface OpenAiApi {

    /**
     * 일반 요청용
     * @param authorization
     * @param contentType
     * @param requestBody
     * @return
     */
    @PostMapping(value = "/chat/completions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String requestChat(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Content-Type") String contentType,
            @RequestBody Map<String, Object> requestBody
    );

    /**
     * 이미지 생성용
     * @param authorization
     * @param contentType
     * @param requestBody
     * @return
     */
    @PostMapping(value = "/images/generations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    String requestImage(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Content-Type") String contentType,
            @RequestBody Map<String, Object> requestBody
    );

    /**
     * STT 생성용
     * @param authorization
     * @param file
     * @return
     */
    @PostMapping(value = "/audio/transcriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String requestStt(
            @RequestHeader("Authorization") String authorization,
            @RequestPart("model") String model,
            @RequestPart("file") MultipartFile file
    );
}
