package pokeregions.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import pokeregions.PokemonRegions;
import pokeregions.events.ProfessorElm;
import pokeregions.monsters.act2.enemies.*;

import java.util.ArrayList;

public class Johto extends AbstractPokemonRegionDungeon {

    public static String ID = PokemonRegions.makeID(Johto.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Johto() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.onEnterEvent(ProfessorElm.class);
    }

    public Johto(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Johto(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd, p, saveFile);
    }

    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.125F;
        } else {
            cardUpgradedChance = 0.25F;
        }
    }

    @Override
    public String getBodyText() {
        if (CardCrawlGame.dungeon instanceof AbstractPokemonRegionDungeon) {
            return TEXT[2];
        } else {
            String[] oldStrings = CardCrawlGame.languagePack.getUIString(Kanto.ID).TEXT;
            return oldStrings[2];
        }

    }

    @Override
    public String getOptionText() {
        if (CardCrawlGame.dungeon instanceof AbstractPokemonRegionDungeon) {
            return TEXT[3];
        } else {
            String[] oldStrings = CardCrawlGame.languagePack.getUIString(Kanto.ID).TEXT;
            return oldStrings[3];
        }
    }

    @Override
    protected void generateMonsters() {
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(KingdraEnemy.ID, 2.0F));
        monsters.add(new MonsterInfo(AzumarillEnemy.ID, 2.0F));
        //monsters.add(new MonsterInfo("Shell Parasite", 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.SWINUB_3, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.SLUGMA_2, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(EncounterIDs.AZUMARILL_AND_MANTINE, 4.0F));
        monsters.add(new MonsterInfo(EncounterIDs.KINGDRA_AND_LANTURN, 4.0F));
        monsters.add(new MonsterInfo(QuagsireEnemy.ID, 6.0F));
        monsters.add(new MonsterInfo(SkarmoryEnemy.ID, 6.0F));
        monsters.add(new MonsterInfo(EncounterIDs.CROBAT_2, 6.0F));
        monsters.add(new MonsterInfo(EncounterIDs.MAGCARGO_AND_SLUGMA, 4.0F));
        monsters.add(new MonsterInfo(EncounterIDs.PILOSWINE_AND_SWINUBS, 4.0F));
        //monsters.add(new MonsterInfo("Shelled Parasite and Fungi", 3.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(SteelixEnemy.ID, 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.TYRANITAR_GROUP, 1.0F));
        monsters.add(new MonsterInfo(ScizorEnemy.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        String previous = monsterList.get(monsterList.size() - 1);
        if (previous.equals(KingdraEnemy.ID)) {
            retVal.add(EncounterIDs.KINGDRA_AND_LANTURN);
        }
        if (previous.equals(AzumarillEnemy.ID)) {
            retVal.add(EncounterIDs.AZUMARILL_AND_MANTINE);
        }
        if (previous.equals(EncounterIDs.SLUGMA_2)) {
            retVal.add(EncounterIDs.MAGCARGO_AND_SLUGMA);
        }
        if (previous.equals(EncounterIDs.SWINUB_3)) {
            retVal.add(EncounterIDs.PILOSWINE_AND_SWINUBS);
        }
        return retVal;
    }
}