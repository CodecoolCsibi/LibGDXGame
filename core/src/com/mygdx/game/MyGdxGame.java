package com.mygdx.game;

import character.Player;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;


public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private ExtendViewport viewport;
	private SpriteBatch batch;

	private boolean inGame = false;
	private Socket socket;
	private HashMap<String, Player> players;
	private Player me;

	private String serverURI = "http://localhost:8080";
	private String team = "blue";

	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(800, 600, camera);
		batch = new SpriteBatch();
		players = new HashMap<>();
		connectToHost();
		configSocketEvents();



	}

	@Override
	public void render () {
		if(!inGame) {
			if (Gdx.input.isKeyPressed(Input.Keys.H)){
				inGame = true;
			} else if(Gdx.input.isKeyPressed(Input.Keys.P)){

				inGame = true;
			}

		}
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for(HashMap.Entry<String, Player> entry : players.entrySet()){
			entry.getValue().draw(batch);
		}
		batch.end();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		socket.close();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		batch.setProjectionMatrix(camera.combined);
	}

	private void connectToHost(){
		System.out.println("Started game as a Peer.");

		try {
			socket = IO.socket(serverURI);
			socket.connect();
			socket.emit("team", team);
			System.out.println("connection established");
		} catch (Exception e) {
			System.out.println("no connection");
			System.exit(1);
		}
	}

	public void configSocketEvents(){
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Gdx.app.log("SocketIO", "Connected");
			}
		}).on("socketID", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = (JSONObject) args[0];
				try{
					String id = data.getString("id");
					Gdx.app.log("SocketIO", "My id:" + id);
				} catch (JSONException e){
					Gdx.app.log("SocketIO", "Error getting id");
					e.printStackTrace();
					System.exit(1);
				}
			}
		}).on("newPlayer", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject newPlayer = (JSONObject) args[0];
				try{
					String id = newPlayer.getString("id");
					players.put(id, new Player((newPlayer.getString("team").equals("blue"))? "blue.png" : "red.png"));
					Gdx.app.log("SocketIO", "New player connected:" + id);
				} catch (JSONException e){
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
		}).on("getPlayers", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONArray playersData = (JSONArray) args[0];
				try{
					for (int i=0; i < playersData.length(); i++) {
						JSONObject playerJSONObject = playersData.getJSONObject(i);
						String id = playerJSONObject.getString("id");
						float x = BigDecimal.valueOf(playerJSONObject.getDouble("x")).floatValue();
						float y = BigDecimal.valueOf(playerJSONObject.getDouble("y")).floatValue();
						String team = playerJSONObject.getString("team");
						players.put(id, new Player(id, x, y, team));
						Gdx.app.log("SocketIO", "Player Created: " + id);
					}
				}catch (JSONException e){
					Gdx.app.log("SocketIO",  "Getting players failed.");
					e.printStackTrace();
					System.exit(1);
				}
			}
		});
	}
}
