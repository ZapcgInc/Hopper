@payment_options
Feature: Validations for Payment Options API.

  Background:
    Given partner test setup
    And with shopping query parameters
      | currency          | USD               |
      | language          | en-US             |
      | country_code      | US                |
#      | property_id       |             20321 |
      | occupancy         | 2-9,4             |
      | sales_channel     | website           |
      | sales_environment | hotel_only        |
      | sort_type         | preferred         |
      | include           | all_rates         |
      | rate_option       | closed_user_group |
    And with query param "property_id" from config
    And with request DateFormat "yyyy-MM-dd"
    And set checkin "90" from today with lengthOfStay "2" by default

  ########################Business Validation

  @business_test
  Scenario:[PAYT1] Payment Options API successful response
    Given run shopping
    When run paymentOptions
    Then the response code for "PAYMENT_OPTIONS" should be 200


  @business_test
  Scenario:[PAYT3] Payment Options API for validation of "CARD_TYPE"
    Given run shopping
    When run paymentOptions
    Then the response code for "PAYMENT_OPTIONS" should be 200
    And validate "CARD_TYPE"  for "PAYMENT_OPTIONS"