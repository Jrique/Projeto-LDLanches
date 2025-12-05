package com.ldlanches.demo.Config

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageProducer
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.fasterxml.jackson.databind.ObjectMapper

@Service
object SensorStorage {
    var temp: Double = 0.0
    var lastUpdate: String = "--:--"

    fun atualizar(newTemp: Double) {
        temp = newTemp
        lastUpdate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        println("Recebido do Wokwi: $temp °C")
    }
}

@Configuration
class MqttConfig {

    @Bean
    fun mqttClientFactory(): MqttPahoClientFactory {
        val factory = DefaultMqttPahoClientFactory()
        val options = MqttConnectOptions()

        options.serverURIs = arrayOf("tcp://test.mosquitto.org:1883")
        options.isCleanSession = true
        factory.connectionOptions = options
        return factory
    }

    @Bean
    fun mqttInputChannel(): MessageChannel = DirectChannel()

    @Bean
    fun inbound(): MessageProducer {
        val clientId = "ldlanches-backend-" + System.currentTimeMillis()

        val adapter = MqttPahoMessageDrivenChannelAdapter(clientId, mqttClientFactory(), "WOKWI/sensores")

        adapter.setCompletionTimeout(5000)
        adapter.setConverter(DefaultPahoMessageConverter())
        adapter.setQos(1)
        adapter.outputChannel = mqttInputChannel()
        return adapter
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    fun handler(): MessageHandler {
        val mapper = ObjectMapper()

        return MessageHandler { message: Message<*> ->
            try {
                val payload = message.payload.toString()

                val jsonNode = mapper.readTree(payload)
                if (jsonNode.has("temperatura")) {
                    val temp = jsonNode.get("temperatura").asDouble()
                    SensorStorage.atualizar(temp)
                }
            } catch (e: Exception) {
                println("Ignorando mensagem inválida: ${message.payload}")
            }
        }
    }
}