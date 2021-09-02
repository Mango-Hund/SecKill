package com.mango.seckill.rabbitmq;

import com.mango.seckill.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MQSender {

    private final AmqpTemplate amqpTemplate;

    public void sendSeckillMessage(SeckillMessage sm) {
        String msg = JsonUtil.object2JsonStr(sm);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, msg);
    }
}
