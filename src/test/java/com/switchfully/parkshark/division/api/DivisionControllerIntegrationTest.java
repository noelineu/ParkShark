package com.switchfully.parkshark.division.api;

import com.switchfully.parkshark.division.api.dto.CreateDivisionDto;
import com.switchfully.parkshark.division.api.dto.DirectorDto;
import com.switchfully.parkshark.division.domain.Division;
import com.switchfully.parkshark.division.domain.DivisionRepository;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DivisionControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DivisionRepository divisionRepository;

    @Test
    void givenValidDivisionDto_whenCreatingDivision_thenReturnCreatedDivision() {
        CreateDivisionDto request = new CreateDivisionDto(
                "Big entreprise",
                "Small entreprise",
                new DirectorDto("Noéline", "LaBourreau"),
                null
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
                new DirectorDto("Noéline", "LaBourreau"),
                null
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

    @Test
    void givenDivisionsExist_whenGettingAllDivisions_thenReturnSameCountAsDatabase() {
        long expectedCount = divisionRepository.count();

        given()
                .port(port)
                .accept(ContentType.JSON)
                .when()
                .get("/divisions")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("size()", equalTo((int) expectedCount));
    }

    @Test
    void givenExistingDivision_whenPostingACreateDivisionWithParentId_thenReturnCreated() {
        Division division = divisionRepository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        int parentId = division.getId();

        CreateDivisionDto request = new CreateDivisionDto(
                "Division With Parent ID",
                "Small entreprise",
                new DirectorDto("Noéline", "LaBourreau"),
                parentId
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
                .body("parentId", equalTo(parentId));
    }


    @Test
    void givenWrongIdForParent_whenPostingACreateDivisionWithParentId_thenReturnBadRequest() {

        int parentId = 65416151;

        CreateDivisionDto request = new CreateDivisionDto(
                "Division With Parent ID",
                "Small entreprise",
                new DirectorDto("Noéline", "LaBourreau"),
                parentId
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

    @Test
    void givenExistingDivision_whenGettingDivisionById_thenReturnDivision() {
        given()
                .port(port)
                .accept(ContentType.JSON)
                .when()
                .get("/divisions/1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("name", equalTo("ParkShark North"))
                .body("originalName", equalTo("Parking Vlaanderen NV"))
                .body("directorDto.lastName", equalTo("Sharkano"));
    }

    @Test
    void givenUnknownId_whenGettingDivisionById_thenReturnError() {
        given()
                .port(port)
                .accept(ContentType.JSON)
                .when()
                .get("/divisions/999999")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("detail", equalTo("No division found for id: 999999"));
    }

}
