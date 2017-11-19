package com.kashipazha.TCP;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class setupSockets implements MySocket{
        public Thread thread ;
        private DatagramPacket packet;
        public Queue<DatagramPacket> myPackets = new LinkedList<>();
        private ByteBuffer senderBuffer = ByteBuffer.allocate(20000000); //20 MB buffer
        private int windowSize = 20;
        private InetAddress clientIP;
        private int clientPort;
        private int seqNum;
        private boolean imSleep=false;

        public InetAddress getIP(){
            return clientIP;
        }
        public int getPort(){
            return clientPort;
        }


        private static String byteToBinaryS(byte[] infos){
            String s1="";
            for(byte B : infos){
                s1 = s1.concat(String.format("%8s", Integer.toBinaryString(B & 0xFF)).replace(' ', '0'));
            }
            return Integer.toString(Integer.parseInt(s1, 2));
        }

        synchronized void wakeMeUpForIO(){
            if (imSleep)
                notify();
            imSleep = false;
        }
        synchronized void sleepMeForIO() throws InterruptedException{
            imSleep = true;
            wait();

        }

        public static void setHeader(int begin, int end, int data, ByteBuffer b) {

            int temp = data;
            int remain;

            while (temp / 256 >= 1) {
                if (end >= begin) {

                    remain = temp % 256;
                    temp = temp / 256;
                    b.put(end, Byte.parseByte(Integer.toString(remain)));
                    end--;
                }
            }
            b.put(end, Byte.parseByte(Integer.toString(temp)));
        }


        synchronized void getMyPacket() throws IOException, InterruptedException{
            System.out.println("wait for my packet");

            if(myPackets.size() == 0){
                sleepMeForIO();
            }

            packet = myPackets.remove();

        }

        private void HandShake() throws Exception{
            System.out.println("enter the HandShake");
            Map<String,String> header = getHeaders();
            clientIP = packet.getAddress();
            clientPort = packet.getPort();
            ByteBuffer body = ByteBuffer.allocate(255);
            DatagramPacket SYNACK = new DatagramPacket(body.array(), body.capacity(), packet.getAddress(), packet.getPort());
            seqNum = 32;

            setHeader(0, 3,32, body);


            setHeader(4, 7,Integer.parseInt(header.get("seqNumber"))+1, body);

            setHeader(9, 9,18, body);


            mainServer.sendPacket(SYNACK);
            getMyPacket();
            header = getHeaders();

            if (header.get("ackNum").equals("33"))
                System.out.println("congrats, connection has been setup successfully");

        }
        public setupSockets(DatagramPacket packet) throws Exception{
            System.out.println("setupSockets constructed");
            //3 way handshake here
            this.packet = packet;
            thread = new Thread(new threadHandle() );
            thread.start();

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

        public class threadHandle implements Runnable {
            public void run() {
                try {
                    HandShake();
                    while (true){
                        //read and write must be here
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
}
