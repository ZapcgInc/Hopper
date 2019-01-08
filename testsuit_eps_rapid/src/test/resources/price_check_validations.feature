@preBook
Feature: Validations for PreBooking API.

  Background:
    Given setup
    And for partner "EPS"
    And API at "https://test.ean.com"
    And for version "2.1"
    And with request headers
      | Accept          | application/json |
      | Accept-Encoding | gzip             |
      | Customer-Ip     | 127.0.0.1        |
      | User-Agent      | Hopper/1.0       |
    And with authHeaderKey
      | apikey | mq7ijoev87orvkq4mqo8dr2tf |
      | secret | 587btntj2ihg5             |
    And with shopping query parameters
      | checkin           | 2019-02-15        |
      | checkout          | 2019-02-17        |
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
    And with shopping end point "properties/availability"

  Scenario: PreBooking API successful response
    Given shopping complete
    When run preBooking
    Then verify success response 200