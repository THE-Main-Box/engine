package official.sketchBook.engine.components_related.base_component;

import com.badlogic.gdx.Gdx;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class KeyBindedControllerComponent implements Component {

    protected final Map<Integer, Consumer<Boolean>> keyBindings;
    protected final Map<Integer, Boolean> keyStates;

    public KeyBindedControllerComponent() {
        this.keyBindings = new HashMap<>();
        this.keyStates = new HashMap<>();
    }

    public void bindKey(int key, Consumer<Boolean> action) {
        keyBindings.put(key, action);
        keyStates.put(key, false); // Inicializa como solto
    }

    public void handleKeyDown(int keycode) {
        if (keyBindings.containsKey(keycode) && !keyStates.get(keycode)) {
            keyStates.put(keycode, true);
            keyBindings.get(keycode).accept(true);
        }
    }

    public void handleKeyUp(int keycode) {
        if (keyBindings.containsKey(keycode) && keyStates.get(keycode)) {
            keyStates.put(keycode, false);
            keyBindings.get(keycode).accept(false);
        }
    }

    @Override
    public void update(float delta) {
        checkKeyStates();
    }

    private void checkKeyStates() {
        for (int key : keyBindings.keySet()) {
            boolean isPressed = Gdx.input.isKeyPressed(key);
            boolean wasPressed = keyStates.get(key);

            if (isPressed && !wasPressed) {
                handleKeyDown(key);
            } else if (!isPressed && wasPressed) {
                handleKeyUp(key);
            }
        }
    }
}
