package com.hong.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by Hongwei on 2015/10/9.
 * Handlers a server-side channel
 */
public class DiscardServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Discard
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                System.out.print((char) in.readByte()); // FIXME 中文乱码
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
//        ((ByteBuf) msg).release();

        // ECHO 响应式协议，此处将客户端的输入返回给客户端
//        ctx.write(msg);
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when a exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
