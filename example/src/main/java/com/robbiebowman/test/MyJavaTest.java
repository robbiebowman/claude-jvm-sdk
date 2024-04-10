package com.robbiebowman.test;

import com.robbiebowman.claude.ToolDescription;

public class MyJavaTest {

    public static void sendLatter(String addressee, String contents) {

    }

    @ToolDescription("Gets the current mayor of a given city")
    public static String getCurrentMayor(
            @ToolDescription("The city whose mayor to find")
            String city,
            @ToolDescription("The country of the city, in case there's ambiguity of the city's name")
            String country,
            SomeNesting misc
    ) {
        return "Eric Adams";
    }
}
