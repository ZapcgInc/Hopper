package com.hopper.tests.util.validations;

import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.model.TestContext;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;

/**
 * Checks if the Desired API is up and servicing requests.
 */
public class CheckAPIAvailability
{
    // To avoid checking the API for every test scenario.
    private boolean m_appRunning;

    public CheckAPIAvailability()
    {
        m_appRunning = false;
    }

    public void test(TestContext criteria)
    {
        final SupportedPartners partner = criteria.getPartner();
        switch (partner)
        {
            case EPS:
            {
                _checkEPSAvailability(criteria);
                break;
            }
            default:
            {
                throw new UnsupportedOperationException("Authorization for Partner :" + partner.name() + "is currently unsupported");
            }
        }
    }

    private void _checkEPSAvailability(TestContext criteria)
    {
        if (!m_appRunning)
        {
            try
            {
                final Response response = RestAssured.with().get(criteria.getHost());
            }
            catch (Exception e)
            {
                if (e.getMessage().contains("Connection refused"))
                {
                    Assert.fail("ERROR :Web application is not running. So cannot test functionality.");
                    return;
                }
                else
                {
                    e.printStackTrace();
                }
            }
            m_appRunning = true;
        }
    }
}
