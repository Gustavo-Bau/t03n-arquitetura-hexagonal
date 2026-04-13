package com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.rest.mapper;

import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PessoaBO;
import com.fag.lucasmartins.arquitetura_software.infrastructure.adapters.in.rest.dto.PessoaDTO;

public class PessoaDTOMapper {

    private PessoaDTOMapper() {
    }

    public static PessoaBO toBo(PessoaDTO dto) {
        final PessoaBO bo = new PessoaBO();
        bo.setId(dto.getId());
        bo.setNomeCompleto(dto.getNomeCompleto());
        bo.setCpf(dto.getCpf());
        bo.setDataNascimento(dto.getDataNascimento());
        bo.setEmail(dto.getEmail());
        bo.setTelefone(dto.getTelefone());

        return bo;
    }

    public static PessoaDTO toDto(PessoaBO bo) {
        final PessoaDTO dto = new PessoaDTO();
        dto.setId(bo.getId());
        dto.setNomeCompleto(bo.getNomeCompleto());
        dto.setCpf(bo.getCpf());
        dto.setDataNascimento(bo.getDataNascimento());
        dto.setEmail(bo.getEmail());
        dto.setTelefone(bo.getTelefone());

        return dto;
    }
}