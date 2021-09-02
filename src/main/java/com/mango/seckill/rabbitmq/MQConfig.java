package com.mango.seckill.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    public static final String  SECKILL_QUEUE = "seckill.queue";
    @Bean
    public Queue miaoshaQueue() {
        return new Queue("seckill.queue", true);
    }
}
