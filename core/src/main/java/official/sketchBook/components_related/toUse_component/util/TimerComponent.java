package official.sketchBook.components_related.toUse_component.util;

import official.sketchBook.components_related.base_component.Component;

public class TimerComponent extends Component {
    private float timeElapsed = 0;
    private float targetTime;
    private boolean running = false;

    public TimerComponent() {
    }

    public TimerComponent(float targetTime) {
        this.targetTime = targetTime;
    }

    public void update(float deltaTime) {
        if (running) {
            timeElapsed += deltaTime;
            if (timeElapsed >= targetTime) {
                timeElapsed = targetTime; // Garante que não ultrapasse o tempo
            }
        }
    }

    public void resetByFinished() {
        if (isFinished()) {
            stop();
            reset();
        }
    }

    public boolean isFinished() {
        return (timeElapsed >= targetTime);
    }

    public void stop() {
        running = false;
    }

    public void start() {
        running = true;
    }

    public void reset() {
        timeElapsed = 0;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    public float getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(float targetTime) {
        this.targetTime = targetTime;
        if (timeElapsed >= targetTime) { // Se já passou do novo tempo-alvo, marca como finalizado
            stop();
        }
    }

    public boolean isRunning() {
        return running;
    }

}
