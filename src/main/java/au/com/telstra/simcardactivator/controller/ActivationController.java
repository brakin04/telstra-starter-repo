package au.com.telstra.simcardactivator.controller;

import au.com.telstra.simcardactivator.model.ActivationRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ActivationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody ActivationRequest request) {
        String actuatorUrl = "http://localhost:8444/actuate";

        // Create request payload
        Map<String, String> payload = Collections.singletonMap("iccid", request.getIccid());

        try {
            // Send POST request to actuator service
            ResponseEntity<Map> response = restTemplate.postForEntity(actuatorUrl, payload, Map.class);

            // Check success status from actuator response
            boolean success = (boolean) response.getBody().get("success");

            if (success) {
                System.out.println("SIM activation successful!");
                return ResponseEntity.ok("SIM activation successful!");
            } else {
                System.out.println("SIM activation failed.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SIM activation failed.");
            }

        } catch (Exception e) {
            System.err.println("Error communicating with actuator: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to communicate with the actuator service.");
        }
    }
}