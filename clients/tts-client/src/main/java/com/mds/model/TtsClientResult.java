package com.mds.model;

import java.util.List;

public record TtsClientResult(String ttsUrl, List<Double> timestamps) {
}
