package clientserver;

//use future
//use threads

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sourcefiles.FingerprintMatcher;
import sourcefiles.FingerprintTemplate;

/**
 * An implementation of finding a minutia
 *
 * @author DEGUZMAN
 */
public class MinutiaeFinder {
    Map<String, String> minutiae;
    FingerprintMatcher matcher;
    String id = "b";
    ExecutorService executor;

    /**
     * Takes the prints location
     *
     * @param prints
     */
    public MinutiaeFinder(Map<String, String> prints) {
        minutiae = prints;
    }

    /**
     * used to check if a minutia exist in the given map collection
     * @param minutia the minutia to check
     * @return the id of the minutia
     */
    String find(String minutia) {
        FingerprintTemplate probe = new FingerprintTemplate().deserialize(minutia);
        matcher = new FingerprintMatcher().index(probe);
        //executor = Executors.newCachedThreadPool();
        
        //minutiae.keySet().forEach((item) -> executor.execute(new MatchingThread(item,minutiae.get(item))));

        //executor.shutdown(); // shut down worker threads when their tasks complete
        for(String item : minutiae.keySet()){
            FingerprintTemplate matching = new FingerprintTemplate().deserialize(minutiae.get(item));
            if (matcher.match(matching) > 40)
                return item;
        }
        
        return "";
    }

    synchronized private String getID() {
        while ("b".equals(id) && !executor.isTerminated()) //while id has not been set yet and the executor has not been terminated or shutdown, wait
        {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }

        return id;
    }

    synchronized private void setID(String idFound) {
        this.id = idFound;
        executor.shutdownNow();
        notifyAll(); //wake up thread to get location
    }

    private class MatchingThread implements Runnable {
        String minutiaeToMatch;
        String id;

        public MatchingThread(String id,String minutia) {
            this.minutiaeToMatch = minutia;
        }

        @Override
        public void run() {
            FingerprintTemplate matching = new FingerprintTemplate().deserialize(minutiaeToMatch);
            if (matcher.match(matching) > 40) {
                setID(id);
            }
        }

    }
}
