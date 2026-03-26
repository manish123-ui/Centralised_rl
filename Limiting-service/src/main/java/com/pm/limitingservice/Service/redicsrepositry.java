package com.pm.limitingservice.Service;


import com.pm.limitingservice.Dtos.CustomerDto;
import org.springframework.data.repository.CrudRepository;

public interface redicsrepositry extends CrudRepository<CustomerDto,String> {

}
