package com.projeto.inventario.ui;

import com.projeto.inventario.client.ProdutoApiClient;

import javax.swing.*;
import java.awt.*;

public class ProdutoScreen extends JFrame {

    private JTextArea area;

    public ProdutoScreen() {
        setTitle("Estoque");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        area = new JTextArea();
        JButton botao = new JButton("Carregar Produtos");

        botao.addActionListener(e -> carregarProdutos());

        add(new JScrollPane(area), BorderLayout.CENTER);
        add(botao, BorderLayout.SOUTH);
    }

    private void carregarProdutos() {
        ProdutoApiClient api = new ProdutoApiClient();
        String resposta = api.listarProdutos();

        area.setText(resposta);
    }

    public static void main(String[] args) {
        new ProdutoScreen().setVisible(true);
    }
}
``