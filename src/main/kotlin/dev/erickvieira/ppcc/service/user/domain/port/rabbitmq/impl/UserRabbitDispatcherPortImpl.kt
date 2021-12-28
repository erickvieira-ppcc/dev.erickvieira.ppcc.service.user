package dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.impl

import com.google.gson.Gson
import dev.erickvieira.ppcc.service.user.adapter.rabbitmq.RabbitDispatcherAdapter
import dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.UserRabbitDispatcherPort
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserRabbitDispatcherPortImpl(
    private val rabbitDispatcherAdapter: RabbitDispatcherAdapter,
    private val userQueueName: String
) : UserRabbitDispatcherPort {

    private val gson: Gson = Gson()

    override fun dispatch(userId: UUID) =
        rabbitDispatcherAdapter.dispatch(queue = userQueueName, message = userId.toString())

}