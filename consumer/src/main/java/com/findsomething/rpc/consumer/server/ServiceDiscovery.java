package com.findsomething.rpc.consumer.server;

import com.alibaba.fastjson.JSONObject;
import com.findsomething.rpc.common.constants.RpcConstant;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/** @author link */
@Component
public class ServiceDiscovery {

    @Value("${registry.address}")
    private String registryAddress;

    @Autowired ConnectManage connectManage;

    /** 服务器地址列表 */
    private volatile List<String> addressList = new ArrayList<>();

    private ZkClient client;

    Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    @PostConstruct
    public void init() {
        client = connectServer();
        watchNode(client);
    }

    private ZkClient connectServer() {
        return new ZkClient(registryAddress, 30000, 30000);
    }

    private void watchNode(final ZkClient client) {
        List<String> nodeList =
                client.subscribeChildChanges(
                        RpcConstant.ZK_REGISTRY_PATH,
                        (s, nodes) -> {
                            logger.info("监听到子节点数据变化{}", JSONObject.toJSONString(nodes));
                            addressList.clear();
                            getNodeData(nodes);
                            updateConnectedServer();
                        });
        getNodeData(nodeList);
        logger.info("已发现服务列表...{}", JSONObject.toJSONString(addressList));
        updateConnectedServer();
    }

    private void updateConnectedServer() {
        connectManage.updateConnectServer(addressList);
    }

    private void getNodeData(List<String> nodes) {
        logger.info("/rpc子节点数据为:{}", JSONObject.toJSONString(nodes));
        for (String node : nodes) {
            String address = client.readData(RpcConstant.ZK_REGISTRY_PATH + "/" + node);
            addressList.add(address);
        }
    }
}
