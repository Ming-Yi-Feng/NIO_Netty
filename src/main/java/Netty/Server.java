package Netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class Server {
    public static void main(String[] args) {

        //1.Netty的启动器
        new ServerBootstrap()

                .group(new NioEventLoopGroup()) //2.建立事件组

                .channel(NioServerSocketChannel.class) //3.选择服务器的ServerSocketChannel实现

                .childHandler(new ChannelInitializer<NioSocketChannel>() { //4.boss负责处理连接，worker负责处理读写；执行处理器

                    protected void initChannel(NioSocketChannel ch) throws Exception {//12.连接建立后调用的
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });
                    }
                })

                .bind(8080);//6.绑定监听端口
    }
}
