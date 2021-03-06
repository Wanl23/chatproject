package endpoints;


import coders.MessageDecoder;
import coders.MessageEncoder;
import entities.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@ServerEndpoint(value = "/chat/{user}", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatEndpoint {

    private Session session = null;
    private String username = "anonimus";
    private static List<Session> sessionList = new LinkedList<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("user") String username) {
        this.session = session;
        this.username = username;
        sessionList.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessionList.remove(this.session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(Session session, Message msg) {
        msg.setName(this.username);
        sessionList.forEach(s-> {
            if (s == this.session) return;
            try {
                s.getBasicRemote().sendObject(msg);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        });
    }
}
