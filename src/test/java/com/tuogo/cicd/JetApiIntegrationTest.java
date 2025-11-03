package com.tuogo.cicd;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Map;

import static org.testng.AssertJUnit.assertNotNull;

@SpringBootTest
public class JetApiIntegrationTest extends AbstractTestNGSpringContextTests {

    private final RestTemplate restTemplate = new RestTemplate();

    @Test(description = "Personal Keywords Remote API Test",
        suiteName = "Jet Api Integration Test", priority = 1)
    void remoteApiTest() {
        String url = "https://newtg.natapp4.cc/midjetapi/personal-keywords/list?keyword=123&page=1&size=10";
        RequestEntity<Object> request = new RequestEntity<>(HttpMethod.GET, URI.create(url));
        ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(request, responseType);
        assertNotNull(response);
        assertNotNull(response.getBody());
    }
}
