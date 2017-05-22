package com.mygdx.game;

import character.Player;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private ExtendViewport viewport;
	private SpriteBatch batch;
	private boolean inGame = false;
	private Socket socket;
	private boolean isHost = false;
	private boolean isPlayer = false;
	private Player me;
	private Player enemy;

	private SocketHints socketHints = new SocketHints();

	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ExtendViewport(800, 600, camera);
		batch = new SpriteBatch();
		me = new Player("player.png");
		enemy = new Player("enemy.png");

	}

	@Override
	public void render () {
		if(!inGame) {
			if (Gdx.input.isKeyPressed(Input.Keys.H)){
				startAsHost();
				inGame = true;
				isHost = true;
			} else if(Gdx.input.isKeyPressed(Input.Keys.P)){
				connectToHost();
				inGame = true;
				isPlayer = true;
			}

		}
		me.move();
		if (isPlayer && inGame) {
			if (socket != null){
				String dataLine;
				String[] data;
			try {
				System.out.println("Client: Data: " + me.toString());
				socket.getOutputStream().write(me.toString().getBytes());
				System.out.println("Client: Server's address:" + socket.getRemoteAddress());
				BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				System.out.println("Client: Buffer ready.");
				dataLine = buffer.readLine();
				System.out.println("Client: DataLine:" + dataLine);
				data = dataLine.split(",");
				System.out.println("Client: Data:" + data[0] + "," + data[1] + "," + data[2]);
				enemy.setX(Float.valueOf(data[1]));
				enemy.setY(Float.valueOf(data[2]));
				System.out.println("Client: Information received, enemy's new state:" + enemy.toString());


				System.out.println("Client : Data sent.");
			} catch (Exception e){
				System.out.println("Client: Unable to send data.");
				e.printStackTrace();
				System.exit(1);
				}
			}
		}
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		me.draw(batch);
		enemy.draw(batch);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		batch.setProjectionMatrix(camera.combined);
	}

	private void startAsHost(){
		new Thread(new Runnable(){

			@Override
			public void run() {
				System.out.println("Started game as a Host.");
				ServerSocketHints serverSocketHint = new ServerSocketHints();

				serverSocketHint.acceptTimeout = 99999999;

				ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, "192.168.0.100", 9021, serverSocketHint);
				socket = serverSocket.accept(new SocketHints());
				System.out.println("Server: Client connected.");
				String dataLine;
				String[] data;
				while(true){
					try {
						if(socket.isConnected()) {
							System.out.println("Server: Client's address:" + socket.getRemoteAddress());
							BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							System.out.println("Server: Buffer ready.");
							dataLine = buffer.readLine();
							System.out.println("Server: DataLine:" + dataLine);
							data = dataLine.split(",");
							System.out.println("Server: Data:" + data[0] + "," + data[1] + "," + data[2]);
							enemy.setX(Float.valueOf(data[1]));
							enemy.setY(Float.valueOf(data[2]));
							System.out.println("Server: Information received, enemy's new state:" + enemy.toString());
							socket.getOutputStream().write(me.toString().getBytes());
							System.out.println("Server: Host information sent.");
						} else {
							System.out.println("Server: Client lost.");
						}
					} catch ( Exception e){
						e.printStackTrace();
						System.out.println("Server: Something wrong.");
						System.exit(1);
					}

				}
			}
		}).start();
	}
	private void connectToHost(){
		System.out.println("Started game as a Peer.");
		socketHints.connectTimeout = 999999999;
		//create the socket and connect to the server entered in the text box ( x.x.x.x format ) on port 9021

		try {
			socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "192.168.0.100", 9021, socketHints);
			System.out.println("connection established");
		} catch (Exception e) {
			System.exit(1);
			System.out.println("no connection");
		}
	}
}
