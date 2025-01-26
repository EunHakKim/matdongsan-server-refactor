package com.mds;

import com.mds.model.TtsClientResult;

import java.util.List;

record TtsResponseDto(String ttsUrl, List<Double>timestamps) {
    TtsClientResult toResult() {
        return new TtsClientResult(ttsUrl, timestamps);
    }
}
