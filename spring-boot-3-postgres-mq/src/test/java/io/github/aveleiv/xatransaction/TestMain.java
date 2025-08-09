package io.github.aveleiv.xatransaction;

import org.springframework.boot.SpringApplication;

class TestMain {

    public static void main(String[] args) {
        SpringApplication.from(Main::main).with(TestcontainersConfiguration.class).run(args);
    }
}