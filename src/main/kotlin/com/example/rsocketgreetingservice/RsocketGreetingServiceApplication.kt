package com.example.rsocketgreetingservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.stream.Stream

/**
 * Kotlin jackson library
 * https://github.com/FasterXML/jackson-module-kotlin
 *
 */
@SpringBootApplication
class RsocketGreetingServiceApplication{

    @Bean
    fun objectMapper():ObjectMapper = jacksonObjectMapper()
}

fun main(args: Array<String>) {
    runApplication<RsocketGreetingServiceApplication>(*args)
    readLine()
}

@Component
class JsonHelper(private val objectMapper: ObjectMapper) {

    fun <T> read(json:String , clzz:Class<T>): T  = this
            .objectMapper.readValue(json, clzz)

    fun write (o:Any): String = this
            .objectMapper.writeValueAsString(o)
}

data class GreetingRequest(val name:String)
data class GreetingResponse(val message:String)

@Service
class GreetingService {

    fun greet(request:GreetingRequest) = Flux
            .fromStream( Stream.generate
                {GreetingResponse("Hello ${request.name} @ ${Instant.now()} !")})
            .delayElements(Duration.ofSeconds(1))

}

