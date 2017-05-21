package character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
    private static int idCount = 0;
    private int id;
    private float x;
    private float y;
    private Texture img;

    public Player(){
        id = idCount++;
        x = 400;
        y = 300;
        img = new Texture("player.png");
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
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
    public String toString(){
        return id + "," + x + "," + y;
    }
}
