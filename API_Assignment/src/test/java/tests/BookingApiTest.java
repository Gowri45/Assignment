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

    @Test(priority = 1)
    public void createBooking() {
        // Create booking payload
    	String requestBody = """
    		    {
    		        "firstname" : "testFirstName",
    		        "lastname" : "lastName",
    		        "totalprice" : 10,
    		        "depositpaid" : true,
    		        "bookingdates" : {
    		            "checkin" : "2022-01-01",
    		            "checkout" : "2024-01-01"
    		        },
    		        "additionalneeds" : "testAdd"
    		    }
    		""";

        // Create booking and verify response
        Response response = given()
            .header("Content-Type", "application/json")
            .body(requestBody)
            .post("/booking");

        Assert.assertEquals(response.statusCode(), 200, "Failed to create booking");

        // Extract booking ID for validation
        int bookingId = response.jsonPath().getInt("bookingid");
        System.out.println("Booking ID: " + bookingId);

        // Validate the response body
        Assert.assertEquals(response.jsonPath().getString("booking.firstname"), "testFirstName");
        Assert.assertEquals(response.jsonPath().getString("booking.lastname"), "lastName");
        Assert.assertEquals(response.jsonPath().getInt("booking.totalprice"), 10);
        Assert.assertEquals(response.jsonPath().getBoolean("booking.depositpaid"), true);

        // Store booking ID for future tests (Use a global variable or TestNG @BeforeClass)
        BookingContext.bookingId = bookingId;
    }

    @Test(priority = 2, dependsOnMethods = "createBooking")
    public void validateBooking() {
        // Validate booking using the stored booking ID
        Response response = given()
            .get("/booking/" + BookingContext.bookingId);

        Assert.assertEquals(response.statusCode(), 200, "Booking validation failed");
        Assert.assertEquals(response.jsonPath().getString("firstname"), "testFirstName");
        Assert.assertEquals(response.jsonPath().getString("lastname"), "lastName");
        Assert.assertEquals(response.jsonPath().getInt("totalprice"), 10);
        Assert.assertEquals(response.jsonPath().getBoolean("depositpaid"), true);
    }

    @Test(priority = 3)
    public void createBookingNegativeTest() {
        // Negative test case with missing required fields
        String invalidRequestBody = """
            {
                "lastname" : "lastName"
            }
        """;

        Response response = given()
            .header("Content-Type", "application/json")
            .body(invalidRequestBody)
            .post("/booking");

        Assert.assertEquals(response.statusCode(), 400, "Expected a 400 Bad Request");
    }
}

// Context class to store shared data between tests
class BookingContext {
    public static int bookingId;
}


