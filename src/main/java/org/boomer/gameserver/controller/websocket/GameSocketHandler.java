package org.boomer.gameserver.controller.websocket;

import org.boomer.gameserver.services.WebSocketService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
    }

    /* this function use to handle message from client, we don't need it now */
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) {
//        wsService.broadcast("Echo: " + message.getPayload());
//    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        wsService.removeSession(session);
    }
}
