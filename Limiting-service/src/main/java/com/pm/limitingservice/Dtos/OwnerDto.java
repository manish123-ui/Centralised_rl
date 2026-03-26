package com.pm.limitingservice.Dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OwnerDto {
    private Long id;
    private Long BucketSize;
    private Integer FillingRate;
    private String organizationName;
}
