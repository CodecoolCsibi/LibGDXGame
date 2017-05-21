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
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
		me = new Player();
		enemy = new Player();

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
				System.out.println("no problem with the socki :)");
			try {
				System.out.println("Client: Data: " + me.toString().getBytes(Charset.forName("UTF-8")));
				if (socket.getOutputStream() != null)
					System.out.println("The socki's output is not null.");
				socket.getOutputStream().write(me.toString().getBytes(Charset.forName("UTF-8")));
				System.out.println("Client : Data sent.");
			} catch (Exception e){
				System.out.println("Client: Unable to send data.");
				e.printStackTrace();
				}
			}

		}
		Gdx.gl.glClearColor(1, 0, 0, 1);
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
				ServerSocketHints serverSocketHint = new ServerSocketHints();

				serverSocketHint.acceptTimeout = 99999999;

				ServerSocket serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, "192.168.0.100", 9021, serverSocketHint);


				while(true){
					try {
						socket = serverSocket.accept(null);
						System.out.println("Server: Client connected");
						BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						System.out.println("kész a buffer");
						String[] data = buffer.readLine().split(",");
						System.out.println("Server: data:" + buffer.readLine());
						enemy.setX(Float.valueOf(data[1]));
						enemy.setY(Float.valueOf(data[2]));
						System.out.println("infó jött!!!!!!4négy!");
					} catch ( Exception e){
						e.printStackTrace();
						System.out.println("Nem lett jó");
					}
				}
			}
		}).start();
	}
	private void connectToHost(){
		socketHints.connectTimeout = 4000;
		//create the socket and connect to the server entered in the text box ( x.x.x.x format ) on port 9021

		try {
			socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "192.168.0.100", 9021, socketHints);
			System.out.println("connection established");
		} catch (Exception e) {
			System.out.println("no connection");
		}
	}
}
