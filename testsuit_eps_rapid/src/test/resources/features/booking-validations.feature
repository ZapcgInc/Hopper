@Booking
Feature: Validations for Booking API.

  Background: 
    Given setup for partner with config at "expedia-config.yml"
    And with shopping query parameters
      | currency          | USD               |
      | language          | en-US             |
      | country_code      | US                |
      | property_id       |             20321 |
      | occupancy         | 2-9,4             |
      | sales_channel     | website           |
      | sales_environment | hotel_only        |
      | sort_type         | preferred         |
      | include           | all_rates         |
      | rate_option       | closed_user_group |
    And with request DateFormat "yyyy-MM-dd"
    And set checkin "90" from today with lengthOfStay "3"

  #######################   Rapid Test Scenarios
  @rapid_test
  Scenario: Booking API Rapid test Header "Test" with "standard"
    Given run shopping and preBooking for Booking
    When set header "Test" value "standard"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be "201"

  @rapid_test
  Scenario Outline: Booking API Rapid test Header "<header>" with "<value>"
    Given run shopping and preBooking for Booking
    When set header "<header>" value "<value>"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be "<code>"
    And user should see "BOOKING" response with paris on the filtered "." node
      | type    | <type>    |
      | message | <message> |

    Examples: 
      | header | value                 | code | type                  | message                                                                       |
      | Test   | rooms_unavailable     |  410 | rooms_unavailable     | One or more requested rooms are unavailable.                                  |
      | Test   | price_mismatch        |  409 | price_mismatch        | Payment amount did not match current price, please check price and try again. |
      | Test   | price_unavailable     |  410 | rooms_unavailable     | One or more requested rooms are unavailable.                                  |
      | Test   | internal_server_error |  500 | create.system_failure | An internal server error has occurred.                                        |
      | Test   | service_unavailable   |  503 | create.no_response    | Create failed due to no response from itinerary creation system.              |

  @rapid_test
  Scenario Outline: Booking API Rapid test Header "<header>" with "<value>"
    Given run shopping and preBooking for Booking
    When set header "<header>" value "<value>"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be "<code>"
    And user should see "BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "BOOKING" response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |

    Examples: 
      | header | value       | code | error_type           | error_message                                                                                                                                                                                                |
      | Test   | cc_declined |  400 | payments.invalid     | Payment information is invalid.                                                                                                                                                                              |
      | Test   | invalid     |  400 | test.content_invalid | Content of the test header is invalid. Please use one of the following valid values: STANDARD, ROOMS_UNAVAILABLE, CC_DECLINED, PRICE_UNAVAILABLE, PRICE_MISMATCH, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE |

  @business_test
  Scenario: Booking API successful response
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
