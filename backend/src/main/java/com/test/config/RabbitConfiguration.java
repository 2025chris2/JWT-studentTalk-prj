package com.test.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean("emailQueue")
    public Queue emailQueue(){
        return QueueBuilder
                .durable("mail")
                .build();
    }

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        // 允许反序列化 java.util 包下的类
        converter.addAllowedListPatterns("java.util.*");
        return converter;
    }
}
