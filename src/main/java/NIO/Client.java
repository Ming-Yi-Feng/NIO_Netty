package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Client {
    private final  String HOST = "127.0.0.1";
    private final int PORT = 6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String userName;


    public Client() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);

        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(userName + "is ok");
    }


    public void sendInfo(String info){
        info = userName + " said: " + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void readInfo(){
        try {
            int readChannels = selector.select();
            if(readChannels > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        sc.read(buffer);
                        System.out.println("from server: " + new String(buffer.array()));
                    }
                }
            }
            else {
                //System.out.println("no communication server");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        final Client client = new Client();
        new Thread(){
            public void run(){
                while (true){
                    client.readInfo();
                    try{
                        Thread.currentThread().sleep(3000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String s = scanner.nextLine();
            client.sendInfo(s);
        }
    }
}
