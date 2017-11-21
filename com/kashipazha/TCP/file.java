package com.kashipazha.TCP;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

public class file {
    private ByteBuffer FileBuffer = ByteBuffer.allocate(20000000);


    public void readFile(String pathToFile){
        File file = new File(pathToFile);
        FileInputStream fileInputStream = null;
        byte[] bFile = new byte[(int) file.length()];
        try
        {
            //convert com.kashipazha.TCP.file into array of bytes
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            FileBuffer.put(bFile);
            fileInputStream.close();
            FileBuffer.flip();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean hasSegment(){
        return (FileBuffer.limit() != FileBuffer.position());
    }
    public byte[] getBytes(int size){
        if(FileBuffer.position()+ size <= FileBuffer.limit()){
            byte[] seg = new byte[size];
            FileBuffer.get(seg);
            return seg;
        }
        else{
            byte[] seg = new byte[FileBuffer.limit() - FileBuffer.position() ];
            FileBuffer.get(seg);
            return seg;
        }
    }

}
