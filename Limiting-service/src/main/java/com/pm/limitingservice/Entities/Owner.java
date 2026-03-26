package com.pm.limitingservice.Entities;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "owners")
@Getter
@Setter
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long BucketSize;
    private Integer FillingRate;
    @PrePersist
    public void setDefaultLimitTime() {
        if (FillingRate == null) {
            FillingRate = 1; // default value
        }
        if(BucketSize == null) {
            BucketSize= 60L;
        }
    }
    @Column(nullable = false, unique = true)
    private String organizationName;

    // getters/setters
}

