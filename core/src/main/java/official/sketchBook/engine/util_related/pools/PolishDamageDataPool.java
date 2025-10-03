package official.sketchBook.engine.util_related.pools;

import official.sketchBook.engine.components_related.integration_interfaces.DamageDealerII;
import official.sketchBook.engine.components_related.integration_interfaces.DamageReceiverII;
import official.sketchBook.engine.components_related.toUse_component.util.TimerComponent;
import official.sketchBook.engine.custom_utils_related.CustomPool;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.PolishDamageData;
import official.sketchBook.engine.util_related.utils.data_to_instance_related.damage_related.RawDamageData;

public class PolishDamageDataPool extends CustomPool<PolishDamageData> {
    private final TimerComponent cleanUpTimer = new TimerComponent(1f);

    @Override
    protected PolishDamageData newObject() {
        return new PolishDamageData(this);
    }

    public void update(float delta){
        if(!cleanUpTimer.isRunning()){
            cleanUpTimer.reset();
            cleanUpTimer.start();
        }

        if(cleanUpTimer.isFinished()){
            cleanUpTimer.reset();
            cleanPool();
        }

        cleanUpTimer.update(delta);
    }

    private void cleanPool(){
        this.clear();
    }

    public void initPDD(DamageReceiverII receiver, DamageDealerII dealer, RawDamageData rdd, PolishDamageData toInit) {

        int x = 0;
        int y = 0;

        if (rdd.isApplyKnockBack()) {
            // Posições
            final float rx = receiver.getBody().getPosition().x;
            final float ry = receiver.getBody().getPosition().y;
            final float dx = dealer.getBody().getPosition().x;
            final float dy = dealer.getBody().getPosition().y;

            // Dimensões do receptor
            final float width = receiver.getWidth();
            final float height = receiver.getHeight();

            // Pré-calcula fatores para evitar divisões repetidas
            final float factorX = height / width;
            final float factorY = width / height;

            final float deltaX = rx - dx;
            final float deltaY = ry - dy;

            // Aplicação de knockBack somente se eixo predominante
            if (Math.abs(deltaX) > Math.abs(deltaY) * factorX) x = (deltaX > 0 ? 1 : (deltaX < 0 ? -1 : 0));
            if (Math.abs(deltaY) > Math.abs(deltaX) * factorY) y = (deltaY > 0 ? 1 : (deltaY < 0 ? -1 : 0));
        }

        toInit.init(x, y, rdd, dealer);
    }



}
