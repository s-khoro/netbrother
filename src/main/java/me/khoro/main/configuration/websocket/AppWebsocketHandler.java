package me.khoro.main.configuration.websocket;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.nio.ByteBuffer;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppWebsocketHandler extends BinaryWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Websocket connection established");
        new Thread(new PosUpdateProcessor(session)).start();
//        session.sendMessage(new BinaryMessage("KEK".getBytes(StandardCharsets.UTF_8)));

        // new WebSocketBinaryProcessor
//        super.afterConnectionEstablished(session);
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        log.info("Got new text message throw webSocket");
//        super.handleTextMessage(session, message);
//    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        log.info("Got new binary message throw webSocket");
//        super.handleBinaryMessage(session, message);
    }

    private static class PosUpdateProcessor implements Runnable {

        private final WebSocketSession session;
        private final Random random = new Random();

        public PosUpdateProcessor(WebSocketSession session) {
            this.session = session;
        }

        @SneakyThrows
        @Override
        public void run() {
             while (session.isOpen()) {
                 ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);;
                 double value = random.nextDouble();
                 byteBuffer.putDouble(value);
                 session.sendMessage(new BinaryMessage(byteBuffer.array()));
                 log.info("Pushing {} value, time={}", value, System.currentTimeMillis());
                 Thread.sleep(1000);
             }
        }
    }
}
