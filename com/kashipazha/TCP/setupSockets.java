package com.kashipazha.TCP;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.Math;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;
import java.util.Timer;

public class setupSockets implements MySocket{
        public Thread thread ;
        private DatagramPacket packet;
        public Queue<DatagramPacket> myPackets = new LinkedList<>();
        //private ByteBuffer senderBuffer = ByteBuffer.allocate(20000000); //20 MB buffer
        private ArrayList<segment> senderBuffer = new ArrayList<>(78125);
        private InetAddress clientIP;
        private int clientPort;
        private setupSegment segmentHandler;
        private boolean imSleep=false;
        private file FileReader;
        private int sendBase;
        private int nextSeqNum;
        private int windowSize;
        private int recentAck;
        private double sampleRTT;
        private double estimatedRTT;
        private double devRTT;
        private double timeoutInterval;

        private void estimatedRTT(alpha) {

            estimatedRTT = (1-alpha) * sampleRTT + alpha * sampleRTT;
        };
        private void devRTT(beta) {
            devRTT = (1-beta) * devRTT + beta * abs(sampleRTT - estimatedRTT);
        };
        private void timeoutInterval() {
            timeoutInterval =  estimatedRTT + 4 * devRTT;
        };

        public InetAddress getIP(){
            return clientIP;
        }
        public int getPort(){
            return clientPort;
        }

        private void updateWindow(int receiveAck) {

            sendBase = (receiveAck > sendBase) ? receiveAck : sendBase;
        }

        private int hasPacketToSend() {

            return ((nextSeqNum <= (sendBase + windowSize)) ? nextSeqNum : -1);
        }

        private String event() {
            String event;
            if(hasPacketToSend() > -1) {
                event = new String("sendPacket");
            }
            //else if timer timeouts
            //event = new String("timeout);
            //else if receiveAck
            //updateWindow()
            return event;
        }

        private void sendPacketsReceiveAcks() {

            String event = event();
            switch (event) {

                case "timeOut": //restart timer
                     break;

                case "newAck":
                    updateWindow(recentAck);
                    break;

                case "sendPacket": //send Packet
                    send(senderBuffer[nextSeqNum].getData());
                    nextSeqNum++;

                default: // do nothing
            }
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

        synchronized void getMyPacket() throws IOException, InterruptedException{
            System.out.println("wait for my packet");

            if(myPackets.size() == 0) {
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

            segmentHandler.setSeqNum(0);
            segmentHandler.setHeader(0, 3,0, body);


            segmentHandler.setHeader(4, 7,Integer.parseInt(header.get("seqNumber"))+1, body);

            segmentHandler.setHeader(9, 9,18, body);


            mainServer.sendPacket(SYNACK);
            getMyPacket();
            header = getHeaders();

            if (header.get("ackNum").equals("1"))
                System.out.println("congrats, connection has been setup successfully");

        }

        public setupSockets(DatagramPacket packet) throws Exception{
            System.out.println("setupSockets constructed");
            //3 way handshake here
            this.packet = packet;
            sendBase = 0;
            nextSeqNum = 0;
            devRTT = 0;
            windowSize = 5;
            estimatedRTT = 0;
            this.segmentHandler = new setupSegment();
            thread = new Thread(new threadHandle() );
            thread.start();

        }

        private void segmentFileInput(){
            while (FileReader.hasSegment()){
                byte[] readedBytes = FileReader.getBytes(240);
                segment builtSegment = segmentHandler.saveTheSegment(readedBytes);
                senderBuffer.add(builtSegment);
            }
        }

        public void send(String pathToFile) throws Exception {
            FileReader.readFile(pathToFile);
            segmentFileInput();
        }

        public void read(String pathToFile) throws Exception {};

        public void send(byte[] array) throws Exception{
            DatagramPacket filePacket = new DatagramPacket(array, array.length, clientIP, clientPort);
            mainServer.sendPacket(filePacket);
        };
        public void read(byte[] array) throws Exception{};
        public void close() throws Exception{};

        public Map<String,String> getHeaders() throws Exception{
            Map<String,String> m = new HashMap<>();
            byte[] info = packet.getData();
            m.put("seqNumber", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,0,4)));
            m.put("ackNum", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,4,8)));
            m.put("infos", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,8,10)));
            m.put("receiveWindow", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,10,12)));
            m.put("checksum", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,12,14)));
            m.put("urgDataPointer", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,14,16)));
            m.put("options", segmentHandler.byteToBinaryS(Arrays.copyOfRange(info,16,20)));
            return m;
        }

        public class threadHandle implements Runnable {
            public void run() {
                try {
                    HandShake();
                    FileReader = new file();
                    send("./hello.txt");
                    while (true){
                        //send and check received packets
                        sendPacketsReceiveAcks();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }


}
