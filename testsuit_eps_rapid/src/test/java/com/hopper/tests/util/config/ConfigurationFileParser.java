package com.hopper.tests.util.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.hopper.tests.model.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.hopper.tests.util.logging.LoggingUtil;

/**
 * Parses Test config file at a given destination.
 */
public class ConfigurationFileParser
{
    private static final ObjectMapper YAML_OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    public static TestConfig parse(final String pathToConfigFile)
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