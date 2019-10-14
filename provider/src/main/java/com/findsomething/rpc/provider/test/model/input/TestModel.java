package com.findsomething.rpc.provider.test.model.input;

import lombok.Data;

/** @author link */
@Data
public class TestModel {

    @Data
    public class SubModel {
        private String subHello;
    }

    private String hello;
    private Integer world;
    private SubModel subModel;
}
