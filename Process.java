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
	private List<Integer> neighbors; // set of neighbors for process
	private List<Integer> children; // children of process for termination detection
	private List<Integer> others; // non-children
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
		this.parent = 0;
		children = new ArrayList<Integer>();
		
	}

	public int getProcessId() {
		return processId;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public int getMaxId() {
		return maxId;
	}

	public void setMaxId(int maxId) {
		this.maxId = maxId;
	}

	public List<Integer> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Integer> neighbors) {
		this.neighbors = neighbors;
	}

	public List<Integer> getChildren() {
		return children;
	}

	public void setChildren(List<Integer> children) {
		this.children = children;
	}

	public List<Integer> getOthers() {
		return others;
	}

	public void setOthers(List<Integer> others) {
		this.others = others;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public boolean isTermination() {
		return termination;
	}

	public void setTermination(boolean termination) {
		this.termination = termination;
	}
}

