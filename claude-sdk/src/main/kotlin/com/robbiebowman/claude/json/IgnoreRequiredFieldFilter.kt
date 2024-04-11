package com.robbiebowman.claude.json

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider

@JsonFilter("ignoreRequiredFieldFilter")
class IgnoreRequiredFieldFilter {
    companion object {
        val provider = SimpleFilterProvider().addFilter(
            "ignoreRequiredFieldFilter",
            SimpleBeanPropertyFilter.serializeAllExcept("required")
        )
    }
}