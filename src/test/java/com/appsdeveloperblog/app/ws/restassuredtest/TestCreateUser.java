package com.appsdeveloperblog.app.ws.restassuredtest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

class TestCreateUser {

  private final String CONTEXT_PATH = "/mobile-app-ws";

  @BeforeEach
  void setUp() throws Exception {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8080;
  }

  @Test
  final void testCreateUser() {

    final List<Map<String, String>> userAddresses = new ArrayList<>();

    final Map<String, String> shippingAddress = new HashMap<>();
    shippingAddress.put("city", "Vancouver");
    shippingAddress.put("country", "Canada");
    shippingAddress.put("streetName", "123 Street name");
    shippingAddress.put("postalCode", "123456");
    shippingAddress.put("type", "shipping");

    final Map<String, String> billingAddress = new HashMap<>();
    billingAddress.put("city", "Vancouver");
    billingAddress.put("country", "Canada");
    billingAddress.put("streetName", "123 Street name");
    billingAddress.put("postalCode", "123456");
    billingAddress.put("type", "billing");

    userAddresses.add(shippingAddress);
    userAddresses.add(billingAddress);

    final Map<String, Object> userDetails = new HashMap<>();
    userDetails.put("firstName", "Etienne");
    userDetails.put("lastName", "Estrangin");
    userDetails.put("email", "etienne.estrangin@gmail.com");
    userDetails.put("password", "123");
    userDetails.put("addresses", userAddresses);

    // @formatter:off
    final Response response = given().contentType("application/json")
          .accept("application/json")
          .body(userDetails)
          .when()
          .post(CONTEXT_PATH + "/users")
          .then()
          .statusCode(HttpStatus.SC_OK)
          .contentType("application/json")
          .extract()
          .response();
    // @formatter:on

    final String userId = response.jsonPath().getString("userId");
    assertNotNull(userId);
    assertTrue(userId.length() == 30);

    final String bodyString = response.body().asString();
    try {

      final JSONObject responseBodyJson = new JSONObject(bodyString);
      final JSONArray addresses = responseBodyJson.getJSONArray("addresses");

      assertNotNull(addresses);
      assertTrue(addresses.length() == 2);

      final String addressId = addresses.getJSONObject(0).getString("addressId");
      assertNotNull(addressId);
      assertTrue(addressId.length() == 30);

    } catch (final JSONException e) {
      fail(e.getMessage());
    }
  }
}
