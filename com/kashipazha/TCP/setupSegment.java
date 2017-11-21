package com.kashipazha.TCP;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class setupSegment {
    //we can have Map
    private int seqNum;

    public void setSeqNum(int num){
        seqNum = num;
    }
    public int getSeqNum(){
        return seqNum;
    }
    public void setHeader(int begin, int end, int data, ByteBuffer b) {

        long temp = data;

        long remain;

        while (temp / 256 >= 1) {
            if (end >= begin) {

                remain = temp % 256;
                temp = temp / 256;
                b.put(end, Byte.parseByte(Long.toString(remain)));
                end--;
            }
        }
        b.put(end, Byte.parseByte(Long.toString(temp)));
    }

    public static String byteToBinaryS(byte[] infos){
        String s1="";
        for(byte B : infos){
            s1 = s1.concat(String.format("%8s", Integer.toBinaryString(B & 0xFF)).replace(' ', '0'));
        }
        return Integer.toString(Integer.parseInt(s1, 2));
    }

    public segment saveTheSegment(byte[] readedBytes){
        ByteBuffer MySegment = ByteBuffer.allocate(256);
        this.setHeader(0,3,seqNum, MySegment);
        this.setHeader(9,9,0, MySegment);
        MySegment.position(16);
        MySegment.put(readedBytes);
        MySegment.flip();
        segment constructedSeg = new segment(new DatagramPacket(MySegment.array(), readedBytes.length+16));
        seqNum += readedBytes.length +16;
        return constructedSeg;
    }


}