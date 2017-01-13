package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.sprites.Starship;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyGdxGame extends ApplicationAdapter {
    private final float UPDATE_TIME = 1/60f; // how often we send starship position to server
    float timer; // keep track of how much time has passed since prev msg was sent
	SpriteBatch batch;
	private Socket socket;
    String id;
    Starship player;
    Texture playerShip;
    Texture friendlyShip;
    HashMap<String, Starship> friendlyPlayers;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
        playerShip = new Texture("playerShip2.png"); // inside assets folder
        friendlyShip = new Texture("playerShip.png"); // inside assets folder
        friendlyPlayers = new HashMap<String, Starship>();
		connectSocket();
        configSocketEvents();
	}

    public void updateServer(float dt){
        timer += dt;
        if (timer >= UPDATE_TIME && player != null && player.hasMoved()) {
            // send x & y coordinates
            JSONObject data = new JSONObject();
            try{
                data.put("x", player.getX());
                data.put("y", player.getY());
                socket.emit("playerMoved", data);
            } catch (JSONException e){
                Gdx.app.log("SOCKET.IO", "Error sending update data");
            }
        }
    }

    public void connectSocket() {
		try{
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		} catch (Exception e){
			System.out.println(e);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput(Gdx.graphics.getDeltaTime());
        updateServer(Gdx.graphics.getDeltaTime());

		batch.begin();
//		batch.draw(img, 0, 0); // gdx logo
        if (player != null) {
            player.draw(batch);
        }

        for (HashMap.Entry<String, Starship> entry : friendlyPlayers.entrySet()){
            // Draw every friendly ship inside the HashMap
            entry.getValue().draw(batch);
        }
		batch.end();
	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    public void handleInput(float deltaTime) {
        if (player != null) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
                // moving in relation to how much time has changed since last updated
                player.setPosition(player.getX() + (-200 * deltaTime), player.getY());
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
                // moving in relation to how much time has changed since last updated
                player.setPosition(player.getX() + (+200 * deltaTime), player.getY());
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        playerShip.dispose();
        friendlyShip.dispose();
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args)  {
                Gdx.app.log("SocketIO", "Connected");
                player = new Starship(playerShip);
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0]; // one piece of data
                try {
                    id = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + id);
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting ID");
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId = data.getString("id");
                    Gdx.app.log("SocketIO", "New Player Connect: " + playerId);
                    friendlyPlayers.put(playerId, new Starship(friendlyShip));
                }catch(JSONException e){
                    Gdx.app.log("SocketIO", "Error getting New PlayerID");
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0]; // one piece of data
                try {
                    id = data.getString("id");
                    friendlyPlayers.remove(id);
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting disconnected player ID");
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0]; // one piece of data
                try {
                    String playerID = data.getString("id");
                    Double x = data.getDouble("x");
                    Double y = data.getDouble("y");
                    if (friendlyPlayers.get(playerID) != null){
                        friendlyPlayers.get(playerID).setPosition(x.floatValue(), y.floatValue());
                    }
                } catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting disconnected player ID");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray) args[0];
                try{
                    // Create a new starship for every object inside of JSONArray
                    for (int i = 0; i < objects.length(); i++){
                        Starship coopPlayer = new Starship(friendlyShip);
                        // Set pos of coopPlayer to the current pos that player
                        // is in the game world that the server sent
                        Vector2 position = new Vector2();
                        // we need float value because coopPlayer.setPosition takes in float vlaues
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                        coopPlayer.setPosition(position.x, position.y);

                        friendlyPlayers.put(objects.getJSONObject(i).getString("id"), coopPlayer);

                    }
                } catch (JSONException e){

                }
            }
        });
    }
}


