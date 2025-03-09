package com.gnnny.deadlock4j.core.transport.tcp;

import com.deadlock4j.proto.MessageProto;
import com.gnnny.deadlock4j.core.event.DeadlockEvent;
import com.gnnny.deadlock4j.core.event.ThreadDeadlockEvent;
import com.gnnny.deadlock4j.util.ProtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TcpEventSendStrategyTest {

    private TcpEventSender tcpEventSender;
    private TcpEventSendStrategy sendStrategy;

    @BeforeEach
    void set_up() {
        tcpEventSender = mock(TcpEventSender.class);
        sendStrategy = new TcpEventSendStrategy(tcpEventSender);
    }

    @Test
    void should_convert_and_send_event_tcp() {
        // given
        DeadlockEvent event = new ThreadDeadlockEvent("thread-2", 102L, "WAITING");
        MessageProto message = ProtoConverter.toProto(event);

        // when
        sendStrategy.send(event);

        // then
        verify(tcpEventSender).send(message);
    }
}
