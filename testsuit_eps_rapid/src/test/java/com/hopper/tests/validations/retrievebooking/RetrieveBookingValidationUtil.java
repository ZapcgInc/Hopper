package com.hopper.tests.validations.retrievebooking;

import com.hopper.tests.data.model.response.booking.BookingRetrieveResponse;
import com.hopper.tests.definitions.model.TestContext;
import com.hopper.tests.validations.constants.ResponseValidationField;
import org.junit.Assert;

import java.awt.print.Book;

public class RetrieveBookingValidationUtil {

    public static void validate(TestContext context, ResponseValidationField validationField) {
        final BookingRetrieveResponse response = context.getBookingRetrieveResponse();
        Assert.assertNotNull(
                "PreBooking response is missing",
                response
        );

        switch (validationField)
        {
            case EMAIL_ADDRESS:
            {
                _validateEmailAddress(response);
                break;
            }
            case ITINERARY_ID:
            {
                _validateItineraryId(response);
                break;
            }
            case CANCEL_HREF:
            {
                _validateCancelHref(response);
                break;
            }
            case ROOM_ID:
            {
                _validateRoomId(response);
                break;
            }
            case CHECK_IN_DATE:
            {
                _validateCheckinDate(response);
                break;
            }
            case CHECK_OUT_DATE:
            {
                _validateCheckoutDate(response);
                break;
            }
            case NUM_ADULTS:
            {
                _validateNumOfAdults(response);
                break;
            }
            case RATE_ID:
            {
                _validateRateId(response);
                break;
            }
            case GIVEN_NAME:
            {
                _validateGivenName(response);
                break;
            }
            case FAMILY_NAME:
            {
                _validateFamilyName(response);
                break;
            }
            case PHONE:
            {
                _validatePhone(response);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validationField + "] unsupported");
            }
        }
    }

    private static void _validateCancelHref(BookingRetrieveResponse response) {
        Assert.assertTrue("method is not 'DELETE'","DELETE".equals(response.getLinks().get("cancel").getMethod()));
        Assert.assertNotNull("href is missing",response.getLinks().get("cancel").getHref());
    }

    private static void _validateItineraryId(BookingRetrieveResponse response) {
        Assert.assertNotNull("itinerary_id is missing",response.getItineraryId());
    }

    private static void _validateEmailAddress(BookingRetrieveResponse response) {

      //  response

    }

    private static void _validateRoomId(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->
                Assert.assertNotNull(
                        "Room Id is missing"+room.getId())
        );
    }

    private static void _validateCheckinDate(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->
                Assert.assertNotNull(room.getCheckin())
        );
    }
    private static void _validateCheckoutDate(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room-> Assert.assertNotNull(room.getCheckout()));
    }

    private static void _validateNumOfAdults(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->Assert.assertNotNull(room.getNumberOfAdults()));
    }
    private static void _validateRateId(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->Assert.assertNotNull(room.getRate().getId()));
    }
//    private static void _validateNightlyRateValue(BookingRetrieveResponse response)
//    {
//        response.getRooms().forEach(room -> Assert.assertTrue(room.getRate().getN));
//    }

    private static void _validateGivenName(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->Assert.assertNotNull(room.getGivenName()));
    }

    private static void _validateFamilyName(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->Assert.assertNotNull(room.getFamilyName()));
    }

    private static void _validatePhone(BookingRetrieveResponse response)
    {
        response.getRooms().forEach(room->Assert.assertNotNull(room.getPhone()));
    }
//    private static void _validateAdress(BookingRetrieveResponse response)
//    {
//        response.getRooms().forEach(room -> Assert.assertNotNull(room.getAddress()));
//    }

    private static void _validateCountryCode(BookingRetrieveResponse response)
    {

//        response.forEach(room -> {
//            String countryCode =room.getAddress().getCountryCode();
//            Assert.assertTrue("Country Code length more than 2",countryCode.length()==2);
//            });
    }




}
