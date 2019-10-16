package com.findsomething.rpc.common.constants;

/** @author link */
public class RpcConstant {

    public static final String PING = "ping";

    public static final String ZK_REGISTRY_PATH = "/rpc/provider";

    public static String getServerPath(String serverName) {
        return String.format("%s/%s", ZK_REGISTRY_PATH, serverName);
    }
}
