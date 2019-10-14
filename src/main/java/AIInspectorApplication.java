
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//@EnableTransactionManagement
//@MapperScan("com.aiinspector.dao.mapper")
@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.aiinspector")
public class AIInspectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(AIInspectorApplication.class, args);
	}
}

