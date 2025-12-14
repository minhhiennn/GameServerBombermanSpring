package org.boomer.gameserver.config;

//import org.boomer.gameserver.controller.websocket.SimpleSocketHandler;

import org.boomer.gameserver.controller.websocket.GameSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameSocketHandler gameHandler;

    public WebSocketConfig(GameSocketHandler gameHandler) {
        this.gameHandler = gameHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler, "/ws/game")
                .setAllowedOrigins("*");
    }
}
