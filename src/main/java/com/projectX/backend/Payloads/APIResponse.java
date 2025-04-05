package com.projectX.backend.Payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class APIResponse {
    private String message;
    private boolean status;
    public APIResponse(String message,boolean status){
        this.message=message;
        this.status=status;
    }
}
