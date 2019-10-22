package features;

import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class petStoreStep {

    Response response;
    String baseUrl = "https://petstore.swagger.io/v2";

    @Given("^a request to create a pet with following details is sent$")
    public void a_request_to_create_a_pet_with_following_details_is_sent(DataTable table) throws Throwable {

        List<Map<String, String>> list = table.asMaps(String.class, String.class);

        Map requiredList = list.get(0);

        RestAssured.baseURI = baseUrl;
        RequestSpecification httpRequest = RestAssured.given();

        JSONObject category = new JSONObject();
        category.put("id", requiredList.get("categoryId"));
        category.put("name", requiredList.get("categoryName"));

        JSONArray photoUrls = new JSONArray();
        photoUrls.add(requiredList.get("photoUrls"));


        JSONArray tags = new JSONArray();
        JSONObject tagObjects = new JSONObject();
        tagObjects.put("id", requiredList.get("tagId"));
        tagObjects.put("name", requiredList.get("tagName"));
        tags.add(tagObjects);


        JSONObject requestParams = new JSONObject();
        requestParams.put("name", requiredList.get("name"));
        requestParams.put("photoUrls", photoUrls);
        requestParams.put("status", requiredList.get("status"));
        requestParams.put("category", category);
        requestParams.put("tags", tags);


        httpRequest.header("Content-Type", "application/json");
        httpRequest.body(requestParams.toJSONString());
        response = httpRequest.post("/pet");
    }


    @Then("^response body with pet details \"([^\"]*)\" as \"([^\"]*)\" should be returned$")
    public void response_body_with_pet_details_as_should_be_returned(String details, String expectedValue) throws Throwable {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String actualDetails = jsonPathEvaluator.get(details).toString();
        Assert.assertEquals(actualDetails, expectedValue);
    }

    @When("^pet details \"([^\"]*)\" is updated to \"([^\"]*)\"$")
    public void pet_details_is_updated_to(String details, String expectedValue) throws Throwable {

        JsonPath jsonPathEvaluator = response.jsonPath();
        String currentId = jsonPathEvaluator.get("id").toString();
        String endPoint = "/pet/" + currentId;

        RestAssured.baseURI = baseUrl;
        RequestSpecification httpRequest = RestAssured.given();

        httpRequest.urlEncodingEnabled(true).param(details, expectedValue);
        httpRequest.post(endPoint);
        response = httpRequest.get(endPoint);
    }

    @Then("^response body with pet details category name as \"([^\"]*)\" should be returned$")
    public void response_body_with_pet_details_category_name_as_should_be_returned(String expectedName) throws Throwable {

        JsonPath jsonPathEvaluator = response.jsonPath();
        String actualDetails = ((LinkedHashMap) jsonPathEvaluator.get("category")).get("name").toString();
        Assert.assertEquals(actualDetails, expectedName);
    }

    @Then("^response body with pet details category id as \"([^\"]*)\" should be returned$")
    public void response_body_with_pet_details_category_id_as_should_be_returned(String expectedId) throws Throwable {

        JsonPath jsonPathEvaluator = response.jsonPath();
        String actualDetails = ((LinkedHashMap) jsonPathEvaluator.get("category")).get("id").toString();
        Assert.assertEquals(actualDetails, expectedId);
    }

    @Then("^response body with pet details tag name as \"([^\"]*)\" should be returned$")
    public void response_body_with_pet_details_tag_name_as_should_be_returned(String tagName) throws Throwable {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String actualDetails = jsonPathEvaluator.get("tags").toString();
        Assert.assertTrue(actualDetails.contains(tagName));
    }

    @Then("^response body with pet details tag id as \"([^\"]*)\" should be returned$")
    public void response_body_with_pet_details_tag_id_as_should_be_returned(String expectedId) throws Throwable {

        JsonPath jsonPathEvaluator = response.jsonPath();
        String actualDetails = jsonPathEvaluator.get("tags").toString();
        Assert.assertTrue(actualDetails.contains(expectedId));
    }

    @Then("^response body with pet details photoUrls containing \"([^\"]*)\" should be returned$")
    public void responseBodyWithPetDetailsPhotoUrlsContainingShouldBeReturned(String expectedUrls) throws Throwable {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String actualDetails = ((ArrayList) jsonPathEvaluator.get("photoUrls")).toString();
        Assert.assertTrue(actualDetails.contains(expectedUrls));
    }


    @After
    public void cleanUp() {
        createdPetIsDeleted();
    }


    @When("^created Pet is deleted$")
    public void createdPetIsDeleted() {

        JsonPath jsonPathEvaluator = response.jsonPath();
        if (response != null && response.body().asString().contains("id") ) {
            String currentId = jsonPathEvaluator.get("id").toString();
            String endPoint = "/pet/" + currentId;
            RestAssured.baseURI = baseUrl;
            RequestSpecification httpRequest = RestAssured.given();
            httpRequest.delete(endPoint);
        }
    }

    @Then("^message with \"([^\"]*)\" is displayed during retrieval$")
    public void messageWithIsDisplayedDuringRetrieval(String message) throws Throwable {
        JsonPath jsonPathEvaluator = response.jsonPath();
        String currentId = jsonPathEvaluator.get("id").toString();
        String endPoint = "/pet/" + currentId;

        RestAssured.baseURI = baseUrl;
        RequestSpecification httpRequest = RestAssured.given();

        response = httpRequest.get(endPoint);
        jsonPathEvaluator = response.jsonPath();
        String body = jsonPathEvaluator.get("message").toString();

        Assert.assertEquals(message.toLowerCase(), body.toLowerCase());
    }
}
