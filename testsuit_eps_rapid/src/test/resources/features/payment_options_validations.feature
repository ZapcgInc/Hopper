@payment_options
Feature: Validations for Payment Options API.

  Background:
    Given partner test setup
    And with shopping query parameters
      | checkin           | 2019-02-15        |
      | checkout          | 2019-02-17        |
      | currency          | USD               |
      | language          | en-US             |
      | country_code      | US                |
      | property_id       | 20321             |
      | occupancy         | 2-9,4             |
      | sales_channel     | website           |
      | sales_environment | hotel_only        |
      | sort_type         | preferred         |
      | include           | all_rates         |
      | rate_option       | closed_user_group |
    And with shopping end point "properties/availability"

  Scenario: Payment Options API successful response
    Given run shopping
    When run paymentOptions
    Then the response code for "PAYMENT_OPTIONS" should be 200


  Scenario: Payment Options API for validation of "credit_card"
    Given run shopping
    When run paymentOptions
    Then the response code for "PAYMENT_OPTIONS" should be 200
    And validate "CARD_TYPE"  for "PAYMENT_OPTIONS"