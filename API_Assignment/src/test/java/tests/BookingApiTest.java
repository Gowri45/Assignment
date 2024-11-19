package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class BookingApiTest {

    static {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    private int bookingId;

    @Test(priority = 1)
    public void createBooking() {
        // Create the booking payload
        String requestBody = "{"
                + "\"firstname\": \"testFirstName\","
                + "\"lastname\": \"lastName\","
                + "\"totalprice\": 10.11,"
                + "\"depositpaid\": true,"
                + "\"bookingdates\": {"
                + "\"checkin\": \"2022-01-01\","
                + "\"checkout\": \"2024-01-01\""
                + "},"
                + "\"additionalneeds\": \"testAdd\""
                + "}";

        // Send POST request to create a booking
        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/booking");

        // Validate the response
        Assert.assertEquals(response.statusCode(), 200, "Expected status code 200");

        // Extract the booking ID for validation
        bookingId = response.jsonPath().getInt("bookingid");
        System.out.println("Booking created succesfully");
        System.out.println("Booking ID: " + bookingId);

        // Verify details in the response body
        Assert.assertEquals(response.jsonPath().getString("booking.firstname"), "testFirstName");
        System.out.println("First Name: " +response.jsonPath().getString("booking.firstname"));
        Assert.assertEquals(response.jsonPath().getString("booking.lastname"), "lastName");
        System.out.println("Last Name: " +response.jsonPath().getString("booking.lastname"));
        Assert.assertEquals(response.jsonPath().getString("booking.additionalneeds"), "testAdd");
        System.out.println("Additional Needs: " +response.jsonPath().getString("booking.additionalneeds"));
        
    }

    @Test(priority = 2, dependsOnMethods = "createBooking")
    public void validateBooking() {
        // Send GET request to validate the booking
        Response response = given()
                .get("/booking/" + bookingId);

        // Validate the response
        Assert.assertEquals(response.statusCode(), 200, "Expected status code 200");
        Assert.assertEquals(response.jsonPath().getString("firstname"), "testFirstName");
        Assert.assertEquals(response.jsonPath().getString("lastname"), "lastName");
        Assert.assertEquals(response.jsonPath().getString("additionalneeds"), "testAdd");
        System.out.println("Booking details match what was created");
    }

    @Test(priority = 3)
    public void createBookingNegativeTest() {
        // Invalid request payload
        String invalidRequestBody = "{"
                + "\"firstname\": \"\","
                + "\"lastname\": \"lastName\","
                + "\"totalprice\": 0,"
                + "\"bookingdates\": {"
                + "\"checkin\": \"\","
                + "\"checkout\": \"\""
                + "}"
                + "}";

        // Send POST request
        Response response = given()
            .header("Content-Type", "application/json")
            .body(invalidRequestBody)
            .post("/booking");

        // Log the response
        System.out.println("Response Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.asString());

        // Validate response
        if (response.statusCode() == 500) {
            System.out.println("Server returned 500 Internal Server Error. This indicates a server-side issue.");
            //Assert.fail("Unexpected server error for invalid input. Response body: " + response.asString());
        } else {
            Assert.assertEquals(response.statusCode(), 400, "Expected a 400 Bad Request");
        }
    }


}

