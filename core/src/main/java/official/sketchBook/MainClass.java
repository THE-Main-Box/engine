package official.sketchBook;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainClass extends Game {
    public SpriteBatch batch;
    public SpriteBatch uiBatch;

    private PlayScreen gameScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();

        gameScreen = new PlayScreen(this);
        this.setScreen(gameScreen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        uiBatch.dispose();

        gameScreen.dispose();
    }


}
