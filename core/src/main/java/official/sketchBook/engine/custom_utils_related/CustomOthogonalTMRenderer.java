package official.sketchBook.engine.custom_utils_related;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class CustomOthogonalTMRenderer extends OrthogonalTiledMapRenderer {
    public CustomOthogonalTMRenderer(TiledMap map) {
        super(map);
    }

    public CustomOthogonalTMRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public CustomOthogonalTMRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public CustomOthogonalTMRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    public void setUnitScale(float unitScale){
        this.unitScale = unitScale;
    }
}
