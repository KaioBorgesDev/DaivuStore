package com.example.entity;

import java.io.Serializable;

public class ProdutoCart implements Serializable {
    private Produto produto;
    private int quantidadeProduto;

    public ProdutoCart(Produto produto, int quantidadeProduto) {
        this.produto = produto;
        this.quantidadeProduto = quantidadeProduto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidadeProduto() {
        return quantidadeProduto;
    }

    public void setQuantidadeProduto(int quantidadeProduto) {
        this.quantidadeProduto = quantidadeProduto;
    }
}
