#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: Availability part of shopping API validations.


Background:
	Given simple init
	And web application endpoint url is "https://test.ean.com"
	And version is "2.1"
	And headers are
	  | Accept                          | application/json           |
	  | Accept-Encoding                 | gzip                       |
	  | Customer-Ip                     | 127.0.0.1         				 |
	  | User-Agent                      | Hopper/1.0                 |
	And Generate authHeaderKey with
	  |apikey|mq7ijoev87orvkq4mqo8dr2tf|
	  |secret|587btntj2ihg5|
	And queryParams are
		| currency                         | USD         								|
		| language                         | en-US         							|
		| country_code                     | US               					|
		| property_id											 | 20321											|
		| occupancy                        | 2-9,4           						|
		| sales_channel                    | website           					|
		| sales_environment                | hotel_only                 |
		| sort_type                        | preferred         					|
    | include           							 | all_rates                  |
    | rate_option                      | closed_user_group          |
 	And set dateFormat "yyy-MM-dd"
	And set checkin "10" from today with lengthOfStay "5"
	And set mulitple values for queryParam "occupancy" with "2-9,1|2"
	And set mulitple values for queryParam "property_id" with "8521|3317|19762|9100"
	And user sets GET request to "properties/availability"



#######################   Rapid Test Scenarios
@rapid_test
Scenario Outline: Availability Rapid test Header "<header>" with "<value>"
	Given Basic web application is running
	When user sets header "<header>" value "<value>"
	And performs GET request
	Then the response code should be "<code>"
	And user should see json response with paris on the filterd "." node
	  |type  				| <type> 		|
	  |message   		| <message>	|	
  Examples: 
  | header  | value 									| code  | type										| message																																							|
  | Test 		| no_availability 				| 404   | no_availability 				| No availability was found for the properties requested. 														| 
  | Test 		| unknown_internal_error 	| 500   | unknown_internal_error 	| An internal server error has occurred. 																							| 
  | Test 		| service_unavailable 		| 503   | service_unavailable 		| This service is currently unavailable. 																							| 
  | Test 		| forbidden							 	| 403   | request_forbidden 			| Your request could not be authorized. Ensure that you have access. 									| 


@rapid_test
Scenario: Rapid test with invalid value like "Test=INVALID"  
	Given Basic web application is running
	When user sets header "Test" value "INVALID"
	And performs GET request
	Then the response code should be 400
	And user should see json response with paris on the filterd "." node
	  |type   		| invalid_input   																														 |
	  |message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
	    |type   	| test.content_invalid  		|
	    |message  | Content of the test header is invalid. Please use one of the following valid values: forbidden, no_availability, service_unavailable, standard, unknown_internal_error 	|


#######################   Data Validation Test Scenarios for headers

#Scenario: Missing User-Agent in header is not returning error
#Scenario: Missing Accept-Encoding in header is not returning error
	
@data_test
Scenario: Missing Customer-Ip in header
	Given Basic web application is running
	When user sets header "Customer-Ip" value "null"
	And performs GET request
	Then the response code should be 400
	And user should see json response with paris on the filterd "." node
	  |type   		| invalid_input   																														 |
	  |message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
	    |type   	| customer_ip.required  		|
	    |message  | Customer-Ip header is required and must be a valid IP Address. 	|
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
	    |name   	| Customer-Ip 		|
	    |type  		| header 					|

#######################   Data Validation Test Scenarios for query parameters

@data_test	
Scenario Outline: Availability API missing Query Param "<query_param>"
	Given Basic web application is running
	When user sets queryParam "<query_param>" value "null" 
	And performs GET request
	Then the response code should be 400
	And user should see json response with paris on the filterd "." node
	  |type   		| invalid_input   																														 |
	  |message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
    |type   	| <error_type>  		|
    |message  | <error_message> 	|
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
    |name   	| <query_param> 		|
    |type  		| querystring 			|
  Examples: 
  | query_param  				| error_type  									| error_message								| 
  | property_id 				| property_id.required   				| Property Id is required. 		|
  | checkin 						| checkin.required   						| Checkin is required. 				| 
  | checkout 						| checkout.required   					| Checkout is required. 			|
  | currency 						| currency.required   					| Currency code is required. 	|
  | language 						| language.required   					| Language code is required. 	| 
  | country_code 				| country_code.required   			| Country code is required. 	|
 	| occupancy 					| occupancy.required   					| Occupancy is required. 			| 
 	| sales_channel 			| sales_channel.required   			| Sales Channel is required.  Accepted sales_channel values are: [website, agent_tool, mobile_app, mobile_web, cache, meta]. 	|
 	| sales_environment 	| sales_environment.required   	| Sales Environment is required.  Accepted sales_environment values are: [hotel_only, hotel_package, loyalty]. 	|
 	| sort_type 					| sort_type.required   					| Sort Type is required.  Accepted sort_type values are: [preferred]. 			| 
