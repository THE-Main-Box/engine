package official.sketchBook.gameState_related.model;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface StateMethods {
    public void update(float delta);

    public void updateUi(float delta);

    public void render(SpriteBatch batch);

    public void renderUi(SpriteBatch uiBatch);

    public boolean handleMouseMoved(int screenX, int screenY);

    public boolean handleTouchDown(int screenX, int screenY, int button);

    public boolean handleTouchUp(int screenX, int screenY, int button);

    public boolean handleTouchDragged(int screenX, int screenY, int button);

    public boolean handleKeyDown(int keycode);

    public boolean handleKeyUp(int keycode);

}
