package com.marekhudyma.springboot.validations;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  // basic validation

  @Test
  void shouldPassBasicValidationForEndpointWithBasicValidation() throws Exception {
    String url = format("http://localhost:%s/endpoint-basic-validation", port);
    MyController.Body body = new MyController.Body("notEmail", 1_000);

    HttpEntity<Object> entity = new HttpEntity<Object>(body);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @Test
  void shouldFailValidationForEndpointWithBasicValidationBecauseEmptyString() throws Exception {
    String url = format("http://localhost:%s/endpoint-basic-validation", port);
    MyController.Body body = new MyController.Body(null, 100);

    HttpEntity<Object> entity = new HttpEntity<Object>(body);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  void shouldFailValidationForEndpointWithBasicValidationBecauseIntegerProperty() throws Exception {
    String url = format("http://localhost:%s/endpoint-basic-validation", port);
    MyController.Body body = new MyController.Body("notEmail", -1);

    HttpEntity<Object> entity = new HttpEntity<Object>(body);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is4xxClientError());
  }

  // extended validation

  @Test
  void shouldPassBasicValidationForEndpointWithExtendedValidation() throws Exception {
    String url = format("http://localhost:%s/endpoint-extended-validation", port);
    MyController.Body body = new MyController.Body("email@example.com", 10);

    HttpEntity<Object> entity = new HttpEntity<Object>(body);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @Test
  void shouldFailValidationForEndpointWithExtendedValidationBecauseNotProperEmail() throws Exception {
    String url = format("http://localhost:%s/endpoint-extended-validation", port);
    MyController.Body body = new MyController.Body("not-email", 10);

    HttpEntity<Object> entity = new HttpEntity<Object>(body);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is4xxClientError());
  }

  @Test
  void shouldFailValidationForEndpointWithExtendedValidationBecauseIntegerTooBig() throws Exception {
    String url = format("http://localhost:%s/endpoint-extended-validation", port);
    MyController.Body body = new MyController.Body("email@example.com", 1_000);

    HttpEntity<Object> entity = new HttpEntity<Object>(body);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is4xxClientError());
  }

  // mixed validation

  @Test
  void shouldPassBasicValidationForEndpointWithMixedValidation() throws Exception {
    String url = format("http://localhost:%s/endpoint-mixed-validation", port);
    MyController.Body body = new MyController.Body("not-email", 10);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Request-Context", "basic");
    HttpEntity<Object> entity = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  @Test
  void shouldFailForExtendedValidationForEndpointWithMixedValidation() throws Exception {
    String url = format("http://localhost:%s/endpoint-mixed-validation", port);
    MyController.Body body = new MyController.Body("not-email", 1_000);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Request-Context", "extended");
    HttpEntity<Object> entity = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is4xxClientError());
  }

  // limited view

  @Test
  void shouldTransportOnlyLimitedView() throws Exception {
    String url = format("http://localhost:%s/endpoint-limited-view", port);
    MyController.Body body = new MyController.Body("not-email", 10);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Request-Context", "basic");
    HttpEntity<Object> entity = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is2xxSuccessful());
  }

  // custom validation

  @Test
  void shouldFailForCustomValidation() throws Exception {
    String url = format("http://localhost:%s//endpoint-custom-validation", port);
    MyController.Body body = new MyController.Body("ABC-XZY", 10);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Request-Context", "custom");
    HttpEntity<Object> entity = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    assertTrue(response.getStatusCode().is4xxClientError());
  }

}
