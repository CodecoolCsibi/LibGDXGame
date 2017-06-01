package character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Player {

    private String id;
    private float x;
    private float y;
    private Texture img;

    public Player(Texture texture){
        id = "0";
        x = 400;
        y = 300;
        img = texture;
    }

    public Player(String team, HashMap<String, Texture> textureMap) {
        this.id = "0";
        this.x = 400;
        this.y = 300;
        Gdx.app.log("PlayerModel", "Team: "  + team);
        if(team.equals("RED")){
            Gdx.app.log("PlayerModel:", "Team is Red");
            img = textureMap.get("RED");
        } else if(team.equals("BLUE")){
            Gdx.app.log("PlayerModel:", "Team is Blue");
            img = textureMap.get("BLUE");
        }
    }

    public Player(String id, float x, float y, String team, HashMap<String, Texture> textureMap){
        this.id = id;
        this.x = x;
        this.y = y;
        Gdx.app.log("PlayerModel", "Team: "  + team);
        if(team.equals("RED")){
            Gdx.app.log("PlayerModel:", "Team is Red");
            img = textureMap.get("RED");
        } else if(team.equals("BLUE")){
            Gdx.app.log("PlayerModel:", "Team is Blue");
            img = textureMap.get("BLUE");
        }
    }

    public String getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Texture getImg() {
        return img;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }


    public void setImg(Texture img) {
        this.img = img;
    }




    public void move(){
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            y -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP))
            y += 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            x += 200 * Gdx.graphics.getDeltaTime();
    }
    public void draw(SpriteBatch batch){
        batch.draw(img, x, y);
    }

    public void dispose(){
        img.dispose();
    }

    public static Player fromJSONObject(JSONObject playerJSON, HashMap<String, Texture> textureMap) throws JSONException {

        String id = playerJSON.getString("id");
        float x = ((Double) playerJSON.getDouble("x")).floatValue();
        float y = ((Double) playerJSON.getDouble("y")).floatValue();
        String team = playerJSON.getString("team");

        return new Player(id, x, y, team, textureMap);
    }

    public String toString(){return id + "," + x + "," + y + "\n";}
}
