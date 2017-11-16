package com.kashipazha.TCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class mainServer extends MyServerSocket {
    private DatagramSocket mainServerSocket = null;
    private ArrayList<MySocket> handlingConnections = new  ArrayList<MySocket>();

    private static Boolean isBitSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }

    public MySocket accept() throws IOException{
        DatagramPacket packet = new DatagramPacket(new byte[256], 256);
        mainServerSocket.receive(packet);
        byte packetContent[] = packet.getData();
        byte connectionStabHeader[] = Arrays.copyOfRange(packetContent,0,1);
//        String s ="0b" + ("0000000" + Integer.toBinaryString(0xFF & connectionStabHeader[0])).replaceAll(".*(.{8})$", "$1");
        System.out.println(Integer.toBinaryString(connectionStabHeader[0] & 0xFF));

        String setConnection = Integer.toBinaryString(connectionStabHeader[0] & 0xFF);
        if(setConnection.equals("10")){
            System.out.println("packet is for setting up a connection");
            return new setupSockets(packet);
        }
        return null;
    }
    public mainServer() throws Exception{
        super(3000);
        try {
            mainServerSocket = new DatagramSocket(1339);
//            InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("hooman.com"), 1339);
//            mainServerSocket.bind(address);
            System.out.println("server listening on port: "+ mainServerSocket.getLocalPort() +" IP: "+ mainServerSocket.getLocalAddress());
            boolean run = true;
            while(true){
                MySocket S = accept();
                handlingConnections.add(S);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    public class setupSockets implements MySocket{
        private Thread thread ;
        private DatagramPacket packet;

        public setupSockets(DatagramPacket packet){
            System.out.println("setupSockets constructed");
            thread = new threadHandle();
            this.packet = packet;
            thread.run();
        }

        public void send(String pathToFile) throws Exception {}
        public void read(String pathToFile) throws Exception {};

        public void send(byte[] array) throws Exception{};
        public void read(byte[] array) throws Exception{};

        public void close() throws Exception{};

        public Map<String,String> getHeaders() throws Exception{
            try {

                Map<String,String> map=new HashMap<String,String>();
                

            } catch (Exception e) {


            }
        }

        public class threadHandle extends Thread{
            public void run(){
                System.out.println("thread created");
            }
        }
    }
    public static void main(String[] args) throws Exception{
        mainServer Server = new mainServer();

    }
}
