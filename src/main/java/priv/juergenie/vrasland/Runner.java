package priv.juergenie.vrasland;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"priv.juergenie.vrasland.*"})
public class Runner {
    public static void main(String[] args) {
        System.out.println("hello world!");
        SpringApplication.run(Runner.class, args);
    }
}
