package com.hong.netty.telnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by caihongwei on 15/11/2017 11:06 AM.
 */
public final class TelnetClient {
    private static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = 8080;
    static final String HOST = "127.0.0.1";

    public static void main(String[] args) throws IOException, InterruptedException {
        SslContext sslContext;
        if (SSL) {
            sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslContext = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new TelnetClientInitializer(sslContext));

            // Start the connection attempt.
            Channel channel = b.connect(HOST, PORT).sync().channel();

            // Read commands from stdin.
            ChannelFuture lastChannelFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                // Sends the received line to server.
                lastChannelFuture = channel.writeAndFlush(line + "\r\n");

                // If user typed the 'bye' command, wait until the server closes the connection.
                if ("bye".equalsIgnoreCase(line)) {
                    channel.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastChannelFuture != null) {
                lastChannelFuture.sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
