package org.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApiResponse implements Serializable {

    private Boolean success;
    private String message;


}
