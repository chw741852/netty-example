package com.hong.netty.uptime;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by caihongwei on 14/11/2017 8:13 PM.
 */
public class UptimeClient {
    static final int RECONNECT_DELAY = 5;
    static final String HOST = "127.0.0.1";
    static final int PORT = 8080;
    private static final int READ_TIMEOUT = 10;

    private static final UptimeClientHandler HANDLER = new UptimeClientHandler();
    private static final Bootstrap b = new Bootstrap();

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(HOST, PORT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0, 0), HANDLER);
                    }
                });
        b.connect();
    }

    static void connect() {
        b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    HANDLER.startTime = -1;
                    HANDLER.println("Failed to connect: " + future.cause());
                }
            }
        });
    }
}
