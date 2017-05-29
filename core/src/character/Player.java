package character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {


    private String id;
    private float x;
    private float y;
    private Texture img;
    public Player(String team) {
        this.id = "0";
        this.x = 400;
        this.y = 300;
        switch (team) {
            case "RED":
                img = new Texture("red.png");
                break;
            case "BLUE":
                img = new Texture("blue.png");
                break;
            default:
                img = new Texture("blue.png");
                break;
        }
    }
    public Player(String id, float x, float y, String team){
        this.id = id;
        this.x = x;
        this.y = y;
        switch (team) {
            case "RED":
                img = new Texture("red.png");
                break;
            case "BLUE":
                img = new Texture("blue.png");
                break;
            default:
                img = new Texture("blue.png");
                break;
        }
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
       return id;
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
    public String toString(){return id + "," + x + "," + y + "\n";}
}
