package message;

import java.io.Serializable;

public class JoinRoomResponse implements Serializable {
    private String response;
    private boolean error;





    public String getResponse() {
        return response;
    }

    public boolean isError() {
        return error;
    }
}
