package com.example.sprintjava;

import com.example.components.Notifier;
import com.example.dao.ClienteDAO;
import com.example.dao.ProdutoDAO;
import com.example.entity.Cliente;
import com.example.entity.IdUser;
import com.example.entity.Produto;
import com.example.entity.ProdutoCart;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainClientController{
    @FXML
    private TextField MaxPrice;
    @FXML
    private TextField MinPrice;
    @FXML
    private Label bReset;
    @FXML
    private Label btApply;
    @FXML
    private Rectangle rectangle;
    @FXML
    private Pane root;
    @FXML
    private Pane paneCard;
    @FXML
    private FlowPane container;
    @FXML
    private FlowPane containerCart;
    @FXML
    private TextField tfSearch;
    @FXML
    private Pane filterPane;
    @FXML
    private Label qntItems;
    @FXML
    private Label totalPrice;
    @FXML
    private Pane accountPane;
    @FXML
    private Label email;
    @FXML
    private Label username;
    @FXML
    private FlowPane containerCheck;
    @FXML
    private AnchorPane ap;

    private final ProdutoDAO productDAO = new ProdutoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private List<Produto> productList;
    private Boolean isSelected = false;
    private ArrayList<ProdutoCart> produtosCarrinho = new ArrayList<>();

    //COMPONENTS CARD
    private AnchorPane card;
    private Button cart;
    private ImageView imageView;

    //FILTER
    private boolean isOpen = false;
    private Integer minP=0;
    private Integer maxP=0;
    private boolean listenersRegistered = false;
    private List<Produto> filterResults;
    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private final ArrayList<CheckBox> listCheckBox = new ArrayList<>();

    @FXML
    private void searchPressed() {
        String searchTerm = tfSearch.getText().trim();
        List<Produto> searchResults = searchTerm.isEmpty() ? filterResults : performSearch(searchTerm);
        displaySearchResults(searchResults);
    }

    @FXML
    private void initialize(){
        if (root != null){
            loadPage("mainClientPanel",160);
        }
        productList = productDAO.getAllProdutos();
        filterResults = productList;
        Platform.runLater(() -> {
            createProductCards();
            createCardsCart();
            createCheckBox();
            if (ap != null) {
                Scene scene = ap.getScene();
                scene.setOnMouseClicked((MouseEvent event) -> {
                    Double xScene = event.getX();
                    Double yScene = event.getY();

                    if (xScene <= 81 || xScene >= 281 || yScene <= 500 || yScene >= 580 && accountPane.isVisible()){
                        accountPane.setVisible(false);
                    }
                });
            }
        });
    }

    @FXML
    private void closeClicked(){
        Platform.exit();
        System.exit(0);
    }

    private void createProductCards() {
        if (container != null){
            for (Produto product : productList) {
                createProductCard(product);
            }
        }
    }
    private void createCardsCart() {
        if (containerCart != null) {
            containerCart.getChildren().clear();
            produtosCarrinho = carregarListaDoArquivo();
            for (ProdutoCart produto : produtosCarrinho) {
                createCardCart(produto);
            }
        }
    }
    private void updateCardCart(ProdutoCart newProdutoCart) {
        for (int i = 0; i < produtosCarrinho.size(); i++) {
            ProdutoCart produtoCart = produtosCarrinho.get(i);
            if (Objects.equals(produtoCart.getProduto().getId(), newProdutoCart.getProduto().getId())) {
                produtosCarrinho.set(i, newProdutoCart);
                salvarListaEmArquivo();
                createCardsCart();
                break;
            }
        }
        salvarListaEmArquivo();
    }

    private void createCheckBox(){
        if (containerCheck != null) {
            ArrayList<String> listCheckS = productDAO.getAllUniqueBrands();
            containerCheck.setVgap(5);
            for (String string : listCheckS) {
                CheckBox checkBox = new CheckBox(string);
                checkBox.setStyle("""
                        -fx-background-color:  transparent;
                        -fx-focus-traversable:  false;
                        -fx-font-weight: 800;
                        -fx-font-family: 'Inter', sans-serif;
                        -fx-font-style: normal;
                        -fx-line-height: normal;
                        -fx-font-size: 18px;
                        """);
                listCheckBox.add(checkBox);
                containerCheck.getChildren().add(checkBox);
            }
        }
    }

    private void createProductCard(Produto product) {
        AnchorPane card = new AnchorPane();
        card.setMaxHeight(269.0);
        card.setMaxWidth(233.0);
        card.setMinHeight(269.0);
        card.setMinWidth(233.0);
        card.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 7px; -fx-border-radius: 7px;  -fx-end-margin: 10px;");
        card.setPadding(new Insets(0, 8, 0, 0));

        container.setHgap(7);
        container.setVgap(10);

        Produto finalProduct = getCachedProduct(product);

        Label brandLabel = createLabel(product.getMarca(), 5.0, 4.0, "700", 24, "#000");
        Label descriptionLabel = createLabel(product.getDescricao(), 5.0, 34.0, null, 17, "#8bb57a");
        Label mlLabel = createLabel(product.getMl(), 5.0, 234.0, null, 16, "#000");
        Label priceLabel = createLabel("R$ " + product.getPreco(), 120.0, 228.0, "700", 20, "#000");

        card.getChildren().addAll(brandLabel, descriptionLabel, mlLabel, priceLabel);

        loadProductImageAsync(product.getLinkImagem(), card);

        container.getChildren().add(card);

        card.setOnMouseClicked(event -> {
            if (!isSelected) {
                selectedCard(finalProduct);
            }
        });

        card.setOnMouseEntered(mouseEvent -> card.setStyle(card.getStyle()+ """
        transition: -fx-background-color 1s ease,
        -fx-scale-x 1s ease,
        -fx-scale-y 1s ease;

        -fx-scale-x: 1.01;
        -fx-scale-Y: 1.01;
        -fx-background-color: #c2c0c0;
                """));
        card.setOnMouseExited(mouseEvent -> card.setStyle(card.getStyle()+ """
        transition: -fx-background-color 1s ease,
        -fx-scale-x 1s ease,
        -fx-scale-y 1s ease;

        -fx-scale-x: 1;
        -fx-scale-Y: 1;
        -fx-background-color: #D9D9D9;
                """));
    }
    private void createCardCart(ProdutoCart produtoCarrinho){
        AnchorPane root = new AnchorPane();
        root.setPrefSize(754, 125);

        Label precoPerfume = createLabel("R$ " + produtoCarrinho.getProduto().getPreco() * produtoCarrinho.getQuantidadeProduto(), 662, 43, "700", 24, "#000");
        Label marcaPefume = createLabel(produtoCarrinho.getProduto().getMarca(), 144, 24, "700", 24, "#000");
        Label nomePerfume = createLabel(produtoCarrinho.getProduto().getDescricao(), 145, 55, "700", 16, "#7bac69");
        Label quantidadeML = createLabel(produtoCarrinho.getProduto().getMl(), 145, 74, "700", 17, "#000");
        Label quantidadePerfume = createLabel(String.valueOf(produtoCarrinho.getQuantidadeProduto()), 463, 45, "700", 24, "#000");

        // Crie Botões
        Button botaoMenos = new Button();
        botaoMenos.setLayoutX(406);
        botaoMenos.setLayoutY(44);
        botaoMenos.setStyle("-fx-border-radius: 100%; -fx-border-color: transparent; -fx-background-radius: 100%; -fx-background-color: #D9D9D9" );
        botaoMenos.setPrefSize(37, 35);
        botaoMenos.getStyleClass().add("botao-menos");

        Button botaoMais = new Button();
        botaoMais.setLayoutX(495);
        botaoMais.setLayoutY(44);
        botaoMais.setPrefSize(37, 35);
        botaoMais.setStyle("-fx-border-radius: 100%; -fx-border-color: transparent; -fx-background-radius: 100%; -fx-background-color: #D9D9D9" );
        botaoMais.getStyleClass().add("botao-mais");

        // Crie Retângulos
        Rectangle retangulo1 = createRectangle(14, 3.4, 418, 60);
        Rectangle retangulo2 = createRectangle(14, 3.4, 507, 60);
        Rectangle retangulo3 = createRectangle(3.4, 14, 512, 55);

        botaoMais.setOnMouseClicked(mouseEvent -> moreButton(produtoCarrinho));
        retangulo2.setOnMouseClicked(mouseEvent -> moreButton(produtoCarrinho));
        retangulo3.setOnMouseClicked(mouseEvent -> moreButton(produtoCarrinho));

        botaoMenos.setOnMouseClicked(mouseEvent -> lessButton(produtoCarrinho));
        retangulo1.setOnMouseClicked(mouseEvent -> lessButton(produtoCarrinho));

        Image cachedImage = ImageCache.getImageByURL(produtoCarrinho.getProduto().getLinkImagem());
        ImageView imageView = new ImageView(cachedImage);
        imageView.setFitWidth(59);
        imageView.setFitHeight(97);
        imageView.setLayoutX(13);
        imageView.setLayoutY(14);

        root.getChildren().addAll(
                precoPerfume,
                marcaPefume,
                nomePerfume,
                quantidadeML,
                quantidadePerfume,
                botaoMenos,
                botaoMais,
                retangulo1,
                retangulo2,
                retangulo3,
                imageView
        );

        updateLabelsCart();

        Platform.runLater(() -> containerCart.getChildren().add(root));
    }

    private void moreButton(ProdutoCart produtoCarrinho){
        if (produtoCarrinho.getQuantidadeProduto() < 10) {
            produtoCarrinho.setQuantidadeProduto(produtoCarrinho.getQuantidadeProduto() + 1);
            updateCardCart(produtoCarrinho);
            updateLabelsCart();
            return;
        }
        updateLabelsCart();
        showNotification("Numero maximo do mesmo produto atingido", false);
    }
    private void lessButton(ProdutoCart produtoCarrinho){
        if (produtoCarrinho.getQuantidadeProduto() > 1) {
            produtoCarrinho.setQuantidadeProduto(produtoCarrinho.getQuantidadeProduto() - 1);
            updateCardCart(produtoCarrinho);
            updateLabelsCart();
               return;
        }
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Deseja Excluir?");
        confirmationDialog.setHeaderText(null);
        confirmationDialog.setContentText("Você tem certeza que deseja excluir esse item do carrinho?");

        Optional<ButtonType> result = confirmationDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            produtosCarrinho.remove(produtoCarrinho);
            salvarListaEmArquivo();
            createCardsCart();
            updateLabelsCart();
            showNotification("Produto removido do carrinho", true);
        }
    }

    private void updateLabelsCart() {
        double totalPriceValue = 0.0;
        int totalQuantity = 0;

        for (ProdutoCart produtoCart : produtosCarrinho) {
            double productPrice = produtoCart.getProduto().getPreco();
            int quantity = produtoCart.getQuantidadeProduto();

            totalPriceValue += (productPrice * quantity);
            totalQuantity += quantity;
        }

        // Atualize as Labels com os valores calculados
        int finalTotalQuantity = totalQuantity;
        double finalTotalPriceValue = totalPriceValue;
        Platform.runLater(() -> {
            totalPrice.setText("R$: " + String.format("%.2f", finalTotalPriceValue));
            qntItems.setText(finalTotalQuantity + " items");
        });
    }

    private Rectangle createRectangle(double w, double h, double x, double y){
        Rectangle rectangle = new Rectangle(w, h);
        rectangle.setLayoutX(x);
        rectangle.setLayoutY(y);
        rectangle.setStroke(Color.BLACK);
        rectangle.setArcHeight(6);
        rectangle.setArcWidth(6);
        rectangle.setStrokeWidth(0);
        return rectangle;
    }
    private void selectedCard(Produto product){
        isSelected = true;

        card = new AnchorPane();
        card.setPrefHeight(333.0);
        card.setPrefWidth(262.0);
        card.setStyle("-fx-background-color: #777777; -fx-background-radius: 7px; -fx-border-radius: 7px;  -fx-end-margin: 10px");
        paneCard.setMouseTransparent(false);

        Label brandLabel = createLabel(product.getMarca(), 0, 4.0, "700", 24, "#000");
        Label descriptionLabel = createLabel(product.getDescricao(), 0, 34.0, "700", 17, "#8bb57a");
        Label mlLabel = createLabel(product.getMl(), 36.0, 234.0, "700", 16, "#000");
        Label priceLabel = createLabel("R$ " + product.getPreco(), 146.0, 228.0, "700", 20, "#000");

        brandLabel.setMaxWidth(262);
        brandLabel.setMinWidth(262);
        brandLabel.setAlignment(Pos.CENTER);

        descriptionLabel.setMaxWidth(262);
        descriptionLabel.setMinWidth(262);
        descriptionLabel.setAlignment(Pos.CENTER);

        card.getChildren().add(brandLabel);
        card.getChildren().add(descriptionLabel);
        card.getChildren().add(mlLabel);
        card.getChildren().add(priceLabel);

        imageView = new ImageView(product.getLinkImagem());
        imageView.setFitWidth(93);
        imageView.setFitHeight(152);
        imageView.setLayoutX(87);
        imageView.setLayoutY(58.5);
        card.getChildren().add(imageView);

        cart = createButton("Add to Cart", "#7BAC69", 131,
                () -> addToCart(product, product.getDescricao() + " Adicionado ao carrinho"));
        cart.setStyle(cart.getStyle()+"-fx-border-radius: 0 0 7 0; -fx-background-radius: 0 0 7 0; -fx-border-width: 1 0 0 0.5");
        card.getChildren().add(cart);

        Button cancel = createButton("Cancel", "#FF4D4D", 0, () -> {
            card.setVisible(false);
            paneCard.setMouseTransparent(true);
            isSelected = false;
        });
        cancel.setStyle(cancel.getStyle()+"-fx-border-radius: 0 0 0 7; -fx-background-radius: 0 0 0 7; -fx-border-width: 1 0.5 0 0");
        card.getChildren().add(cancel);

        if (paneCard!= null) {
            paneCard.setVisible(true);
            card.setLayoutX(0);
            card.setLayoutY(0);
            paneCard.getChildren().add(card);
        }

        card.setOnMouseClicked(mouseEvent -> {
            double mouseX = mouseEvent.getSceneX();
            double mouseY = mouseEvent.getSceneY();

            double buttonMinX = card.localToScene(cart.getBoundsInLocal()).getMinX();
            double buttonMaxX = card.localToScene(cart.getBoundsInLocal()).getMaxX();
            double buttonMinY = card.localToScene(cart.getBoundsInLocal()).getMinY();
            double buttonMaxY = card.localToScene(cart.getBoundsInLocal()).getMaxY();

            if (!(mouseX >= buttonMinX && mouseX <= buttonMaxX && mouseY >= buttonMinY && mouseY <= buttonMaxY)) {
                card.setVisible(false);
                paneCard.setMouseTransparent(true);
                isSelected = false;
            }
        });
    }

    @FXML
    void accountClicked() {
        Cliente clienteLogado = clienteDAO.get(Long.parseLong(IdUser.getIduser().toString()));
        username.setText("Usuário: "+ clienteLogado.getNomeDeUsuario());
        email.setText("Email: " + clienteLogado.getEmail());

        if (accountPane.isVisible()){
            accountPane.setVisible(false);
            return;
        }
        accountPane.setVisible(true);
    }
    @FXML
    void backClicked(){
        //APAGAR ARQUIVO
        try {
            File file = new File("logAccount.ser");
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("Arquivo logAccount.ser apagado com sucesso.");
                } else {
                    System.err.println("Não foi possível apagar o arquivo.");
                }
            } else {
                System.err.println("O arquivo logAccount.ser não existe.");
            }
        } catch (Exception ignored) {}

        navigateTo();
    }
    @FXML
    private void homeClicked() {
        loadPage("mainClientPanel",160);
        rectangle.setLayoutX(12);
        rectangle.setLayoutY(31);
    }

    @FXML
    private void storeClicked() {
        loadPage("storeClientPanel",100);
        rectangle.setLayoutX(12);
        rectangle.setLayoutY(92);
    }

    @FXML
    private void cartClicked() {
        loadPage("cartClientPanel", 40);
        rectangle.setLayoutX(12);
        rectangle.setLayoutY(154);
    }

    @FXML
    private void removeClicked(){
        produtosCarrinho.clear();
        salvarListaEmArquivo();
        containerCart.getChildren().clear();
        updateLabelsCart();
    }

    @FXML
    private void filterClicked() {
        configureTextFieldFormatter(MaxPrice);
        configureTextFieldFormatter(MinPrice);

        if (!listenersRegistered) {
            registerFilterListeners();
            listenersRegistered = true;
        }
        if (isOpen) {
            performFilter();
            filterPane.setVisible(false);
            isOpen = false;
        } else {
            container.setMouseTransparent(true);
            filterPane.setVisible(true);
            isOpen = true;
        }
    }

    private void registerFilterListeners() {
        bReset.setOnMouseClicked(e -> {
            for (CheckBox checkBox: listCheckBox) {
                checkBox.setSelected(false);
            }
            MinPrice.setText("");
            MaxPrice.setText("");
            minP = null;
            maxP = null;
            filterResults = productList;
        });

        btApply.setOnMouseClicked(e -> performFilter());
    }

    private void performFilter(){
        ArrayList<String> listCheckS = new ArrayList<>();
        minP = MinPrice.getText().isBlank() ? null : Integer.parseInt(MinPrice.getText());
        maxP = MaxPrice.getText().isBlank() ? null : Integer.parseInt(MaxPrice.getText());
        for (CheckBox checkBox : listCheckBox) {
            if (checkBox.isSelected()){
                listCheckS.add(checkBox.getText());
            }
        }
        displaySearchResults(performFilter(listCheckS, minP, maxP));

        filterPane.setVisible(false);
        container.setMouseTransparent(false);
    }
    private List<Produto> performFilter(ArrayList<String> listCheckS, Integer minPrice, Integer maxPrice) {
        filterResults = new ArrayList<>();
        for (Produto product : productList) {
            if (minPrice == null && maxPrice != null){
                minPrice = 0;
                MinPrice.setText("0");
            }
            if (minPrice != null && maxPrice == null) {
                maxPrice = 99999;
                MinPrice.setText("99999");
            }
            if (minPrice != null && !listCheckS.isEmpty()){
                int productPrice = (int) Math.round(product.getPreco());
                if (listCheckS.contains(product.getMarca()) && productPrice >= minPrice && productPrice <= maxPrice) {
                    filterResults.add(product);
                }
            }
            else if (minPrice != null){
                int productPrice = (int) Math.round(product.getPreco());
                if (productPrice >= minPrice && productPrice <= maxPrice) {
                    filterResults.add(product);
                }
            }
            else if(!listCheckS.isEmpty()){
                if (listCheckS.contains(product.getMarca())) {
                    filterResults.add(product);
                }
            }
            else {
                filterResults = productList;
            }
        }
        return filterResults;
    }

    private void loadPage(String page, int x){
        Parent newPane = null;

        try {
            newPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(page + ".fxml")));
        } catch (IOException ex){
            Logger.getLogger(MainClientController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (newPane != null) {
            root.getChildren().clear();
            newPane.setLayoutX(x);
            newPane.setLayoutY(20);
            root.getChildren().add(newPane);
        }
    }

    private Label createLabel(String text, double layoutX, double layoutY, String fontWeight, int fontSize, String color) {
        Label label = new Label(text);
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);

        if (fontWeight != null) {
            label.setStyle("-fx-font-weight: " + fontWeight + "; -fx-font-size: " + fontSize + "px; -fx-text-fill: " + color);
        } else {
            label.setStyle("-fx-font-size: " + fontSize + "px; -fx-text-fill: " + color);
        }

        return label;
    }
    private Produto getCachedProduct(Produto product) {
        Produto cachedProduct = DataCache.getProductById(product.getId());
        return cachedProduct != null ? cachedProduct : cacheAndReturnProduct(product);
    }
    private Produto cacheAndReturnProduct(Produto product) {
        DataCache.cacheProduct(product);
        return product;
    }

    private void addToCart(Produto product, String message) {
        produtosCarrinho = carregarListaDoArquivo();
        ProdutoCart existingProduct = findProductInCart(product);
        if (existingProduct != null) {
            if (existingProduct.getQuantidadeProduto() < 10) {
                existingProduct.setQuantidadeProduto(existingProduct.getQuantidadeProduto() + 1);
                salvarListaEmArquivo();
                showNotification(message, true);
                card.setVisible(false);
                paneCard.setMouseTransparent(true);
                isSelected = false;
                return;
            }
            showNotification("Numero maximo do mesmo produto atingido", false);
        } else {
            if (produtosCarrinho.size() < 5){
                produtosCarrinho.add(new ProdutoCart(product, 1));
                salvarListaEmArquivo();
                showNotification(message, true);
                card.setVisible(false);
                paneCard.setMouseTransparent(true);
                isSelected = false;
                return;
            }
            showNotification("O numero maximo de produtos no carrinho é 5", false);
        }
        salvarListaEmArquivo();
    }

    private ProdutoCart findProductInCart(Produto product) {
        for (ProdutoCart item : produtosCarrinho) {
            if (Objects.equals(item.getProduto().getId(), product.getId())) {
                return item;
            }
        }
        return null;
    }

    private Button createButton(String text, String backgroundColor, double layoutX, Runnable onClick) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + backgroundColor + "; -fx-border-color: black; -fx-font-size: " + 17 + "px;");
        button.setLayoutX(layoutX);
        button.setLayoutY(280);
        button.setPrefWidth(131);
        button.setPrefHeight(53);
        button.setOnMouseClicked(mouseEvent -> Platform.runLater(onClick));
        return button;
    }
    private void loadProductImageAsync(String imageURL, AnchorPane card) {
        Image cachedImage = ImageCache.getImageByURL(imageURL);

        if (cachedImage != null) {
            displayImage(cachedImage, card);
        } else {
            ImageCache.cacheImage(imageURL);

            Thread imageLoadThread = new Thread(() -> {
                Image image = new Image(imageURL, true);
                image.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() >= 1.0) {
                        Platform.runLater(() -> displayImage(image, card));
                    }
                });
            });
            imageLoadThread.setDaemon(true);
            imageLoadThread.start();
        }
    }

    private void displayImage(Image image, AnchorPane card) {
        imageView = new ImageView(image);
        imageView.setFitWidth(93);
        imageView.setFitHeight(152);
        imageView.setLayoutX(62.5);
        imageView.setLayoutY(58.5);
        card.getChildren().add(imageView);
    }

    private List<Produto> performSearch(String searchTerm) {
        List<Produto> searchResults = new ArrayList<>();
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        for (Produto product : filterResults) {
            if (product.getDescricao().toLowerCase().contains(lowerCaseSearchTerm)) {
                searchResults.add(product);
            }
        }
        return searchResults;
    }

    private void displaySearchResults(List<Produto> searchResults) {
        container.getChildren().clear();
        searchResults.forEach(this::createProductCard);
    }

    private void navigateTo() {
        try {
            stage = (Stage) root.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            scene.setOnMouseDragged(event -> {
                if (event.getSceneY() <= 100) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
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

    private void salvarListaEmArquivo() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("produtosCarrinho.ser"))) {
            out.writeObject(produtosCarrinho);
            System.out.println("Lista de produtos salva com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    private ArrayList<ProdutoCart> carregarListaDoArquivo() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("produtosCarrinho.ser"))) {
            return (ArrayList<ProdutoCart>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void configureTextFieldFormatter(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]{0,4}")) {
                return change;
            }
            return null; // Retorna null para rejeitar a mudança
        };

        // Aplica o operador usando um TextFormatter
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    private void showNotification(String message, boolean isSuccess) {
        Notifier notifier = new Notifier(message, isSuccess);
        notifier.show();
    }

    //***ESTILIZACAO***
    @FXML
    void mouseEntered(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();

        sourceNode.setStyle("""
         transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;

            -fx-scale-x: 1.03;
            -fx-scale-Y: 1.03;
        """);
    }

    @FXML
    void mouseExited(MouseEvent event) {
        Node sourceNode = (Node) event.getSource();

        sourceNode.setStyle("""
         transition: -fx-background-color 1s ease,
            -fx-scale-x 1s ease,
            -fx-scale-y 1s ease;

            -fx-scale-x: 1;
            -fx-scale-Y: 1;
        """);
    }
}