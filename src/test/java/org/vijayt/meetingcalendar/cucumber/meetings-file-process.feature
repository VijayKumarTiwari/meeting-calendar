Feature: The feature describes how a meeting request file is processed

  Scenario: A error is thrown if file is not found
    Given a file "non-existing.txt"
    When file is processed
    Then error is reported

  Scenario: A error is thrown if file is empty
    Given a file "empty.txt"
    When file is processed
    Then error is reported

  Scenario: A error is thrown if file has invalid meeting request
    Given a file "invalid.txt"
    When file is processed
    Then error is reported

  Scenario: A valid meeting request file is processed properly
    Given a file "input.txt"
    When file is processed
    Then output is same as "output.txt"