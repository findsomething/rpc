package com.findsomething.rpc.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/** @author link */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.findsomething.rpc.common",
        "com.findsomething.rpc.provider"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.run(args);
    }
}
