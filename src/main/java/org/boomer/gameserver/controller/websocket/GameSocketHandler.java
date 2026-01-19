package org.boomer.gameserver.controller.websocket;

import org.boomer.gameserver.services.WebSocketService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;

@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private final WebSocketService wsService;

    public GameSocketHandler(WebSocketService wsService) {
        this.wsService = wsService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        URI uri = session.getUri();
        String query = uri.getQuery();
        String username = query.split("=")[1];

        System.out.println("New WebSocket connection established. Username: " + username);

        wsService.addSession(username, session);
        wsService.onPing(session); // init lastPing
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();

        if ("PING".equals(payload)) {
            wsService.onPing(session);
            // Optionally, send a PONG response
            // Uncomment the following line if you want to send a PONG message back
//            session.sendMessage(new TextMessage("PONG"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        wsService.removeSession(session);
    }
}
