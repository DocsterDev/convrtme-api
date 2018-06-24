
package com.convrt.controller;

import com.convrt.service.AutoCompleteService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/autocomplete")
public class AutoCompleteController {

    @Autowired
    private AutoCompleteService youtubeAutoCompleteService;

    @GetMapping
    public JsonNode autocomplete(@RequestParam(value = "input", defaultValue = "false") String input) {
        return youtubeAutoCompleteService.getAutoCompleteLookup(input);
    }

}

