package com.findsomething.rpc.provider.server;

import com.findsomething.rpc.common.constants.RpcConstant;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** @author link */
@Component
public class ServiceRegister {

    Logger logger = LoggerFactory.getLogger(ServiceRegister.class);

    @Value("${registry.address}")
    private String registryAddress;

    private String serverPath;

    public void register(String serverName, String serverAddress) {
        if (null == serverAddress) {
            return;
        }
        this.serverPath = RpcConstant.getServerPath(serverName);
        ZkClient client = connectServer();
        if (client != null) {
            addRootNode(client);
            createNode(client, serverAddress);
        }
    }

    private ZkClient connectServer() {
        return new ZkClient(registryAddress, 20000, 20000);
    }

    private void addRootNode(ZkClient client) {
        boolean exists = client.exists(serverPath);
        if (!exists) {
            client.createPersistent(serverPath);
            logger.info("创建zookeeper主节点 {}", serverPath);
        }
    }

    private void createNode(ZkClient client, String serverAddress) {
        String path =
                client.create(
                        this.serverPath + "/node",
                        serverAddress,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL);
        logger.info("创建zookeeper数据节点 （{} => {})", path, serverAddress);
    }
}
