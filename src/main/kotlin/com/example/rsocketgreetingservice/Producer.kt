package com.example.rsocketgreetingservice


import io.rsocket.AbstractRSocket
import io.rsocket.ConnectionSetupPayload
import io.rsocket.Payload

import io.rsocket.RSocket
import io.rsocket.RSocketFactory
import io.rsocket.SocketAcceptor
import io.rsocket.transport.netty.client.TcpClientTransport
import io.rsocket.transport.netty.server.TcpServerTransport

import io.rsocket.util.DefaultPayload

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class Producer(val jsonHelper: JsonHelper, val greetingService: GreetingService) {

    @EventListener(ApplicationReadyEvent::class)
    fun start(){

        println("Starting Producer ...")

        val socketAcceptor: SocketAcceptor =  object:SocketAcceptor {

            override fun accept(setup: ConnectionSetupPayload, sendingSocket: RSocket): Mono<RSocket> {

                val response: AbstractRSocket = object:AbstractRSocket() {

                    override fun requestStream(payload: Payload) : Flux<Payload> {

                        val json = payload.dataUtf8
                        val greetingRequest  =  jsonHelper.read(json, GreetingRequest::class.java)

                        return greetingService.greet(greetingRequest)
                                .map(jsonHelper::write)
                                .map {json -> DefaultPayload.create(json) }
                    }
                }

                return Mono.just(response)
            }

        }

        val tcpServerTransport = TcpServerTransport.create(7000)

        RSocketFactory
                .receive()
                .acceptor(socketAcceptor)
                .transport(tcpServerTransport)
                .start()
                .block()


    }

}

