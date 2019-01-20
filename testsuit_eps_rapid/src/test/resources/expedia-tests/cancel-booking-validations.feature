@Booking
Feature: Validations for PreBooking API.

  Background:
    Given setup for partner with config at "expedia-config.yml"
    And with shopping query parameters
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
    And with request DateFormat "yyyy-MM-dd"
    And set checkin "90" from today with lengthOfStay "5"
    And set multiple values for "SHOPPING" queryParam "property_id" with "8521|3317|19762|9100"

  #######################   Rapid Test Scenarios
  @business_test
  Scenario: Run booking and validate Status
    Given set checkin "5" from today with lengthOfStay "3"
    And run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "true"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200
    And cancel booking
    And the response code for "CANCEL_BOOKING" should be 204