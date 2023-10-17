package com.example.entity;

import java.io.Serializable;

public class Cliente implements Serializable {
    private Long id;
    private String nomeDeUsuario;
    private String email;
    private String senha;
    private String code;
    private int isAdm;

    public Cliente(String nomeDeUsuario, String email, String senha) {
        this.nomeDeUsuario = nomeDeUsuario;
        this.email = email;
        this.senha = senha;
    }

    public Cliente(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeDeUsuario() {
        return nomeDeUsuario;
    }

    public void setNomeDeUsuario(String nome_de_usuario) {
        this.nomeDeUsuario = nome_de_usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int isAdm() {
        return isAdm;
    }

    public void setAdm(int adm) {
        isAdm = adm;
    }
}
