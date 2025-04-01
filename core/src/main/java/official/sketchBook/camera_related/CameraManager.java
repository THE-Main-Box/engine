package official.sketchBook.camera_related;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.math.MathUtils.lerp;

public class CameraManager {
    private final OrthographicCamera camera;
    private final Viewport viewport;

    public int maxLvlOffSetX;
    public int maxLvlOffSetY;

    public int rightBorder = 5;
    public int leftBorder = -5;
    public int topBorder = 5;
    public int bottomBorder = -20;

    public float xLvlOffSet = 0, yLvlOffSet = 0;
    public float xEase = 0.5f, yEase = 0.5f, zEase = 1;

    public CameraManager(float viewportWidth, float viewportHeight) {
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(viewportWidth, viewportHeight, camera);
        this.camera.position.set(viewportWidth / 2f, viewportHeight / 2f, 0);
        this.camera.update();
    }

    public void updateViewport(int width, int height) {
        viewport.update(width, height, true);
    }

    public void update() {
        camera.update();
    }

    public void setZoom(float zoom) {
        camera.zoom = zoom;
        camera.update();
    }

    public void trackObjectByOffset(int targetX, int targetY) {
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        updateXOffset(targetX, rightBorder, leftBorder, effectiveViewportWidth);
        updateYOffset(targetY, bottomBorder, topBorder, effectiveViewportHeight);

        camera.position.x = effectiveViewportWidth / 2f + xLvlOffSet;
        camera.position.y = effectiveViewportHeight / 2f + yLvlOffSet;

        camera.update();
    }

    private void updateXOffset(float targetX, int rightBorder, int leftBorder, float effectiveViewportWidth) {
        float centerX = effectiveViewportWidth / 2f + xLvlOffSet;
        float diffX = targetX - centerX;

        if (diffX > rightBorder) {
            xLvlOffSet = roundLerp(xLvlOffSet, xLvlOffSet + (diffX - rightBorder), xEase);
        } else if (diffX < leftBorder) {
            xLvlOffSet = roundLerp(xLvlOffSet, xLvlOffSet + (diffX - leftBorder), xEase);
        }

        xLvlOffSet = clamp(xLvlOffSet, 0, maxLvlOffSetX);
    }

    private void updateYOffset(float targetY, int bottomBorder, int topBorder, float effectiveViewportHeight) {
        float centerY = effectiveViewportHeight / 2f + yLvlOffSet;
        float diffY = targetY - centerY;

        if (diffY < bottomBorder) {
            yLvlOffSet = roundLerp(yLvlOffSet, yLvlOffSet + (diffY - bottomBorder), yEase);
        } else if (diffY > topBorder) {
            yLvlOffSet = roundLerp(yLvlOffSet, yLvlOffSet + (diffY - topBorder), yEase);
        }

        yLvlOffSet = clamp(yLvlOffSet, 0, maxLvlOffSetY);
    }

    // 🔥 Lerp que arredonda suavemente para evitar travamentos
    private float roundLerp(float from, float to, float smoothFactor) {
        float lerped = lerp(from, to, smoothFactor);
        return Math.round(lerped * 10) / 10f; // Arredonda para 1 casa decimal
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    public void setLeftBorder(int leftBorder) {
        this.leftBorder = leftBorder;
    }

    public void setRightBorder(int rightBorder) {
        this.rightBorder = rightBorder;
    }

    public void setTopBorder(int topBorder) {
        this.topBorder = topBorder;
    }

    public void setBottomBorder(int bottomBorder) {
        this.bottomBorder = bottomBorder;
    }

    public float getxEase() {
        return xEase;
    }

    public float getyEase() {
        return yEase;
    }

    public float getzEase() {
        return zEase;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setEase(float xEase, float yEase, float zEase) {
        this.xEase = xEase;
        this.yEase = yEase;
        this.zEase = zEase;
    }

    public boolean isObjectWithinBounds(int targetX, int targetY) {
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        float centerX = effectiveViewportWidth / 2f + xLvlOffSet;
        float centerY = effectiveViewportHeight / 2f + yLvlOffSet;

        // Calcula a diferença entre a posição do objeto e o centro da câmera
        float diffX = targetX - centerX;
        float diffY = targetY - centerY;

        // Verifica se o objeto está dentro dos limites definidos pelas bordas
        boolean isWithinXBounds = diffX >= leftBorder && diffX <= rightBorder;
        boolean isWithinYBounds = diffY >= bottomBorder && diffY <= topBorder;

        return isWithinXBounds && isWithinYBounds;
    }

}
