package official.sketchBook.components_related.toUse_component.entity;

import official.sketchBook.animation_related.ObjectAnimationPlayer;
import official.sketchBook.components_related.base_component.Component;
import official.sketchBook.gameObject_related.entities.Player;

import static official.sketchBook.util_related.info.values.AnimationTitles.Entity.*;

public class PlayerAnimationManagerComponent implements Component {
    private Player player;

    public PlayerAnimationManagerComponent(Player player) {
        this.player = player;
    }

    @Override
    public void update(float delta) {
        ObjectAnimationPlayer animationPlayer = player.getObjectAnimationPlayerList().get(0);

        // Ordem de prioridade: Pulo > Queda > Corrida > Idle
        if (handleJumpAnimation(animationPlayer)) return;
        if (handleAirborneAnimation(animationPlayer)) return;
        if (handleRunAnimation(animationPlayer)) return;
        handleIdleAnimation(animationPlayer);
    }

    private boolean handleJumpAnimation(ObjectAnimationPlayer ani) {
        float vy = player.getBody().getLinearVelocity().y;

        // Só entramos aqui enquanto estivermos no ar
        if (!player.isOnGround()) {
            // 1) Subida plena: vy acima do threshold de stall
            if (vy > 0.15f) {
                ani.setAnimation(jump);
                ani.setAnimationLooping(false);
                ani.setAutoUpdateAni(false);
                ani.setAniTick(0);

                // 2) Stall (pico do salto): vy próximo de zero, dentro do intervalo [-t, +t]
            } else if (Math.abs(vy) <= player.getjComponent().getFallSpeedAfterJCancel()) {
                ani.setAnimation(jump);
                ani.setAnimationLooping(false);
                ani.setAutoUpdateAni(false);
                ani.setAniTick(1);

                // 3) Queda: vy negativo além do stall threshold
            } else {
                ani.setAnimation(fall);
                ani.setAnimationLooping(false);
                ani.setAutoUpdateAni(false);
                ani.setAniTick(0);
            }

            return true;
        }

        // Quando tocar o chão, entramos no afterFall
        if (player.getjComponent().isEntityLanded() && !player.getMoveC().isMoving()) {
            ani.setAnimation(afterFall);
            ani.setAnimationLooping(false);
            ani.setAutoUpdateAni(true);
            return true;
        }

        return false;
    }


    private boolean handleAirborneAnimation(ObjectAnimationPlayer ani) {
        // só entra aqui se ainda não está no chão
        if (player.isOnGround()) return false;

        // se já tá tocando afterFall e não terminou, mantém
        if (ani.getCurrentAnimationKey().equals(afterFall)
            && !ani.isAnimationFinished()) {
            return true;
        }

        // queda livre normal (loop)
        if (player.getBody().getLinearVelocity().y < 0) {
            ani.setAnimation(fall);
            ani.setAnimationLooping(true);
            ani.setAutoUpdateAni(true);
        }

        return true;
    }

    private boolean handleRunAnimation(ObjectAnimationPlayer animationPlayer) {
        if (!player.isOnGround() || !player.getMoveC().isMoving() || isPlayingAfterFall(animationPlayer)) return false;

        animationPlayer.setAutoUpdateAni(true);
        animationPlayer.setAnimation(run);

        return true;
    }

    private void handleIdleAnimation(ObjectAnimationPlayer animationPlayer) {
        if (isPlayingAfterFall(animationPlayer)) return;

        animationPlayer.setAutoUpdateAni(true);
        animationPlayer.setAnimation(idle);
    }

    public boolean isPlayingAfterFall(ObjectAnimationPlayer ani) {
        return ani.getCurrentAnimationKey().equals(afterFall)
            && !ani.isAnimationFinished();
    }

}
