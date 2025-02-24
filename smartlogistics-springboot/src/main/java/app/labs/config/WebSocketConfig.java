package app.labs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import app.labs.websocket.ProgressWebSocketHandler;

@Configuration
@EnableWebSocketMessageBroker  // ✅ WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

	private final ProgressWebSocketHandler progressWebSocketHandler;
	
	public WebSocketConfig(ProgressWebSocketHandler progressWebSocketHandler) {
        this.progressWebSocketHandler = progressWebSocketHandler;
    }
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");  // ✅ 클라이언트가 구독할 메시지 브로커 경로
        config.setApplicationDestinationPrefixes("/app");  // ✅ 클라이언트가 보낼 메시지 경로
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // ✅ WebSocket 연결 엔드포인트
                .setAllowedOrigins("http://localhost", "http://127.0.0.1", "http://localhost:8000", "http://localhost:8081", "http://localhost:8080")
        		//.setAllowedOrigins("*")
        		.withSockJS();
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(progressWebSocketHandler, "/ws/progress")  // WebSocket 핸들러 등록
        		.setAllowedOriginPatterns("http://localhost:8000", "http://localhost:8080", "http://localhost", "http://127.0.0.1")  // 모든 출처에서 연결 허용
                .addInterceptors(new HttpSessionHandshakeInterceptor());  // 핸드셰이크 인터셉터 추가 (선택적)
    }
    
}