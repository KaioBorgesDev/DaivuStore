package com.example.dao;

import com.example.components.Notifier;
import com.example.entity.Cliente;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailSender {
    ClienteDAO clienteDAO = new ClienteDAO();
    javafx.scene.image.ImageView loading;
    public EmailSender(){}

    public void sendEmailAsync(String to, ImageView loading) {
        this.loading = loading;
        Runnable emailTask = () -> {
            sendEmail(to);
        };

        Thread thread = new Thread(emailTask);
        thread.start();
    }

    private void sendEmail(String to){
        String from = "kaiotestes08@gmail.com";

        final String username = "kaiotestes08@gmail.com";//email de testes
        final String password = "qpez hgbd noej trgt";//senha para aplicativos terceiros

        // GMail SMTP server
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.ssl.trust", host);

        // Pega a session do objeto
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Random random = new Random();

            //Parte que cria o codigo aleatorio, com 4 digitos
            int code = random.nextInt(1000,9999);

            String sql = "SELECT id FROM clientes WHERE email = ?";
            Long id = Long.valueOf(clienteDAO.destinoCodigo(to,sql,"id"));
            Cliente cliente = new Cliente();
            cliente.setCode(String.valueOf(code));
            cliente.setId(id);

            clienteDAO.updateCodigo(cliente);

            Message message = new MimeMessage(session);

            // campo de cabeçalho, do destino
            message.setFrom(new InternetAddress(from));

            // Seta quem vai receber
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));

            // Cabecalho do texto
            message.setSubject("Codigo de Verificação - DAIVU");

            // Mensagem pra ser eviada
            message.setText("Olá, o codigo de verifição é "+ code + ", caso não tenha sido você, favor desconsiderar. ");
            // Envia a mensagem
            Transport.send(message);

            showNotification("Email Enviado!",true);
            loading.setVisible(false);
        } catch (MessagingException e) {
            showNotification("Email Não Pode ser Enviado!",false);
            loading.setVisible(false);
            throw new RuntimeException(e);
        }
    }

    private void showNotification(String message, boolean isSuccess) {
        Platform.runLater(() -> {
            Notifier notifier = new Notifier(message, isSuccess);
            notifier.show();
        });
    }
}
