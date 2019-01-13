@preBook
Feature: Validations for PreBooking API.

  Background: 
    Given setup for partner "EPS"
    And API at "https://test.ean.com"
    And for version "2.1"
    And with request headers
      | Accept          | application/json |
      | Accept-Encoding | gzip             |
      | Customer-Ip     | 127.0.0.1        |
      | User-Agent      | Hopper/1.0       |
    And Generate authHeaderKey with
      | apikey | mq7ijoev87orvkq4mqo8dr2tf |
      | secret | 587btntj2ihg5             |
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
    And set checkin "90" from today with lengthOfStay "5"
    And with shopping end point "properties/availability"

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
      | Test   | unknown_internal_error |  500 | unknown_internal_error | An internal server error has occurred. |
      | Test   | service_unavailable    |  503 | service_unavailable    | This service is currently unavailable. |

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
      | Test   | matched       |  200 | matched       |
      | Test   | price_changed |  409 | price_changed |
      | Test   | sold_out      |  410 | sold_out      |

  ################# Business Validations ################################
  @busiess_test
  Scenario: PreBooking API successful response
    Given run shopping
    When run preBooking
    Then the response code for "PRE_BOOKING" should be 200
