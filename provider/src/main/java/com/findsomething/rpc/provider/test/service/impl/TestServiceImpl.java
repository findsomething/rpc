package com.findsomething.rpc.provider.test.service.impl;

import com.findsomething.rpc.common.annontation.RpcService;
import com.findsomething.rpc.provider.test.model.input.TestModel;
import com.findsomething.rpc.provider.test.model.output.TestOutModel;
import com.findsomething.rpc.provider.test.service.TestService;
import lombok.extern.slf4j.Slf4j;

/** @author link */
@RpcService
@Slf4j
public class TestServiceImpl implements TestService {

    @Override
    public TestOutModel hello(TestModel testModel) {
        log.info("input {}", testModel);
        TestOutModel testOutModel = new TestOutModel();
        return testOutModel;
    }
}
