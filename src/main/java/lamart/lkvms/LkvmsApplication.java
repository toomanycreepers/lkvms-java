package lamart.lkvms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class
})
@EnableJpaAuditing
public class LkvmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LkvmsApplication.class, args);
	}

}
