package com.rentanora.backend.health;

import com.rentanora.common.model.health.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rentanora.backend.constants.AuthConstants.HEALTH_PATH;

@RestController
public class HealthController {

    @GetMapping(HEALTH_PATH)
    public HealthResponse health() {
        return new HealthResponse("UP");
    }
}