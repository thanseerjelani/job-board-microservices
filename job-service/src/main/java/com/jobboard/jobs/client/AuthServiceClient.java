package com.jobboard.jobs.client;

import com.jobboard.jobs.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service")  // Service name in Eureka
public interface AuthServiceClient {

    @GetMapping("/api/auth/me")
    UserDTO getCurrentUser(@RequestHeader("Authorization") String token);
}