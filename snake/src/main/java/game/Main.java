package game;

import proto.SnakesProto;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends Application {

    private ConcurrentHashMap<HostInfo, SnakesProto.GameMessage.AnnouncementMsg> sessionInfoMap = new ConcurrentHashMap<>();
    private Discoverer discoverer;

    private String name;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        if(!LoginWindow.display()) {
            return;
        }

        name = LoginWindow.getName();

        stage.setTitle("Snake game\t    " + name);

        Thread t;
        try {
            discoverer = new Discoverer(sessionInfoMap);

            t = new Thread(discoverer);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        stage.setOnCloseRequest(actionEvent -> t.interrupt() );

        TableView<SessionInfo> tableView = createTableViewColumns();

        Button newGameButton = new Button("New game");
        newGameButton.setOnAction(actionEvent -> {
            if(NewGameWindow.display()) {
                SnakesProto.GameConfig gameConfig = createGameConfig();

                new GameWindow(gameConfig,
                        discoverer, name, SnakesProto.NodeRole.MASTER);
            }
        });

        Button connectButton = new Button("Connect");
        connectButton.setOnAction(actionEvent -> {
            ObservableList<SessionInfo> sessionSelected;
            sessionSelected  = tableView.getSelectionModel().getSelectedItems();

            if(sessionSelected.size() == 0) {
                return;
            }

            SessionInfo si = sessionSelected.get(0);

            HostInfo hostInfo = new HostInfo(si.getIp(), si.getPort());

            new GameWindow(si.getGameConfig(),
                    discoverer, name, SnakesProto.NodeRole.NORMAL, hostInfo);

        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(actionEvent -> {
            tableView.getItems().removeAll();
            tableView.setItems(getSessionsInfo());
        });



        HBox bottomMenu = new HBox(100);
        bottomMenu.setMinHeight(100);
        bottomMenu.getChildren().addAll(newGameButton, connectButton, refreshButton);
        bottomMenu.setAlignment(Pos.CENTER);

        VBox layout = new VBox();
        layout.getChildren().addAll(tableView, bottomMenu);
        layout.setMinWidth(800);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    private TableView<SessionInfo> createTableViewColumns() {
        TableView<SessionInfo> tableView;

        TableColumn<SessionInfo, String> nameColumn = new TableColumn<>("NAME");
        nameColumn.setMaxWidth(250);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SessionInfo, InetAddress> ipColumn = new TableColumn<>("IP");
        ipColumn.setMaxWidth(250);
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

        TableColumn<SessionInfo, Integer> portColumn = new TableColumn<>("Port");
        portColumn.setMaxWidth(50);
        portColumn.setCellValueFactory(new PropertyValueFactory<>("port"));

        TableColumn<SessionInfo, Integer> widthColumn = new TableColumn<>("Width");
        widthColumn.setMaxWidth(50);
        widthColumn.setCellValueFactory(new PropertyValueFactory<>("width"));

        TableColumn<SessionInfo, Integer> heightColumn = new TableColumn<>("Height");
        heightColumn.setMaxWidth(50);
        heightColumn.setCellValueFactory(new PropertyValueFactory<>("height"));

        TableColumn<SessionInfo, Integer> baseFoodColumn = new TableColumn<>("Base food");
        baseFoodColumn.setMaxWidth(75);
        baseFoodColumn.setCellValueFactory(new PropertyValueFactory<>("baseFood"));

        TableColumn<SessionInfo, Double> foodMultiplyerColumn = new TableColumn<>("Food multi");
        foodMultiplyerColumn.setMaxWidth(75);
        foodMultiplyerColumn.setCellValueFactory(new PropertyValueFactory<>("foodMultiplyer"));

        TableColumn<SessionInfo, Double> foodDropChanceColumn = new TableColumn<>("Drop chance");
        foodDropChanceColumn.setMaxWidth(175);
        foodDropChanceColumn.setCellValueFactory(new PropertyValueFactory<>("foodDropChance"));

        TableColumn<SessionInfo, Integer> numOfPlayersColumn = new TableColumn<>("Num of players");
        numOfPlayersColumn.setMaxWidth(175);
        numOfPlayersColumn.setCellValueFactory(new PropertyValueFactory<>("numOfPlayers"));

        TableColumn<SessionInfo, Boolean> canJoinColumn = new TableColumn<>("Can join");
        canJoinColumn.setMaxWidth(170);
        canJoinColumn.setCellValueFactory(new PropertyValueFactory<>("canJoin"));
        canJoinColumn.setSortType(TableColumn.SortType.DESCENDING);

        tableView = new TableView<>();
        tableView.setItems(getSessionsInfo());
        tableView.getColumns().addAll(ipColumn, portColumn, nameColumn, widthColumn, heightColumn, baseFoodColumn, foodMultiplyerColumn, foodDropChanceColumn, numOfPlayersColumn, canJoinColumn);

        return tableView;
    }

    private ObservableList<SessionInfo> getSessionsInfo() {
        ObservableList<SessionInfo> sessionsInfo = FXCollections.observableArrayList();

        for(Map.Entry<HostInfo, SnakesProto.GameMessage.AnnouncementMsg> entry: sessionInfoMap.entrySet()) {
            for(SnakesProto.GamePlayer gamePlayer : entry.getValue().getPlayers().getPlayersList()) {
                if(gamePlayer.getRole() == SnakesProto.NodeRole.MASTER) {
                    SnakesProto.GameConfig gameConfig = entry.getValue().getConfig();
                    sessionsInfo.add(new SessionInfo(
                            entry.getKey().getIp(), entry.getKey().getPort(), gamePlayer.getName(), gameConfig.getWidth(),
                            gameConfig.getHeight(), gameConfig.getFoodStatic(), gameConfig.getFoodPerPlayer(),
                            gameConfig.getDeadFoodProb(), entry.getValue().getPlayers().getPlayersCount(), entry.getValue().getCanJoin(),
                            entry.getValue().getConfig())
                    );
                    break;
                }
            }

        }

        return sessionsInfo;
    }

    private SnakesProto.GameConfig createGameConfig() {
        return SnakesProto.GameConfig.newBuilder()
                .setWidth(NewGameWindow.getWidth())
                .setHeight(NewGameWindow.getHeight())
                .setFoodStatic(NewGameWindow.getBaseFood())
                .setFoodPerPlayer(NewGameWindow.getFoodMultiplyer())
                .setStateDelayMs(NewGameWindow.getStateDelay())
                .setDeadFoodProb(NewGameWindow.getFoodDropChance())
                .setPingDelayMs(NewGameWindow.getPingDelay())
                .setNodeTimeoutMs(NewGameWindow.getNodeTimeout())
                .build();
    }

}
