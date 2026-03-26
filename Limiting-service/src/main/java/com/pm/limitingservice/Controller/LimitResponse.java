package com.pm.limitingservice.Controller;

import lombok.Getter;
import lombok.Setter;


public class LimitResponse {
    public String message;
    public int Statuscode;
    public LimitResponse(String message,int Statuscode){
        this.message=message;
        this.Statuscode=Statuscode;
    }

}
