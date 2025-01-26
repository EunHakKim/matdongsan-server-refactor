package com.mds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tts-api", url = "${tts.api.url}")
interface TtsApi {

    /**
     * TTS 생성용
     * @param ttsRequestDto
     * @return
     */
    @PostMapping(value = "/generate-tts", consumes = MediaType.APPLICATION_JSON_VALUE)
    TtsResponseDto sendTTSRequest(@RequestBody TtsRequestDto ttsRequestDto);
}
