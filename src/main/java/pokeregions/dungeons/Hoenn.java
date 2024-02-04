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
import pokeregions.monsters.act3.enemies.BreloomEnemy;
import pokeregions.monsters.act3.enemies.DeoxysEnemy;
import pokeregions.monsters.act3.enemies.SalamenceEnemy;
import pokeregions.monsters.act3.enemies.SlakingEnemy;

import java.util.ArrayList;

public class Hoenn extends AbstractPokemonRegionDungeon {

    public static String ID = PokemonRegions.makeID(Hoenn.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Hoenn() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        // this.onEnterEvent(ProfessorOak.class);
    }

    public Hoenn(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Hoenn(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
        monsters.add(new MonsterInfo("3 Darklings", 2.0F)); // 3 Trapinch
        monsters.add(new MonsterInfo("Orb Walker", 2.0F)); // Tropius
        monsters.add(new MonsterInfo(EncounterIDs.ARONS_3, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(BreloomEnemy.ID, 1.0F));
        monsters.add(new MonsterInfo(SlakingEnemy.ID, 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.AGGRON_AND_ARONS, 1.0F));
        monsters.add(new MonsterInfo("Maw", 1.0F)); // Metagross
        monsters.add(new MonsterInfo("Sphere and 2 Shapes", 1.0F)); // Solrock and Lunatone
        monsters.add(new MonsterInfo("Jaw Worm Horde", 1.0F)); // 3 Masquerain
        monsters.add(new MonsterInfo("3 Darklings", 1.0F)); // 2 Trapinch and Flygon
        monsters.add(new MonsterInfo("Writhing Mass", 1.0F)); // Gardevoir
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(DeoxysEnemy.ID, 2.0F));
        monsters.add(new MonsterInfo(SalamenceEnemy.ID, 2.0F));
        monsters.add(new MonsterInfo("Reptomancer", 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, true);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
        String previous = monsterList.get(monsterList.size() - 1);
        if (previous.equals(EncounterIDs.RATS_2) || previous.equals(VulpixEnemy.ID)) {
            retVal.add(EncounterIDs.FOX_AND_RAT);
        }
        if (previous.equals(EncounterIDs.DIGLETTS_2) || previous.equals(RhyhornEnemy.ID)) {
            retVal.add(EncounterIDs.RHYHORN_AND_DIGLETT);
        }
        return retVal;
    }
}