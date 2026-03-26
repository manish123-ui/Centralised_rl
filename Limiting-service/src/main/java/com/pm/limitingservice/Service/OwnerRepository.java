package com.pm.limitingservice.Service;

import com.pm.limitingservice.Entities.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner,Long> {
}
