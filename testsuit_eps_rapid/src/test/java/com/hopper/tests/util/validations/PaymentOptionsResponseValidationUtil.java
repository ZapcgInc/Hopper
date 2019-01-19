package com.hopper.tests.util.validations;

import com.hopper.tests.model.TestContext;
import com.hopper.tests.model.response.payment.PaymentOptionResponse;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

import java.util.Optional;

public class PaymentOptionsResponseValidationUtil
{
    enum ValidatorField
    {
        CARD_TYPE,
        ;
    }

    public static void validate(final TestContext context, ValidatorField field)
    {
        switch (field)
        {
            case CARD_TYPE:
            {
                _validateCardType(context);
                break;
            }
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
