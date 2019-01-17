@Basic_Validations
Feature: EAN API Version, Authorization, Invalid resource validations
  Raise request(s) and validate mandatory data elements checks, Validate HTTP response code and parse JSON response

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

#######################  Basic Validation Scenarios

  @basic_test
  Scenario: Missing Authorization header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "Authorization" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 401
    And user should see json response with paris on the filtered "." node
      | type    | request_unauthenticated                                                                                                |
      | message | The authorization header is missing or invalid.  Ensure that your request follows the guidelines in our documentation. |

  @basic_test
  Scenario: Invalid Authorization header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "Authorization" value "abc123"
    And run shopping
    Then the response code for "SHOPPING" should be 401
    And user should see json response with paris on the filtered "." node
      | type    | request_unauthenticated                                                                                                |
      | message | The authorization header is missing or invalid.  Ensure that your request follows the guidelines in our documentation. |

  @basic_test
  Scenario: Invalid resource
    Given Basic web application is running
    And with shopping end point "properties/availability123"
    And run shopping
    Then the response code for "SHOPPING" should be 404
    And user should see json response with paris on the filtered "." node
      | type    | resource_not_found                    |
      | message | The resource requested was not found. |

  @basic_test
  Scenario: Invalid version
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And for version "1"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | version.required                                                            |
      | message | You have not specified a version, the supported versions are: [2, 2.1, 2.2] |
    And user should see json response with paris on the filtered "fields[0]" node
      | name  | version |
      | type  | path    |
      | value | missing |

  @basic_test
  Scenario: Missing version
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And for version "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400

#  @basic_test
#  Scenario: Invalid Accept header
#    Given Basic web application is running
#    And with shopping end point "properties/availability"
#    And set header "Accept" value "abc123"
#    And run shopping
#    Then the response code for "SHOPPING" should be 406
#    And user should see json response with paris on the filtered "." node
#      | type    | not_acceptable                                                            |
#      | message | The server can't provide an accceptable response matching the request, please check the nested errors for details. |
#    And user should see json response with paris on the filtered "errors[0]" node
#      | type  | accept.not_accepted  |
#      | message  | Accept header is required and must accept application/json.    |


  @basic_test
  Scenario: Missing Accept header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "Accept" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                                                                |
      | message | An invalid request was sent in, please check the nested errors for details. |

#  @basic_test
#    Scenario: Invalid Accept-Encoding header
#      Given Basic web application is running
#      And with shopping end point "properties/availability"
#      And set header "Accept-Encoding" value "abc123"
#      And run shopping
#      Then the response code for "SHOPPING" should be 400
#      And user should see json response with paris on the filtered "." node
#        | type    | invalid_input                                            |
#        | message | An invalid request was sent in, please check the nested errors for details. |

  @basic_test
  Scenario: Missing Accept-Encoding header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "Accept-Encoding" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                                                                |
      | message | An invalid request was sent in, please check the nested errors for details. |

  @basic_test
  Scenario: Invalid Customer IP header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "Customer IP" value "abc123"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                            |
      | message | An invalid request was sent in, please check the nested errors for details. |

  @basic_test
  Scenario: Missing Customer IP header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "Customer IP" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                                                                |
      | message | An invalid request was sent in, please check the nested errors for details. |

#  @basic_test
#  Scenario: Invalid User-Agent header
#    Given Basic web application is running
#    And with shopping end point "properties/availability"
#    And set header "User-Agent" value "abc123"
#    And run shopping
#    Then the response code for "SHOPPING" should be 400
#    And user should see json response with paris on the filtered "." node
#      | type    | invalid_input                                            |
#      | message | An invalid request was sent in, please check the nested errors for details. |
#

  @basic_test
  Scenario: Missing User-Agent header
    Given Basic web application is running
    And with shopping end point "properties/availability"
    And set header "User-Agent" value "null"
    And run shopping
    Then the response code for "SHOPPING" should be 400
    And user should see json response with paris on the filtered "." node
      | type    | invalid_input                                                                                                |
      | message | An invalid request was sent in, please check the nested errors for details. |




