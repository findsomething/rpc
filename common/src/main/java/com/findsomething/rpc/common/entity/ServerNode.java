package com.findsomething.rpc.common.entity;

import lombok.Data;

/** @author link */
@Data
public class ServerNode {

    public ServerNode(String serverName, String host, Integer port) {
        this.serverName = serverName;
        this.host = host;
        this.port = port;
    }

    private String serverName;
    private String host;
    private Integer port;
}
