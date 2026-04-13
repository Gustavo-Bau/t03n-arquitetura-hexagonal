package com.fag.lucasmartins.arquitetura_software.application.ports.in.service;

import com.fag.lucasmartins.arquitetura_software.core.domain.bo.PessoaBO;

public interface PessoaServicePort {

    PessoaBO salvar(PessoaBO pessoaBO);
}