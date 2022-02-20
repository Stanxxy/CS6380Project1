package Project1;

public class Message {
	private String messageType; // message type
	private int senderId; // sender process
	private int infoId;
	
	public Message() {
		this.messageType = "";
		this.senderId = 0;
		this.infoId = 0;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}
}

