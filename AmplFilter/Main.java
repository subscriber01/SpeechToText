package com.roboy;

import java.io.*;
import java.math.BigInteger;

public class Main
{
    public static void main(String[] args)
    {
        int i = 0, j = 0, n = 0;
        int bitsPerSample = 0;
        long subchunk2Size = 0;
        BigInteger threshold = new BigInteger("1000000000000000");
        try( FileInputStream instream=new FileInputStream("C://Users//Bratik//Desktop//Voice_006.wav");
            FileOutputStream outstream=new FileOutputStream("C://Users//Bratik//Desktop//Voice_006-1.wav") )
        {
            byte[] buffer = new byte[instream.available()];
            instream.read(buffer, 0, buffer.length);
            bitsPerSample = buffer[34]<0 ? (int)(256+buffer[34]) : (int)buffer[34];
            bitsPerSample = (int)(bitsPerSample << 8);
            bitsPerSample += buffer[35]<0 ? (int)(256+buffer[35]) : (int)buffer[35];
            BigInteger smpl_seq = new BigInteger("0");
            for (i=40; i<43; i++)
            {
                subchunk2Size += buffer[i]<0 ? (long)(256+buffer[i]) : (long)buffer[i];
                //System.out.println((short)(256+buffer[i]));
                subchunk2Size = (long)(subchunk2Size << 8);
                //System.out.println(Long.toBinaryString(subchunk2Size));
            }
            subchunk2Size += buffer[i]<0 ? (long)(256+buffer[i]) : (long)buffer[i];;//i == 43
            n = (int)(subchunk2Size*8/bitsPerSample);

            //System.out.println(subchunk2Size);
            //System.out.println(bitsPerSample);
            //System.out.println(subchunk2Size*8/bitsPerSample);
                System.out.println(n);
            for (i=44; i<n; )
            {
                System.out.println(i);
                //sample transformation
                for (j=0; j<bitsPerSample/8; j++)
                {
                    smpl_seq = smpl_seq.add( BigInteger.valueOf(buffer[i]<0 ? (short)(256+buffer[i]) : (short)buffer[i]) );
                    smpl_seq = smpl_seq.shiftLeft(8);
                }
                smpl_seq = smpl_seq.add( BigInteger.valueOf(buffer[i]<0 ? (short)(256+buffer[i]) : (short)buffer[i]) );
                if (smpl_seq.compareTo(threshold) <= 0)
                    smpl_seq = BigInteger.ZERO;
                byte [] smpl_arr = new byte[bitsPerSample/8];
                smpl_arr = smpl_seq.toByteArray();
                for (j=0; j<bitsPerSample/8; j++)
                {
                    buffer[i+j] = smpl_arr[j];
                }
                //-----------------------------------
                i += bitsPerSample/8;
            }
            outstream.write(buffer, 0, buffer.length);/**/
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}