#########
Scenario: Availability API with invalid date format for checkin/checkout
	Given Basic web application is running
	When set dateFormat "MM dd,YYYY"
	When set checkin "10" from today with lengthOfStay "5"
	And performs GET request
	Then the response code should be 400
	And user should see json response with paris on the filterd "." node
		|type   		| invalid_input   																														 |
		|message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
		|type   	| checkin.invalid_date_format |
		|message    | Invalid checkin format. It must be formatted in ISO 8601 (YYYY-mm-dd) http://www.iso.org/iso/catalogue_detail?csnumber=40874.	|
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
		|name   	| checkin		|
		|type  		| querystring 			|
	And user should see json response with paris on the filterd "errors[1]" node
		|type   	| checkout.invalid_date_format   																														 |
		|message    | Invalid date format. It must be formatted in ISO 8601 (YYYY-mm-dd)http://www.iso.org/iso/catalogue_detail?csnumber=40874.	|
	And user should see json response with paris on the filterd "errors[1].fields[0]" node
		|name   	| checkout		|
		|type  		| querystring 			|


	Scenario Outline: Availability API with "<scenario>" for "<query_param>"
	Given Basic web application is running
	When set checkin "<days>" from today with lengthOfStay "5"
	And performs GET request
	Then the response code should be 400
	And user should see json response with paris on the filterd "." node
		|type   		| invalid_input   																														 |
		|message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
		|type   	| <error_type>  		|
		|message  | <error_message> 	|
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
		|name   	| checkin 		|
		|type  		| querystring 			|
	Examples:
		|scenario           |days   |   error_type                    |  error_message  |
		|past-dates	        | -2    |   checkin.invalid_date_in_the_past   |  Checkin cannot be in the past. |
		|Too-Advance-Checkin| 510   |  checkin.invalid_date_too_far_out  |  Checkin too far in the future. |


Scenario: Availability API with check-in date greater than check-out date
	Given Basic web application is running
	When user sets queryParam "checkout" value "2018-12-15"
	And performs GET request
	Then the response code should be "400"
	And user should see json response with paris on the filterd "." node
		|type   		| invalid_input   																														 |
		|message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
		|type   	| checkout.invalid_checkout_before_checkin  		|
		|message  | Checkout must be after checkin. |
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
		|name   	| checkin 		|
		|type  		| querystring 			|
##		|value      | 2019-01-19				|
	And user should see json response with paris on the filterd "errors[0].fields[1]" node
		|name   	| checkout 		|
		|type  		| querystring 			|
##		|value      | 2018-12-15				|

#Scenario: Availability searched beyond 500 days in advance of the current day
#	Given Basic web application is running
#	When user sets queryParam "checkin" value "2020-02-03"
#	And performs GET request
#	Then the response code should be "400"
#	And user should see json response with paris on the filterd "." node
#		|type   		| invalid_input   																													 |
#		|message    | An invalid request was sent in, please check the nested errors for details.  |
#	And user should see json response with paris on the filterd "errors[0]" node
#		|type   	| checkin.invalid_date_too_far_out		|
#		|message  |Checkin too far in the future. |
#	And user should see json response with paris on the filterd "errors[0].fields[0]" node
#		|name   	| checkin 		|
#		|type  		| querystring 			|
#		|value      | 2020-12-15				|

