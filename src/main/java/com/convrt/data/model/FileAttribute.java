package com.convrt.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FileAttribute {

    private final UUID id = UUID.randomUUID();
    private String name;

    public FileAttribute(String s) {
        setName(name);
    }

    public FileAttribute() { }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
