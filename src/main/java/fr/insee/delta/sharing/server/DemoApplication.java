package fr.insee.delta.sharing.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

  public static void main(String[] args) {
    System.setProperty("hadoop.home.dir", "/");
    SpringApplication.run(DemoApplication.class, args);
  }

}
