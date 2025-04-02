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

    // HTTP header constants.
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    // Error message constants.
    private static final String LLM_API_KEY_NOT_SET = "// LLM_API_KEY environment variable is not set.";
    private static final String NO_VALID_RESPONSE = "// No valid response from LLM.";
    private static final String LLM_API_ERROR_PREFIX = "// LLM API error: ";
    private static final String EXCEPTION_CALLING_LLM_PREFIX = "// Exception calling LLM API: ";

    /**
     * Calls the configured LLM API to generate an implementation snippet based on the given operation.
     * Assumes all required configuration properties are present in additionalProperties.
     *
     * @param op The CodegenOperation containing details about the API operation.
     * @param additionalProperties A map containing configuration properties from the generator config.
     * @return A String containing the generated code snippet or an error message.
     */
    public String callLLMForImplementation(CodegenOperation op, Map<String, Object> additionalProperties) {
        // Retrieve configuration directly from additionalProperties.
        // Assumes these keys exist and have the correct types based on config.yml.
        String apiEndpoint = (String) additionalProperties.get("llmApiEndpoint");
        String model = (String) additionalProperties.get("llmModel");
        String systemPrompt = (String) additionalProperties.get("llmSystemPrompt");
        // Use helper methods for numeric types for cleaner casting.
        double temperature = getDoubleProperty(additionalProperties, "llmTemperature");
        int maxTokens = getIntProperty(additionalProperties, "llmMaxTokens");
        double topP = getDoubleProperty(additionalProperties, "llmTopP");
        double frequencyPenalty = getDoubleProperty(additionalProperties, "llmFrequencyPenalty");
        double presencePenalty = getDoubleProperty(additionalProperties, "llmPresencePenalty");

        // Retrieve the API key directly from the LLM_API_KEY environment variable.
        String apiKey = System.getenv("LLM_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
             System.err.println("Warning: LLM_API_KEY environment variable not set.");
             return LLM_API_KEY_NOT_SET;
        }

        // Build the LLM prompt using operation details.
        String prompt = buildLLMPrompt(op);

        // Build the request payload for the LLM API.
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", model);
        payload.put("temperature", temperature);
        payload.put("max_tokens", maxTokens);
        payload.put("top_p", topP);
        payload.put("frequency_penalty", frequencyPenalty);
        payload.put("presence_penalty", presencePenalty);

        // Construct the messages list with a system prompt and a user prompt.
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
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
                    .uri(URI.create(apiEndpoint)) // Use configured endpoint
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
     * @param additionalProperties A map containing configuration properties from the generator config.
     */
    public void processOperation(CodegenOperation op, Map<String, Object> additionalProperties) {
        Object xScope = op.vendorExtensions.get("x-scope");
        // Check if core LLM configuration is present before attempting to process (using llmApiEndpoint as a proxy)
        if (xScope != null && "package".equals(xScope.toString()) && additionalProperties.containsKey("llmApiEndpoint")) {
            String llmImplementation = callLLMForImplementation(op, additionalProperties);
            op.vendorExtensions.put("llmImplementation", llmImplementation);
        }
    }

    // Helper method to safely get a double property without defaults
    private double getDoubleProperty(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        // Attempt direct cast or parse, will throw exception if invalid format/type
        return Double.parseDouble(value.toString());
    }

    // Helper method to safely get an integer property without defaults
    private int getIntProperty(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
         // Attempt direct cast or parse, will throw exception if invalid format/type
        return Integer.parseInt(value.toString());
    }
}
