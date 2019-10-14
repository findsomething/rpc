package com.findsomething.rpc.provider.test.service;

import com.findsomething.rpc.provider.test.model.input.TestModel;
import com.findsomething.rpc.provider.test.model.output.TestOutModel;

/** @author link */
public interface TestService {

    TestOutModel hello(TestModel testModel);
}
