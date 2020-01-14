package pingidentity.com.bootapi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude={org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration.class})
public class App {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(App.class, args);
    }

}