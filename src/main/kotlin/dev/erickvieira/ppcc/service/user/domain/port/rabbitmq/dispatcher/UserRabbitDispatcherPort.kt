package dev.erickvieira.ppcc.service.user.domain.port.rabbitmq.dispatcher

import java.util.*

interface UserRabbitDispatcherPort {

    fun dispatch(userId: UUID)

}