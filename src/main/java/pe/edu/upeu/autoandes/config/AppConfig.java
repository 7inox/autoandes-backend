package pe.edu.upeu.autoandes.config;

import java.time.*;
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {
  @Bean
  public Clock clock() {
    return Clock.system(ZoneId.of("America/Lima"));
  }
}
