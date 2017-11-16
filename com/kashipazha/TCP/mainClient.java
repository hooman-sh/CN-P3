package com.kashipazha.TCP;


import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class mainClient {
    private DatagramSocket clientSock;

    public  mainClient() throws UnknownHostException, SocketException, IOException{
        System.out.println("enter the server port ");
        clientSock = new DatagramSocket();
        int port = 1339;
        InetAddress address = InetAddress.getByName("localhost");
        System.out.println("server IP "+address+" server Port:"+ port);
        byte buf[] = new byte[255];
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        buf[0] = (byte) (buf[0]| (1<<1));
        packet.setData(buf);
        System.out.println(Integer.toBinaryString(buf[0] & 0xFF));
        clientSock.send(packet);
        System.out.println("packet setted up and sent");
    }

    public static void main(String[] args) throws Exception{
        mainClient M= new mainClient();
    }

}
