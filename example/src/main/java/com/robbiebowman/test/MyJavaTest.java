package com.robbiebowman.test;

import com.robbiebowman.claude.ToolDescription;

public class MyJavaTest {

    @ToolDescription("Gets the current mayor of a given city")
    public static String getCurrentMayor(
            @ToolDescription("The city whose mayor to find")
            String city,
            @ToolDescription("The country of the city, in case there's ambiguity of the city's name")
            String country,
            @ToolDescription("Whether this mayor is serving or retired")
            ServingStatus status
    ) {
        return "Eric Adams";
    }
}
