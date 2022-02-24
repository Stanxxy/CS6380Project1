package src;

public class Message {
	private Type messageType; // message type
	private int senderId; // sender process
	private int infoId;
	
	public Message(int senderId, Type messageType) {
		this.messageType = messageType;
		this.senderId = senderId;
		this.infoId = -1;
	}

	public Message(int senderId, int infoId, Type messageType) {
		this.messageType = messageType;
		this.senderId = senderId;
		this.infoId = infoId;
	}

	public Type getMessageType() {
		return messageType;
	}

	public int getSenderId() {
		return senderId;
	}

	public int getInfoId() {
		return infoId;
	}
}



