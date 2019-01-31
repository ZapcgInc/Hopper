@PreBook
Feature: Validations for PreBooking API.

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
    And set checkin "90" from today with lengthOfStay "5" by default

  #######################   Rapid Test Scenarios
  @rapid_test
  Scenario Outline:<test_case> PreBooking API Rapid test Header "<header>" with "<value>"
    Given run shopping
    When set header "<header>" value "<value>"
    And run preBooking
    Then the response code for "PRE_BOOKING" should be "<code>"
    And user should see "PRE_BOOKING" response with paris on the filtered "." node
      | type    | <type>    |
      | message | <message> |

    Examples: 
     |test_case | header | value                  | code | type                   | message                                |
     |[PCHK3] | Test   | unknown_internal_error |  500 | unknown_internal_error | An internal server error has occurred. |
     |[PCHK4] | Test   | service_unavailable    |  503 | service_unavailable    | This service is currently unavailable. |

  @rapid_test
  Scenario Outline:<test_case> PreBooking API Rapid test Header "<header>" with "<value>"
    Given run shopping
    When set header "<header>" value "<value>"
    And run preBooking
    Then the response code for "PRE_BOOKING" should be "<code>"
    And user should see "PRE_BOOKING" response with paris on the filtered "." node
      | status | <status> |

    Examples: 
      |test_case| header | value         | code | status        |
      |[PCHK2]| Test   | matched       |  200 | matched       |
      |[PCHK5]| Test   | price_changed |  409 | price_changed |
      |[PCHK6]| Test   | sold_out      |  410 | sold_out      |

  @rapid_test
  Scenario Outline:<test_case> PreBooking API Rapid test Header "<header>" with "<value>"
    Given run shopping
    When set header "<header>" value "<value>"
    And run preBooking
    Then the response code for "PRE_BOOKING" should be "<code>"
    And user should see "PRE_BOOKING" response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see "PRE_BOOKING" response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |

    Examples: 
      |test_case| header | value   | code | error_type           | error_message                                                                                                                                                      |
      |[PCHK16]| Test   | invalid |  400 | test.content_invalid | Content of the test header is invalid. Please use one of the following valid values: matched, price_changed, service_unavailable, sold_out, unknown_internal_error |

  ################# Business Validations ################################
  @busiess_test
  Scenario: [PCHK1] PreBooking API successful response
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200

  @business_test
  Scenario:[PCHK9] PreBooking API validation for night room prices available for all LOS
    Given set checkin "5" from today with lengthOfStay "3"
    And run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "PRE_BOOKING" response element "NIGHTLY_PRICE_COUNT" matches values "3"

  @business_test
  Scenario:[PCHK10] PreBooking API validation for Total Price is matching
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate that "TOTAL_PRICE" for "PRE_BOOKING" is the sum of individual room stay values with taxes and fees

  @business_test
  Scenario: [PCHK11] PreBooking API validation with multiple Query Param "occupancy" and single Query Param "property_id"
    Given set "SHOPPING" queryParam "occupancy" value "null"
    And set multiple values for "SHOPPING" queryParam "occupancy" with "2-9,4|3"
    And run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "PRE_BOOKING" response element "OCCUPANCY" matches values "2-9,4|3"

  @business_test
  Scenario:[PCHK12] PreBooking API validation for "STAY_PRICE_TYPE"
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "PRE_BOOKING" response element "STAY_PRICE_TYPE" matches values "base_rate|tax_and_service_fee|extra_person_fee|property_fee|sales_tax|adjustment"

  @business_test
  Scenario Outline:<test_case> PreBooking API validation for "href" in "<field>"
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "<field>"  for "PRE_BOOKING"

    Examples: 
      |test_case| field         |
      |[PCHK13]| BOOKING_LINK  |
      |[PCHK14]| SHOPPING_LINK |

  @business_test
  Scenario:[PCHK15] PreBooking API validation for "STATUS"
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be "200"
    And the element "STATUS" for "PRE_BOOKING" should have value belongs to "matched|price_change|sold_out"
