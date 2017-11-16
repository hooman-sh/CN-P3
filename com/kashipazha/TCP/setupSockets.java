package com.kashipazha.TCP;

import java.net.DatagramPacket;
import java.util.Map;

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

        public Map<String,String> getHeaders() throws Exception{return null;}

        public class threadHandle extends Thread{
            public void run(){
                System.out.println("thread created");
            }
        }
}
