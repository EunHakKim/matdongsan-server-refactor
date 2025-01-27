package com.mds;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "prompts")
class OpenAiPrompts {
    private Map<Integer, String> en;
    private Map<Integer, String> ko;
    private String question;
    private String generateCommand;
    private String generateSummary;
    private String generateImage;

    public Map<Integer, String> getEn() {
        return en;
    }

    public Map<Integer, String> getKo() {
        return ko;
    }

    public String getQuestion() {
        return question;
    }

    public String getGenerateCommand() {
        return generateCommand;
    }

    public String getGenerateSummary() {
        return generateSummary;
    }

    public String getGenerateImage() {
        return generateImage;
    }

    public void setEn(Map<Integer, String> en) {
        this.en = en;
    }

    public void setKo(Map<Integer, String> ko) {
        this.ko = ko;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setGenerateCommand(String generateCommand) {
        this.generateCommand = generateCommand;
    }

    public void setGenerateSummary(String generateSummary) {
        this.generateSummary = generateSummary;
    }

    public void setGenerateImage(String generateImage) {
        this.generateImage = generateImage;
    }
}
