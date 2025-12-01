package org.example.sdinotificaretimpreal.controller; // ATENTIE LA LINIA ASTA

import org.example.sdinotificaretimpreal.config.RabbitConfig; // Importam config-ul tau
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SimpMessagingTemplate wsTemplate;

    @PostMapping("/send")
    public String sendEvent(@RequestBody String message) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "notificari.key", message);
        return "Mesaj trimis in sistemul distribuit!";
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        System.out.println("Primit din RabbitMQ: " + message);
        wsTemplate.convertAndSend("/topic/alerts", message);
    }
}