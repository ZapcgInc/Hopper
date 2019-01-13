package com.hopper.tests.util.validations;

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
    private static boolean m_appRunning;


    public void test(TestContext criteria)
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
