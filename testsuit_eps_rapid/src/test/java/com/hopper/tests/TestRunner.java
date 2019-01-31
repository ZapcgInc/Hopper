package com.hopper.tests;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.hopper.tests.authorization.Authorization;
import com.hopper.tests.config.ConfigurationHelper;
import com.hopper.tests.config.model.TestConfig;
import com.hopper.tests.util.logging.LoggingUtil;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.hopper.tests.definitions",
        plugin={"json:target/cucumber.json"},
        dryRun = false,
        strict = false,
        monochrome = true
)

public class TestRunner {

    @BeforeClass
    public static void beforeClass() {
    	LoggingUtil.log("**********************************");
    	TestConfig config = ConfigurationHelper.getConfig();    	
    	if (config != null ) {
    		LoggingUtil.log("Starting Validations for partenr: " + config.getPartner() );
    		LoggingUtil.log("Validations are executed for partner URL: " + config.getAPI() );
    		LoggingUtil.log("Validations are executed for partner Version: "+ config.getVersion() );
    		
        	String auth_type = config.getAuthParams().get(Authorization.AUTH_TYPE);
    		if(auth_type == null) {
    			LoggingUtil.log("Authorization method not provided. Setting default authorization method: "+ Authorization.DEFAULT_AUTH );
    		} else {
        		LoggingUtil.log("Authorization method for partner: "+ auth_type );
    		}
    		
    	} else {
    		LoggingUtil.log("Missing configuration file. Please pass partner name using 'mvn -DhopperPartner=<partnername>'");
    		LoggingUtil.log("Stopping the test validations");
    		System.exit(0);;
    	}
        
    }

    @AfterClass
    public static void afterClass() {
    	LoggingUtil.log("***********************************");
    }
}
