package dota;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by IntelliJ IDEA.
 * User: Joshua Campbell
 * Date: 3/19/21
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        try {
            DotaMatchStatCollector dotaMatchStatCollector = DotaMatchStatCollector.getInstance();
            dotaMatchStatCollector.startReadingFromAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
