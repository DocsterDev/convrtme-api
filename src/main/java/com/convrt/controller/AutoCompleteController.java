
package com.convrt.controller;

import com.convrt.service.AutoCompleteService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/autocomplete")
public class AutoCompleteController {

    @Autowired
    private AutoCompleteService autoCompleteService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Cacheable("autocomplete")
    public JsonNode autocomplete(@RequestParam(value = "input", defaultValue = "false") String input) {
        return autoCompleteService.getAutoCompleteLookup(input);
    }

}

