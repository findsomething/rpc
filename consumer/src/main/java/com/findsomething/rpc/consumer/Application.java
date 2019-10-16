package com.findsomething.rpc.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author link
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.findsomething.rpc.common",
        "com.findsomething.rpc.consumer"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }
}
