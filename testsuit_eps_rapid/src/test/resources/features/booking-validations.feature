@Booking
Feature: Validations for Booking API.

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

    ###################### Business Test Scenarios

  @business_test
  Scenario: Booking API successful response for "HOLD" false
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "RETRIEVE_BOOKING_LINK"  for "BOOKING"

  @business_test
  Scenario Outline: Booking API for validation of "<field>"
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "<field>"  for "BOOKING"
    Examples:
    |field|
    |ITINERARY_ID|
    |LINKS       |
    |RETRIEVE_METHOD|
    |RETRIEVE_HREF  |
    |CANCEL_HREF    |

  @business_test
  Scenario: Booking API for validation for "cancel" node
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201
    And validate "BOOKING" response element "CANCEL_METHOD" matches values "DELETE"

#  @business_test
#  Scenario: Booking API for validation for "payments"
#    Given run shopping and preBooking for Booking
#    And validate "BOOKING_LINK"  for "PRE_BOOKING"
#    When set "BOOKING" field "payment_type" value "affiliate_collect"
#    And run booking with hold "false"
#    Then the response code for "BOOKING" should be 201

  @business_test
  Scenario: Booking API for validation for multiple room request
    Given set multiple values for "SHOPPING" queryParam "occupancy" with "2-2,3|3"
    And run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 201

 ################################# Data Validation Test Scenarios

  @data_test
  Scenario Outline: Booking API without <element>
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    When set "BOOKING" field "<element>" value "null"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 400
    And user should see "BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "BOOKING" response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |

    Examples:
    |element    | error_type           | error_message             |
    |given_name | given_name.required  | Given name is required.   |
    |family_name| family_name.required | Family name is required.  |
    |email      |email.required        | Email address is required.|
    |phone      |phone.required        |Phone is required.         |
  #  |smoking    |smoking.required|Smoking is required.|
    |rooms      |rooms.required        |Rooms is required.         |
    |country_code|address.country_code.invalid|Address country code is invalid.                           |
    |line_1     |address.line_1.invalid      |Address line 1 is invalid.                                 |
    |type       | payments.type.not_supported | Provided payment [] is not a supported payment type. |
    |contact_given_name| given_name.invalid|Given name is invalid. |
    |contact_family_name| family_name.invalid| Family name is invalid.|
    |contact_email|  email.invalid            | Customer email address is invalid.|
    |contact_phone| phone.required             | Phone is required. |

  @data_test
  Scenario Outline: Booking API with invalid <element>
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    When set "BOOKING" field "<element>" value "<value>"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 400
    And user should see "BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "BOOKING" response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |

    Examples:
    |element     | value |error_type   |error_message            |
    |phone       | 12#   |phone.invalid|Phone is invalid.        |
    |email       | jay   |email.invalid|Email address is invalid.|
    |country_code| test  |address.country_code.invalid|Address country code must be 2 characters.|
    |affiliate_confirmation_id|1234567890123456789012345688888888888888888888888888888888    |affiliate_confirmation_id.invalid_exceeds_char_limit|Affiliate confirmation ID exceeded the 28 character maximum.|
    |type        |test   |payments.type.not_supported             |Provided payment [test] is not a supported payment type. |
    |card_type   |test   |payments.credit_card.type.invalid|Credit card type is invalid. |
    |card_number |11111111111111111111111111111111111|payments.credit_card.number.invalid| Credit card number is invalid.|
    |security_code|AQQ                               | payments.credit_card.security_code.invalid|Security code is invalid.|
    |expiration_month|14|payments.credit_card.expiration_month.length_invalid|Credit card expiration month is not two digits long.|
    |expiration_year |1997|payments.credit_card.expired|Credit card is expired.|
    |contact_email|jay   |email.invalid| Customer email address is invalid.|
    |contact_phone|12$   |phone.invalid | Phone number is invalid.                       |


  @data_test
  Scenario: Booking API with invalid token
    Given run shopping and preBooking for Booking
    And validate "BOOKING_LINK"  for "PRE_BOOKING"
    When set invalid token for "BOOKING"
    And run booking with hold "false"
    Then the response code for "BOOKING" should be 400
    And user should see "BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "BOOKING" response with paris on the filtered "errors[0]" node
      | type    | link.invalid    |
      | message | Link is invalid. |