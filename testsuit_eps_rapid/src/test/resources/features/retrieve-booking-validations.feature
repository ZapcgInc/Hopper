@Retrieve_Booking
Feature: Validations for Booking Retrieve API.

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


  #######################   Business Test Scenarios

  @business_test
  Scenario: Retrieve Booking API with successful response
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200

  @business_test
  Scenario Outline:<test_case> Retrieve Booking API for validation of <ELEMENT>
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200
    And validate element "<ELEMENT>"  for "RETRIEVE_BOOKING"
  Examples:
    |test_case|ELEMENT|
    |[RETR9]|ITINERARY_ID|
    |[RETR7]|CANCEL_HREF|
    |[RETR8]|ROOM_ID    |
    |[RETR10]|CHECK_IN_DATE|
    |[RETR11]|CHECK_OUT_DATE|
    |[RETR12]|NUM_ADULTS    |
    |[RETR14]|RATE_ID       |
    |[RETR21]|GIVEN_NAME    |
    |[RETR22]|FAMILY_NAME   |
    |[RETR24]|PHONE         |
    |[RETR16]|NIGHTLY_RATE  |
    |[RETR13]|SMOKING|
    |[RETR15]|REFUNDABLE|
    |[RETR25]|ADDRESS|
    |[RETR26]|COUNTRY_CODE|
    |[RETR27]|CREATION_DATE_TIME|
    |[RETR17]|CURRENCY_CODE     |
    |[RETR19]|FEES              |



  @business_test
  Scenario:[RETR18] Retrieve Booking API for validation of "STATUS"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200
    And validate element "STATUS" for "RETRIEVE_BOOKING" contains value among "pending|booked|cancelled"

  @business_test
  Scenario:[RETR23] Retrieve Booking API for validation with  Query Param "email" that was not connected with booking
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And set "RETRIEVE_BOOKING" field "email" value "abc@gmail.com"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200

  @business_test
  Scenario:[RETR32] Retrieve Booking API for validation of multiple rooms
    Given set multiple values for "SHOPPING" queryParam "occupancy" with "2-2,3|3"
    And run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200

  @business_test
  Scenario: Retrieve Booking API for all itineraries with successful response
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And retrieve booking to get from all itineraries
    Then the response code for "RETRIEVE_BOOKING_ALL_ITINERARIES" should be 200

  @business_test
  Scenario:[RETR4] Retrieve Booking API for invalid Query Param "email"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    When set "RETRIEVE_BOOKING" field "email" value "abc"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 200


    ####################### Data Validations
  @data_test
  Scenario:[RETR28] Retrieve Booking API with invalid token
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"
    And set invalid token for "RETRIEVE_BOOKING"
    And retrieve booking
    Then the response code for "RETRIEVE_BOOKING" should be 404
    And user should see "RETRIEVE_BOOKING" response with paris on the filtered "." node
      | type    | resource_not_found                             |
      | message | Itinerary was not found with provided request. |

  @data_test
  Scenario: [RETR1] Retrieve Booking API for all itineraries for missing Query Param "affiliate_reference_id"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    When set "RETRIEVE_BOOKING_ALL_ITINERARIES" field "affiliate_reference_id" value "null"
    And retrieve booking to get from all itineraries
    Then the response code for "RETRIEVE_BOOKING_ALL_ITINERARIES" should be 400
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "." node
      | type    | invalid_input    |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "errors[0]" node
      | type    | affiliate_reference_id.required    |
      | message | Affiliate reference id is required. |

  @data_test
  Scenario:[RETR3] Retrieve Booking API for all itineraries for missing Query Param "email"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    When set "RETRIEVE_BOOKING_ALL_ITINERARIES" field "email" value "null"
    And retrieve booking to get from all itineraries
    Then the response code for "RETRIEVE_BOOKING_ALL_ITINERARIES" should be 404
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "." node
      | type    | resource_not_found    |
      | message | Itinerary was not found with provided request. |


  @data_test
  Scenario:[RETR4] Retrieve Booking API for all itineraries for invalid Query Param "email"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    When set "RETRIEVE_BOOKING_ALL_ITINERARIES" field "email" value "abc"
    And retrieve booking to get from all itineraries
    Then the response code for "RETRIEVE_BOOKING_ALL_ITINERARIES" should be 400
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "." node
      | type    | invalid_input    |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "errors[0]" node
      | type    | email.invalid    |
      | message | Customer email address is invalid. |


  @data_test
  Scenario: [RETR2] Retrieve Booking API for all itineraries for invalid Query Param "affiliate_reference_id"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    When set "RETRIEVE_BOOKING_ALL_ITINERARIES" field "affiliate_reference_id" value "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111"
    And retrieve booking to get from all itineraries
    Then the response code for "RETRIEVE_BOOKING_ALL_ITINERARIES" should be 400
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "." node
      | type    | invalid_input    |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "errors[0]" node
      | type    | affiliate_reference_id.invalid_exceeds_char_limit    |
      | message | Affiliate Reference Id exceeded the 28 character maximum. |

  @data_test
  Scenario:[RETR5] Retrieve Booking API for all itineraries for different "email" as of the booking request
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    When set "RETRIEVE_BOOKING_ALL_ITINERARIES" field "email" value "smith@gmail.com"
    And retrieve booking to get from all itineraries
    Then the response code for "RETRIEVE_BOOKING_ALL_ITINERARIES" should be 404
    And user should see "RETRIEVE_BOOKING_ALL_ITINERARIES" response with paris on the filtered "." node
      | type    | resource_not_found    |
      | message | Itinerary was not found with provided request. |









