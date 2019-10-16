package com.findsomething.rpc.common.utils;

import com.findsomething.rpc.common.constants.RpcConstant;
import com.findsomething.rpc.common.entity.ServerNode;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/** @author link */
public class ZkHelper {

    private static final Integer DEFAULT_PORT = 80;

    private static final Logger logger = LoggerFactory.getLogger(ZkHelper.class);

    public static ZkClient createClient(String registryAddress) {
        return new ZkClient(registryAddress, 20000, 20000);
    }

    public static boolean initProviderRootNode(ZkClient client, String serverName) {
        String serverPath = RpcConstant.getServerPath(serverName);
        if (client == null) {
            logger.error("client is null, failed to add provider root node {}", serverPath);
            return false;
        }
        boolean exists = client.exists(serverPath);
        if (!exists) {
            client.createPersistent(serverPath);
            logger.info("create {} success", serverPath);
        }
        return true;
    }

    public static boolean addProviderNode(ZkClient client, String serverName, String address) {
        String serverNode = getServerNode(serverName);
        if (client == null) {
            logger.error("client is null, failed to add provider node {}", serverNode);
            return false;
        }
        try {
            String node =
                    client.create(
                            serverNode,
                            address,
                            ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("create {} success", node);
        } catch (Exception e) {
            logger.error("create {} failed {}", serverNode, e);
        }

        return true;
    }

    public static List<ServerNode> getServers(ZkClient client, String serverName) {
        List<String> servers = client.getChildren(RpcConstant.ZK_REGISTRY_PATH);
        List<ServerNode> list = new ArrayList<>();
        for (String server : servers) {
            if (!server.equals(serverName)) {
                continue;
            }
            List<String> serverNodes = client.getChildren(RpcConstant.getServerPath(server));
            for (String node : serverNodes) {
                ServerNode serverNode = getServerNode(client, serverName, node);
                list.add(serverNode);
            }
        }
        return list;
    }

    private static ServerNode getServerNode(ZkClient client, String serverName, String node) {
        String nodeName = RpcConstant.getServerPath(serverName) + "/" + node;
        System.out.println(nodeName);
        String address = client.readData(nodeName);
        String[] serverPort = address.split(":");
        String serverAddress = serverPort[0];
        Integer port = (serverPort.length <= 1) ? DEFAULT_PORT : Integer.parseInt(serverPort[1]);
        return new ServerNode(serverName, serverAddress, port);
    }

    private static String getServerNode(String serverName) {
        return String.format("%s/node", RpcConstant.getServerPath(serverName));
    }
}
