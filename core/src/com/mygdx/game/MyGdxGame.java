package com.mygdx.game;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import character.Player;


public class MyGdxGame extends ApplicationAdapter {
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;
    private final float UPDATE_TIME = 1 / 60f;
    private float timer = 0;

    private boolean inGame = false;
    private Socket socket;
    private HashMap<String, Player> players;
    private HashMap<String, Texture> textures = new HashMap<>();
    private Texture redTexture;
    private Texture blueTexture;


    private String serverURI = "http://localhost:8080";
    private String team = "blue";
    private Player localPlayer;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(800, 600, camera);
        batch = new SpriteBatch();
        redTexture = new Texture("red.png");
        blueTexture = new Texture("blue.png");
        textures.put("RED", redTexture);
        textures.put("BLUE", blueTexture);
        players = new HashMap<>();
        connectToHost();
        configSocketEvents();


    }

    public void updateServer(float dt) {
        timer += dt;
        if (timer >= UPDATE_TIME && localPlayer != null) {
            JSONObject data = new JSONObject();
            try {
                data.put("x", localPlayer.getX());
                data.put("y", localPlayer.getY());
                socket.emit("playerMoved", data);
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error sending update data.");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    @Override
    public void render() {
        updateServer(Gdx.graphics.getDeltaTime());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        if (localPlayer != null) {
            localPlayer.move();
            localPlayer.draw(batch);
        }
        if (players.size() > 0) {
            for (HashMap.Entry<String, Player> entry : players.entrySet()) {
                Gdx.app.log("entry", entry.getValue().toString());
                entry.getValue().draw(batch);
            }
        }
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        socket.close();
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    private void connectToHost() {
        System.out.println("Started game as a Peer.");

        try {
            socket = IO.socket(serverURI);
            socket.connect();
            System.out.println("connection established");
        } catch (Exception e) {
            System.out.println("no connection");
            System.exit(1);
        }
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    String team = data.getString("team");
                    localPlayer = new Player(id, 400, 300, team, textures);
                    Gdx.app.log("Client", "Local player : " + localPlayer.toString());
                    Gdx.app.log("SocketIO", "My id:" + id);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting id");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject newPlayer = (JSONObject) args[0];
                try {
                    String id = newPlayer.getString("id");
                    players.put(id, Player.fromJSONObject(newPlayer, textures));
                    Gdx.app.log("SocketIO", "New player connected:" + newPlayer.toString());
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting new player's id");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    players.remove(id);
                    Gdx.app.log("SocketIO", "Player disconnected: " + id);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting  disconnected player's id.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }).on("players", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray playersData = (JSONArray) args[0];
                try {
                    for (int i = 0; i < playersData.length(); i++) {
                        JSONObject playerJSONObject = playersData.getJSONObject(i);

                        String id = playerJSONObject.getString("id");
                        if(!id.equals(localPlayer.getId())) {
                            players.put(id, Player.fromJSONObject(playerJSONObject, textures));
                            Gdx.app.log("SocketIO", "Player Created: " + id);
                        }
                    }
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Getting players failed.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    if(players.get(id) != null){
                        float x = ((Double) data.getDouble("x")).floatValue();
                        float y = ((Double) data.getDouble("y")).floatValue();
                        players.get(id).setX(x);
                        players.get(id).setY(y);
                    }
                    Gdx.app.log("SocketIO", "Player moved: " + id);
                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting  disconnected player's id.");
                    e.printStackTrace();
                    System.exit(1);
                }

            }
        });
    }
}