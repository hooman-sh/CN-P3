package com.kashipazha.TCP;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class setupSockets implements MySocket{
        private Thread thread ;
        private DatagramPacket packet;
        private ByteBuffer senderBuffer = ByteBuffer.allocate(20000000); //20 MB buffer
        private int windowSize = 20;
        private InetAddress clientIP;
        private int clientPort;
        private int seqNum;


        private static String byteToBinaryS(byte[] infos){
            String s1="";
            for(byte B : infos){
                s1 = s1.concat(String.format("%8s", Integer.toBinaryString(B & 0xFF)).replace(' ', '0'));
            }
            return Integer.toString(Integer.parseInt(s1, 2));
        }

        private void getMyPacket(DatagramPacket p) throws IOException{
            mainServer.getPacket(p);
            if(p.getAddress() == clientIP && p.getPort() == clientPort)
                packet = p;

        }

        private boolean HandShake() throws Exception{
            Map<String,String> header = getHeaders();
            clientIP = packet.getAddress();
            clientPort = packet.getPort();
            for (String name: header.keySet()){
                String value = header.get(name);
                System.out.println(name + " " + value);
            }
            ByteBuffer body = ByteBuffer.allocate(255);
            DatagramPacket SYNACK = new DatagramPacket(body.array(), body.capacity(), packet.getAddress(), packet.getPort());
            seqNum = 32;

            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte(Integer.toString(seqNum))); //set seq num
            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte(Integer.toString(Integer.parseInt(header.get("seqNumber"))+1))); //set acknum
            body.put(Byte.parseByte("0"));
            body.put(Byte.parseByte("18"));

            mainServer.sendPacket(SYNACK);
            getMyPacket(packet);
            header = getHeaders();


            if (byteToBinaryS(Arrays.copyOfRange(packet.getData(),4,8)).equals("33"))
                System.out.println("congrats, connection has been setup successfully");

            return true;
        }
        public setupSockets(DatagramPacket packet) throws Exception{
            System.out.println("setupSockets constructed");
            //3 way handshake here
            this.packet = packet;
            if(HandShake()) {
                thread = new threadHandle();
                thread.run();
            }
        }


        public void send(String pathToFile) throws Exception {}
        public void read(String pathToFile) throws Exception {};

        public void send(byte[] array) throws Exception{};
        public void read(byte[] array) throws Exception{};

        public void close() throws Exception{};

        public Map<String,String> getHeaders() throws Exception{
            Map<String,String> m = new HashMap<>();
            byte[] info = packet.getData();
            m.put("seqNumber", byteToBinaryS(Arrays.copyOfRange(info,0,4)));
            m.put("ackNum", byteToBinaryS(Arrays.copyOfRange(info,4,8)));
            m.put("infos", byteToBinaryS(Arrays.copyOfRange(info,8,10)));
            m.put("receiveWindow", byteToBinaryS(Arrays.copyOfRange(info,10,12)));
            m.put("checksum", byteToBinaryS(Arrays.copyOfRange(info,12,14)));
            m.put("urgDataPointer", byteToBinaryS(Arrays.copyOfRange(info,14,16)));
            m.put("options", byteToBinaryS(Arrays.copyOfRange(info,16,20)));
            return m;
        }

        public class threadHandle extends Thread{
            public void run(){
                System.out.println("thread created");
            }
        }
}
