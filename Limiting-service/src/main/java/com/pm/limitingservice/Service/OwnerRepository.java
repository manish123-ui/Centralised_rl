package com.pm.limitingservice.Service;

import com.pm.limitingservice.Entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner,Long> {
    Optional<Owner> findByOrganizationName(String organizationName);
}
