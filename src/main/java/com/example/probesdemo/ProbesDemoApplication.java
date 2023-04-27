package com.example.probesdemo;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ProbesDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProbesDemoApplication.class, args);
	}


@RestController
class IndexController {
	@Value("#{environment.MY_POD_NAME}")
	private String pod_name;
	
	@Autowired
    private HealthEndpoint healthEndpoint;
	
    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    String index() {
        return " Hello from pod: " + pod_name + "\n" + " Current unix time: "+ Instant.now().getEpochSecond()+"\n Health Status: "
		+ healthEndpoint.health().getStatus();
    }
}
}

@Component
@Slf4j
class FakeHealthIndicator implements HealthIndicator {
    private final long timeCreated = Instant.now().getEpochSecond();
	private final int DELAY = 60; 
    @Override
    public Health health() {
        // This health indicator simulates a service indicator (such as a database)
        // which takes a lot of time to get initialized.
        final long now = Instant.now().getEpochSecond();
        final boolean ready = (now - timeCreated) > DELAY;
        log.debug("Fake health indicator: {}", ready ? "UP" : "DOWN");

        return ready ? Health.up().build() : Health.down().build();
    }
}
