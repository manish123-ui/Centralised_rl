package com.pm.limitingservice.Controller;

import com.pm.limitingservice.Config.ResourceNotfound;
import com.pm.limitingservice.Dtos.CustomerDto;
import com.pm.limitingservice.Dtos.OwnerDto;
import com.pm.limitingservice.Service.LimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/limit/")
@RequiredArgsConstructor
public class OwnerController {
    /*
    now let's discuss about path or routes of api control
    /limit/{userid} i am going to find him based on authorization token
    so first I find owner
    then developer and then now apply first shard the key with owner+userid and that make unique shard key so and then
    hmget to find last refill and no of token remain
    so first fill the token based on last refill and then run q and remove that based on that give 429 or 201 request access point
    also as it goes ok inc the request for that owner with user id in db but that will be slow and incosistent is ok little bit delay in analytics is ok
    for that now i have to build these
    here now look at the structure of the redics db
    for redics cluster here on thing whole
    so redics 1.primary key+userid will be primary key of redics
    2.fulltoken
    3.timestmap at fill
    4.nooftoken now
     */
    private final LimitService limitService;
    @GetMapping("{user_id}")
    public LimitResponse getLimit(@PathVariable Integer user_id,@RequestHeader("X-Company-Id") Integer CompanyId){
        return limitService.getuserstatus(user_id,CompanyId);
    }
    @Validated
    @PostMapping
    public OwnerDto createLimit(@Validated @RequestBody OwnerDto customer){
        return limitService.createowner(customer);
    }
    @GetMapping("{ownerId}")
    public ResponseEntity<OwnerDto> getOwner(@PathVariable Long ownerId) {
        Optional<OwnerDto> owner = limitService.getowner(ownerId);

        if (owner.isPresent()) {
            return ResponseEntity.ok(owner.get());
        } else {
            throw new ResourceNotfound("Owner not found with this id " + ownerId);
        }
    }
    @PutMapping("modify/")
    public OwnerDto modifyLimit(@Validated @RequestBody OwnerDto customer){
        return limitService.modify(customer);
    }


}
