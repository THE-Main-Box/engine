package official.sketchBook.engine.test;

import official.sketchBook.engine.components_related.integration_interfaces.LevelComponentHolderII;
import official.sketchBook.engine.components_related.toUse_component.object.LevelingComponent;

public class TestLevelingSystem {

    public static void main(String[] args) {


        LevelComponentHolderII holder = new LevelComponentHolderII() {
            @Override
            public void onLevelUp(LevelingComponent lvlC) {
                System.out.println("yay");
            }

            @Override
            public void onLevelDown(LevelingComponent lvlC) {

                System.out.println("aaa");
            }

            @Override
            public LevelingComponent getLvlC() {
                return null;
            }
        };

        LevelingComponent lvl = new LevelingComponent(holder);

        lvl.init(
            0,
            5,
            0,
            2
        );

        lvl.addProgress(14);
        printLVL(lvl);

        lvl.removeProgress(12);
        printLVL(lvl);
    }

    private static void printLVL(LevelingComponent lvl){
        System.out.println("Current LVL: " + lvl.getCurrentLvl() + "/" + lvl.getMaxLvl());
        System.out.println("Current pro: " + lvl.getCurrentLvlProgress() + "/" + lvl.getMaxLvlProgress());
        System.out.println("//--///---//---");
    }
}
