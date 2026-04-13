package com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.out.persistence.h2.repository;

import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.out.persistence.h2.entity.ProdutoEntity;
import com.fag.lucasmartins.arquitetura_software.core.domain.bo.ProdutoBO;
import com.fag.lucasmartins.arquitetura_software.application.ports.out.persistence.h2.ProdutoRepositoryPort;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.out.persistence.h2.jpa.ProdutoJpaRepository;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.out.persistence.h2.mapper.ProdutoMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ProdutoRepositoryAdapter implements ProdutoRepositoryPort {

    private final ProdutoJpaRepository produtoJpaRepository;

    public ProdutoRepositoryAdapter(ProdutoJpaRepository produtoJpaRepository) {
        this.produtoJpaRepository = produtoJpaRepository;
    }

    @Override
    public ProdutoBO salvar(ProdutoBO produtoBO) {
        ProdutoEntity produtoEntity = ProdutoMapper.toEntity(produtoBO);

        produtoJpaRepository.save(produtoEntity);

        return ProdutoMapper.toBO(produtoEntity);
    }
}
