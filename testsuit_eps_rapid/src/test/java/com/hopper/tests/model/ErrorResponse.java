package com.hopper.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse
{
    @JsonProperty("type")
    private String type;

    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<Error> errors;

    public ErrorResponse(){}

    public String getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }

    public List<Error> getErrors()
    {
        return errors;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error
    {
        @JsonProperty("type")
        private String type;

        @JsonProperty("message")
        private String message;

        @JsonProperty("fields")
        private List<ErrorField> fields;

        public Error(){}

        public String getType()
        {
            return type;
        }

        public String getMessage()
        {
            return message;
        }

        public List<ErrorField> getFields()
        {
            return fields;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorField
    {
        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("value")
        private String value;

        public ErrorField(){}

        public String getName()
        {
            return name;
        }

        public String getType()
        {
            return type;
        }

        public String getValue()
        {
            return value;
        }
    }
}
