package br.com.fiap.cadchamados.Model;

/**
 * Created by leandro on 9/4/17.
 */

public class Item {

    private String codigo;

    private String descricao;

    private String data;

    private String finalizado;

    public Item(String codigo, String descricao, String data, String finalizado) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.data = data;
        this.finalizado = finalizado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(String finalizado) {
        this.finalizado = finalizado;
    }

    @Override
    public String toString() {
        return  codigo + " - " + descricao + " - " + data + " - finalizado: "  + finalizado;
    }
}
