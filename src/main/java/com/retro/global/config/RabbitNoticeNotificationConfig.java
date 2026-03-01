package com.retro.global.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties(NoticeNotificationRabbitProperties.class)
public class RabbitNoticeNotificationConfig {

  private final Environment environment;

  public RabbitNoticeNotificationConfig(Environment environment) {
    this.environment = environment;
  }

  @Bean
  public Jackson2JsonMessageConverter rabbitMessageConverter() {
    return new Jackson2JsonMessageConverter() {
      @Override
      protected Message createMessage(Object objectToConvert, MessageProperties messageProperties) {
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        return super.createMessage(objectToConvert, messageProperties);
      }
    };
  }


  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
      Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }

  @Bean
  public DirectExchange noticeExchange(NoticeNotificationRabbitProperties properties) {
    return new DirectExchange(properties.exchange(), true, false);
  }

  @Bean
  public Queue noticeQueue(NoticeNotificationRabbitProperties properties) {
    return QueueBuilder.durable(properties.queue())
        .deadLetterExchange(properties.deadLetterExchange())
        .build();
  }

  @PostConstruct
  public void checkRabbitHost() {
    System.out.println("Rabbit Host: " + environment.getProperty("spring.app.rabbitmq.host"));
  }

  @Bean
  public Binding noticeBinding(@Qualifier("noticeQueue") Queue noticeQueue,
      @Qualifier("noticeExchange") DirectExchange noticeExchange,
      NoticeNotificationRabbitProperties properties) {
    return BindingBuilder.bind(noticeQueue)
        .to(noticeExchange)
        .with(properties.routingKey());
  }

  @Bean
  public DirectExchange noticeDeadLetterExchange(NoticeNotificationRabbitProperties properties) {
    return new DirectExchange(properties.deadLetterExchange(), true, false);
  }

  @Bean
  public Queue noticeDeadLetterQueue(NoticeNotificationRabbitProperties properties) {
    return QueueBuilder.durable(properties.deadLetterQueue()).build();
  }

  @Bean
  public Binding noticeDeadLetterBinding(
      @Qualifier("noticeDeadLetterQueue") Queue noticeDeadLetterQueue,
      @Qualifier("noticeDeadLetterExchange") DirectExchange noticeDeadLetterExchange,
      NoticeNotificationRabbitProperties properties) {
    return BindingBuilder.bind(noticeDeadLetterQueue)
        .to(noticeDeadLetterExchange)
        .with(properties.routingKey());
  }
}
