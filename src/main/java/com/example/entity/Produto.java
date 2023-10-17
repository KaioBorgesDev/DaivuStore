package com.example.entity;

import java.io.Serializable;

public class Produto implements Serializable {
     private Long id;
     private String marca;
     private String descricao;
     private String ml;
     private Double preco;
     private String linkImagem;

    public Produto(String marca, String descricao, String ml, Double preco, String linkImagem) {
        this.marca = marca;
        this.descricao = descricao;
        this.ml = ml;
        this.preco = preco;
        this.linkImagem = linkImagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMl() {
        return ml;
    }

    public void setMl(String ml) {
        this.ml = ml;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getLinkImagem() {
        return linkImagem;
    }

    public void setLinkImagem(String linkImagem) {
        this.linkImagem = linkImagem;
    }
}
