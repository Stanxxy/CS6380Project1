package src;

//Importing utility classes
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerProcess extends Process implements Runnable{
	private int processId; // unique process id
	private boolean status; // status - active or running
	private int maxId; // max id process has seen
	private boolean isLeader;
	private boolean isReadyToTerminate = false;

	private HashMap<Integer, Process> neighbors; // set of neighbors for process
	private Set<Integer> children; // children of process for termination detection
	private Set<Integer> others; // non-children
	private Set<Integer> terminatedNeighbor; // non-children

	private int parent; // parent of process
	private Process master; // reference of master process
	private CyclicBarrier barrier; // sync message processing, sending, etc.

	// temporary space, empty after each round
	private Set<Integer> receivedREJsFrom = new HashSet<>();
	private Set<Integer> receivedACKsFrom = new HashSet<>();

	public WorkerProcess(int processId) {
		this.processId = processId;
		this.inbox = new LinkedBlockingQueue<Message>();
		
	}
	
	public void setBarrier(CyclicBarrier barrier) {
		this.barrier = barrier;
	}
	
	public void setWorkerProcess(Process master, HashMap<Integer, Process> neighbors, CyclicBarrier barrier) {
		this.status = true; // active
		this.maxId = processId;
		this.parent = -1;
		this.master = master;
		this.isLeader = false;
		this.inbox = new LinkedBlockingQueue<Message>();
		this.barrier = barrier;

		this.neighbors = neighbors;
		this.children = new HashSet<>();
		this.others = new HashSet<>();
		this.terminatedNeighbor = new HashSet<>();
	}

	public int getProcessId() {
		return processId;
	}

	public int getParent() {
		return this.parent;
	}

	private void waitToStartRound() throws InterruptedException {
		boolean flag = false;
		while(!flag){
			Message msg = this.inbox.take();
			if(msg.getMessageType().equals(Type.BGN)){
				// start current round
				flag = true;
			} else if(msg.getMessageType().equals(Type.FIN)){
				// start self destruction
				this.status = false;
				
				this.broadcast(this.neighbors.values(), msg);
				flag = true;
			} else {
				this.inbox.offer(msg);
			}
		}
	}

	private void compareId() throws InterruptedException {
		// handle message bc xpl and tmn would be handled at the same time
		int numOfMessages = this.inbox.size();
		for(int i=0;i<numOfMessages; i++) {
			Message msg = this.inbox.take();
			switch (msg.getMessageType()) {
				case XPL:
					// this time the info id is
					if (msg.getInfoId() > this.maxId) {
						// update maxID
						this.maxId = msg.getInfoId();
						this.parent = msg.getSenderId();
					}
					break;
				case TMN:
					// this time the info id is
					this.terminatedNeighbor.add(msg.getSenderId());
					break;
				default:
					this.inbox.offer(msg);
					break;
			}
		}
	}

	private void sendResponse(){
		for(Map.Entry<Integer, Process> pair : neighbors.entrySet()){
			if(pair.getKey().equals(this.parent)){
				Message msg = new Message(this.processId, Type.ACK);
				pair.getValue().putInMessage(msg);
			} else {
				Message msg = new Message(this.processId, this.maxId, Type.REJ);
				pair.getValue().putInMessage(msg);
			}
		}
	}

	private void collectResponse() throws InterruptedException {
		int numOfMessages = this.inbox.size();
		for(int i=0;i<numOfMessages; i++){
			Message msg = this.inbox.take();
			switch (msg.getMessageType()){
				case ACK:
					// this time the info id is
					this.children.add(msg.getSenderId());
					this.others.remove(msg.getSenderId());
					this.receivedACKsFrom.add(msg.getSenderId());
					break;
				case REJ:
					this.others.add(msg.getSenderId());
					this.children.remove(msg.getSenderId());
					if (msg.getInfoId() != this.maxId){
						continue;
					}
					this.receivedREJsFrom.add(msg.getSenderId());

					this.isLeader = false;
					break;
				default:
					this.inbox.offer(msg);
					break;
			}
		}
	}

	private void explore(){
		Message msg = new Message(this.processId, this.maxId, Type.XPL);
		this.broadcast(this.neighbors.values(), msg);
	}

	private void checkLeader(){
		if(this.parent == -1 && this.terminatedNeighbor.size() == this.neighbors.size()){
			this.isLeader = true;
		}
	}

	private void checkTerminate(){
		// check if current thread should terminate
		boolean allChildrenTerminated = false;
		boolean receivedNACKFromOthers = false;

		// check if a process has received notifications of completion from all its children
		Set<Integer> temp = new HashSet<>(this.children);
		temp.removeAll(this.terminatedNeighbor);
		if (temp.isEmpty()) {
			//System.out.println(this.processId + " " + this.terminatedNeighbor);
			// all children have terminated
			allChildrenTerminated = true;
		}

		// check if a process has received REJs from other neighbors
		// the process shouldn't expect a REJ from the parent to terminate, it may have happened in an earlier round
		temp = new HashSet<>(this.others);
		temp.removeAll(receivedREJsFrom);
		if (temp.isEmpty()){// && !others.isEmpty()) {
			receivedNACKFromOthers = true;
		}

		if (this.parent != -1 && allChildrenTerminated && receivedNACKFromOthers) {
			this.isReadyToTerminate = true;
		} else isReadyToTerminate = this.parent == -1 && allChildrenTerminated && isLeader;
	}

	@Override
	public void run(){
		while(status){
			try {
				waitToStartRound();
				if(!this.status){
					break;
				}
				explore();
				this.barrier.await();
				compareId();
				//System.out.println("Process: " + this.processId + this.terminatedNeighbor);
				sendResponse();
				this.barrier.await();
				collectResponse();

				checkLeader();
				checkTerminate();
				
				this.receivedREJsFrom.clear();
				this.receivedACKsFrom.clear();
				
				if(this.isLeader && this.isReadyToTerminate) {
					Message msg = new Message(this.processId, Type.LDB);
					this.master.putInMessage(msg);
					broadcast(this.neighbors.values(), new Message(this.processId, Type.FIN));
				}

				if(this.isReadyToTerminate){
					Message msg = new Message(this.processId, this.parent, Type.TMN);
					this.master.putInMessage(msg);
					broadcast(this.neighbors.values(), msg);
				} else{
					Message msg = new Message(this.processId, Type.END);
					this.master.putInMessage(msg);
				}
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}



	@Override
	public String toString() {
		return "Process{" +
				"uid=" + this.processId +
				'}';
	}
}
