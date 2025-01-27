package com.mds;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class OpenAiClient {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.tokens}")
    private int maxTokens;

    private final OpenAiPrompts openAiPrompts;
    private final OpenAiApi openAiApi;

    public OpenAiClient(OpenAiPrompts openAiPrompts, OpenAiApi openAiApi) {
        this.openAiPrompts = openAiPrompts;
        this.openAiApi = openAiApi;
    }

    /**
     * 동화 생성 요청을 전송
     * @param given
     * @param language
     * @return
     */
    public String requestStory(int age, String language, String given) {
        Map<Integer, String> templates = language.equals("EN") ? openAiPrompts.getEn() : openAiPrompts.getKo();
        String template = templates.get(age).replace("{given}", given);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", Objects.equals(language, "KO") ? "chatgpt-4o-latest" : "gpt-4o-mini");
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.9);
        requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", openAiPrompts.getGenerateCommand()),
                Map.of("role", "user", "content", template)
        });

        try {
            return openAiApi.requestChat("Bearer " + apiKey, "application/json", requestBody);
        } catch (FeignException.FeignClientException e) {
            throw new OpenAiClientException("CLIENT");
        } catch (Exception e) {
            throw new OpenAiClientException(e.getMessage());
        }
    }

    /**
     * 동화 요약 요청을 전송
     * @param content
     * @return
     */
    public String requestSummary(String content) {
        String template = openAiPrompts.getGenerateSummary().replace("{story}", content);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("temperature", 0.7);
        requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", "You are making a prompt for an image generation model which image will be used as children's book cover."),
                Map.of("role", "user", "content", template)
        });

        try {
            return openAiApi.requestChat("Bearer " + apiKey, "application/json", requestBody);
        } catch (FeignException.FeignClientException e) {
            throw new OpenAiClientException("CLIENT");
        } catch (Exception e) {
            throw new OpenAiClientException(e.getMessage());
        }
    }

    /**
     * 동화 이미지 생성 요청을 전송
     * @param scene
     * @return
     */
    public String requestImage(String scene) {
        String template = openAiPrompts.getGenerateImage().replace("{scene}", scene);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "dall-e-3");
        requestBody.put("prompt", template);
        requestBody.put("quality", "standard");
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");

        try {
            return openAiApi.requestImage("Bearer " + apiKey, "application/json", requestBody);
        } catch (FeignException.FeignClientException e) {
            throw new OpenAiClientException("CLIENT");
        } catch (Exception e) {
            throw new OpenAiClientException(e.getMessage());
        }
    }

    /**
     * STT 요청 전송 및 응답을 반환
     * @param file
     * @return
     */
    public String requestStt(MultipartFile file) {
        try {
            return openAiApi.requestStt("Bearer " + apiKey, "whisper-1", file);
        } catch (FeignException.FeignClientException e) {
            throw new OpenAiClientException("CLIENT");
        } catch (Exception e) {
            throw new OpenAiClientException(e.getMessage());
        }
    }

    /**
     * 동화 질문 생성 요청 전송
     * @param age
     * @param language
     * @param content
     * @return
     */
    public String requestQuestion(int age, String language, String content) {
        String template = openAiPrompts.getQuestion().replace("{age}", Integer.toString(age));
        template = template.replace("{language}", language);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("messages", new Object[]{
                Map.of("role", "system", "content", template),
                Map.of("role", "user", "content", content)
        });

        try {
            return openAiApi.requestChat("Bearer " + apiKey, "application/json", requestBody);
        } catch (FeignException.FeignClientException e) {
            throw new OpenAiClientException("CLIENT");
        } catch (Exception e) {
            throw new OpenAiClientException(e.getMessage());
        }
    }
}
