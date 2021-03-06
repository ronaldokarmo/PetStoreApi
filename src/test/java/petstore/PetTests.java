package petstore;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.Data;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

public class PetTests {

    private int petId;
    private Data json;

    @BeforeMethod
    public void SetUp() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        json = new Data();
    }

    @Test(priority = 1)
    public void incluirPet() throws IOException {
        String bodyJson = json.getJson("db/pet1.json");

        petId = given()
                .log().all()
                .contentType("application/json")
                .body(bodyJson)
        .when()
                .post("/pet")
        .then()
                .log().all()
                .statusCode(200)
                .body("category.name", is("Dogs"))
                .body("name", is("Nainai"))
                .body("tags.name", contains("breed"))
                .body("status", is("available"))
        .extract()
                .jsonPath().getInt("id");

        System.out.println("petId: ".concat(String.valueOf(petId)));
    }

    @Test(priority = 2)
    public void consultarPet() {
        given()
                .log().all()
                .contentType("application/json")
        .when()
                .get("/pet/".concat(String.valueOf(petId)))
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(petId))
                .body("name", is("Nainai"))
                .body("status", is("available"));
    }

    @Test(priority = 3)
    public void alterarPet() throws IOException {
        String bodyJson = json.getJson("db/pet2.json");

        given()
                .log().all()
                .contentType("application/json")
                .body(bodyJson)
        .when()
                .put("/pet")
        .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Jazz"))
                .body("status", is("sold"));
    }

    @Test(priority = 4)
    public void  excluirPet() {
        given()
                .log().all()
                .contentType("application/json")
        .when()
                .delete("/pet/".concat(String.valueOf(petId)))
        .then()
                .log().all()
                .statusCode(200)
                .body("code", is(200))
                .body("type", is ("unknown"))
                .body("message", is(String.valueOf(petId)));
    }

    @Test(priority = 5)
    public void consultarPetPorStatus(){
        String status = "available";

        given()
                .log().all()
                .contentType("application/json")
        .when()
                .get("/pet/findByStatus?status=".concat(status))
        .then()
                .log().all()
                .statusCode(200)
                .body("name[]", everyItem(equalTo("Jazz")));
    }
}