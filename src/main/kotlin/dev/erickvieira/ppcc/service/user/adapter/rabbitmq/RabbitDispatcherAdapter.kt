package dev.erickvieira.ppcc.service.user.adapter.rabbitmq

interface RabbitDispatcherAdapter {

    fun dispatch(queue: String, message: String)

}