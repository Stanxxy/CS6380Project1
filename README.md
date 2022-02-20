# CS6380Project1

The repo for the first distributed computing project

## Design doc

P0 - Main Thread

- Initialize
- Start round
- End round
- Read from input file
- Print Leader

Process

- Fields:

  - processId - int / string ( Can be a hash UUID )
  - status - boolean \[active/inactive\] ( for running or kill )
  - round - int ## do we really need to care about round? I think its just for testing purpose
  - maxId - int / string
  - neighbors - set ( neighbours )
  - children - set ( children )
  - others - set ( non children )
  - parent - int / string
  - termination - boolean ( for node terminate or not )
  - inbox - BlockingQueue ( for message receiving )
  - barrier - Barrier ( synchronizer provided externally to sync message processing, message sending and everything )

  * round_lock - Lock ( )

- Constructor: (int/string processId, List\<int\> neighbours) -> None
  // Initialize the process and do
- run() -> None ( start the outer while loop )
  // the flood procedure for each process
  Util functions
- sendMessage ( process: Process, message: Message ) -> None
  // send single message
- broadcastMessage ( receivers: Set, message: Message ) -> None
  // send message to a set of processes
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

Message

Fields:

- Type string ( specific type of the message )
  - XPL - explore ( the message with maxi-id )
  - ACK - ack message ( if sender is parent )
  - REJ - reject message ( if sender is not parent )
  - LDB - broadcast leader to all follower ( if flood finished )
  - BGN - begin one round ( sent by main to worker for begin of one round )
  - END - end one round ( sent by worker to main for report end of a round )
  - TMN - tell other process current process terminated ( confirm message to terminate the flood )
  - FIN - kill all process ( sent by main to worker )
- senderId - int/string ( the UID of sender process )
- infoId - int/string ( the info ID could be various meaning )
  - parent ID ( when sent terminate to master )
  - max-id ( when doing exploration )

## Comments:

- Sennan:
  - In the version of Sharayu he keeps the round value in each process while I think we don't have to do that
  * We could add one round of leader broad casting
