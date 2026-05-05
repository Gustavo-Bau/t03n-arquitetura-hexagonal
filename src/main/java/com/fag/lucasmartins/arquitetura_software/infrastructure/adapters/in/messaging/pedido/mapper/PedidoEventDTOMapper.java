package com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.pedido.mapper;

import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PedidoBO;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PedidoProdutoBO;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PessoaBO;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.ProdutoBO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.messaging.pedido.dto.PedidoEventDTO;

import java.util.ArrayList;
import java.util.List;

public class PedidoEventDTOMapper {

    private PedidoEventDTOMapper() {
    }

    public static PedidoBO toBo(PedidoEventDTO dto) {
        final PedidoBO pedidoBO = new PedidoBO();
        pedidoBO.setCep(dto.getZipCode());

        final PessoaBO pessoaBO = new PessoaBO();
        pessoaBO.setId(dto.getCustomerId());
        pedidoBO.setPessoa(pessoaBO);

        final List<PedidoProdutoBO> itens = new ArrayList<>();
        if (dto.getOrderItems() != null) {
            for (PedidoEventDTO.OrderItemDTO itemDTO : dto.getOrderItems()) {
                final PedidoProdutoBO itemBO = new PedidoProdutoBO();
                itemBO.setQuantidade(itemDTO.getAmount() == null ? 0 : itemDTO.getAmount());

                final ProdutoBO produtoBO = new ProdutoBO();
                produtoBO.setId(itemDTO.getSku());
                itemBO.setProduto(produtoBO);

                itens.add(itemBO);
            }
        }
        pedidoBO.setItens(itens);

        return pedidoBO;
    }
}
