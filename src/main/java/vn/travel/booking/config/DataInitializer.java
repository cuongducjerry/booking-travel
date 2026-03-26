package vn.travel.booking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.travel.booking.service.InitService;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final InitService initService;

    @Override
    public void run(String... args) {
        initService.init();
    }
}
