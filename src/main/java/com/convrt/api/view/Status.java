package com.convrt.api.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Status {

    public Status(boolean valid){
        this.valid = valid;
    }

    public Status(long count){
        this.count = count;
    }

    private Boolean valid;
    private Long count;
}
