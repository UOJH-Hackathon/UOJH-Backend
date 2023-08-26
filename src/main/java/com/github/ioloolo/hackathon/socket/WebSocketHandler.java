package com.github.ioloolo.hackathon.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

	private static final List<WebSocketSession> list = new ArrayList<>();

	public static Optional<WebSocketSession> arduino = Optional.empty();

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		String msg = message.getPayload();

		if (msg.equals("auth:arduino")) {
			arduino = Optional.of(session);

			sendUsers("arduino open");
			log.info("아두이노 연결됨.");
		} else {
			String s = msg
					.replace("to_arduino:", "")
					.replace("from_arduino:", "");

			if (msg.startsWith("to_arduino:")) {
				sendArduino(s);

				log.info("아두이노에게: " + s);
			} else if (msg.startsWith("from_arduino:")) {
				sendUsers(s);

				log.info("아두이노가: " + s);
			}
		}
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		list.add(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		if (arduino.isPresent() && session.equals(arduino.get())) {
			arduino = Optional.empty();

			sendUsers("arduino close");
			log.info("아두이노 연결 끊김.");
		}

		list.remove(session);
	}

	public void sendUsers(String text) {
		for (WebSocketSession sess : list) {
			send(sess, text);
		}
	}

	public void sendArduino(String text) {
		arduino.ifPresent(sess -> {
			send(sess, text);
		});
	}

	private void send(WebSocketSession session, String text) {
		if (session != null) {
			synchronized (session) {
				try {
					session.sendMessage(new TextMessage(text));
				} catch (Throwable ignored) {}
			}
		}
	}
}
