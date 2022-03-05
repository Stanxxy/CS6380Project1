package src;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MainThread {

	public static void main(String[] args) {
		String input = args[0];
		File input_file = new File(input);
		try {
			BufferedReader r = new BufferedReader(new FileReader(input_file));
			int n = Integer.parseInt(r.readLine()); // get number of processes
			final CyclicBarrier barrier = new CyclicBarrier(n);
			
			HashMap<Integer, Integer> pid = new HashMap<>();
			 
			int[] process_ids = new int[n]; // process ids
			int[][] graph = new int[n][n]; // adjacency graph
			
			String[] ids = r.readLine().split(" "); // split by space to get process id
			
			for(int i = 0; i < ids.length; i++) {
				process_ids[i] = Integer.parseInt(ids[i]);
			}
			
			for(int i = 0; i < ids.length; i++) {
				String [] neighbors = r.readLine().split(" "); // get neighbors for each process
				
				for(int j = 0; j < neighbors.length; j++) {
					graph[i][j] = Integer.parseInt(neighbors[j]);
				}
			}
			
			System.out.println(n);
			System.out.println(Arrays.toString(process_ids));
			System.out.println(Arrays.deepToString(graph));
			
			WorkerProcess[] processes = new WorkerProcess[n];
			
			WorkerProcess master = new WorkerProcess(-100);
			
			for(int i = 0; i < processes.length; i++) {
				processes[i] = new WorkerProcess(process_ids[i]);
			}
			
			for (int i = 0; i < processes.length; i++) {
				HashMap<Integer, Process> neighbors = new HashMap<>();
				WorkerProcess p = processes[i];
			
				int[] adj = graph[i];
				
				for(int neighbor: adj) {
					if(neighbor != 0) {
						neighbors.put(process_ids[neighbor - 1], processes[neighbor - 1]);
					}
				}
	
				p.setWorkerProcess(master, neighbors, barrier);
				p.putInMessage(new Message(master.getProcessId(), Type.BGN));
			}
			
			for(int i = 0; i < processes.length; i++) {
				pid.put(processes[i].getProcessId(), i);
			}
			
			Thread[] threads = new Thread[n];
			
			for(int i = 0; i < threads.length; i++) {
				Thread thread = new Thread(processes[i]);

				threads[i] = thread;
				thread.start();
			}
			int leader = -1;
			boolean run = true;
			while(run) {
				int num_leaders = 0;
				int numOfMessages = master.inbox.size();
				
				for(int i=0;i<numOfMessages; i++){
					Message m = master.inbox.take();
					int index = pid.get(m.getSenderId());
					WorkerProcess p = processes[index];
					if(m.getMessageType().equals(Type.END) || m.getMessageType().equals(Type.TMN)) {
						//System.out.println("End: " + m.getSenderId());
						p.putInMessage(new Message(master.getProcessId(), Type.BGN));
						//barrier.await();
					}
				}
				
				for(WorkerProcess p: processes) {
					if(p.getParent() == -1) {
						num_leaders++;
					}
				}
				
				if(num_leaders == 1) {
					for(WorkerProcess p: processes) {
						if(p.getParent() == -1) {
							leader = p.getProcessId();
							run = false;
							break;
						}
					}
					
					Message fin = new Message(leader, Type.FIN); // leader tells everyone to stop
					for(WorkerProcess p: processes) {
						p.putInMessage(fin);
					}
					
				}
				
			}
			
			//for(WorkerProcess p: processes) {
					//System.out.println("Process Id " + p.getProcessId() + " neighbors " + p.getTerminatedNeighbors());
				//}
			//}
			System.out.println("Leader: " + leader);
			
			//System.out.println("main done");
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}


