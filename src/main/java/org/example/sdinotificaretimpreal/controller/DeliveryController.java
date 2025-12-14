package org.example.sdinotificaretimpreal.controller;

import org.example.sdinotificaretimpreal.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class DeliveryController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    // 1. Primim comanda sau statusul (de la Client sau Restaurant)
    @PostMapping("/send")
    public String processOrder(@RequestBody String jsonMessage) {
        // O trimitem in sistemul distribuit (RabbitMQ)
        // Nu ne intereseaza ce e in JSON, doar il transportam
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "orders.key", jsonMessage);
        return "Order processed";
    }

    // 2. Ascultam ce vine din sistemul distribuit
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveOrder(String message) {
        System.out.println("Eveniment Distribuit: " + message);
        // Trimitem notificarea la toti cei conectati la pagina web
        wsTemplate.convertAndSend("/topic/alerts", message);
    }
}