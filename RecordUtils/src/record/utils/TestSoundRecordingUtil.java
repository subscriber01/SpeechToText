package record.utils;
 
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
 
/**
 * A program to record microphone input as a wave file
 * adapted from www.codejava.net
 *
 */
public class TestSoundRecordingUtil {
	// the duration of the recording in ms
    private static final int RECORD_TIME = 3000;   
    
    public static void main(String[] args) throws Exception {
    	
    	// destination file
    	File waveFile = new File("C:/Users/Roboy/Documents/TestSound/Record.wav"); 
    	
        final SoundRecordingUtil recorder = new SoundRecordingUtil();
         
        // create a separate thread for recording
        Thread recordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Start recording...");
                    recorder.start();
                } catch (LineUnavailableException ex) {
                    ex.printStackTrace();
                    System.exit(-1);
                }              
            }
        });
         
        recordThread.start();
         
        try {
            Thread.sleep(RECORD_TIME);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
         
        try {
            recorder.stop();
            recorder.save(waveFile);
            System.out.println("STOPPED");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        System.out.println("DONE");
    }
 
}