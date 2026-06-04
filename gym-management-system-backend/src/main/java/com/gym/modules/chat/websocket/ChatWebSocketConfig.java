package com.gym.modules.chat.websocket;

import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatWebSocketConfig {

    @Bean
    public TomcatContextCustomizer chatWebSocketContextCustomizer() {
        return context -> context.addApplicationListener("org.apache.tomcat.websocket.server.WsContextListener");
    }
}
