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

    private String fileDirectory;

    private String fileName;


    public ChatMessage(String senderName, String roomName, ChatMessageType type, ImageIcon userImage) {
        this.senderName = senderName;
        this.roomName = roomName;
        this.type = type;
        this.data = data;
        this.userImage = userImage;
    }


    public ChatMessage(String senderName, ChatMessageType type, ImageIcon userImage) {
        this.senderName = senderName;
        this.type = type;
        this.userImage = userImage;
    }

    public ChatMessage(String senderName, ChatMessageType type, byte[] file, String fileName, String fileDirectory) {
        this.senderName = senderName;
        this.type = type;
        this.file = file;
        this.fileName = fileName;
        this.fileDirectory = fileDirectory;

    }

    public String getSenderName() {
        return senderName;
    }


    public ChatMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public byte[] getFileData() {
        return file;
    }

    public String getFileName(){
        return fileName;
    }


    public ImageIcon getUserImage() {
        return userImage;
    }
}