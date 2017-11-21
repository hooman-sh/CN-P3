package com.kashipazha.TCP;

import java.net.DatagramPacket;
import java.util.Arrays;

public class segment {
    private DatagramPacket packet;
    private boolean isAcked=false;
    public boolean isAcked() { return isAcked; }
    public segment(DatagramPacket packet){
        this.packet = packet;
    }
    public int getSeq(){

        return Integer.getInteger(setupSegment.byteToBinaryS(Arrays.copyOfRange(packet.getData(),0,3)));
    }
    public void savePacket(DatagramPacket packet) {

        this.packet = packet;
    }
    public byte[] getData() {
        return packet.getData();
    }

}
