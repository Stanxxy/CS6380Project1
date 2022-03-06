# CS6380Project1

The repo for the first distributed computing project

- Group members:

  - Sennan Liu
  - Arjun Sridhar
  - Sai Nikhil Voruganti

- Contributions:
  - Sennan Liu
    - Independently contributed the code of Process.java, Type.java, Message.java
    - Partially contributed on the code of WorkerProcess.java(70%)
    - Organized the group on group meeting and scheduling, manage code repo
    - Help on quality assurance of the first version of code by proposing a type of testing cases
    - Help teammates understand the algo logic
  - Arjun Sridhar
    - Partially contributed to WorkerProcess.java
    - Mainly contributed to code of MainThread.java
    - Helped test code
    - Helped debug and fix issues in final version of code
  - Sai Nikhil Voruganti
    - Partially contributed to MainThread.java
    - Termination Process in WorkerProcess.java
    - Came up with several use cases to test
    - Mainly debugged and fixed issues in final version of code

## How to compile

- Java version:

```
java version "1.8.0_172"
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)
```

- command

```
mkdir out
javac -encoding utf-8 -d ./out ./src/*.java
```

## How to use

```
java -cp ./out src.MainThread $SAMPLE_INPUT_FILE
```

## Sample input file

- You could refer to the sample input file "homebrew_input.txt" in that file we give a representation of the following network:
  [pic here]
- On the first line there is an integer "n" indicates the total number of worker process
- On the second line there are n integers indicates the process ID. Doesn't have to be ordered
- On the upcoming n lines, the neighbour of each process should be listed in each line. The integers should be process index not process ID (e.g. we may have process ID: 23 26 57 but if 23 and 26 are neighbours we shoud have 2 at the second line instead of 26)

* see the **homebrew_input.txt**

## Sample output

- The ouput contains four components:
  - The first line is the total number of processes
  - The second line is a list indicates the process IDs
  - THe thrid line contains a 2d list within which each 1d list contains the index of neighbour processes (index start with 1) and padded with 0s
  - The fourth line contains the final result: Leader: xx. xx would be the process ID of leader process

## Design doc

P0 - master Thread

- Creates barrier structure to synchronize message processing, sending, etc.
- Reads in the number of threads, process ids, and adjaceny list from the input file
- Creates n number of threads, where each thread has a unique process id and its neighbors based on the adjaceny list
- Sends begin message to all processes to signify that round has begun
- Waits for all processes to send message that they have finished round
- Once all processes have finished round, sends begin message to all processes to start next round
- Loop over previous 2 steps
- The leader will send a message to the main thread saying it is leader, and the main thread will then exit the loop and output the leader

Process

- Fields:

  - processId - int / string ( Can be a hash UUID )
  - status - boolean \[active/inactive\] ( for running or kill )
  - maxId - int / string
  - neighbors - set ( neighbors )
  - children - set ( children )
  - others - set ( non children )
  - parent - int / string
  - termination - boolean ( for node terminate or not )
  - inbox - LinkedBlockingQueue ( for message receiving )
  - barrier - Barrier ( synchronizer provided externally to sync message processing, message sending and everything )

  * terminatedNeighbor - Set ( The neighbour that is ready to terminate);
  * receivedREJsFrom - Set (buffer for counting acks in each round)
  * receivedACKsFrom - Set (buffer for counting rejs in each round)

- Constructor: (int/string processId, List\<int\> neighbours) -> None
  // Initialize the process and do
- run() -> None
  // the flood procedure for each process, start the outer while loop
  Util functions
- waitToStartRound() -> None
  // wait to start one round. Could start or kill the process
- compareId () -> None
  // compare the current stored id with the discovered maximum id from explore messages. Update parent if necessary
- sendResponse () -> None
  // send acks to parents and rejects to all other neighbours
- explore () -> None
  // send explore messages to all neighbours with its own id and maximum id a process knows

* collect response -> None
  // update the children and siblinig set

- checkTerminate () -> boolean
  // check if terminated
- checkIfLeader () -> boolean
  // check if leader
- run () -> None
  // run the process

Type (enum class):

- XPL - explore ( the message with maxi-id )
- ACK - ack message ( if sender is parent )
- REJ - reject message ( if sender is not parent )
- LDB - broadcast leader to all follower ( if flood finished. _This is originally designed but was finally deprecated_ )
- BGN - begin one round ( sent by master to worker for begin of one round )
- END - end one round ( sent by worker to master for report end of a round )
- TMN - tell other process current process terminated ( confirm message to terminate the flood )
- FIN - kill all process ( sent by master to worker )

Message

Fields:

- type - Type ( specific type of the message )

- senderId - int/string ( the UID of sender process )
- infoId - int/string ( the info ID could be various meaning )
  - parent ID ( when sent terminate to master )
  - max-id ( when doing exploration )

Process:

- putInMessage ( message: Message ) -> None
  // put in a message into the inbox of a process
- broadcast ( processSet: Set, message: Message ) -> None
  // send message to a set of processes

* start -> None // start the thread logic
* run -> None // run as a separate thread

## Comments:
