package official.sketchBook.util_related.helpers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.ContactListener;
import official.sketchBook.animation_related.Sprite;
import official.sketchBook.gameState_related.Playing;

public class HelpMethods {

    public static TextureRegion obtainCurrentSpriteImage(Sprite sprite, int textureWidth, int textureHeight, Texture spriteSheet, boolean flipX) {

        TextureRegion region = new TextureRegion(
            spriteSheet,
            sprite.getIndexX() * textureWidth,
            sprite.getIndexY() * textureHeight,
            textureWidth,
            textureHeight
        );

        if (flipX && !region.isFlipX()) {
            region.flip(true, false);  // Inverte horizontalmente
        } else if (!flipX && region.isFlipX()) {
            region.flip(true, false);  // Corrige se já estiver invertido
        }

        return region;
    }

    //valida se existe o listener antes de adicionar ou remover
    public static synchronized void handleContactListener(boolean remove, String listenerKey, ContactListener listener) {
        if (!remove) {

            if (Playing.multiContactListener.existListener(listenerKey))
                Playing.multiContactListener.removeListener(listenerKey);

            Playing.multiContactListener.addListener(listenerKey, listener);
        } else {
            if (Playing.multiContactListener.existListener(listenerKey))
                Playing.multiContactListener.removeListener(listenerKey);
        }
    }
}
