package official.sketchBook.gameData_related;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import official.sketchBook.components_related.toUse_component.PhysicsComponent;
import official.sketchBook.gameObject_related.GameObject;

import java.util.ArrayList;
import java.util.List;

public class GameObjectManager {

    private List<GameObject> gameObjectList = new ArrayList<>();

    public void syncObjectsBodies(){
        for(GameObject object : gameObjectList){
            object.getPhysicsC().syncBodyObjectPos();
        }
    }

    public void updateObjects(float delta){
        for(GameObject object : gameObjectList){
            object.update(delta);
        }
    }

    public void renderObjects(SpriteBatch batch){
        for(GameObject object : gameObjectList){
            object.render(batch);
        }
    }

    public void dispose(){
        for(GameObject object : gameObjectList){
            object.dispose();
        }
    }

    public void removeGameObject(GameObject object){
        gameObjectList.remove(object);
    }

    public void addGameObject(GameObject object) {
        gameObjectList.add(object);
    }

    public List<GameObject> getGameObjectList() {
        return gameObjectList;
    }
}
