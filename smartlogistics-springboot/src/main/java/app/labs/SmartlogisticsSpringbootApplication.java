package app.labs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("app.labs") // ✅ 컨트롤러가 있는 패키지를 명확히 스캔하도록 설정
public class SmartlogisticsSpringbootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartlogisticsSpringbootApplication.class, args);
    }
}
