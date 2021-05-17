package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {
    //定义属性
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private  static  final  int PORT = 6667;
    //初始化
    public Server(){
        try{
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        catch (Exception e) {e.printStackTrace();}
    }
    //监听
    public void  listen(){
        try {
            while (true)
            {
                int res = selector.select(2000);
                if(res > 0){
                    //遍历key集合,selectedKeys是正在发生消息的key
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext())
                    {
                        SelectionKey key = iterator.next();

                        //事件处理
                        if(key.isAcceptable()){
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            //给出提示
                            System.out.println(socketChannel.getLocalAddress() + "go online");
                        }


                        if(key.isReadable()){
                            read(key);
                        }
                        iterator.remove();
                    }
                }else {
                    System.out.println("do other things");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = null;
        try{
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            if(count > 0){
                String s = new String(buffer.array());
                System.out.println("from client" + s);

                //向其他客户端转发消息
                sendInfoToOthers(s,channel);
            }else{
                key.cancel();
            }
        }catch (Exception e){
            System.out.println(channel.getLocalAddress() + "is down");
            key.cancel();
            channel.close();
        }
    }


    private void sendInfoToOthers(String s, SocketChannel channel) throws IOException {
        System.out.println("server begin to sendMSG...");
        //遍历所有通道
        for (SelectionKey key: selector.keys()){
            SocketChannel targetChannel = (SocketChannel) key.channel();
            if(targetChannel != channel){
                ByteBuffer wrap = ByteBuffer.wrap(s.getBytes());
                targetChannel.write(wrap);
            }
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
