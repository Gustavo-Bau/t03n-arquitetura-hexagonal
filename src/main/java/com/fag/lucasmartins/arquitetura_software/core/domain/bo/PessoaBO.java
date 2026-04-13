package com.fag.lucasmartins.arquitetura_software.core.domain.bo;

import com.fag.lucasmartins.arquitetura_software.core.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

public class PessoaBO {

    private UUID id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String telefone;

    public void validar() {
        if (nomeCompleto == null || nomeCompleto.trim().isEmpty()) {
            throw new DomainException("Nome completo é obrigatório.");
        }
        if (cpf == null || cpf.length() != 11) {
            throw new DomainException("CPF deve ter exatamente 11 caracteres.");
        }
        if (dataNascimento == null) {
            throw new DomainException("Data de nascimento é obrigatória.");
        }
        if (Period.between(dataNascimento, LocalDate.now()).getYears() < 18) {
            throw new DomainException("Cliente deve ter no mínimo 18 anos.");
        }
        if (email == null || !email.contains("@")) {
            throw new DomainException("E-mail deve ser obrigatório e conter '@'.");
        }
        if (telefone == null || telefone.length() != 11) {
            throw new DomainException("Telefone deve ter exatamente 11 caracteres.");
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}