Scenario Outline: Availability API with "<scenario>" with "<numOfAdult>"
	Given Basic web application is running
	When user sets queryParam "occupancy" value "<numOfAdult>-<ageOfChildren>"
	And performs GET request
	Then the response code should be "400"
	And user should see json response with paris on the filterd "." node
		|type   		| invalid_input   																													 |
		|message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
		|type   	| <error_type>		|
		|message  | <error_message> |
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
		|name   	| occupancy 		|
		|type  		| querystring 			|
		|value      | <value>				|
	Examples:
	| scenario                        | numOfAdult | ageOfChildren | error_type | error_message | value |
	| occupancy_of_more_than_8_people | 9              | 0         | number_of_adults.invalid_above_maximum | Number of adults must be less than 9.| 9 |
	| no adult                        | 0              | 2,3,4,5,6,7,8 | number_of_adults.invalid_below_minimum | Number of adults must be greater than 0.| 0 |
## 	| child age =0                    | 6              | 0         |                                        |                                      |   |
	| chid age > 18                   | 2              | 20        | child_age.invalid_outside_accepted_range | Child age must be between 0 and 17.| 20  |


####### Data Validation Tests for invalid param format
	Scenario Outline: Availability API with invalid format of <query_param>
	Given Basic web application is running
	When user sets queryParam "<query_param>" value "<value>"
	And performs GET request
	Then the response code should be "400"
	And user should see json response with paris on the filterd "." node
		|type   		| invalid_input   																													 |
		|message    | An invalid request was sent in, please check the nested errors for details.  |
	And user should see json response with paris on the filterd "errors[0]" node
		|type   	| <error_type>		|
		|message  | <error_message> |
	And user should see json response with paris on the filterd "errors[0].fields[0]" node
		|name   	| <query_param> 		|
		|type  		| querystring 			|
		|value      | <value>				|
	Examples:
	|query_param| value |error_type|error_message|
#	|currency   | RRR   |currency.not_supported | Currency is not supported. Supported currencies are: [AED, ARS, AUD, BRL, CAD, CHF, CNY, DKK, EGP, EUR, GBP, HKD, IDR, ILS, INR, JPY, KRW, MXN, MYR, NOK, NZD, PHP, PLN, RUB, SAR, SEK, SGD, TRY, TWD, USD, VND, ZAR]|
#	|language   | as-US | language.not_supported | Language is not supported. Supported languages are: [ar-SA, cs-CZ, da-DK, de-DE, el-GR, en-US, es-ES, es-MX, fi-FI, fr-CA, fr-FR, hr-HR, hu-HU, id-ID, is-IS, it-IT, ja-JP, ko-KR, lt-LT, ms-MY, nb-NO, nl-NL, pl-PL, pt-BR, pt-PT, ru-RU, sk-SK, sv-SE, th-TH, tr-TR, uk-UA, vi-VN, zh-CN, zh-TW]|
#	|country_code| RRR |country_code.invalid| Country code is invalid.                                                                                                                                                                                                                                                                              |
##	|occupancy    | #   |number_of_adults.invalid_below_minimum|Number of adults must be greater than 0.                                                                                                                                                                                                                                                                                               |
#	|property_id  | 11111 |                                      |                                                                                                                                                                                                                                                                                                                                       |
#	|sales_channel		  |test   |  sales_channel.invalid |Sales Channel is invalid.  Accepted sales_channel values are: [website, agent_tool, mobile_app, mobile_web, cache, meta].|
#	|sales_environment    |   test    |  sales_environment.invalid|Sales Environment is invalid.  Accepted sales_environment values are: [hotel_only, hotel_package, loyalty].  |
#	|sort_type            |test       |sort_type.invalid | Sort Type is invalid.  Accepted sort_type values are: [preferred]. |


################# Business Validations ################################
Scenario: Availability API successful response
	Given Basic web application is running
	When performs GET request
	Then the response code should be 200

#Scenario Outline : Validate "<node>" in response
#	Given Basic web application is running
#	When performs GET request
#	Then the response code should be "200"
#	And user should see valid response for "<node>"
#	Examples:
#	| node  |
#	| property_id  |
#	| href         |
#	| merchant_of_record |

#Scenario : Validate merchant_of_record in the response
#	Given Basic web application is running
#	And user sets queryParam "property_id" value "8521"
#	When performs GET request
#	Then the response code should be "200"
	#And user should see valid response for merchant_of_record