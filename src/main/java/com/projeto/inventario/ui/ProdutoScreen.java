package com.projeto.inventario.ui;

import com.projeto.inventario.client.InventarioApiClient;
import com.projeto.inventario.client.InventarioApiClient.ApiClientException;
import com.projeto.inventario.model.Produto;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdutoScreen extends JFrame {

    private final InventarioApiClient apiClient;
    private final DefaultTableModel produtosModel;
    private final JTable produtosTable;
    private final JTextField nomeField;
    private final JTextField descricaoField;
    private final JTextField precoField;
    private final JTextField quantidadeField;
    private final JLabel statusLabel;
    private final NumberFormat moedaFormat;
    private List<Produto> produtos = List.of();
    private Long produtoSelecionadoId;

    public ProdutoScreen(InventarioApiClient apiClient, String username) {
        this.apiClient = apiClient;
        this.moedaFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        setTitle("Inventario - " + username);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        nomeField = new JTextField();
        descricaoField = new JTextField();
        precoField = new JTextField();
        quantidadeField = new JTextField();
        statusLabel = new JLabel("Conectado como " + username);

        produtosModel = new DefaultTableModel(
                new String[]{"ID", "Nome", "Descricao", "Preco", "Estoque"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        produtosTable = new JTable(produtosModel);
        produtosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        produtosTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                carregarProdutoSelecionado();
            }
        });

        setContentPane(criarConteudo(username));
        carregarProdutos();
    }

    private JPanel criarConteudo(String username) {
        JPanel conteudo = new JPanel(new BorderLayout(12, 12));
        conteudo.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        conteudo.add(criarCabecalho(username), BorderLayout.NORTH);
        conteudo.add(new JScrollPane(produtosTable), BorderLayout.CENTER);
        conteudo.add(criarFormulario(), BorderLayout.SOUTH);
        return conteudo;
    }

    private JPanel criarCabecalho(String username) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(statusLabel, BorderLayout.WEST);

        JButton atualizarButton = new JButton("Atualizar");
        atualizarButton.addActionListener(event -> carregarProdutos());

        JButton sairButton = new JButton("Sair");
        sairButton.addActionListener(event -> {
            apiClient.limparSessao();
            new LoginScreen().setVisible(true);
            dispose();
        });

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.add(atualizarButton);
        acoes.add(sairButton);
        painel.add(acoes, BorderLayout.EAST);
        return painel;
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Produto"));

        JPanel campos = new JPanel(new GridLayout(2, 4, 8, 6));
        campos.add(new JLabel("Nome"));
        campos.add(new JLabel("Descricao"));
        campos.add(new JLabel("Preco"));
        campos.add(new JLabel("Quantidade"));
        campos.add(nomeField);
        campos.add(descricaoField);
        campos.add(precoField);
        campos.add(quantidadeField);

        JButton novoButton = new JButton("Novo");
        novoButton.addActionListener(event -> limparFormulario());

        JButton salvarButton = new JButton("Salvar");
        salvarButton.addActionListener(event -> salvarProduto());

        JButton excluirButton = new JButton("Excluir");
        excluirButton.addActionListener(event -> excluirProduto());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acoes.add(novoButton);
        acoes.add(excluirButton);
        acoes.add(salvarButton);

        painel.add(campos, BorderLayout.CENTER);
        painel.add(acoes, BorderLayout.SOUTH);
        return painel;
    }

    private void carregarProdutos() {
        statusLabel.setText("Carregando produtos...");
        executarAsync(
                apiClient::listarProdutos,
                resultado -> {
                    produtos = resultado;
                    preencherTabela();
                    statusLabel.setText(produtos.size() + " produto(s) carregado(s)");
                }
        );
    }

    private void salvarProduto() {
        try {
            Produto produto = lerFormulario();
            statusLabel.setText("Salvando produto...");

            executarAsync(
                    () -> produtoSelecionadoId == null
                            ? apiClient.criarProduto(produto)
                            : apiClient.atualizarProduto(produtoSelecionadoId, produto),
                    salvo -> {
                        limparFormulario();
                        carregarProdutos();
                        JOptionPane.showMessageDialog(this, "Produto salvo com sucesso.");
                    }
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Dados invalidos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirProduto() {
        if (produtoSelecionadoId == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto para excluir.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Deseja excluir o produto selecionado?",
                "Confirmar exclusao",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        Long id = produtoSelecionadoId;
        statusLabel.setText("Excluindo produto...");
        executarAsync(
                () -> {
                    apiClient.deletarProduto(id);
                    return null;
                },
                ignored -> {
                    limparFormulario();
                    carregarProdutos();
                }
        );
    }

    private Produto lerFormulario() {
        String nome = nomeField.getText().trim();
        String descricao = descricaoField.getText().trim();

        if (nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do produto.");
        }

        try {
            BigDecimal preco = new BigDecimal(precoField.getText().trim().replace(",", "."));
            int quantidade = Integer.parseInt(quantidadeField.getText().trim());

            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("O preco deve ser maior que zero.");
            }
            if (quantidade < 0) {
                throw new IllegalArgumentException("A quantidade nao pode ser negativa.");
            }

            return Produto.builder()
                    .nome(nome)
                    .descricao(descricao)
                    .preco(preco)
                    .quantidadeEstoque(quantidade)
                    .build();
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Informe preco e quantidade validos.");
        }
    }

    private void preencherTabela() {
        produtosModel.setRowCount(0);
        for (Produto produto : produtos) {
            produtosModel.addRow(new Object[]{
                    produto.getId(),
                    produto.getNome(),
                    produto.getDescricao(),
                    moedaFormat.format(produto.getPreco()),
                    produto.getQuantidadeEstoque()
            });
        }
    }

    private void carregarProdutoSelecionado() {
        int linha = produtosTable.getSelectedRow();
        if (linha < 0 || linha >= produtos.size()) {
            return;
        }

        Produto produto = produtos.get(linha);
        produtoSelecionadoId = produto.getId();
        nomeField.setText(produto.getNome());
        descricaoField.setText(produto.getDescricao());
        precoField.setText(produto.getPreco().toPlainString());
        quantidadeField.setText(String.valueOf(produto.getQuantidadeEstoque()));
    }

    private void limparFormulario() {
        produtoSelecionadoId = null;
        produtosTable.clearSelection();
        nomeField.setText("");
        descricaoField.setText("");
        precoField.setText("");
        quantidadeField.setText("");
        nomeField.requestFocus();
    }

    private <T> void executarAsync(ApiAction<T> action, java.util.function.Consumer<T> onSuccess) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return action.execute();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (Exception exception) {
                    mostrarErro(exception);
                }
            }
        }.execute();
    }

    private void mostrarErro(Exception exception) {
        Throwable cause = exception.getCause();
        String message = cause instanceof ApiClientException
                ? cause.getMessage()
                : "Nao foi possivel concluir a operacao.";

        statusLabel.setText("Falha na comunicacao com a API");
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    @FunctionalInterface
    private interface ApiAction<T> {
        T execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
