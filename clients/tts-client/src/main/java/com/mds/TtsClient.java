package com.mds;

import com.mds.model.TtsClientResult;
import org.springframework.stereotype.Component;

import static feign.FeignException.*;

@Component
public class TtsClient {

    private final TtsApi ttsApi;

    public TtsClient(TtsApi ttsApi) {
        this.ttsApi = ttsApi;
    }

    public TtsClientResult requestTts(String file_name, String language, String text, String folder) {
        try{
            TtsRequestDto request = new TtsRequestDto(file_name, language, text, folder);
            return ttsApi.sendTTSRequest(request).toResult();
        } catch (FeignClientException e) {
            throw new TtsClientException("CLIENT");
        } catch (Exception e) {
            throw new TtsClientException(e.getMessage());
        }
    }

}
