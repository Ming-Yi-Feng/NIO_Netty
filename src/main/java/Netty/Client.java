package Netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        //7.启动类
        new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)

                .handler(new ChannelInitializer<NioSocketChannel>() {//10 添加处理器

                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder()); //12 初始化执行,连接建立后调用
                    }
                })
                //11 连接服务器

                .connect(new InetSocketAddress("localhost",8080))
                //13 同步建立，直到连接建立
                .sync()

                //14 代表连接后的channel对象
                .channel()
                //写入数据
                .writeAndFlush("hello,world");
    }
}
