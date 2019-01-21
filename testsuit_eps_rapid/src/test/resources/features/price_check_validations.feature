@PreBook
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

  #######################   Rapid Test Scenarios
  @rapid_test
  Scenario Outline: PreBooking API Rapid test Header "<header>" with "<value>"
    Given run shopping
    When set header "<header>" value "<value>"
    And run preBooking
    Then the response code for "PRE_BOOKING" should be "<code>"
    And user should see "PRE_BOOKING" response with paris on the filtered "." node
      | type    | <type>    |
      | message | <message> |

    Examples:
      | header | value                  | code | type                   | message                                |
      | Test   | unknown_internal_error | 500  | unknown_internal_error | An internal server error has occurred. |
      | Test   | service_unavailable    | 503  | service_unavailable    | This service is currently unavailable. |

  @rapid_test
  Scenario Outline: PreBooking API Rapid test Header "<header>" with "<value>"
    Given run shopping
    When set header "<header>" value "<value>"
    And run preBooking
    Then the response code for "PRE_BOOKING" should be "<code>"
    And user should see "PRE_BOOKING" response with paris on the filtered "." node
      | status | <status> |

    Examples:
      | header | value         | code | status        |
      | Test   | matched       | 200  | matched       |
      | Test   | price_changed | 409  | price_changed |
      | Test   | sold_out      | 410  | sold_out      |

#    @rapid_test
#      Scenario: PreBooking API Rapid test Header "Test" with "standard"
#      Given run shopping
#      When set header "Test" value "standard"
#      And run preBooking
#      Then the response code for "PRE_BOOKING" should be 200

  ################# Business Validations ################################
  @busiess_test
  Scenario: PreBooking API successful response
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200


  @business_test
  Scenario: PreBooking API validation for night room prices available for all LOS
    Given set checkin "5" from today with lengthOfStay "3"
    And run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "PRE_BOOKING" response element "NIGHTLY_PRICE_COUNT" matches values "3"

  Scenario: PreBooking API validation for Total Price is matching
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate that "TOTAL_PRICE" for "PRE_BOOKING" is the sum of individual room stay values with taxes and fees

  Scenario: PreBooking API validation with multiple Query Param "occupancy" and single Query Param "property_id"
    Given set "SHOPPING" queryParam "occupancy" value "null"
    And set multiple values for "SHOPPING" queryParam "occupancy" with "2-9,4|3"
    And run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "PRE_BOOKING" response element "OCCUPANCY" matches values "2-9,4|3"

  Scenario: PreBooking API validation for "STAY_PRICE_TYPE"
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "PRE_BOOKING" response element "STAY_PRICE_TYPE" matches values "base_rate|tax_and_service_fee|extra_person_fee|property_fee|sales_tax|adjustment"


  Scenario Outline: PreBooking API validation for "href" in "<field>"
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
    And validate "<field>"  for "PRE_BOOKING"
    Examples:
      | field         |
      | BOOKING_LINK  |
      | SHOPPING_LINK |

  Scenario: PreBooking API validation for "status"
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be "200"
    And the element "STATUS" for "PRE_BOOKING" should have value belongs to "matched|price_change|sold_out"