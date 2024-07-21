package com.zufar.icedlatte.payment.endpoint;

import com.zufar.icedlatte.order.endpoint.OrderEndpoint;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.zufar.icedlatte.test.config.RestUtils.getJwtToken;
import static com.zufar.icedlatte.test.config.RestUtils.getRequestBody;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@Testcontainers
@DisplayName("PaymentEndpoint Tests")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentEndpointTest {

    private static final String ORDER_CREATE_BODY = "/order/model/create-order-body.json";

    protected static RequestSpecification specification;

    protected static String jwtToken;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.11-bullseye");

    @LocalServerPort
    protected Integer port;

    @Value("${jwt.email}")
    protected String email;

    @Value("${jwt.password}")
    protected String password;

    @BeforeEach
    void tokenAndSpecification() {
        jwtToken = getJwtToken(port, email, password);
        specification = given()
                .log().all(true)
                .port(port)
                .header("Authorization", "Bearer " + jwtToken)
                .basePath(PaymentEndpoint.PAYMENT_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    String createOrder() {
        String body = getRequestBody(ORDER_CREATE_BODY);

        Response response = given()
                .log().all(true)
                .port(port)
                .header("Authorization", "Bearer " + jwtToken)
                .basePath(OrderEndpoint.ORDER_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post();

        return response.then().extract().path("id");
    }

    @Test
    @DisplayName("Should create payment session successfully and return object containing client secret")
    void processPayment() {
        var orderId = createOrder();
        Response paymentResponse = given(specification)
                .param("orderId", orderId)
                .get();

        paymentResponse.then().statusCode(HttpStatus.OK.value());
        paymentResponse.then().body("sessionId", notNullValue());
        paymentResponse.then().body("clientSecret", notNullValue());
    }
}