package com.peoplehere.shared.common.webhook;

import static com.peoplehere.shared.common.webhook.DiscordMessage.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import java.lang.management.ManagementFactory;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 디스코드 웹훅의 rate-limit은 초당 5회로 제한됨.
 * @see <a href="https://discord.com/developers/docs/topics/rate-limits">Rate Limits on Discord's API</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebhook implements AlertWebhook {

	@Value("${webhook.discord.channel.alert:#{null}}")
	String alertChannel;
	@Value("${webhook.discord.channel.status:#{null}}")
	String serverStatusChannel;
	@Value("${webhook.discord.active:false}")
	boolean webhookActive;

	@Value("${spring.application.name:#{null}}")
	String appName;
	@Value("${app.ip.public:#{null}}")
	String publicIp;
	@Value("${app.ip.local:#{null}}")
	String localIp;

	private final RestClient restClient;
	private static boolean isInit = false;
	private static final int COLOR_GREEN = 65280;
	private static final int COLOR_RED = 16711680;

	@PostConstruct
	void init() {
		log.info("discord webhook 초기화됨. 기본 알림채널 {}", alertChannel);
		log.info("discord webhook 초기화됨. 서버상태 알림채널 {}", serverStatusChannel);
	}

	/**
	 * spring application 시작시 시간 및 ip 기록
	 * @param event
	 */
	@EventListener(ApplicationStartedEvent.class)
	public void onStart(final ApplicationStartedEvent event) {
		isInit = StringUtils.hasText(alertChannel) && StringUtils.hasText(serverStatusChannel);

		if (isInit) {
			String title = "[%s] 모니터링 디스코드 채널 활성화".formatted(appName);
			String description = "public: %s | local: %s".formatted(publicIp, localIp);
			String fieldContent = "%s초".formatted(((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000));
			DiscordMessage message = toMessage(title, description, COLOR_GREEN,
				Collections.singletonList(new EmbedObject.Field("spring start", fieldContent, true)));
			sendMessage(serverStatusChannel, message);
		}
	}

	/**
	 * spring application 종료시 시간 및 ip 기록(의도치 않은 종료 체크)
	 * @param event
	 */
	@EventListener(ContextClosedEvent.class)
	public void onClosed(ContextClosedEvent event) {
		isInit = StringUtils.hasText(alertChannel) && StringUtils.hasText(serverStatusChannel);
		if (isInit) {
			String title = "[%s] 어플리케이션 종료".formatted(appName);
			String description = "public: %s | local: %s".formatted(publicIp, localIp);
			String fieldContent = "%s초".formatted(((double)ManagementFactory.getRuntimeMXBean().getUptime() / 1000));
			DiscordMessage message = toMessage(title, description, COLOR_GREEN,
				Collections.singletonList(new EmbedObject.Field("spring closed", fieldContent, true)));
			sendMessage(serverStatusChannel, message);
		}
	}

	@Override
	public void alertInfo(String title, String infoMessage) {

		String formattedTitle = ":white_check_mark: %s".formatted(title);
		String description = "**[알림]**: %s\n**[%s]** **public**: %s | **local**: %s".formatted(infoMessage, appName,
			publicIp, localIp);
		DiscordMessage message = toMessage(formattedTitle, description, COLOR_GREEN);
		sendMessage(alertChannel, message);
	}

	@Override
	public void alertError(String title, String errorMessage) {

		String formattedTitle = ":warning: %s".formatted(title);
		String description = "**[예외]**: %s\n**[%s]** **public**: %s | **local**: %s".formatted(errorMessage, appName,
			publicIp, localIp);
		DiscordMessage message = toMessage(formattedTitle, description, COLOR_RED);
		sendMessage(alertChannel, message);

	}

	/**
	 * 디스코드 웹훅 전송
	 * Todo: 추후 에러 응답이 자주 발생할 경우 retry 로직 추가 및 read timeout 조정 우선은 10초
	 * @param channel
	 * @param message
	 */
	private void sendMessage(String channel, DiscordMessage message) {
		if (!isInit || !webhookActive) {
			log.warn("디스코드 웹훅 초기화 안됨. 스킵");
			return;
		}

		try {
			restClient.post()
				.uri(channel)
				.contentType(APPLICATION_JSON)
				.body(message)
				.exchange((request, response) -> {
					if (response.getStatusCode().isSameCodeAs(NO_CONTENT)) {
						log.info("디스코드 웹훅 전송 완료");
					}
					if (response.getStatusCode().isError()) {
						log.error("디스코드 웹훅 전송 실패 - code: {}. 우선은 skip", response.getStatusCode());
					}
					return response;
				});
		} catch (Exception e) {
			log.error("디스코드 웹훅 전송 실패 우선은 skip", e);
		}
	}
}
