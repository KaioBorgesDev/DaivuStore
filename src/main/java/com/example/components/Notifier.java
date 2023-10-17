package com.example.components;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

public class Notifier {
    private static final int POPUP_HEIGHT = 60;
    private int popupWidth; // Largura desejada da notificação
    private static final Duration DISPLAY_DURATION = Duration.seconds(3);
    private boolean isShowing = false;
    private final Stage stage;

    public Notifier(String message, boolean isSuccess) {
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);

        Image iconImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(isSuccess ? "/imgs/correto.png" : "/imgs/erro.png")));
        ImageView iconView = new ImageView(iconImage);
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);

        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: black");

        // Crie um novo HBox para a cena temporária
        HBox tempContent = new HBox(10, iconView, label);
        tempContent.setAlignment(Pos.CENTER_LEFT);
        tempContent.setStyle("-fx-background-color: #535353; -fx-background-radius: 10px; -fx-border-radius: 10px");
        tempContent.setPadding(new Insets(5,5,5,5));

        // Crie uma cena temporária com o novo HBox
        Scene tempScene = new Scene(tempContent, 1, 1);
        tempScene.getRoot().applyCss();
        tempScene.getRoot().layout();

        popupWidth = (int) tempContent.prefWidth(-1) + 20;

        HBox content = new HBox(10, iconView, label);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setStyle("-fx-background-color: #535353; -fx-background-radius: 10px; -fx-border-radius: 10px");
        content.setPadding(new Insets(5,5,5,5));

        // Crie a cena final com o HBox final e a largura preferida
        Scene scene = new Scene(content, popupWidth, POPUP_HEIGHT);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        // Obtenha a tela primária para calcular a posição
        Screen primaryScreen = Screen.getPrimary();
        double screenWidth = primaryScreen.getBounds().getWidth();
        double screenHeight = primaryScreen.getBounds().getHeight();

        // Defina a posição X e Y para o canto inferior direito
        double xPos = screenWidth - popupWidth - 20; // Ajuste a posição X conforme necessário
        double yPos = screenHeight - POPUP_HEIGHT - 60; // Ajuste a posição Y conforme necessário
        stage.setX(xPos);
        stage.setY(yPos);

        // Configurar transição de opacidade
        stage.setOpacity(0);
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.millis(500), new KeyValue(stage.opacityProperty(), 1))
        );
        fadeIn.play();

        // Configurar transição de deslocamento
        TranslateTransition showTransition = new TranslateTransition(Duration.millis(500), tempContent);
        showTransition.setFromX(popupWidth); // Comece fora da tela à direita
        showTransition.setToX(0);
        showTransition.play();
    }

    public void show() {
        if (!isShowing) {
            isShowing = true;
            stage.show();

            Timeline timeline = new Timeline(
                    new KeyFrame(DISPLAY_DURATION, e -> close())
            );
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    public void close() {
        // Configurar transição de desaparecimento
        TranslateTransition hideTransition = new TranslateTransition(Duration.millis(500), stage.getScene().getRoot());
        hideTransition.setFromX(0);
        hideTransition.setToX(popupWidth);
        hideTransition.setOnFinished(event -> {
            stage.close();
            isShowing = false; // Atualize a flag após fechar a notificação
        });
        hideTransition.play();
    }
}
