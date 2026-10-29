package com.switchfully.parkshark.division.api;

import com.switchfully.parkshark.division.api.dto.CreateDivisionDto;
import com.switchfully.parkshark.division.api.dto.DirectorDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DivisionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void givenValidDivisionDto_whenCreatingDivision_thenReturnCreatedDivision() {
        CreateDivisionDto request = new CreateDivisionDto(
                "Big entreprise",
                "Small entreprise",
                new DirectorDto("Noéline", "LaBourreau")
        );

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post("/divisions")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("name", equalTo("Big entreprise"))
                .body("originalName", equalTo("Small entreprise"))
                .body("directorDto.firstName", equalTo("Noéline"))
                .body("directorDto.lastName", equalTo("LaBourreau"));
    }

    @Test
    void givenInvalidDivisionDto_whenCreatingDivision_thenReturn400() {
        CreateDivisionDto request = new CreateDivisionDto(
                "",
                "Small entreprise",
                new DirectorDto("Noéline", "LaBourreau")
        );

        given()
                .port(port)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
        .when()
                .post("/divisions")
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
