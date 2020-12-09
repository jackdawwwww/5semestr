package Controllers;

import proto.SnakesProto;

import java.net.InetAddress;

public class SessionInfo {
    private InetAddress ip;
    private int port;

    private String name;

    private int width;
    private int height;
    private int baseFood;
    private double foodMultiplier;
    private final double foodDropChance;
    private int numOfPlayers;
    private boolean canJoin;

    private SnakesProto.GameConfig gameConfig;

    public SessionInfo(InetAddress _ip, int _port, String _name,
                       int _width, int _height,
                       int _baseFood, double _foodMultiplier, double _foodDropChance,
                       int _numOfPlayers, boolean _canJoin, SnakesProto.GameConfig _gameConfig) {

        ip = _ip;
        port = _port;
        name = _name;
        width = _width;
        height = _height;
        baseFood = _baseFood;
        foodMultiplier = _foodMultiplier;
        foodDropChance = _foodDropChance;
        numOfPlayers = _numOfPlayers;
        canJoin = _canJoin;
        gameConfig = _gameConfig;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBaseFood() {
        return baseFood;
    }

    public double getFoodMultiplier() {
        return foodMultiplier;
    }

    public double getFoodDropChance() {
        return foodDropChance;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public SnakesProto.GameConfig getGameConfig() {
        return gameConfig;
    }

    public String getName() {
        return name;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }


    public void setPort(int port) {
        this.port = port;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBaseFood(int baseFood) {
        this.baseFood = baseFood;
    }

    public void setFoodMultiplier(double foodMultiplier) {
        this.foodMultiplier = foodMultiplier;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public void setCanJoin(boolean canJoin) {
        this.canJoin = canJoin;
    }

    public void setGameConfig(SnakesProto.GameConfig gameConfig) {
        this.gameConfig = gameConfig;
    }

    public void setName(String name) {
        this.name = name;
    }
}