package com.zufar.icedlatte.order.endpoint;

import com.zufar.icedlatte.cart.endpoint.CartEndpoint;
import com.zufar.icedlatte.cart.entity.ShoppingCart;
import com.zufar.icedlatte.openapi.dto.OrderStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static com.zufar.icedlatte.test.config.RestAssertion.assertRestApiBadRequestResponse;
import static com.zufar.icedlatte.test.config.RestAssertion.assertRestApiBodySchemaResponse;
import static com.zufar.icedlatte.test.config.RestAssertion.assertRestApiOkResponse;
import static com.zufar.icedlatte.test.config.RestUtils.getJwtToken;
import static com.zufar.icedlatte.test.config.RestUtils.getRequestBody;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@Testcontainers
@DisplayName("OrderEndpoint Tests")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderEndpointTest {

    private static final String SHOPPING_CART_ADD_BODY_LOCATION = "/cart/model/cart-add-body.json";
    private static final String ORDER_CREATE_BODY = "/order/model/create-order-body.json";
    private static final String ORDER_ADD_BAD_BODY = "/order/model/add-order-bad-body.json";
    private static final String ORDER_RESPONSE_SCHEMA = "order/model/schema/order-response-schema.json";
    private static final String FAILED_ORDER_SCHEMA = "common/model/schema/failed-request-schema.json";
    private static final String ORDER_LIST_SCHEMA = "order/model/schema/order-list-schema.json";

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
                .basePath(OrderEndpoint.ORDER_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    @Test
    @DisplayName("Should create order successfully and return object containing status 'CREATED'")
    void shouldCreateOrderSuccessfully() {
        String body = getRequestBody(ORDER_CREATE_BODY);

        Response response = given(specification)
                .body(body)
                .post();

        assertRestApiBodySchemaResponse(response, HttpStatus.OK, ORDER_RESPONSE_SCHEMA)
                .body("status", equalTo(OrderStatus.CREATED.toString()));
    }

    @Test
    @DisplayName("Missing required fields in request body. Should return 400 Bad Request")
    void shouldReturnBadRequestForBadBody() {
        String body = getRequestBody(ORDER_ADD_BAD_BODY);

        Response response = given(specification)
                .body(body)
                .post();

        assertRestApiBadRequestResponse(response, FAILED_ORDER_SCHEMA);
    }

    @Test
    @DisplayName("Can't access order URL w/o token. Should return 400 Bad Request")
    void shouldReturnUnauthorized() {
        specification = given()
                .log().all(true)
                .port(port)
                .basePath(OrderEndpoint.ORDER_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        String body = getRequestBody(ORDER_CREATE_BODY);
        Response responsePost = given(specification)
                .body(body)
                .post();
        Response responseGet = given(specification)
                .get();

        responsePost.then().statusCode(HttpStatus.BAD_REQUEST.value());
        responseGet.then().statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @Disabled("Temporarily skip, need to re-create shopping cart")
    @DisplayName("Should return list of orders")
    void shouldReturnListOfOrders() {
        // Create 1st order
        given(specification)
                .body(getRequestBody(ORDER_CREATE_BODY))
                .post();
        // Create new shopping cart
        createShoppingCart();
        // Create 2st order
        given(specification)
                .body(getRequestBody(ORDER_CREATE_BODY))
                .post();

        Response responseNoParam = given(specification)
                .get();
        Response responseWithParam = given(specification)
                .param("status", OrderStatus.CREATED)
                .get();

        var CREATED = OrderStatus.CREATED.toString();

        assertRestApiOkResponse(responseNoParam, ORDER_LIST_SCHEMA);
        responseNoParam.then().body("[0].status", is(CREATED));
        responseNoParam.then().body("[1].status", is(CREATED));

        assertRestApiOkResponse(responseWithParam, ORDER_LIST_SCHEMA);
        responseWithParam.then().body("[0].status", is(CREATED));
        responseWithParam.then().body("[1].status", is(CREATED));
    }

    @Test
    @DisplayName("Should return empty list of orders if no order matches the status")
    void shouldReturnEmptyListOfOrders() {
        // Create 1st order
        given(specification)
                .body(getRequestBody(ORDER_CREATE_BODY))
                .post();
        // Create new shopping cart
        createShoppingCart();
        // Create 2st order
        given(specification)
                .body(getRequestBody(ORDER_CREATE_BODY))
                .post();

        Response response = given(specification)
                .param("status", OrderStatus.DELIVERY)
                .get();

        assertRestApiBodySchemaResponse(response, HttpStatus.OK, ORDER_LIST_SCHEMA)
                .body("$[]", Matchers.hasSize(0));
    }

    @Test
    @DisplayName("Incorrect value for parameter status. Should return 400 Bad Request")
    void shouldReturnBadRequestForBadStatusParam() {
        Response response = given(specification)
                .param("status", "WRONG_VALUE")
                .get();

        assertRestApiBadRequestResponse(response, FAILED_ORDER_SCHEMA);
        response.then().body("message", is("Incorrect status value. Supported status: [CREATED, PAID, DELIVERY, FINISHED]"));
    }

    void createShoppingCart() {
        String body = getRequestBody(SHOPPING_CART_ADD_BODY_LOCATION);
        given()
                .log().all(true)
                .port(port)
                .header("Authorization", "Bearer " + jwtToken)
                .basePath(CartEndpoint.CART_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post("/items");
    }
}