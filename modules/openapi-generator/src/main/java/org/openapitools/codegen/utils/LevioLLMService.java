package org.openapitools.codegen.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.codegen.CodegenOperation;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevioLLMService {

    // Environment variables and API endpoint constants
    private static final String OPENAI_API_KEY_ENV = "OPENAI_API_KEY";
    private static final String CHATGPT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    // Request headers and authorization constants
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    // Model and prompt constants
    private static final String MODEL = "gpt-4";
    private static final String FALLBACK_PROMPT_PREFIX = "Implement operation ";
    // Customize the prompt
    private static final String SYSTEM_PROMPT = """
            You are a NestJS expert. Generate TypeScript code for NestJS controllers/services operation: 
            - method decorators  
            - Proper dependency injection
            - Request/response DTOs where needed
            - Error and exception handling 
            Return ONLY the code block with no additional text.
            """;

    // Error and response message constants
    private static final String LLM_API_KEY_NOT_SET = "// LLM API key is not set.";
    private static final String NO_VALID_RESPONSE = "// No valid response from LLM.";
    private static final String LLM_API_ERROR_PREFIX = "// LLM API error: ";
    private static final String EXCEPTION_CALLING_LLM_PREFIX = "// Exception calling LLM API: ";

    /**
     * Calls the ChatGPT API to generate an implementation snippet based on the given operation.
     *
     * @param op The CodegenOperation containing details about the API operation.
     * @return A String containing the generated code snippet or an error message.
     */
    public String callLLMForImplementation(CodegenOperation op) {
        // Retrieve the API key from an environment variable.
        String apiKey = System.getenv(OPENAI_API_KEY_ENV);
        if (apiKey == null || apiKey.isEmpty()) {
            return LLM_API_KEY_NOT_SET;
        }

        // Prepare a prompt based on the operation's summary or fallback to operationId.
        String prompt = (op.summary != null && !op.summary.isEmpty())
                ? op.summary
                : FALLBACK_PROMPT_PREFIX + op.operationId;

        // Build the request payload for ChatGPT API.
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", MODEL);

        // Build the messages array with a system prompt and a user prompt.
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_PROMPT);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        payload.put("messages", messages);

        try {
            // Convert payload to JSON string.
            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(payload);

            // Create HTTP client and request.
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHATGPT_API_ENDPOINT))
                    .header(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                    .header(HEADER_AUTHORIZATION, AUTHORIZATION_PREFIX + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Send the request and get the response.
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = mapper.readTree(response.body());
                // ChatGPT API returns a "choices" array; take the first element's message.content.
                if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                    JsonNode firstChoice = root.get("choices").get(0);
                    if (firstChoice.has("message") && firstChoice.get("message").has("content")) {
                        return firstChoice.get("message").get("content").asText();
                    }
                }
                return NO_VALID_RESPONSE;
            } else {
                return LLM_API_ERROR_PREFIX + response.statusCode() + " " + response.body();
            }
        } catch (Exception e) {
            return EXCEPTION_CALLING_LLM_PREFIX + e.getMessage();
        }
    }
}

