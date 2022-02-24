package src;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public abstract class Process extends Thread {

    protected BlockingQueue<Message> inbox;

    public void putInMessage(Message m){
        this.inbox.offer(m);
    }

    public void broadcast(Collection<? extends Process> processSet, Message m){
        for(Process p : processSet){
            p.putInMessage(m);
        }
    }

    @Override
    public void start() {
        super.start();
    }


    public abstract void run();
}


