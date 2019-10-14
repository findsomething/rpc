package com.findsomething.rpc.consumer.server;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/** @author link */
@Component
public class ConnectManage {

    @Autowired private NettyClient nettyClient;

    Logger logger = LoggerFactory.getLogger(ConnectManage.class);

    private AtomicInteger roundRobin = new AtomicInteger(0);
    private CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();
    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();

    /**
     * 很简单的轮询方案
     *
     * @return
     */
    public Channel chooseChannel() {
        if (channels.size() <= 0) {
            return null;
        }
        int size = channels.size();
        int index = (roundRobin.getAndAdd(1) + size) % size;
        return channels.get(index);
    }

    public synchronized void updateConnectServer(List<String> addressList) {
        // 1. 如果可用服务列表是空的话，则做清空动作
        if (null == addressList || addressList.size() == 0) {
            removeAllChannels();
            return;
        }

        HashSet<SocketAddress> newAllServerNodeSet = getNewSocketAddresses(addressList);

        // 2. 创建新通道连接
        connectAllChannel(newAllServerNodeSet);

        // 3. 移出无效通道连接
        removeInvalidChannels(newAllServerNodeSet);
    }

    public void removeChannel(Channel channel) {
        logger.info("从连接管理器中移除失效Channel.{}", channel.remoteAddress());
        SocketAddress remotePeer = channel.remoteAddress();
        channelNodes.remove(remotePeer);
        channels.remove(channel);
    }

    private void removeInvalidChannels(HashSet<SocketAddress> newAllServerNodeSet) {
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            SocketAddress remotePeer = channel.remoteAddress();
            if (!newAllServerNodeSet.contains(remotePeer)) {
                logger.info("删除失效服务节点 {}" + remotePeer);
                Channel channelNode = channelNodes.get(remotePeer);
                if (channelNode != null) {
                    channelNode.close();
                }
                channels.remove(channel);
                channelNodes.remove(remotePeer);
            }
        }
    }

    private void connectAllChannel(HashSet<SocketAddress> newAllServerNodeSet) {
        for (final SocketAddress serverNodeAddress : newAllServerNodeSet) {
            Channel channel = channelNodes.get(serverNodeAddress);
            if (null != channel && channel.isOpen()) {
                logger.info("当前服务节点已存在,无需重新连接.{}", serverNodeAddress);
                continue;
            }
            connectServerNode(serverNodeAddress);
        }
    }

    private HashSet<SocketAddress> getNewSocketAddresses(List<String> addressList) {
        HashSet<SocketAddress> newAllServerNodeSet = new HashSet<>();
        for (int i = 0; i < addressList.size(); i++) {
            String[] array = addressList.get(i).split(":");
            if (array.length == 2) {
                String host = array[0];
                int port = Integer.parseInt(array[1]);
                final SocketAddress remotePeer = new InetSocketAddress(host, port);
                newAllServerNodeSet.add(remotePeer);
            }
        }
        return newAllServerNodeSet;
    }

    private void removeAllChannels() {
        logger.error("没有可用的服务器节点，全部服务节点已经关闭!");
        for (final Channel channel : channels) {
            SocketAddress remotePeer = channel.remoteAddress();
            Channel handleNode = channelNodes.get(remotePeer);
            handleNode.close();
        }
        channels.clear();
        channelNodes.clear();
    }

    private void connectServerNode(SocketAddress address) {
        try {
            Channel channel = nettyClient.doConnect(address);
            addChannel(channel, address);
        } catch (InterruptedException e) {
            logger.error("未能成功连接到服务器:{} {}", address, e);
        }
    }

    private void addChannel(Channel channel, SocketAddress address) {
        logger.info("加入Channel到连接管理器.{}", address);
        channels.add(channel);
        channelNodes.put(address, channel);
    }
}
