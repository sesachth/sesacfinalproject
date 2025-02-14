package app.labs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // ✅ WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // ✅ 클라이언트가 구독할 메시지 브로커 경로
        config.setApplicationDestinationPrefixes("/app");  // ✅ 클라이언트가 보낼 메시지 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ WebSocket 연결 엔드포인트
                .setAllowedOrigins("http://localhost", "http://127.0.0.1", "http://localhost:8000", "http://localhost:8081")
                .withSockJS();
    }
}
