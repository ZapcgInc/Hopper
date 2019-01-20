package com.hopper.tests.validations.paymentoptions;

import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.payment.PaymentOptionResponse;
import com.hopper.tests.validations.constants.ResponseValidationField;
import com.hopper.tests.validations.model.Range;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.List;
import java.util.Optional;

/**
 * Util class for Payment Options Response Validations
 */
public class PaymentOptionsResponseValidationUtil
{
    public static void validate(final TestContext context, final ResponseValidationField validationField)
    {
        switch (validationField)
        {
            case CARD_TYPE:
            {
                _validateCardType(context);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("field [" + validationField.name() + "], not supported.");
            }
        }
    }

    public static void validate(final TestContext context, final ResponseValidationField validateField, final int count)
    {
        switch (validateField)
        {
            default:
            {
                throw new UnsupportedOperationException("Validation Field [" + validateField + "] unsupported");
            }
        }
    }

    public static void validate(final TestContext context, ResponseValidationField field, Range<Integer> expectedRange)
    {
        switch (field)
        {
            default:
            {
                throw new UnsupportedOperationException("field [" + field.name() + "], not supported.");
            }
        }
    }

    public static void validate(final TestContext context, ResponseValidationField field, List<String> expectedValues)
    {
        switch (field)
        {
            default:
            {
                throw new UnsupportedOperationException("field [" + field.name() + "], not supported.");
            }
        }
    }

    private static void _validateCardType(final TestContext context)
    {
        final Optional<PaymentOptionResponse.CreditCard> creditCard = context.getPaymentOptionResponse().getCreditCard();

        Assert.assertTrue("Payment Options missing credit card information",
                creditCard.isPresent());

        Assert.assertFalse("Credit Card doesn't contain card Options",
                creditCard.get().getCardOptions().isEmpty());

        creditCard.get().getCardOptions()
                .forEach(cardOption ->
                {
                    Assert.assertTrue("Card name is missing", StringUtils.isNotEmpty(cardOption.getName()));
                    Assert.assertTrue("Card Type is missing", StringUtils.isNotEmpty(cardOption.getCardType()));
                });
    }

}
