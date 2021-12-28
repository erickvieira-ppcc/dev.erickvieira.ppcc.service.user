package dev.erickvieira.ppcc.service.user.config

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitMQConfig {

    @Value("\${ppcc.userqueue}")
    private lateinit var userQueueName: String

    @Bean
    open fun userQueueName() = userQueueName

    @Bean
    open fun userQueue() = Queue(userQueueName, false)

}