package com.kashipazha.TCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class mainServer extends MyServerSocket {
    private static DatagramSocket mainServerSocket = null;
    private ArrayList<setupSockets> handlingConnections = new  ArrayList<setupSockets>();

    private static Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }

    public static void getPacket(DatagramPacket p) throws IOException{
        mainServerSocket.receive(p);
    }

    public static void sendPacket(DatagramPacket p)throws IOException{
        mainServerSocket.send(p);
    }
    private String checkPacket(DatagramPacket packet){
        Map<String, String> headers= new HashMap<String, String>();
        byte[]  data = packet.getData();
        headers.put("srcIP",packet.getAddress().toString());
        headers.put("srcPort", Integer.toString(packet.getPort()));
        String s1 = String.format("%8s", Integer.toBinaryString(data[9] & 0xFF)).replace(' ', '0');
        headers.put("ConnectionStab", s1);
        if(headers.get("ConnectionStab").equals("00000010")){
            return "newConnection";
        }
        else
            return "exist";
    }

    private MySocket retransmit(DatagramPacket packet){
        for(setupSockets s: handlingConnections){
            if(s.getPort() == packet.getPort() && s.getIP() == s.getIP()){
                s.myPackets.add(packet);
                System.out.println("packet retransmitted");
                s.wakeMeUpForIO();
                return s;
            }
        }
        return null;
    }

    public MySocket accept() throws Exception{
        DatagramPacket packet = new DatagramPacket(new byte[256], 256);
        mainServerSocket.receive(packet);
        MySocket m=null;
            switch (checkPacket(packet)){
                case "newConnection":{
                    setupSockets s  = new setupSockets(packet);
                    handlingConnections.add(s);
                    m=s;
                    break;
                }case "exist":{
                    m=retransmit(packet);
                    break;
                }
                default:;
            }
            return m;
    }
    public mainServer() throws Exception{
        super(1339);
        try {
            mainServerSocket = new DatagramSocket(1339);
//            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("hooman.com"), 1339);
//            mainServerSocket.bind(address);
            System.out.println("server listening on port: "+ mainServerSocket.getLocalPort() +" IP: "+ mainServerSocket.getLocalAddress());
            boolean run = true;
            while(true){
                MySocket S = accept();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) throws Exception{
        mainServer Server = new mainServer();

    }
}
