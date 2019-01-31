@Cancel_Room_Booking
Feature: Validations for Room Cancellation API.

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
    And set checkin "90" from today with lengthOfStay "2"

  #######################   Rapid Test Scenarios
  @rapid_test
  Scenario: Cancel room API Rapid test Header "Test" with "standard"
    Given run shopping and preBooking for Booking
    And run booking with hold "false"
    And retrieve booking
    When set header "Test" value "standard"
    And cancel room booking
    Then the response code for "CANCEL_BOOKING" should be 204

  @rapid_test
  Scenario Outline: Cancel room API Rapid test Header "<header>" with "<value>"
    Given run shopping and preBooking for Booking
    And run booking with hold "false"
    And retrieve booking
    When set header "<header>" value "<value>"
    And cancel room booking
    Then the response code for "CANCEL_BOOKING" should be "<code>"
    And user should see "CANCEL_BOOKING" response with paris on the filtered "." node
      | type    | <type>    |
      | message | <message> |

    Examples: 
      | header | value                 | code | type                  | message                                                     |
      | Test   | post_stay_cancel      |  400 | cancel.post_checkout  | Room cannot be cancelled after checkout/completion of stay. |
      | Test   | internal_server_error |  500 | cancel.system_failure | An internal server error has occurred.                      |
      | Test   | service_unavailable   |  503 | service_unavailable   | This service is currently unavailable.                      |

  @rapid_test
  Scenario Outline: Cancel room API Rapid test Header "<header>" with "<value>"
    Given run shopping and preBooking for Booking
    And run booking with hold "false"
    And retrieve booking
    When set header "<header>" value "<value>"
    And cancel room booking
    Then the response code for "CANCEL_BOOKING" should be "<code>"
    And user should see "CANCEL_BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "CANCEL_BOOKING" response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |

    Examples: 
      | header | value   | code | error_type           | error_message                                                                                                                                               |
      | Test   | invalid |  400 | test.content_invalid | Content of the test header is invalid. Please use one of the following valid values: STANDARD, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE, POST_STAY_CANCEL |

    #################### Business Validations

   @business_test
   Scenario: Cancel room API for validation of Single room cancellation of multiple room
     Given set multiple values for "SHOPPING" queryParam "occupancy" with "2-3,4|3"
     And run shopping and preBooking for Booking
     And validate "BOOKING_LINK"  for "PRE_BOOKING"
     And run booking with hold "false"
     Then the response code for "BOOKING" should be 201
     And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
     And retrieve booking
     Then the response code for "RETRIEVE_BOOKING" should be 200
     And cancel room booking
     And the response code for "CANCEL_BOOKING" should be 204

  @business_test
  Scenario: Cancel room API for validation of cancelling the entire booking for multiple rooms
    Given set multiple values for "SHOPPING" queryParam "occupancy" with "2-3,4|3"
    And run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200
    And cancel booking
    And the response code for "CANCEL_BOOKING" should be 204

  @business_test
  Scenario: Cancel room API for validation of cancelling booking for single room
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200
    And cancel room booking
    And the response code for "CANCEL_BOOKING" should be 204




