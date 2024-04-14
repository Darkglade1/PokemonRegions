package pokeregions.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import pokeregions.PokemonRegions;
import pokeregions.events.ProfessorOak;
import pokeregions.monsters.act1.enemies.*;
import pokeregions.monsters.act2.enemies.QuagsireEnemy;
import pokeregions.monsters.act2.enemies.ScizorEnemy;
import pokeregions.monsters.act2.enemies.SteelixEnemy;

import java.util.ArrayList;

public class Johto extends AbstractPokemonRegionDungeon {

    public static String ID = PokemonRegions.makeID(Johto.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Johto() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        //this.onEnterEvent(ProfessorOak.class);
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
        monsters.add(new MonsterInfo("Spheric Guardian", 2.0F)); // Kingdra
        monsters.add(new MonsterInfo("Chosen", 2.0F)); // Azumaril
        //monsters.add(new MonsterInfo("Shell Parasite", 2.0F));
        monsters.add(new MonsterInfo("3 Byrds", 2.0F)); // 3 Swinubs
        monsters.add(new MonsterInfo(EncounterIDs.SLUGMA_2, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo("Chosen and Byrds", 2.0F)); // Azumaril and Mantine
        monsters.add(new MonsterInfo("Sentry and Sphere", 2.0F)); // Kingdra and Lanturn
        monsters.add(new MonsterInfo(QuagsireEnemy.ID, 6.0F));
        monsters.add(new MonsterInfo("Snecko", 4.0F));
        monsters.add(new MonsterInfo("Centurion and Healer", 6.0F)); // 2 Skarmory
        monsters.add(new MonsterInfo(EncounterIDs.MAGCARGO_AND_SLUGMA, 3.0F));
        monsters.add(new MonsterInfo("3 Cultists", 3.0F)); // 2 Swinubs and Piloswine
        //monsters.add(new MonsterInfo("Shelled Parasite and Fungi", 3.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(SteelixEnemy.ID, 1.0F));
        monsters.add(new MonsterInfo("Slavers", 1.0F)); // Tyranitar Squad
        monsters.add(new MonsterInfo(ScizorEnemy.ID, 1.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        switch ((String)monsterList.get(monsterList.size() - 1)) {
            case "Spheric Guardian":
                retVal.add("Sentry and Sphere");
                break;
            case "3 Byrds":
                retVal.add("Chosen and Byrds");
                break;
            case "Chosen":
                retVal.add("Chosen and Byrds");
                retVal.add("Cultist and Chosen");
        }
        return retVal;
    }
}