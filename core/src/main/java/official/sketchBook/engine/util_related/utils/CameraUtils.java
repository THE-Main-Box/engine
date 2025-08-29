package official.sketchBook.engine.util_related.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import official.sketchBook.engine.camera_related.CameraManager;

public class CameraUtils {

    public static void updateCameraMovementParams(CameraManager cameraManager, float width, float height) {
        OrthographicCamera cam = cameraManager.getCamera();
        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cameraManager.maxLvlOffSetX = Math.max(0, Math.round(width - effectiveViewportWidth));
        cameraManager.maxLvlOffSetY = Math.max(0, Math.round(height - effectiveViewportHeight));
    }

}
