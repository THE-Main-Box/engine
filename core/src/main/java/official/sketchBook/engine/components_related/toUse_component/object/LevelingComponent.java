package official.sketchBook.engine.components_related.toUse_component.object;

import official.sketchBook.engine.components_related.base_component.Component;
import official.sketchBook.engine.components_related.integration_interfaces.LevelComponentHolderII;

public class LevelingComponent implements Component {

    /// Dono do componente de levels
    private final LevelComponentHolderII owner;

    /// Nível atual
    private int currentLvl;

    /// Nível máximo que podemos chegar
    private int maxLvl;

    /// Progresso de nível atual
    private float currentLvlProgress;

    /// O quanto precisamos progredir de nível para passar pro próximo nível
    private float maxLvlProgress;


    public LevelingComponent(LevelComponentHolderII owner) {
        this.owner = owner;
    }

    public void init(
        int currentLevel,
        int maxLevel,
        float currentLevelProgress,
        float levelMaxProgress
    ) {
        this.currentLvl = currentLevel;
        this.maxLvl = maxLevel;
        this.currentLvlProgress = currentLevelProgress;
        this.maxLvlProgress = levelMaxProgress;
    }

    @Override
    public void update(float delta) {

    }

    public void addProgress(float progress) {
        // Armazenamos o progresso total (progresso atual + novo)
        float total = progress + currentLvlProgress;

        // Zeramos o progresso atual temporariamente (como você queria)
        currentLvlProgress = 0f;

        // Enquanto existir progresso para aplicar
        while (total > 0f) {

            // Se já estamos no nível máximo, armazenamos até o cap e paramos
            if (currentLvl >= maxLvl) {
                // Mantém até maxLvlProgress, descarta o resto
                currentLvlProgress = Math.min(total, maxLvlProgress);
                break;
            }

            // Quanto falta para preencher o nível atual
            float need = maxLvlProgress - currentLvlProgress;

            // Quanto vamos adicionar neste passo (ou o que resta)
            float add = Math.min(total, need);

            // Aplicamos esse valor
            currentLvlProgress += add;

            // Reduzimos o total pelo mesmo valor (essa é a correção chave)
            total -= add;

            // Se encheu o nível, sobe (levelUp zera currentLvlProgress como no seu código)
            if (currentLvlProgress >= maxLvlProgress) {
                levelUp(); // seu levelUp() já faz currentLvlProgress = 0 e owner.onLevelUp(this);
            } else {
                // Não encheu: o total foi consumido, saímos
                break;
            }
        }
    }

    /**
     * Remove progresso e lida com a regressão de nível (perda de XP).
     * @param progress Quantidade de progresso a ser removida (Ex: perda por dano).
     */
    public void removeProgress(float progress) {
        // Não faz nada se já estiver no nível base
        if (currentLvl <= 1) {
            currentLvlProgress = 0;
            return;
        }

        currentLvlProgress -= progress; // Subtrai a perda total do progresso atual

        // Lida com a regressão de nível (continua regredindo enquanto o progresso for negativo e o nível > 0)
        while (currentLvlProgress < 0 && currentLvl > 0) {
            currentLvl--;
            currentLvlProgress += maxLvlProgress; // Adiciona o valor do nível perdido para compensar
            owner.onLevelDown(this); // Notifica o dono da descida de nível
        }

        // Garante que o progresso não fique negativo (se atingiu o nível 1)
        if (currentLvl <= 1 && currentLvlProgress < 0) {
            currentLvlProgress = 0;
        }
    }

    // ====================================================================
    // MÉTODOS DE CONTROLE MANUAL (Útil para debuffs/reset)
    // ====================================================================

    /**
     * Força a subida de um nível, independentemente do progresso.
     */
    public void levelUp() {
        if (currentLvl < maxLvl) {
            currentLvl++;
            currentLvlProgress = 0; // Zera o progresso ou pode setar para 1/levelMaxProgress
            owner.onLevelUp(this);
        }
    }

    /**
     * Força a descida de um nível, independentemente do progresso.
     */
    public void levelDown() {
        if (currentLvl > 1) {
            currentLvl--;
            currentLvlProgress = maxLvlProgress;
            owner.onLevelDown(this);
        }
    }


    public int getCurrentLvl() {
        return currentLvl;
    }

    // Outros getters e setters conforme necessário (ex: para HUD)
    public float getCurrentLvlProgress() {
        return currentLvlProgress;
    }

    public int getMaxLvl() {
        return maxLvl;
    }

    public float getMaxLvlProgress() {
        return maxLvlProgress;
    }

    public LevelComponentHolderII getOwner() {
        return owner;
    }
}
