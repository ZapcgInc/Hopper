package com.hopper.tests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import com.hopper.tests.config.model.TestConfig;
import com.hopper.tests.constants.GlobalConstants;
import com.hopper.tests.constants.SupportedPartners;
import com.hopper.tests.util.logging.LoggingUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Finds the relevant partner configuration file and loads the Test configuration.
 */
public class ConfigurationHelper
{
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    private static TestConfig config = null;
    
    private static void load()
    {
        final String partnerToRun = System.getProperty(GlobalConstants.PARTNER_ENV_VARIABLE);
        if (StringUtils.isEmpty(partnerToRun))
        {
            LoggingUtil.log("Partner not configured : Defaulting to EPS");
        }

        final String partner = StringUtils.isEmpty(partnerToRun)
                ? GlobalConstants.DEFAULT_PARTNER
                : partnerToRun;


        LoggingUtil.log("Loading Environment for Partner : [" + partner + "]");

        config = _parse(partner + GlobalConstants.CONFIG_FILE_TYPE);
    }

    public static TestConfig getConfig() {
    	if(config == null ) {
    		load();
    	}
    	return config;
    }
    
    
    private static TestConfig _parse(final String pathToConfigFile)
    {
        try
        {
            Path resourceDirectory = Paths.get("src", "test", "resources", "config", pathToConfigFile);
            return YAML_OBJECT_MAPPER.readValue(new File(resourceDirectory.toString()), TestConfig.class);
        }
        catch (IOException e)
        {
            LoggingUtil.log("Unable to parse Config file at location : " + pathToConfigFile);
            throw new RuntimeException("Unable to parse Config file at location : " + pathToConfigFile, e);
        }
    }
}
