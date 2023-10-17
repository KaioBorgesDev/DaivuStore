package com.example.dao;

import com.example.components.Notifier;
import javafx.application.Platform;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

public class ConnectionDb {
    private static final String LOGIN_BANCO = "yourLogin";
    private static final String SENHA_BANCO = "yourPassword";
    private static final String URL_BANCO = "jdbc:mysql://yourUrl?autoReconnect=true&useSSL=false";
    public static Connection getConnection() {
        Connection conexao = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexao = DriverManager.getConnection(ConnectionDb.URL_BANCO, ConnectionDb.LOGIN_BANCO, ConnectionDb.SENHA_BANCO);
        }
        catch (SQLException e){
            showNotification("Erro ao conectar ao banco de dados. Erro: " + e);
        } catch (ClassNotFoundException e){
            showNotification("Não foi possivel carregar a classe JDBC MySQL. Erro:" + e);
        } catch (Exception e){
            showNotification("Erro geral. Erro: " + e);
        }
        return conexao;
    }

    private static void showNotification(String message) {
        Platform.runLater(() -> {
            Notifier notifier = new Notifier(message, false);
            notifier.show();
        });
    }
}
