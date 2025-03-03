Feature: SIM Card Activation

  Scenario: Successful SIM Card Activation
    Given a SIM card with ICCID "1255789453849037777" and customer email "user1@example.com"
    When the activation request is sent
    Then the response should indicate success
    And the activation record should be stored in the database with status "true"

  Scenario: Failed SIM Card Activation
    Given a SIM card with ICCID "8944500102198304826" and customer email "user2@example.com"
    When the activation request is sent
    Then the response should indicate failure
    And the activation record should be stored in the database with status "false"