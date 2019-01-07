package com.hopper.tests;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources",
        glue = "com.hopper.tests.definitions",
        format={"json:target/cucumber.json"},
        dryRun = false,
        strict = false,
        monochrome = true

)

public class TestRunner {

    @BeforeClass
    public static void beforeClass() {
        System.out.println("**********************************");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("***********************************");
    }
}
