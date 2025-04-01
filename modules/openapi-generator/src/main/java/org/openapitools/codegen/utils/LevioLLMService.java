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

    // Environment and API endpoint constants.
    private static final String OPENAI_API_KEY_ENV = "OPENAI_API_KEY";
    private static final String CHATGPT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";

    // HTTP header constants.
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    // Model and prompt constants.
    private static final String MODEL = "gpt-4o";
    private static final String FALLBACK_PROMPT_PREFIX = "Implement operation ";
    private static final String SYSTEM_PROMPT = """
        You are a NestJS expert.
        Generate a TypeScript function code for NestJS controllers/services operation:
            - method decorators
            - Proper dependency injection
            - Request/response DTOs where needed
            - Error and exception handling
        Return ONLY the code block with no additional text.
        Do not use codeblocks, do not create the class, only the function, do not hardcode any values, do not add imports.
        Use the function description to fully implement the business logic described there.
    """;

    // Error message constants.
    private static final String LLM_API_KEY_NOT_SET = "// LLM API key is not set.";
    private static final String NO_VALID_RESPONSE = "// No valid response from LLM.";
    private static final String LLM_API_ERROR_PREFIX = "// LLM API error: ";
    private static final String EXCEPTION_CALLING_LLM_PREFIX = "// Exception calling LLM API: ";

    // API Parameter Tuning Defaults.
    private static final double TEMPERATURE = 0.3;            // Lower values for deterministic output.
    private static final int MAX_TOKENS = 512;                // Maximum tokens in the response.
    private static final double TOP_P = 1.0;                  // Controls diversity; 1.0 is default.
    private static final double FREQUENCY_PENALTY = 0.0;      // No frequency penalty.
    private static final double PRESENCE_PENALTY = 0.0;       // No presence penalty.

    /**
     * Calls the ChatGPT API to generate an implementation snippet based on the given operation.
     *
     * @param op The CodegenOperation containing details about the API operation.
     * @return A String containing the generated code snippet or an error message.
     */
    public String callLLMForImplementation(CodegenOperation op) {
        // Retrieve the API key from an environment variable.
        String apiKey = System.getenv(OPENAI_API_KEY_ENV) != null ? System.getenv(OPENAI_API_KEY_ENV) : OPENAI_API_KEY_ENV;
        if (apiKey == null || apiKey.isEmpty()) {
            return LLM_API_KEY_NOT_SET;
        }

        // Build the LLM prompt using operation details.
        String prompt = buildLLMPrompt(op);

        // Build the request payload for the ChatGPT API.
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", MODEL);
        payload.put("temperature", TEMPERATURE);
        payload.put("max_tokens", MAX_TOKENS);
        payload.put("top_p", TOP_P);
        payload.put("frequency_penalty", FREQUENCY_PENALTY);
        payload.put("presence_penalty", PRESENCE_PENALTY);

        // Construct the messages list with a system prompt and a user prompt.
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

    /**
     * Builds the LLM prompt based on the operation details.
     *
     * @param op The CodegenOperation containing operation details.
     * @return A prompt string for the LLM.
     */
    private String buildLLMPrompt(CodegenOperation op) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a NestJS implementation for the following operation:\n");
        promptBuilder.append("Operation ID: ").append(op.operationId).append("\n");
        promptBuilder.append("HTTP Method: ").append(op.httpMethod).append("\n");
        promptBuilder.append("Path: ").append(op.path).append("\n");

        // Include parameters if available.
        if (op.allParams != null && !op.allParams.isEmpty()) {
            promptBuilder.append("Parameters:\n");
            op.allParams.forEach(param -> {
                promptBuilder.append("- ").append(param.paramName)
                             .append(" (").append(param.dataType).append(")\n");
            });
        }

        // Include responses if available.
        if (op.responses != null && !op.responses.isEmpty()) {
            promptBuilder.append("Responses:\n");
            op.responses.forEach(response -> {
                promptBuilder.append("- ").append(response.code)
                             .append(": ").append(response.message).append("\n");
            });
        }
        return promptBuilder.toString();
    }

    /**
     * Processes an operation by calling the LLM service if the operation's x-scope extension equals "package".
     *
     * @param op The CodegenOperation to process.
     */
    public void processOperation(CodegenOperation op) {
        Object xScope = op.vendorExtensions.get("x-scope");
        if (xScope != null && "package".equals(xScope.toString())) {
            String llmImplementation = callLLMForImplementation(op);
            op.vendorExtensions.put("llmImplementation", llmImplementation);
        }
    }
}
