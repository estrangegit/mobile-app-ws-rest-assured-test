package com.appsdeveloperblog.app.ws.restassuredtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UsersWebServiceEndpointTest {

  private final String CONTEXT_PATH = "/mobile-app-ws";
  private final String EMAIL_ADDRESS = "etienne.estrangin@gmail.com";
  private final String PASSWORD = "123";
  private final String JSON_HEADER = "application/json";
  private static String authorizationHeader;
  private static String userID;
  private static List<Map<String, String>> addresses;

  @BeforeEach
  void setUp() throws Exception {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8080;
  }

  /**
   * testUserLogin()
   */
  @Test
  final void a() {
    Map<String, String> loginDetails = new HashMap<>();
    loginDetails.put("email", EMAIL_ADDRESS);
    loginDetails.put("password", PASSWORD);

    // @formatter:off
    Response response = given()
      .contentType(JSON_HEADER)
      .accept(JSON_HEADER)
      .body(loginDetails)
      .when()
      .post(CONTEXT_PATH + "/users/login")
      .then()
      .statusCode(HttpStatus.SC_OK)
      .extract()
      .response();
    // @formatter: on

    authorizationHeader = response.header("Authorization");
    userID = response.header("UserID");

    assertNotNull(authorizationHeader);
    assertNotNull(userID);
  }

  /**
   * testGetUserDetails()
   */
  @Test
  final void b() {
    // @formatter:off
    Response response = given()
      .pathParam("id", userID)
      .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
      .contentType(JSON_HEADER)
      .accept(JSON_HEADER)
      .when()
      .get(CONTEXT_PATH + "/users/{id}")
      .then()
      .statusCode(HttpStatus.SC_OK)
      .extract()
      .response();
    // @formatter: on

    String userPublicId = response.jsonPath().getString("userId");
    String userEmail = response.jsonPath().getString("email");
    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");

    addresses = response.jsonPath().getList("addresses");
    String addressId = addresses.get(0).get("addressId");


    assertNotNull(userPublicId);
    assertNotNull(userEmail);
    assertNotNull(firstName);
    assertNotNull(lastName);
    assertEquals(EMAIL_ADDRESS, userEmail);

    assertTrue(addresses.size()==2);
    assertTrue(addressId.length()==30);
  }

  /**
   * Test Update User Details
   */
  @Test
  final void c() {

    Map<String, Object> userDetails = new HashMap<>();
    userDetails.put("firstName", "newFirstName");
    userDetails.put("lastName", "newLastName");

    // @formatter:off
    Response response = given()
    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
    .contentType(JSON_HEADER)
    .accept(JSON_HEADER)
    .pathParam("id", userID)
    .body(userDetails)
    .when()
    .put(CONTEXT_PATH + "/users/{id}")
    .then()
    .statusCode(HttpStatus.SC_OK)
    .extract()
    .response();
    // @formatter:on

    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");

    List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

    assertEquals(firstName, "newFirstName");
    assertEquals(lastName, "newLastName");
    assertNotNull(storedAddresses);
    assertTrue(addresses.size() == storedAddresses.size());
    assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));
  }

  @Test
  final void d() {
    // @formatter:off
    Response response = given()
      .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
      .accept(JSON_HEADER)
      .pathParam("id", userID)
      .when()
      .delete(CONTEXT_PATH + "/users/{id}")
      .then()
      .statusCode(HttpStatus.SC_OK)
      .extract()
      .response();
    // @formatter:on

    String operationResult = response.jsonPath().getString("operationResult");
    assertEquals("SUCCESS", operationResult);
  }
}
