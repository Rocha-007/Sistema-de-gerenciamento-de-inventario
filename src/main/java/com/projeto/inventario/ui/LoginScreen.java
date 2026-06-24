package com.projeto.inventario.ui;

import com.projeto.inventario.client.InventarioApiClient;
import com.projeto.inventario.client.InventarioApiClient.ApiClientException;
import com.projeto.inventario.client.InventarioApiClient.AuthResult;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginScreen extends JFrame {

    private final InventarioApiClient apiClient;

    public LoginScreen() {
        this.apiClient = new InventarioApiClient();

        setTitle("Inventario - Acesso");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(440, 330));
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Entrar", criarPainelLogin());
        abas.addTab("Criar conta", criarPainelRegistro());

        JPanel conteudo = new JPanel(new BorderLayout());
        conteudo.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        conteudo.add(abas, BorderLayout.CENTER);
        setContentPane(conteudo);
    }

    private JPanel criarPainelLogin() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton entrarButton = new JButton("Entrar");

        JPanel painel = criarFormulario();
        adicionarCampo(painel, "Usuario", usernameField, 0);
        adicionarCampo(painel, "Senha", passwordField, 1);
        adicionarBotao(painel, entrarButton, 2);

        entrarButton.addActionListener(event -> executarAutenticacao(
                entrarButton,
                () -> apiClient.login(
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword())
                )
        ));

        return painel;
    }

    private JPanel criarPainelRegistro() {
        JTextField usernameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JButton registrarButton = new JButton("Criar conta");

        JPanel painel = criarFormulario();
        adicionarCampo(painel, "Usuario", usernameField, 0);
        adicionarCampo(painel, "E-mail", emailField, 1);
        adicionarCampo(painel, "Senha", passwordField, 2);
        adicionarBotao(painel, registrarButton, 3);

        registrarButton.addActionListener(event -> executarAutenticacao(
                registrarButton,
                () -> apiClient.registrar(
                        usernameField.getText().trim(),
                        new String(passwordField.getPassword()),
                        emailField.getText().trim()
                )
        ));

        return painel;
    }

    private JPanel criarFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return painel;
    }

    private void adicionarCampo(JPanel painel, String label, java.awt.Component campo, int linha) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(7, 7, 7, 7);
        constraints.gridy = linha;
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        painel.add(new JLabel(label), constraints);

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        painel.add(campo, constraints);
    }

    private void adicionarBotao(JPanel painel, JButton botao, int linha) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = linha;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(14, 7, 7, 7);
        painel.add(botao, constraints);
    }

    private void executarAutenticacao(JButton botao, AuthAction action) {
        botao.setEnabled(false);

        new SwingWorker<AuthResult, Void>() {
            @Override
            protected AuthResult doInBackground() {
                return action.execute();
            }

            @Override
            protected void done() {
                botao.setEnabled(true);
                try {
                    AuthResult result = get();
                    abrirProdutos(result.username());
                } catch (Exception exception) {
                    mostrarErro(exception);
                }
            }
        }.execute();
    }

    private void abrirProdutos(String username) {
        ProdutoScreen produtoScreen = new ProdutoScreen(apiClient, username);
        produtoScreen.setVisible(true);
        dispose();
    }

    private void mostrarErro(Exception exception) {
        Throwable cause = exception.getCause();
        String message = cause instanceof ApiClientException
                ? cause.getMessage()
                : "Nao foi possivel concluir a autenticacao.";

        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    @FunctionalInterface
    private interface AuthAction {
        AuthResult execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
