package com.retro.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq.notice")
public record NoticeNotificationRabbitProperties(
    String exchange,
    String queue,
    String deadLetterExchange,
    String deadLetterQueue,
    String routingKey,
    int chunkSize,
    int maxRetryCount
) {

}