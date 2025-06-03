package startwithco.tosssnapshot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TossSnapshotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TossSnapshotApplication.class, args);
    }

}
