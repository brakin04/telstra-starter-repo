package stepDefinitions;

import au.com.telstra.simcardactivator.SimCardActivator;
import config.TestRestTemplateConfig;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.*;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = {SimCardActivator.class, TestRestTemplateConfig.class}, loader = SpringBootContextLoader.class)
public class SimCardActivatorStepDefinitions {
    @Autowired
    private TestRestTemplate restTemplate;

    private String iccid;
    private String customerEmail;
    private ResponseEntity<String> activationResponse;
    private ResponseEntity<Map> databaseResponse;

    @Given("a SIM card with ICCID {string} and customer email {string}")
    public void aSimCard(String iccid, String customerEmail) {
        this.iccid = iccid;
        this.customerEmail = customerEmail;
    }

    @When("the activation request is sent")
    public void requestSent() {
        String url = "http://localhost:8080/api/activate";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("iccid", iccid);
        requestBody.put("customerEmail", customerEmail);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        activationResponse = restTemplate.postForEntity(url, request, String.class);
    }
    @Then("the response should indicate success")
    public void shouldBeSuccess() {
        assertEquals("SIM activation successful!", activationResponse.getBody());
    }
    @Then("the response should indicate failure")
    public void shouldBeFailure() {
        assertEquals("SIM activation failed.", activationResponse.getBody());
    }
    @Then("the activation record should be stored in the database with status {string}")
    public void storedInDatabase(String expectedStatus) {
        long recordId = iccid.equals("1255789453849037777") ? 1 : 2; // Auto-increment assumption
        String url = "http://localhost:8080/api/activation/" + recordId;

        databaseResponse = restTemplate.getForEntity(url, Map.class);

        assertEquals(HttpStatus.OK, databaseResponse.getStatusCode());
        Map<String, Object> activationRecord = databaseResponse.getBody();

        assertNotNull(activationRecord);
        assertEquals(iccid, activationRecord.get("iccid"));
        assertEquals(customerEmail, activationRecord.get("customerEmail"));
        assertEquals(Boolean.parseBoolean(expectedStatus), activationRecord.get("active"));
    }
}