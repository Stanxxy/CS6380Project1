# CS6380Project1

The repo for the first distributed computing project

## Design doc

P0 - master Thread

- Initialize
- Start round
- End round
- Read from input file
- Print Leader

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
  - inbox - BlockingQueue ( for message receiving )
  - barrier - Barrier ( synchronizer provided externally to sync message processing, message sending and everything )

  * round_lock - Lock ( )

- Constructor: (int/string processId, List\<int\> neighbours) -> None
  // Initialize the process and do
- run() -> None
  // the flood procedure for each process, start the outer while loop
  Util functions
- waitToStart() -> None
  // wait to start one round by
- Comparison ( mailbox: Set ) -> int/string
  // compare the current stored id with the discovered maximum id from explore messages. Also return the greatest id
- setResponse () -> None
  // response to all neighbours with ack or rej
- explore () -> None ( messagesReceived )
  // send explore messages to all neighbours
- checkIfTerminate () -> boolean
  // check if terminated
- checkIfLeader () -> boolean
  // check if leader
- setUpLeader () -> None
  // create or receive the leader message and pass it to children ( if the worker could ). for the leader it would simply pass the message to children and only receive the start command from master thread.

Message

Fields:

- Type string ( specific type of the message )
  - XPL - explore ( the message with maxi-id )
  - ACK - ack message ( if sender is parent )
  - REJ - reject message ( if sender is not parent )
  - LDB - broadcast leader to all follower ( if flood finished )
  - BGN - begin one round ( sent by master to worker for begin of one round )
  - END - end one round ( sent by worker to master for report end of a round )
  - TMN - tell other process current process terminated ( confirm message to terminate the flood )
  - FIN - kill all process ( sent by master to worker )
- senderId - int/string ( the UID of sender process )
- infoId - int/string ( the info ID could be various meaning )
  - parent ID ( when sent terminate to master )
  - max-id ( when doing exploration )

Utils

- sendMessage ( process: Process, message: Message ) -> None
  // send single message
- broadcastMessage ( receivers: Set, message: Message ) -> None
  // send message to a set of processes
- constructGraph (List\<List\<int\>\>) -> Map
  // create a map represent the

## Comments:

- Sennan:
  - In the version of Sharayu he keeps the round value in each process while I think we don't have to do that
  * We could add one round of leader broad casting
