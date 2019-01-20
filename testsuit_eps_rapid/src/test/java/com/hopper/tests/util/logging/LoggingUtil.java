package com.hopper.tests.util.logging;

import com.hopper.tests.model.TestContext;
import cucumber.api.Scenario;

/**
 * Simple Util class for logging.
 */
public class LoggingUtil
{
    public static void printTestDetails(Scenario scenario)
    {
        log("======================================Scenario=================================================");
        log("Scenario ID                 : " + scenario.getId());
        log("Scenario Name               : " + scenario.getName());
        log("Scenario Stage              : " + scenario.getSourceTagNames());
        log("Scenario Status: Started at : " + new java.util.Date());
    }

    public static void printTestStatus(final Scenario scenario, final TestContext context)
    {
        log("Scenario Success : " + (!scenario.isFailed()));
        log("Scenario Status  : " + scenario.getStatus() + " at :" + new java.util.Date());

        if (scenario.isFailed())
        {
            log("Scenario Execution Details :");
            log(context.toString());
        }
    }

    public static void log(String line)
    {
        System.out.println(line);
    }
}
