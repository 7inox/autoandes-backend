package pe.edu.upeu.autoandes.config;
import org.springframework.context.annotation.*; import java.time.*;
@Configuration public class AppConfig {@Bean public Clock clock(){return Clock.system(ZoneId.of("America/Lima"));}}
