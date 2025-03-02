package au.com.telstra.simcardactivator.controller;

import au.com.telstra.simcardactivator.model.ActivationRecord;
import au.com.telstra.simcardactivator.model.ActivationRequest;
import au.com.telstra.simcardactivator.repository.ActivationRecordRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ActivationController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ActivationRecordRepository repository;

    public ActivationController(ActivationRecordRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activateSim(@RequestBody ActivationRequest request) {
        String actuatorUrl = "http://localhost:8444/actuate";

        // new
        String iccid = request.getIccid();
        String email = request.getCustomerEmail();

        // Create request payload
        Map<String, String> payload = Collections.singletonMap("iccid", request.getIccid());

        try {
            // Send POST request to actuator service
            ResponseEntity<Map> response = restTemplate.postForEntity(actuatorUrl, payload, Map.class);

            // Check success status from actuator response
            boolean success = (boolean) response.getBody().get("success");

            // save to db
            ActivationRecord record = new ActivationRecord(iccid, email, success);
            repository.save(record);

            if (success) {
//                System.out.println("SIM activation successful!");
                return ResponseEntity.ok("SIM activation successful!");
            } else {
//                System.out.println("SIM activation failed.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SIM activation failed.");
            }

        } catch (Exception e) {
            System.err.println("Error communicating with actuator: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to communicate with the actuator service.");
        }
    }
    @GetMapping("/activation/{simCardId}")
    public ResponseEntity<Object> getActivationById(@PathVariable Long simCardId) {
        Optional<ActivationRecord> activationRecord = repository.findById(simCardId);

        if (activationRecord.isPresent()) {
            // Return activation details as JSON
            ActivationRecord record = activationRecord.get();
            return ResponseEntity.ok().body(Map.of(
                    "iccid", record.getIccid(),
                    "customerEmail", record.getCustomerEmail(),
                    "active", record.isActive()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SIM activation record not found");
        }
    }
}