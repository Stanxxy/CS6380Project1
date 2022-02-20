package Project1;

//Importing utility classes
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;

public class Process {
	private int processId; // unique process id
	private boolean status; // status - active or running
	private int maxId; // max id process has seen
	private Set<Integer> neighbors; // set of neighbors for process
	private Set<Integer> children; // children of process for termination detection
	private Set<Integer> others; // non-children
	private int parent; // parent of process
	private boolean termination; // whether to terminate or not
	private BlockingQueue<Message> inbox; // 
	private CyclicBarrier barrier; // sync message processing, sending, etc.
	private Lock lock; 
	
	public Process(int processId, List<Integer> neighbors) {
		this.processId = processId;
		this.status = true; // active
		this.maxId = processId;
		termination = false;
		
	}
}
