@availability
Feature: Validations for Availability API

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
    And set checkin "90" from today with lengthOfStay "5"

  #    And set multiple values for "SHOPPING" queryParam "occupancy" with "2-9,0|2"
  #    And set multiple values for "SHOPPING" queryParam "property_id" with "8521|3317|19762|9100"
  #######################   Rapid Test Scenarios
  @rapid_test
  Scenario Outline: Availability API  Rapid test Header "<header>" with "<value>"
    Given Basic web application is running
    When set header "<header>" value "<value>"
    And run shopping
    Then the response code for "SHOPPING" should be "<code>"
    And user should see json response with paris on the filtered "." node
      | type    | <type>    |
      | message | <message> |

    Examples: 
      | header | value                  | code | type                   | message                                                            |
      | Test   | no_availability        |  404 | no_availability        | No availability was found for the properties requested.            |
      | Test   | unknown_internal_error |  500 | unknown_internal_error | An internal server error has occurred.                             |
      | Test   | service_unavailable    |  503 | service_unavailable    | This service is currently unavailable.                             |
      | Test   | forbidden              |  403 | request_forbidden      | Your request could not be authorized. Ensure that you have access. |

  @rapid_test
  Scenario: Availability API Rapid test with invalid value like "Test=INVALID"
    Given Basic web application is running
    When set header "Test" value "INVALID"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | test.content_invalid                                                                                                                                                   |
      | message | Content of the test header is invalid. Please use one of the following valid values: forbidden, no_availability, service_unavailable, standard, unknown_internal_error |

  #######################   Data Validation Test Scenarios for headers
  #Scenario: Missing User-Agent in header is not returning error
  #Scenario: Missing Accept-Encoding in header is not returning error
  @data_test
  Scenario: Availability API Missing Customer-Ip in header
    Given Basic web application is running
    When set header "Customer-Ip" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | customer_ip.required                                           |
      | message | Customer-Ip header is required and must be a valid IP Address. |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name | Customer-Ip |
      | type | header      |

  #######################   Data Validation Test Scenarios for Query parameters
  @data_test
  Scenario Outline: Availability API missing Query Param "<query_param>"
    Given Basic web application is running
    When set "SHOPPING" queryParam "<query_param>" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name | <query_param> |
      | type | querystring   |

    Examples: 
      | query_param       | error_type                 | error_message                                                                                                              |
      | property_id       | property_id.required       | Property Id is required.                                                                                                   |
      | checkin           | checkin.required           | Checkin is required.                                                                                                       |
      | checkout          | checkout.required          | Checkout is required.                                                                                                      |
      | currency          | currency.required          | Currency code is required.                                                                                                 |
      | language          | language.required          | Language code is required.                                                                                                 |
      | country_code      | country_code.required      | Country code is required.                                                                                                  |
      | occupancy         | occupancy.required         | Occupancy is required.                                                                                                     |
      | sales_channel     | sales_channel.required     | Sales Channel is required.  Accepted sales_channel values are: [website, agent_tool, mobile_app, mobile_web, cache, meta]. |
      | sales_environment | sales_environment.required | Sales Environment is required.  Accepted sales_environment values are: [hotel_only, hotel_package, loyalty].               |
      | sort_type         | sort_type.required         | Sort Type is required.  Accepted sort_type values are: [preferred].                                                        |

  @data_test
  Scenario: Availabilty API with more than 250 values for Query Param "property_id"
    Given Basic web application is running
    When set "SHOPPING" queryParam "property_id" value "null"
    And set multiple values for "SHOPPING" queryParam "property_id" with "1|2|3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|76|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|111|112|113|114|115|116|117|118|119|120|121|122|123|124|125|126|127|128|129|130|131|132|133|134|135|136|137|138|139|140|141|142|143|144|145|146|147|148|149|150|151|152|153|154|155|156|157|158|159|160|161|162|163|164|165|166|167|168|169|170|171|172|173|174|175|176|177|178|179|180|181|182|183|184|185|186|187|188|189|190|191|192|193|194|195|196|197|198|199|200|201|202|203|204|205|206|207|208|209|210|211|212|213|214|215|216|217|218|219|220|221|222|223|224|225|226|227|228|229|230|231|232|233|234|235|236|237|238|239|240|241|242|243|244|245|246|247|248|249|250|251"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | property_id.above_maximum                                           |
      | message | The number of property_id's passed in must not be greater than 250. |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name  | property_id |
      | type  | querystring |
      | value |         251 |

  @data_test
  Scenario: Availabilty API with more than 8 values for Query Param "occupancy"
    Given Basic web application is running
    When set "SHOPPING" queryParam "occupancy" value "null"
    And set multiple values for "SHOPPING" queryParam "occupancy" with "1|2|3|4|5|6|7|8|8|"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | number_of_occupancies.invalid_above_maximum |
      | message | Number of occupancies must be less than 9.  |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name  | occupancy   |
      | type  | querystring |
      | value |           9 |

  @data_test
  Scenario: Availability API with total length of stay <days>
    Given Basic web application is running
    When set checkin "3" from today with lengthOfStay "29"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | checkout.invalid_length_of_stay_too_long                                           |
      | message | Invalid date range. Checkin and checkout dates cannot differ by more than 28 days. |

  @data_test
  Scenario: Availability API with invalid date format for Query Param "checkin" and "checkout"
    Given Basic web application is running
    When with request DateFormat "MM dd,YYYY"
    When set checkin "10" from today with lengthOfStay "5"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | checkin.invalid_date_format                                                                                                   |
      | message | Invalid checkin format. It must be formatted in ISO 8601 (YYYY-mm-dd) http://www.iso.org/iso/catalogue_detail?csnumber=40874. |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name | checkin     |
      | type | querystring |
    And user should see json response with paris on the filtered "errors[1]" node
      | type    | checkout.invalid_date_format                                                                                              |
      | message | Invalid date format. It must be formatted in ISO 8601 (YYYY-mm-dd)http://www.iso.org/iso/catalogue_detail?csnumber=40874. |
    And user should see json response with paris on the filtered "errors[1].fields[0]" node
      | name | checkout    |
      | type | querystring |

  @data_test
  Scenario Outline: Availability API with "<scenario>" for query Param "checkin"
    Given Basic web application is running
    When set checkin "<days>" from today with lengthOfStay "5"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name | checkin     |
      | type | querystring |

    Examples: 
      | scenario            | days | error_type                       | error_message                  |
      | past-dates          |   -2 | checkin.invalid_date_in_the_past | Checkin cannot be in the past. |
      | Too-Advance-Checkin |  510 | checkin.invalid_date_too_far_out | Checkin too far in the future. |

  @data_test
  Scenario: Availability API with Query Param "checkin" date greater than Query Param "checkout" date
    Given Basic web application is running
    When set "SHOPPING" queryParam "checkout" value "2018-12-15"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | checkout.invalid_checkout_before_checkin |
      | message | Checkout must be after checkin.          |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name | checkin     |
      | type | querystring |
    And user should see json response with paris on the filtered "errors[0].fields[1]" node
      | name | checkout    |
      | type | querystring |

  @data_test
  Scenario Outline: Availability API with "<scenario>" for Query Param "occupancy"
    Given Basic web application is running
    When set "SHOPPING" queryParam "occupancy" value "<numOfAdult>-<ageOfChildren>"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name  | occupancy   |
      | type  | querystring |
      | value | <value>     |

    Examples: 
      | scenario                      | numOfAdult | ageOfChildren | error_type                               | error_message                            | value |
      # | more than 8 people in a room  |          9 |             0 | number_of_adults.invalid_above_maximum   | Number of adults must be less than 9.    |     9 |
      | no adult in a room            |          0 | 2,3,4,5,6,7,8 | number_of_adults.invalid_below_minimum   | Number of adults must be greater than 0. |     0 |
      | chid age with gerater than 18 |          2 |            20 | child_age.invalid_outside_accepted_range | Child age must be between 0 and 17.      |    20 |

  @data_test
  Scenario Outline: Availability API with invalid  data format for Query Param "<query_param>"
    Given Basic web application is running
    When set "SHOPPING" queryParam "<query_param>" value "<value>"
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                               |
      | message | An invalid request was sent in, please check the nested errors for details. |
    And user should see json response with paris on the filtered "errors[0]" node
      | type    | <error_type>    |
      | message | <error_message> |
    And user should see json response with paris on the filtered "errors[0].fields[0]" node
      | name | <query_param> |
      | type | querystring   |

    Examples: 
      | query_param       | value | error_type                | error_message                                                                                                                                                                                                                                                                                      |
      | currency          | RRR   | currency.not_supported    | Currency is not supported. Supported currencies are: [AED, ARS, AUD, BRL, CAD, CHF, CNY, DKK, EGP, EUR, GBP, HKD, IDR, ILS, INR, JPY, KRW, MXN, MYR, NOK, NZD, PHP, PLN, RUB, SAR, SEK, SGD, TRY, TWD, USD, VND, ZAR]                                                                              |
      | language          | as-US | language.not_supported    | Language is not supported. Supported languages are: [ar-SA, cs-CZ, da-DK, de-DE, el-GR, en-US, es-ES, es-MX, fi-FI, fr-CA, fr-FR, hr-HR, hu-HU, id-ID, is-IS, it-IT, ja-JP, ko-KR, lt-LT, ms-MY, nb-NO, nl-NL, pl-PL, pt-BR, pt-PT, ru-RU, sk-SK, sv-SE, th-TH, tr-TR, uk-UA, vi-VN, zh-CN, zh-TW] |
      | country_code      | RRR   | country_code.invalid      | Country code is invalid.                                                                                                                                                                                                                                                                           |
      | rate_option       | test  | rate_option.invalid       | Rate Option is invalid.  Accepted rate_option values are: [net_rates, closed_user_group].                                                                                                                                                                                                          |
      | sales_channel     | test  | sales_channel.invalid     | Sales Channel is invalid.  Accepted sales_channel values are: [website, agent_tool, mobile_app, mobile_web, cache, meta].                                                                                                                                                                          |
      | sales_environment | test  | sales_environment.invalid | Sales Environment is invalid.  Accepted sales_environment values are: [hotel_only, hotel_package, loyalty].                                                                                                                                                                                        |
      | sort_type         | test  | sort_type.invalid         | Sort Type is invalid.  Accepted sort_type values are: [preferred].                                                                                                                                                                                                                                 |
      | filter            | test  | filter.invalid            | Filter is invalid.  Accepted filter values are: [refundable, expedia_collect, property_collect].                                                                                                                                                                                                   |

  @busiess_test
  Scenario: Availability API successful response
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be 200

  @business_test
  Scenario: Availability API response validation matching occupancies with single Query Param "occupancy" and "property_id"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "SHOPPING" response element "OCCUPANCY" matches values "2-9,4"

  @business_test
  Scenario: Availability API response validation matching occupancies with multiple Query Param "occupancy" and single Query Param "property_id"
    Given Basic web application is running
    When set "SHOPPING" queryParam "occupancy" value "null"
    And set multiple values for "SHOPPING" queryParam "occupancy" with "2-9,4|3"
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "SHOPPING" response element "OCCUPANCY" matches values "2-9,4|3"

  @business_test
  Scenario: Availability API response validation matching occupancies with single Query Param "occupancy" and multiple Query Param "property_id"
    Given Basic web application is running
    When set "SHOPPING" queryParam "property_id" value "null"
    And set multiple values for "SHOPPING" queryParam "property_id" with "8521|3317|19762|9100"
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "SHOPPING" response element "OCCUPANCY" matches values "2-9,4"

  @business_test
  Scenario: Availability API response validation matching occupancies with multiple Query Param "occupancy" and multiple Query Param "property_id"
    Given Basic web application is running
    When set "SHOPPING" queryParam "property_id" value "null"
    And set "SHOPPING" queryParam "occupancy" value "null"
    And set multiple values for "SHOPPING" queryParam "occupancy" with "2-9,4|3"
    And set multiple values for "SHOPPING" queryParam "property_id" with "8521|3317|19762|9100"
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "SHOPPING" response element "OCCUPANCY" matches values "2-9,4|3"

  @business_test
  Scenario: Availability API response validation with child age 0
    Given Basic web application is running
    And set "SHOPPING" queryParam "occupancy" value "2-0"
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "SHOPPING" response element "OCCUPANCY" matches values "2-0"

  @business_test
  Scenario: Availability API response validation for missing Query Param "include"
    Given Basic web application is running
    When set "SHOPPING" queryParam "include" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "ROOM_COUNT" for "SHOPPING" should be 1
    And validate "ROOM_RATES_COUNT" for "SHOPPING" should be 1
    And validate "PROPERTY_LINK_COUNT" for "SHOPPING" should be 1

  @business_test
  Scenario: Availability API response validation with "all_rates" for Query Param "include"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be 200
    And validate "PROPERTY_LINK_COUNT" for "SHOPPING" should be 0

  @business_test
  Scenario: Availability API response validation for "available_rooms"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the element "AVAILABLE_ROOMS" count per "rooms" for "SHOPPING" should be between "0" and "2147483647"

  @business_test
  Scenario: Availability API response validation for "merchant_of_records"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the element "MERCHANT_OF_RECORD" for "SHOPPING" should have value belongs to "expedia|property"

  @business_test
  Scenario Outline: Availability API response validation for "href" in "<field>"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And validate "<field>"  for "SHOPPING"

    Examples: 
      | field                |
      | PRICE_CHECK_LINK     |
      | PAYMENT_OPTIONS_LINK |

  @business_test
  Scenario: Availability API response validation for "cancel_policies" if "refundable_rates" sets to "true"
    Given Basic web application is running
    And set checkin "05" from today with lengthOfStay "2"
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the element "CANCEL_PENALTIES" start and end date (under cancel_penalties) for "SHOPPING" are within check in and check out dates

  @business_test
  Scenario: Availability API response validation for "amenities"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the element "AMENITIES" for "SHOPPING" either have both amenityId and description or have no amenity ID and description (mutually inclusive)

  @business_test
  Scenario: Availability API response validation for "fenced_deal"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And validate "SHOPPING" response element "FENCED_DEAL" matches values "false"

  @business_test
  Scenario: Availability API response validation for "deposit_policies" if true
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And validate "DEPOSIT_POLICIES_LINK"  for "SHOPPING"

  @business_test
  Scenario: Availability API response validation for "STAY_PRICE_TYPE"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the element "STAY_PRICE_TYPE" for "SHOPPING" should have value belongs to "base_rate|tax_and_service_fee|extra_person_fee|property_fee|sales_tax|adjustment"

  @business_test
  Scenario: Availability API response validation for "NIGHTLY_PRICE"
    Given Basic web application is running
    And set checkin "5" from today with lengthOfStay "2"
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the element "NIGHTLY_PRICE_TYPE" for "SHOPPING" should have value belongs to "base_rate|tax_and_service_fee|extra_person_fee|property_fee|sales_tax|adjustment"
    And validate "SHOPPING" response element "NIGHTLY_PRICE_COUNT" matches values "2"

  @business_test
  Scenario: Availability API response validation for "total_price"
    Given Basic web application is running
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And validate that "TOTAL_PRICE" for "SHOPPING" is the sum of individual room stay values with taxes and fees

  @business_test
  Scenario: Availability API response validation for "currency"
    Given Basic web application is running
    And set "SHOPPING" queryParam "currency" value "INR"
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And validate "SHOPPING" response element "CURRENCY_CODE" matches values "INR"

  @business_test
  Scenario: Availability API response validation for "BILLABLE_CURRENCY"
    Given Basic web application is running
    And set "SHOPPING" queryParam "currency" value "INR"
    And run shopping
    Then the response code for "SHOPPING" should be "200"
    And the "BILLABLE_CURRENCY" for "SHOPPING" should be equal to "request_currency"
