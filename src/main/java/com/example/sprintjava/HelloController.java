package com.example.sprintjava;

import com.example.components.Notifier;
import com.example.components.VisiblePasswordFieldSkin;
import com.example.dao.ClienteDAO;
import com.example.entity.Cliente;
import com.example.entity.IdUser;
import com.example.entity.ProdutoCart;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloController implements Serializable{
    @FXML
    private Rectangle rec1;
    @FXML
    private Rectangle rec2;
    @FXML
    private Rectangle rec3;
    @FXML
    private Button buttonGoogle;
    @FXML
    private Button signUpButton;
    @FXML
    private ImageView imageViewer;
    @FXML
    private Button switchLayoutButton;
    @FXML
    private TextField tfEmailLogin;
    @FXML
    private PasswordField tfPasswordLogin;
    @FXML
    private TextField tfEmailRegister;
    @FXML
    private PasswordField tfPasswordRegister;
    @FXML
    private TextField tfUsernameRegister;
    @FXML
    private Button eyeRegister;
    @FXML
    private Button eyeLogin;
    @FXML
    private CheckBox rememberMe;

    private final String[] imagePaths = {"/imgs/perfume1.png", "/imgs/perfume2.png", "/imgs/perfume3.png"};
    private int currentImageIndex = 0;
    private FadeTransition fadeTransition;
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        fadeTransition = new FadeTransition(Duration.seconds(2), imageViewer);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        Duration duration = Duration.seconds(4);
        KeyFrame keyFrame = new KeyFrame(duration, e -> changeImage());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE); // Repete indefinidamente

        if (eyeLogin != null) {
            eyeLogin.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/imgs/closeEye.png")).toExternalForm())));
            VisiblePasswordFieldSkin skin = new VisiblePasswordFieldSkin(tfPasswordLogin, eyeLogin);
            tfPasswordLogin.setSkin(skin);
        } else if (eyeRegister != null) {
            eyeRegister.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/imgs/closeEye.png")).toExternalForm())));
            VisiblePasswordFieldSkin skin = new VisiblePasswordFieldSkin(tfPasswordRegister, eyeRegister);
            tfPasswordRegister.setSkin(skin);
        }
        timeline.play();

        Platform.runLater(() -> {
            carregarLog();
        });
    }

    private final FXMLLoader fxmlLoaderL = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
    private final FXMLLoader fxmlLoaderR = new FXMLLoader(HelloApplication.class.getResource("registerScreen.fxml"));
    private final FXMLLoader fxmlLoaderMainAdm = new FXMLLoader(HelloApplication.class.getResource("mainAdm.fxml"));
    private final FXMLLoader fxmlLoaderMainClient = new FXMLLoader(HelloApplication.class.getResource("mainClient.fxml"));
    private final FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loading.fxml"));

    @FXML
    void LoginClicked() {
        String email = tfEmailLogin.getText();
        String senha = tfPasswordLogin.getText();
        Stage stage2 = (Stage) switchLayoutButton.getScene().getWindow();

        AtomicBoolean navigationDone = new AtomicBoolean(false);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // Agendar a tarefa para mostrar a tela de loading após 500 milisegundos
        executor.schedule(() -> {
            if (!navigationDone.get()) {
                Platform.runLater(() -> load(stage2, fxmlLoader));
            }
        }, 500, TimeUnit.MILLISECONDS);

        Thread backgroundThread = new Thread(() -> {
            Integer loginSucesso = clienteDAO.verificarLogin(email, senha);
            Platform.runLater(() -> {
                if (!navigationDone.get()) {
                    if (loginSucesso != null) {
                        boolean isAdm = clienteDAO.verificarAdm(loginSucesso);
                        if (isAdm) {
                            showNotification("Login como Administrador bem Sucedido.", true);
                            load(stage2, fxmlLoaderMainAdm);
                            IdUser.setIduser(loginSucesso);
                        } else {
                            load(stage2, fxmlLoaderMainClient);
                            showNotification("Login bem Sucedido.", true);
                            IdUser.setIduser(loginSucesso);
                        }
                        if (rememberMe.isSelected()){
                            salvarLog(clienteDAO.get(Long.valueOf(loginSucesso)));
                        }
                    } else {
                        showNotification("Login Falhou.", false);
                        load(stage2, fxmlLoaderL);
                    }
                    navigationDone.set(true);
                }
            });
        });
        backgroundThread.start();
    }

    @FXML
    void RegisterClicked() {
        String nomeDeUsuario = tfUsernameRegister.getText();
        String email = tfEmailRegister.getText();
        String senha = tfPasswordRegister.getText();

        if (nomeDeUsuario.isBlank() || email.isBlank() || senha.isBlank()) {
            showNotification("Nome de usuário, email e senha são obrigatórios.", false);
            return;
        }

        if (!isValidEmail(email)) {
            showNotification("Email inválido. Digite um email válido.", false);
            return;
        }

        if (!isStrongPassword(senha)) {
            showNotification("Senha fraca. A senha deve incluir 8 caracteres, 1 letra maiuscula e um caracter especial.", false);
            return;
        }

        if (nomeDeUsuario.length() <= 7){
            showNotification("Seu nome de usuario deve ter pelomenos 8 caracteres.", false);
            return;
        }

        Stage stage2 = (Stage) switchLayoutButton.getScene().getWindow();

        AtomicBoolean navigationDone = new AtomicBoolean(false);

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        // Agendar a tarefa para mostrar a tela de loading após 500 milisegundos
        executor.schedule(() -> {
            if (!navigationDone.get()) {
                Platform.runLater(() -> load(stage2, fxmlLoader));
            }
        }, 500, TimeUnit.MILLISECONDS);

        Thread backgroundThread = new Thread(() -> {
            Boolean nomeRepetido = clienteDAO.verificarNomeDeUsuarioExistente(nomeDeUsuario);
            Boolean emailRepetido = clienteDAO.verificarEmailExistente(email);
            Platform.runLater(() -> {
                if (!navigationDone.get()) {
                    if (nomeRepetido == null){
                        load(stage2, fxmlLoaderR);
                        return;
                    }
                    if (emailRepetido == null){
                        load(stage2, fxmlLoaderR);
                        return;
                    }
                    if (nomeRepetido) {
                        showNotification("Nome de usuário já está em uso. Escolha outro nome de usuário.", false);
                        navigationDone.set(true);
                        load(stage2, fxmlLoaderR);
                        return;
                    }
                    if (emailRepetido) {
                        showNotification("Email já está em uso. Escolha outro email.", false);
                        navigationDone.set(true);
                        load(stage2, fxmlLoaderR);
                        return;
                    }
                    int insercaoSucesso = clienteDAO.save(new Cliente(nomeDeUsuario, email, senha));
                    if (insercaoSucesso == 1) {
                        showNotification("Cadastro bem-sucedido", true);
                        load(stage2, fxmlLoaderR);
                    } else {
                        showNotification("Erro ao cadastrar o cliente", false);
                        load(stage2, fxmlLoaderR);
                    }
                    navigationDone.set(true);
                }
            });
        });
        backgroundThread.start();
    }

    private void load(Stage stage2, FXMLLoader fxmlLoader){
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            Stage finalStage = stage2;
            scene.setOnMouseDragged(event -> {
                if (event.getSceneY() <= 100) {
                    finalStage.setX(event.getScreenX() - xOffset);
                    finalStage.setY(event.getScreenY() - yOffset);
                }
            });
            if (stage2 != null) {
                stage2.setScene(scene);
                stage2.setResizable(false);
                scene.setFill(Color.TRANSPARENT);
                stage2.initStyle(StageStyle.TRANSPARENT);
                stage2.show();
            }
        } catch (IllegalStateException ignore){}
        catch (Exception e) {e.printStackTrace();}
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@([\\w-]+\\.)+[A-Za-z]{2,4}$");
    }
    private boolean isStrongPassword(String senha) {
        if (senha.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;

        for (char c : senha.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasUpperCase && hasLowerCase && hasDigit) {
                return true;
            }
        }

        return false;
    }

    @FXML
    void mouseEntered(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        sourceNode.setStyle("-fx-text-fill: #027fe9;");
    }

    @FXML
    void mouseExited(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();
        sourceNode.setStyle("-fx-text-fill:" + sourceNode.getId() + ";");
    }

    @FXML
    void ButtonGoogle() {
        buttonGoogle.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1.03;
            -fx-scale-Y: 1.03;
        """);
    }

    @FXML
    void changeToRegister() {
        navigateTo("registerScreen.fxml");
    }

    @FXML
    void changeToLogin() {
        navigateTo("loginScreen.fxml");
    }

    @FXML
    void ButtonGoogleExited() {
        buttonGoogle.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1;
            -fx-scale-Y: 1;
        """);
    }

    @FXML
    void signUpButton() {
        signUpButton.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1.03;
            -fx-scale-Y: 1.03;
        """);
    }

    @FXML
    void signUpButtonExited() {
        signUpButton.setStyle("""
            transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;
            -fx-scale-x: 1;
            -fx-scale-Y: 1;
        """);
    }

    @FXML
    void closeClicked() {
        Platform.exit();
        System.exit(0);
    }

    private void changeImage() {
        if (imageViewer != null) {
            currentImageIndex = (currentImageIndex + 1) % imagePaths.length;
            Image newImage = new Image(Objects.requireNonNull(getClass().getResource(imagePaths[currentImageIndex])).toExternalForm());

            imageViewer.setImage(newImage);
            imageViewer.setOpacity(0.0);

            fadeTransition.setFromValue(0.0);
            fadeTransition.setToValue(1.0);

            fadeTransition.playFromStart();

            switch (currentImageIndex) {
                case 0 -> {
                    rec1.setFill(Color.WHITE);
                    rec2.setFill(Color.rgb(194, 194, 199));
                    rec3.setFill(Color.rgb(194, 194, 199));

                    rec1.setWidth(36);
                    rec2.setWidth(22);
                    rec3.setWidth(22);

                    rec1.setLayoutX(215);
                    rec2.setLayoutX(255);
                    rec3.setLayoutX(281);

                    imageViewer.setFitHeight(437);
                    imageViewer.setFitWidth(437);
                    imageViewer.setY(0);
                }
                case 1 -> {
                    rec1.setFill(Color.rgb(194, 194, 199));
                    rec2.setFill(Color.WHITE);
                    rec3.setFill(Color.rgb(194, 194, 199));

                    rec1.setWidth(22);
                    rec2.setWidth(36);
                    rec3.setWidth(22);

                    rec1.setLayoutX(215);
                    rec2.setLayoutX(241);
                    rec3.setLayoutX(281);

                    imageViewer.setFitHeight(437);
                    imageViewer.setFitWidth(437);
                    imageViewer.setY(0);
                }
                case 2 -> {
                    rec1.setFill(Color.rgb(194, 194, 199));
                    rec2.setFill(Color.rgb(194, 194, 199));
                    rec3.setFill(Color.WHITE);

                    rec1.setWidth(22);
                    rec2.setWidth(22);
                    rec3.setWidth(36);

                    rec1.setLayoutX(215);
                    rec2.setLayoutX(241);
                    rec3.setLayoutX(267);

                    imageViewer.setFitHeight(585);
                    imageViewer.setFitWidth(437);
                    imageViewer.setY(-70);
                }
            }
        }
    }

    private void navigateTo(String fxmlPath) {
        try {
            Stage stage = (Stage) switchLayoutButton.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(fxmlLoader.load());
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            Stage finalStage = stage;
            scene.setOnMouseDragged(event -> {
                if (event.getSceneY() <= 100) {
                    finalStage.setX(event.getScreenX() - xOffset);
                    finalStage.setY(event.getScreenY() - yOffset);
                }
            });

            if (stage != null) {
                stage.setScene(scene);
                stage.setResizable(false);
                scene.setFill(Color.TRANSPARENT);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
            }
        } catch (IllegalStateException ignore){}
        catch (Exception e) {e.printStackTrace();}
    }

    @FXML
    private void productClicked(){
        abrirLinkNoNavegador("https://daivu.netlify.app/#produtos");
    }
    @FXML
    private void storeClicked(){
        abrirLinkNoNavegador("https://daivu.netlify.app");
    }
    @FXML
    private void aboutClicked(){
        abrirLinkNoNavegador("https://daivu.netlify.app/#sobre");
    }


    private void abrirLinkNoNavegador(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            // Lidar com erros, como a falta de suporte ao Desktop
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao abrir o link");
            alert.setHeaderText(null);
            alert.setContentText("Ocorreu um erro ao tentar abrir o link.");

            alert.showAndWait();
        }
    }

    private void showNotification(String message, boolean isSuccess) {
        Notifier notifier = new Notifier(message, isSuccess);
        notifier.show();
    }

    private void salvarLog(Cliente cliente) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("logAccount.ser"))) {
            out.writeObject(cliente);
            System.out.println("Lista de produtos salva com sucesso.");
            System.out.println(cliente.getSenha() + " " + cliente.getEmail());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    private void carregarLog() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("logAccount.ser"))) {
            Cliente cliente = (Cliente) in.readObject();
            if (!cliente.getEmail().isBlank()){
                boolean isAdm = clienteDAO.verificarAdm(Math.toIntExact(cliente.getId()));
                Stage stage2 = (Stage) switchLayoutButton.getScene().getWindow();
                if (isAdm) {
                    showNotification("Login como Administrador bem Sucedido.", true);
                    load(stage2, fxmlLoaderMainAdm);
                    IdUser.setIduser(Math.toIntExact(cliente.getId()));
                } else {
                    load(stage2, fxmlLoaderMainClient);
                    showNotification("Login bem Sucedido.", true);
                    IdUser.setIduser(Math.toIntExact(cliente.getId()));
                }
            }
        } catch (FileNotFoundException e){}
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
