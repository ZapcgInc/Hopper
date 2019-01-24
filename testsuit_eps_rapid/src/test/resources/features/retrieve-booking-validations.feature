@Retrieve_Booking
Feature: Validations for Booking Retrieve API.

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
  Scenario: Retrieve Booking API Rapid test Header "Test" with "standard"
    Given run shopping and preBooking for Booking
    And run booking with hold "false"
    When set header "Test" value "standard"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200

  @rapid_test
  Scenario Outline: Retrieve Booking API Rapid test Header "<header>" with "<value>"
    Given run shopping and preBooking for Booking
    And run booking with hold "false"
    When set header "<header>" value "<value>"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be "<code>"
    And user should see "RETRIEVE_BOOKING" response with paris on the filtered "." node
      | type    | <type>    |
      | message | <message> |

    Examples: 
      | header | value                 | code | type                    | message                                |
      | Test   | internal_server_error |  500 | retrieve.system_failure | An internal server error has occurred. |
      | Test   | service_unavailable   |  503 | service_unavailable     | This service is currently unavailable. |

  @rapid_test
  Scenario Outline: Retrieve Booking API Rapid test Header "<header>" with "<value>"
    Given run shopping and preBooking for Booking
    And run booking with hold "false"
    When set header "<header>" value "<value>"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be "<code>"
    And user should see "RETRIEVE_BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "RETRIEVE_BOOKING" response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |

    Examples: 
      | header | value   | code | error_type           | error_message                                                                                                                             |
      | Test   | invalid |  400 | test.content_invalid | Content of the test header is invalid. Please use one of the following valid values: STANDARD, INTERNAL_SERVER_ERROR, SERVICE_UNAVAILABLE |

#  @business_test
#  Scenario: Retrieve Booking API with token successful response
#    Given run shopping and preBooking for Booking
#    And validate "BOOKING_LINK"  for "PRE_BOOKING"
#    And run booking with hold "false"
#    Then the response code for "BOOKING" should be 201
#    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
#    And retrieve booking
#    Then the response code for "RETRIEVE_BOOKING" should be 200

#  @business_test
#  Scenario Outline: Retrieve Booking API for <ELEMENT>
#    Given run shopping and preBooking for Booking
#    And validate "BOOKING_LINK"  for "PRE_BOOKING"
#    And run booking with hold "false"
#    Then the response code for "BOOKING" should be 201
#    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
#    And retrieve booking
#    Then the response code for "RETRIEVE_BOOKING" should be 200
#    And validate "<ELEMENT>"  for "RETRIEVE_BOOKING"
#    Examples:
#    |ELEMENT|
#    |ITENARY_ID|
##    |CANCEL_HREF|
##    |ROOM_ID    |
##    |CHECK_IN_DATE|
##    |CHECK_OUT_DATE|
##    |NUM_ADULTS    |
##    |RATE_ID       |
##    |GIVEN_NAME    |
##    |FAMILY_NAME   |
##    |PHONE         |





