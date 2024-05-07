package message;

import java.io.Serializable;

public class JoinRoomRequest implements Serializable {
    private String name;
    private String roomName;
    private boolean group;

    public JoinRoomRequest(String name, String roomName) {
        this.name = name;
        this.roomName = roomName;
        this.group = false;
    }


    public String getName() {
        return name;
    }

    public String getRoomName() {
        return roomName;
    }

    public boolean isGroup() {
        return group;
    }
}

