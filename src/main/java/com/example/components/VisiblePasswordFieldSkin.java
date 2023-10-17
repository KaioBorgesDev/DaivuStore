package com.example.components;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class VisiblePasswordFieldSkin extends TextFieldSkin {
    private static final char BULLET = '\u2022';
    private boolean mask = true;

    public VisiblePasswordFieldSkin(PasswordField textField, Button button) {
        super(textField);
        button.setOnMouseClicked(event -> {
            if(mask) {
                mask = false;
                button.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/imgs/eye.png")).toExternalForm())));
            } else {
                mask = true;
                button.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/imgs/closeEye.png")).toExternalForm())));
            }
            textField.setText(textField.getText());

            textField.end();
        });
    }

    @Override
    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField && mask) {
            int n = txt.length();
            return String.valueOf(BULLET).repeat(n);
        } else {
            return txt;
        }
    }
}