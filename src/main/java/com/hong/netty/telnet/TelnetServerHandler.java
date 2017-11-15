package com.hong.netty.telnet;

import io.netty.channel.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by caihongwei on 15/11/2017 10:04 AM.
 */
@ChannelHandler.Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws UnknownHostException {
        // Send greeting for a new connection.
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // Generate and write a response.
        String response;
        boolean close = false;
        if (msg.isEmpty()) {
            response = "Please type something.\r\n";
        } else if ("bye".equalsIgnoreCase(msg)) {
            response = "Have a good day!\r\n";
            close = true;
        } else {
            response = "Did you say " + msg + "?\r\n";
        }

        ChannelFuture f = ctx.write(response);

        if (close) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
