package message;

import javax.swing.*;
import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String senderName;
    private String roomName;
    private String message;
    private byte[] file;
    private ChatMessageType type;
    private ImageIcon userImage;
    private Object data;

    public ChatMessage(String senderName, String roomName, ChatMessageType type, ImageIcon userImage) {
        this.senderName = senderName;
        this.roomName = roomName;
        this.type = type;
        this.data = data;
        this.userImage = userImage;
    }

    public ChatMessage(String senderName, String roomName, ChatMessageType type, String message, ImageIcon userImage) {
        this(senderName, roomName, type, userImage);
        this.message = message;
    }

    public ChatMessage(String senderName, String roomName, ChatMessageType type, byte[] file, ImageIcon userImage) {
        this(senderName, roomName, type, userImage);
        this.file = file;
    }

    public ChatMessage(String senderName, String roomName, ChatMessageType type, String message, byte[] file, ImageIcon userImage) {
        this(senderName, roomName, type, userImage);
        this.message = message;
        this.file = file;
    }

    public ChatMessage(String senderName, ChatMessageType type, ImageIcon userImage) {
        this.senderName = senderName;
        this.type = type;
        this.userImage = userImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRoomName() {
        return roomName;
    }

    public ChatMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getFile() {
        return file;
    }

    public ImageIcon getUserImage() {
        return userImage;
    }
}