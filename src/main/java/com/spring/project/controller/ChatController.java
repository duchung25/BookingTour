package com.spring.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class ChatController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String geminiModel;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    private static final String SYSTEM_PROMPT = "Bạn là trợ lý ảo của Traveler — nền tảng đặt tour du lịch Việt Nam. " +
            "Hãy trả lời bằng tiếng Việt, ngắn gọn, thân thiện và hữu ích. " +
            "Nếu khách hàng hỏi về tour, khuyến mãi, đặt chỗ hoặc thanh toán, " +
            "hãy cung cấp thông tin hỗ trợ tốt nhất. Hotline: 1900 1234, Email: info@traveler.vn.";

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");

        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("reply", "Vui lòng nhập tin nhắn."));
        }

        try {
            String url = String.format(GEMINI_API_URL, geminiModel, geminiApiKey);

            // Build request body for Gemini API
            Map<String, Object> requestBody = Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(Map.of("text", SYSTEM_PROMPT))),
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", userMessage)))),
                    "generationConfig", Map.of(
                            "maxOutputTokens", 500,
                            "temperature", 0.7));

            RestClient restClient = RestClient.create();

            Map<?, ?> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // Parse Gemini response
            String reply = extractReply(response);
            return ResponseEntity.ok(Map.of("reply", reply));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("reply",
                            "Xin lỗi, trợ lý ảo đang gặp sự cố. Vui lòng thử lại sau hoặc liên hệ hotline 1900 1234."));
        }
    }

    @SuppressWarnings("unchecked")
    private String extractReply(Map<?, ?> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Xin lỗi, tôi không thể xử lý yêu cầu này. Vui lòng thử lại.";
        }
    }
}
