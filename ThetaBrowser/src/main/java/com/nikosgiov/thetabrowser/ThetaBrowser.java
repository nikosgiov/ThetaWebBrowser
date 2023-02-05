package com.nikosgiov.thetabrowser;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ThetaBrowser extends Application {

    private WebEngine webEngine;
    private TabPane tabPane;
    private TextField urlBar;
    private HBox navigationBar;
    private MenuButton historyButton = new MenuButton("History");
    private List<String> historyList = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        createNavBar();
        BorderPane root = new BorderPane();
        root.setTop(navigationBar);
        root.setCenter(createTabPane());

        // Create the scene
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm()); // add the CSS file to the scene

        // Show the stage
        stage.setScene(scene);
        stage.setTitle("ThetaWebBrowser");
        Image icon = new Image(getClass().getResourceAsStream("logo.png"));
        stage.getIcons().add(icon);
        stage.show();
    }

    private void createNavBar(){
        // Load the icons from the "resources" folder
        Image backIcon = new Image(getClass().getResourceAsStream("previous.png"));
        Image forwardIcon = new Image(getClass().getResourceAsStream("next.png"));
        Image refreshIcon = new Image(getClass().getResourceAsStream("refresh.png"));
        Image logoIcon = new Image(getClass().getResourceAsStream("logo.png"));
        Image plusIcon = new Image(getClass().getResourceAsStream("plus.png"));
        ImageView backImageView = new ImageView(backIcon);
        backImageView.setFitHeight(20);
        backImageView.setFitWidth(20);
        ImageView forwardImageView = new ImageView(forwardIcon);
        forwardImageView.setFitHeight(20);
        forwardImageView.setFitWidth(20);
        ImageView refrshImageView = new ImageView(refreshIcon);
        refrshImageView.setFitHeight(20);
        refrshImageView.setFitWidth(20);
        ImageView plusImageView = new ImageView(plusIcon);
        plusImageView.setFitHeight(20);
        plusImageView.setFitWidth(20);
        ImageView logoView = new ImageView(logoIcon);
        logoView.setFitHeight(30);
        logoView.setFitWidth(30);

        // Create the back button with the icon
        Button backButton = new Button();
        backButton.setGraphic(backImageView);
        backButton.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        backButton.setOnAction(e -> webEngine.executeScript("history.back()"));

        // Create the forward button with the icon
        Button forwardButton = new Button();
        forwardButton.setGraphic(forwardImageView);
        forwardButton.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        forwardButton.setOnAction(e -> webEngine.executeScript("history.forward()"));

        // Create the refresh button with the icon
        Button refreshButton = new Button();
        refreshButton.setGraphic(refrshImageView);
        refreshButton.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        refreshButton.setOnAction(e -> webEngine.reload());
        // Create add tab button
        Button addTabButton = new Button();
        addTabButton.setGraphic(plusImageView);
        addTabButton.setOnAction(e -> addNewTab("https://www.google.com"));
        // Create Clear History Button
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                historyList.clear();
                historyButton.getItems().clear();
            }
        });

        // Create the URL bar
        urlBar = new TextField();
        urlBar.setPrefWidth(500);
        urlBar.setOnAction(e -> {
            String searchTerm = urlBar.getText();
            String googleSearchUrl = "https://www.google.com/search?q=" + searchTerm;
            webEngine.load(googleSearchUrl);
        });

        // Create the navigation bar
        navigationBar = new HBox(logoView, backButton, forwardButton, refreshButton,addTabButton, urlBar, historyButton, clearButton);
        navigationBar.setPrefHeight(20);
        navigationBar.setAlignment(Pos.TOP_LEFT);
        navigationBar.setSpacing(10);
        navigationBar.setPadding(new Insets(5));
        navigationBar.setId("hboxID");
        navigationBar.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    }

    private TabPane createTabPane() {
        tabPane = new TabPane();
        addNewTab("https://www.google.com");
        return tabPane;
    }
    private void addNewTab(String url) {
        Tab tab = new Tab();
        tab.setClosable(true);
        tab.setContent(createBrowser(url));
        tab.setOnClosed(e -> deleteTab(tab));
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private WebView createBrowser(String url) {
        // Create the web view
        WebView webView = new WebView();
        webEngine = webView.getEngine();
        // Load Google as default
        webEngine.load("https://www.google.com");
        webEngine.titleProperty().addListener((observable, oldValue, newValue) -> {
            updateTabTitle(webView, newValue);
        });
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            urlBar.setText(newValue);
        });
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (!historyList.contains(newValue)) {
                historyList.add(newValue);
                MenuItem item = new MenuItem(newValue);
                item.setOnAction(event -> webEngine.load(item.getText()));
                historyButton.getItems().add(item);
            }
        });
        return webView;
    }

    private void loadPage(String url) {
        WebView currentBrowser = (WebView) tabPane.getSelectionModel().getSelectedItem().getContent();
        WebEngine webEngine = currentBrowser.getEngine();
        webEngine.load(url);
    }

    private void deleteTab(Tab tab) {
        tabPane.getTabs().remove(tab);
    }

    private void updateTabTitle(WebView browser, String title) {
        Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
        selectedTab.setText(title);
        selectedTab.setContent(browser);
    }
}
