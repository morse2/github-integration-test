package com.tuogo.cicd;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.AssertJUnit.*;

@SpringBootTest
public class GenericApiTest extends AbstractTestNGSpringContextTests {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private TestConfig testConfig;
    private Map<String, Object> contextVariables = new HashMap<>();

    @BeforeClass
    public void loadTestConfig() throws IOException {
        InputStream configStream = getClass().getClassLoader().getResourceAsStream("test-config.json");
        assertNotNull("测试配置文件未找到", configStream);
        
        testConfig = objectMapper.readValue(configStream, TestConfig.class);
        assertNotNull("测试配置解析失败", testConfig);
        assertNotNull("测试用例列表为空", testConfig.getTests());
        
        // 初始化上下文变量
        contextVariables.put("timestamp", System.currentTimeMillis());
    }

    @Test
    public void executeTestSequence() {
        AtomicReference<TestResult> previousResult = new AtomicReference<>();
        
        for (int i = 0; i < testConfig.getTests().size(); i++) {
            TestCase testCase = testConfig.getTests().get(i);
            
            try {
                System.out.println("执行测试: " + testCase.getName());
                
                // 检查是否应该执行此测试
                if (!shouldExecuteTest(testCase, previousResult.get())) {
                    System.out.println("跳过测试: " + testCase.getName());
                    continue;
                }
                
                // 执行测试
                TestResult result = executeTestCase(testCase);
                previousResult.set(result);
                
                // 验证结果
                validateTestResult(testCase, result);
                
                // 提取数据到上下文
                extractToContext(testCase, result);
                
                System.out.println("测试通过: " + testCase.getName());
                
            } catch (Exception e) {
                fail("测试失败: " + testCase.getName() + " - " + e.getMessage());
            }
        }
    }

    private boolean shouldExecuteTest(TestCase testCase, TestResult previousResult) {
        String condition = testCase.getNextTestCondition();
        
        if ("always".equalsIgnoreCase(condition)) {
            return true;
        }
        
        if ("success".equalsIgnoreCase(condition)) {
            return previousResult != null && previousResult.isSuccess();
        }
        
        // 默认情况下，如果前一个测试成功则继续执行
        return previousResult == null || previousResult.isSuccess();
    }

    private TestResult executeTestCase(TestCase testCase) {
        try {
            // 构建URL
            String fullUrl = buildUrl(testCase);
            
            // 构建请求头
            HttpHeaders headers = buildHeaders(testCase);
            
            // 构建请求体
            HttpEntity<?> requestEntity = buildRequestEntity(testCase, headers);
            
            // 执行请求
            ResponseEntity<String> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.valueOf(testCase.getMethod().toUpperCase()),
                requestEntity,
                String.class
            );
            
            return new TestResult(true, response.getStatusCodeValue(), response.getBody());
            
        } catch (Exception e) {
            return new TestResult(false, 0, e.getMessage());
        }
    }

    private String buildUrl(TestCase testCase) {
        StringBuilder urlBuilder = new StringBuilder(testConfig.getBaseUrl());
        urlBuilder.append(testCase.getUrl());
        
        if (testCase.getParameters() != null && !testCase.getParameters().isEmpty()) {
            urlBuilder.append("?");
            testCase.getParameters().forEach((key, value) -> {
                String resolvedValue = resolveVariables(value.toString());
                urlBuilder.append(key).append("=").append(resolvedValue).append("&");
            });
            // 移除最后一个"&"
            urlBuilder.setLength(urlBuilder.length() - 1);
        }
        
        return urlBuilder.toString();
    }

    private HttpHeaders buildHeaders(TestCase testCase) {
        HttpHeaders headers = new HttpHeaders();
        
        if (testCase.getHeaders() != null) {
            testCase.getHeaders().forEach(headers::set);
        }
        
        return headers;
    }

    private HttpEntity<?> buildRequestEntity(TestCase testCase, HttpHeaders headers) {
        if (testCase.getBody() != null) {
            // 处理请求体中的变量替换
            Object resolvedBody = resolveBodyVariables(testCase.getBody());
            return new HttpEntity<>(resolvedBody, headers);
        }
        
        return new HttpEntity<>(headers);
    }

    private Object resolveBodyVariables(Object body) {
        if (body instanceof Map) {
            Map<String, Object> resolvedMap = new HashMap<>();
            ((Map<?, ?>) body).forEach((key, value) -> {
                if (value instanceof String) {
                    resolvedMap.put(key.toString(), resolveVariables((String) value));
                } else {
                    resolvedMap.put(key.toString(), value);
                }
            });
            return resolvedMap;
        }
        
        if (body instanceof String) {
            return resolveVariables((String) body);
        }
        
        return body;
    }

    private String resolveVariables(String text) {
        if (text == null) return null;
        
        String result = text;
        for (Map.Entry<String, Object> entry : contextVariables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            if (result.contains(placeholder)) {
                result = result.replace(placeholder, entry.getValue().toString());
            }
        }
        return result;
    }

    private void validateTestResult(TestCase testCase, TestResult result) {
        assertTrue("请求失败: " + testCase.getName(), result.isSuccess());
        assertEquals("HTTP状态码不匹配", testCase.getExpectedStatus(), result.getStatusCode());
        assertNotNull("响应体为空", result.getResponseBody());
    }

    private void extractToContext(TestCase testCase, TestResult result) {
        if (testCase.getExtract() == null || testCase.getExtract().isEmpty()) {
            return;
        }
        
        try {
            JsonNode responseJson = objectMapper.readTree(result.getResponseBody());
            
            testCase.getExtract().forEach((key, jsonPath) -> {
                try {
                    JsonNode node = responseJson.at(jsonPath);
                    if (!node.isMissingNode()) {
                        if (node.isTextual()) {
                            contextVariables.put(key, node.asText());
                        } else if (node.isNumber()) {
                            contextVariables.put(key, node.asLong());
                        } else if (node.isBoolean()) {
                            contextVariables.put(key, node.asBoolean());
                        } else {
                            contextVariables.put(key, node.toString());
                        }
                        System.out.println("提取变量: " + key + " = " + contextVariables.get(key));
                    }
                } catch (Exception e) {
                    System.err.println("提取变量失败: " + key + " - " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            System.err.println("解析响应JSON失败: " + e.getMessage());
        }
    }

    @Data
    public static class TestConfig {
        private String testName;
        private String description;
        private String baseUrl;
        private List<TestCase> tests;
    }

    @Data
    public static class TestCase {
        private String name;
        private String method;
        private String url;
        private Map<String, Object> parameters;
        private Map<String, String> headers;
        private Object body;
        private int expectedStatus;
        private Map<String, String> extract;
        private String nextTestCondition;
    }

    @Data
    public static class TestResult {
        private boolean success;
        private int statusCode;
        private String responseBody;

        public TestResult(boolean success, int statusCode, String responseBody) {
            this.success = success;
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }
    }
}