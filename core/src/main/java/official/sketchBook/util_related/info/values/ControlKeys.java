package official.sketchBook.util_related.info.values;

import com.badlogic.gdx.Input;

public class ControlKeys {

    public static int move_left = Input.Keys.LEFT;
    public static int move_right = Input.Keys.RIGHT;
    public static int jump = Input.Keys.SPACE;
    public static int recharge = Input.Keys.D;
    public static int use = Input.Keys.Z;
    public static int secondaryUse = Input.Keys.X;

    // redefinir uma tecla
    public static void setKey(String action, int newKey) {
        switch (action.toLowerCase()) {
            case "move_left":
                move_left = newKey;
                break;
            case "move_right":
                move_right = newKey;
                break;
            case "jump":
                jump = newKey;
                break;
        }
    }

}
