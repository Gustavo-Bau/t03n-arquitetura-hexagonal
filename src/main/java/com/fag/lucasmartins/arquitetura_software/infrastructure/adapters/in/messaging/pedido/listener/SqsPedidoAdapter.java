package com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.pedido.listener;

import com.fag.lucasmartins.arquitetura_software.application.ports.in.service.PedidoServicePort;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PedidoBO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.entradaestoque.exceptions.ConsumerSQSException;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.pedido.dto.PedidoEventDTO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.pedido.mapper.PedidoEventDTOMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
public class SqsPedidoAdapter {

    private static final Logger log = LoggerFactory.getLogger(SqsPedidoAdapter.class);

    private final PedidoServicePort pedidoServicePort;

    public SqsPedidoAdapter(PedidoServicePort pedidoServicePort) {
        this.pedidoServicePort = pedidoServicePort;
    }

    @SqsListener(value = "${queue.order-events}")
    public void receberMensagem(PedidoEventDTO evento) {
        try {
            log.info("Evento de pedido recebido via SQS [customerId={}, origem={}, occurredAt={}]",
                    evento.getCustomerId(),
                    evento.getOrigin(),
                    evento.getOccurredAt());

            final PedidoBO pedidoBO = PedidoEventDTOMapper.toBo(evento);
            pedidoServicePort.criarPedido(pedidoBO);

            log.info("Pedido de customerId {} processado com sucesso", evento.getCustomerId());
        } catch (Exception e) {
            log.error("Erro ao processar evento de pedido para customerId {}", evento.getCustomerId(), e);
            throw new ConsumerSQSException(
                    "Erro ao processar evento de pedido para customerId " + evento.getCustomerId(),
                    e
            );
        }
    }
}
