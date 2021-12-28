package dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.dispatcher.impl

import dev.erickvieira.ppcc.service.user.adapter.rabbitmq.RabbitDispatcherAdapter
import dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.dispatcher.UserRabbitDispatcherPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserRabbitDispatcherPortImpl(
    private val rabbitDispatcherAdapter: RabbitDispatcherAdapter,
    private val userQueueName: String
) : UserRabbitDispatcherPort {

    override fun dispatch(userId: UUID) =
        rabbitDispatcherAdapter.dispatch(queue = userQueueName, message = userId.toString())

}