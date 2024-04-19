package pokeregions.dungeons;

import actlikeit.dungeons.CustomDungeon;
import pokeregions.PokemonRegions;
import pokeregions.monsters.act1.enemies.MewtwoEnemy;
import pokeregions.monsters.act2.enemies.LugiaEnemy;
import pokeregions.monsters.act3.enemies.GroudonEnemy;
import pokeregions.monsters.act3.enemies.KyogreEnemy;
import pokeregions.monsters.act4.GiratinaEnemy;
import pokeregions.scenes.PokemonScene;
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

    // Play different BGM depending on the boss
    public void setMusic() {
        if (bossKey != null) {
            if (bossKey.equals(EncounterIDs.LEGENDARY_BIRDS) || bossKey.equals(KyogreEnemy.ID)) {
                this.setMainMusic(PokemonRegions.makeMusicPath("OceanicMuseum.ogg"));
            } else if (bossKey.equals(MewtwoEnemy.ID) || bossKey.equals(GroudonEnemy.ID) || bossKey.equals(LugiaEnemy.ID)) {
                this.setMainMusic(PokemonRegions.makeMusicPath("OldaleTown.ogg"));
            } else if (bossKey.equals(GiratinaEnemy.ID)){
                this.setMainMusic(PokemonRegions.makeMusicPath("LavenderTown.ogg"));
            } else {
                this.setMainMusic(PokemonRegions.makeMusicPath("Littleroot.ogg"));
            }
        }
    }
}