package com.findsomething.rpc.common.utils;

import com.findsomething.rpc.common.JMockitUnitTest;
import com.findsomething.rpc.common.entity.ServerNode;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.testng.Assert;

import java.util.List;

import static org.junit.Assert.*;

/** @author link */
public class ZkHelperTest extends JMockitUnitTest {

    @Value("${registry.address}")
    private String registryAddress;

    private String serverName = "testServer";

    @Test
    public void getServers() {

        String[] addresses = new String[] {"127.0.0.1:10001", "127.0.0.1:10002"};

        ZkClient client = ZkHelper.createClient(registryAddress);
        ZkHelper.initProviderRootNode(client, serverName);

        for (String address : addresses) {
            ZkHelper.addProviderNode(client, serverName, address);
        }

        List<ServerNode> servers = ZkHelper.getServers(client, serverName);
        Assert.assertEquals(servers.size(), 2);
        Assert.assertEquals(servers.get(0).getServerName(), serverName);
        Assert.assertEquals(servers.get(0).getPort().intValue(), 10001);
    }
}
