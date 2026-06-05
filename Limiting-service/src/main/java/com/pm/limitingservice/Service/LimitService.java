package com.pm.limitingservice.Service;


import com.pm.limitingservice.Config.ResourceNotfound;
import com.pm.limitingservice.Controller.LimitResponse;
import com.pm.limitingservice.Dtos.CustomerDto;
import com.pm.limitingservice.Dtos.OwnerDto;
import com.pm.limitingservice.Entities.Owner;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LimitService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> rateLimiterScript;
    private final ModelMapper modelMapper;
    private final OwnerRepository ownerRepository;

    public LimitResponse getuserstatus(Integer userId, String CompanyName) {
        Owner owner = ownerRepository.findByOrganizationName(CompanyName)
                .orElseThrow(() -> new ResourceNotfound("Company not found"));
        System.out.println("hey got the owner");
        Long companyId=owner.getId();
        String key = "rate:{company:" + companyId + "}:user:" + userId;
        System.out.println("using this key as in redis Key: " + key);
        long now = System.currentTimeMillis();

        Long result = redisTemplate.execute(
                rateLimiterScript,
                Collections.singletonList(key),          // KEYS[1]
                String.valueOf(owner.getBucketSize()),   // ARGV[1]
                String.valueOf(owner.getFillingRate()),  // ARGV[2]
                String.valueOf(now)                      // ARGV[3]
        );

        if (result == null || result < 0) {
            return new LimitResponse("Too many requests", 429);
        }

        return new LimitResponse("Request allowed", 200);
    }
    // here for race around condn we can use redics template

    public OwnerDto createowner(OwnerDto ustomer) {
        System.out.println("mapping the one to another class in createowner");
        Owner saveowner= modelMapper.map(ustomer, Owner.class);
        Owner SavedOwner= ownerRepository.save(saveowner);
        return  modelMapper.map(SavedOwner, OwnerDto.class);
    }
    public Optional<OwnerDto> getowner(Long owner_id) {
        return ownerRepository.findById(owner_id).map(entity -> modelMapper.map(entity, OwnerDto.class));
    }

    public OwnerDto modify(OwnerDto customer) {
        Owner prevowner= ownerRepository.findById(customer.getId()).orElseThrow();
        Owner newowner= modelMapper.map(prevowner, Owner.class);
        Owner savedowner= ownerRepository.save(newowner);
        return   modelMapper.map(savedowner, OwnerDto.class);
    }
}

