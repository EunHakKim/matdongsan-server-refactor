package com.mds.common.external;

import com.mds.domain.story.dto.StoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "TTSClient", url = "${tts.url}")
public interface TTSClient {

    /**
     * TTS 생성용
     * @param contentType
     * @param ttsCreationRequest
     * @return
     */
    @PostMapping(value = "/generate-tts", consumes = "application/json", produces = "application/json")
    ResponseEntity<StoryDto.TTSResponse> sendTTSRequest(
            @RequestHeader("Content-Type") String contentType,
            @RequestBody StoryDto.TTSCreationRequest ttsCreationRequest
    );
}
