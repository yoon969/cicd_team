package dev.mvc.team1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
@ComponentScan(basePackages = {"dev.mvc"})
public class Team1V2sbm3cApplication {

    public static void main(String[] args) {
        SpringApplication.run(Team1V2sbm3cApplication.class, args);
    }

}
 