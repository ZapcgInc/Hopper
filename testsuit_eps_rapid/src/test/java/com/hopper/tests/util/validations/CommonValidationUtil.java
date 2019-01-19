package com.hopper.tests.util.validations;

import com.hopper.tests.constants.RequestType;
import com.hopper.tests.model.response.Link;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;

public class CommonValidationUtil
{
    public static void validateLink(final Link link, final RequestType requestType, String message, String linkType)
    {
        final String requestTypeMsg = String.format("Request Type [%s] : ", requestType.name());

        Assert.assertNotNull(requestTypeMsg + "Link Missing ",
                link);

        Assert.assertTrue(
                requestTypeMsg + "Link Method missing for : [" + message + "], of Type [" + linkType + "]",
                StringUtils.isNotEmpty(link.getMethod())
        );

        Assert.assertTrue(
                requestTypeMsg + "Link HREF missing for : [" + message + "], of Type [" + linkType + "]",
                StringUtils.isNotEmpty(link.getHref())
        );
    }
}
