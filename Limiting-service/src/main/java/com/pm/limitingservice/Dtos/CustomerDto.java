package com.pm.limitingservice.Dtos;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RedisHash(value = "Customer")
public class CustomerDto {
    @Id
    @Indexed
    private String id;
    private int CompanyId; // "indexed" for faster retrieval,
    // @Id for marking this field as the key
   // private int userid;
    @TimeToLive
    private Long ttl;
    private long TokenRemain;
    private long BucketSize;
    private int FillingRate;
    long lastRefillTime;
}

