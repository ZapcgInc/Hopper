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
		| checkin                          | 2019-02-15           			|
		| checkout                         | 2019-02-17                 |
		| currency                         | USD         								|
		| language                         | en-US         							|
		| country_code                     | US               					|
		| property_id											 | 20321											|
		| occupancy                        | 2-9,4           						|
		| sales_channel                    | website           					|
		| sales_environment                | hotel_only                 |
		| sort_type                        | preferred         					|
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



Scenario: Availability API successful response
	Given Basic web application is running
	When performs GET request
	Then the response code should be 200
