package com.github.yuliyadzemidovich.parceldeliveryapp.filter;

import com.github.yuliyadzemidovich.parceldeliveryapp.ParcelDeliveryAppApplication;
import com.github.yuliyadzemidovich.parceldeliveryapp.TestUtil;
import com.github.yuliyadzemidovich.parceldeliveryapp.dto.OrderDto;
import com.github.yuliyadzemidovich.parceldeliveryapp.entity.User;
import com.github.yuliyadzemidovich.parceldeliveryapp.repository.UserRepository;
import com.github.yuliyadzemidovich.parceldeliveryapp.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ParcelDeliveryAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("local") // include context-path var
@AutoConfigureTestDatabase // use in-memory H2 instead of init Datasource bean with connection to the real database
class JWTAuthorizationFilterIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    TestRestTemplate restTemplate;
    @MockBean
    JwtService jwtServiceMock;
    @MockBean
    UserRepository userRepoMock;

    private String baseUrl;

    @BeforeEach
    void init() {
        baseUrl = "http://localhost:" + port + contextPath;
    }

    @Test
    void allowedNoAuthRequest() {
        String url = baseUrl + "/actuator/health";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void forbiddenNoAuthRequest() {
        String url = baseUrl + "/api/v1/user/orders";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() { });
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void invalidAuthJwtRequest() {
        String url = baseUrl + "/api/v1/user/orders";
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer fake.jwt.token");
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() { });
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void unknownUserWithJwtRequest() {
        String url = baseUrl + "/api/v1/user/orders";

        // mock JWT token successful validation
        String validToken = "mocked.jwt.token";
        when(jwtServiceMock.isValidToken(validToken)).thenReturn(true);
        String notExistingEmail = "fake@email.com";
        when(jwtServiceMock.extractUserEmail(validToken)).thenReturn(notExistingEmail);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() { });
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void allowedWithJwtRequest() {
        String url = baseUrl + "/api/v1/user/orders";

        // mock JWT token successful validation
        String validToken = "mocked.jwt.token";
        when(jwtServiceMock.isValidToken(validToken)).thenReturn(true);
        User existingUser = TestUtil.getExistingUser();
        String existingEmail = existingUser.getEmail();
        when(jwtServiceMock.extractUserEmail(validToken)).thenReturn(existingEmail);

        // mock user exists in the database
        when(userRepoMock.existsByEmail(existingEmail)).thenReturn(true);
        when(userRepoMock.findByEmail(existingEmail)).thenReturn(existingUser);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<OrderDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() { });
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
