package code.dungeons;

import actlikeit.dungeons.CustomDungeon;
import code.scenes.PokemonScene;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;

import java.util.ArrayList;

public class AbstractPokemonRegionDungeon extends CustomDungeon {

    public AbstractPokemonRegionDungeon(String NAME, String ID, String event, boolean genericEvents, int weak, int strong, int elite) {
        super(NAME, ID, event, genericEvents, weak, strong, elite);
        AbstractDungeon.shrineList.clear();
    }

    public AbstractPokemonRegionDungeon(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
        AbstractDungeon.shrineList.clear();
    }

    public AbstractPokemonRegionDungeon(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
        AbstractDungeon.shrineList.clear();
    }

    @Override
    public AbstractScene DungeonScene() {
        return new PokemonScene();
    }

    @Override
    protected void initializeShrineList() {
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in receivePostInitialize()
    }
}