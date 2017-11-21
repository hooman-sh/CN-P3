package com.kashipazha.TCP;


import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

public class mainClient implements MySocket{
    private DatagramSocket clientSock;

    private setupSegment segmentHandler;
    private ByteBuffer receiverBuffer = ByteBuffer.allocate(20000000); //20 MB buffer
    private DatagramPacket rcvPacket ;


    private void createSocket() throws Exception{
        clientSock = new DatagramSocket();
        int port = 1339;
        InetAddress address = InetAddress.getByName("localhost");
        System.out.println("server IP "+address+" server Port:"+ port);
        ByteBuffer buf = ByteBuffer.allocate(255);
        DatagramPacket packet = new DatagramPacket(buf.array(), buf.capacity(), address, port);

        segmentHandler.setSeqNum(0);

        segmentHandler.setHeader(9,9, 2, buf);

        packet.setData(buf.array());

        clientSock.send(packet);
        rcvPacket = new DatagramPacket(buf.array(),buf.capacity());
        clientSock.receive(rcvPacket);

        Map<String,String> header = getHeaders();

        ByteBuffer body = ByteBuffer.allocate(255);
        DatagramPacket SYNACK = new DatagramPacket(body.array(), body.capacity(), packet.getAddress(), packet.getPort());

        segmentHandler.setHeader(0,3, (segmentHandler.getSeqNum()+1), body);

        segmentHandler.setHeader(4,7, Integer.parseInt(header.get("seqNumber"))+1, body);

        segmentHandler.setHeader(9,9, 16, body);

        clientSock.send(SYNACK);
    }
    private static String byteToBinaryS(byte[] infos){
        String s1="";
        for(byte B : infos){
            s1 = s1.concat(String.format("%8s", Integer.toBinaryString(B & 0xFF)).replace(' ', '0'));
        }
        System.out.println(Integer.toString(Integer.parseInt(s1, 2)));
        return Integer.toString(Integer.parseInt(s1, 2));
    }
    private   mainClient() throws Exception{
        segmentHandler = new setupSegment();
        createSocket();
    }
    public void send(String pathToFile) throws Exception{}
    public void read(String pathToFile) throws Exception{
        ByteBuffer readingBuffer = ByteBuffer.allocate(20000);
        OutputStream writeToFile = new FileOutputStream("./hello2.txt");

        read(readingBuffer.array());

        writeToFile.write(readingBuffer.array());
        writeToFile.close();
    }

    public void send(byte[] array) throws Exception{}
    public void read(byte[] array) throws Exception{

        rcvPacket = new DatagramPacket(array, array.length);
        clientSock.receive(rcvPacket);


    }

    public void close() throws Exception{}

    public Map<String,String> getHeaders() throws Exception{
        Map<String,String> m = new HashMap<>();
        byte[] info = rcvPacket.getData();
        m.put("seqNumber", byteToBinaryS(Arrays.copyOfRange(info,0,4)));
        m.put("ackNum", byteToBinaryS(Arrays.copyOfRange(info,4,8)));
        m.put("infos", byteToBinaryS(Arrays.copyOfRange(info,8,10)));
        m.put("receiveWindow", byteToBinaryS(Arrays.copyOfRange(info,10,12)));
        m.put("checksum", byteToBinaryS(Arrays.copyOfRange(info,12,14)));
        m.put("urgDataPointer", byteToBinaryS(Arrays.copyOfRange(info,14,16)));
        m.put("options", byteToBinaryS(Arrays.copyOfRange(info,16,20)));
        return m;
    }

    public static void main(String[] args) throws Exception{
        mainClient M= new mainClient();
//        while (true){
            M.read("hello2.txt");
            // read and write must be here
//        }
    }

}
