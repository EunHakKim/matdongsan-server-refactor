package com.mds;

import com.mds.model.TtsClientResult;
import feign.FeignException;
import org.springframework.stereotype.Component;

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
        } catch (FeignException.FeignClientException e) {
            throw new TtsClientException("CLIENT");
        } catch (Exception e) {
            throw new TtsClientException(e.getMessage());
        }
    }

}
