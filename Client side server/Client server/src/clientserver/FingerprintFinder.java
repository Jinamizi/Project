package clientserver;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sourcefiles.FingerprintMatcher;
import sourcefiles.FingerprintTemplate;

/**
 *An implementation of finding the location of a fingerprint image from the provided
 * @author DEGUZMAN
 */
public class FingerprintFinder {
    String[] printsLocation;
    FingerprintMatcher matcher;
    String location = "";
    ExecutorService executor ;

    /**
     * Takes the prints location 
     * @param prints 
     */
    public FingerprintFinder(String[] prints) {
        printsLocation = prints;
    }

    String find(BufferedImage image) {
        FingerprintTemplate probe = new FingerprintTemplate().create(loadImage(image));
        matcher = new FingerprintMatcher().index(probe);
        executor = Executors.newCachedThreadPool();
        MatchingThread [] threads = new MatchingThread[printsLocation.length];
        for(int i = 0; i < printsLocation.length; i++){
            threads[i] = new MatchingThread(printsLocation[i]);
            executor.execute(threads[i]);
        }
        executor.shutdown(); // shut down worker threads when their tasks complete
        
        return getLocation();
    }
    
    synchronized private String getLocation(){
        while("".equals(location) && !executor.isTerminated())
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        
        return location;
    }
    
    synchronized private void setLocation(String location){
        this.location = location;
        executor.shutdownNow();
        notifyAll();
    }
    
    private byte[] loadImage(BufferedImage bImage) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", bos);

            byte[] data = bos.toByteArray();
            return data;
        } catch (Exception e) {
        }
        return new byte[0];
    }
    
    class MatchingThread implements Runnable{
        String printToMatch;
        public MatchingThread(String print) {
            this.printToMatch = print;
        }

        @Override
        public void run() {
            try {
                BufferedImage image = ImageIO.read(new File(printToMatch));
                FingerprintTemplate matching = new FingerprintTemplate().create(loadImage(image));
                if (matcher.match(matching) > 40 ) setLocation(printToMatch);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
}
