package pokeregions.dungeons;

import actlikeit.dungeons.CustomDungeon;
import pokeregions.PokemonRegions;
import pokeregions.events.ProfessorOak;
import pokeregions.monsters.act1.enemies.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class Kanto extends AbstractPokemonRegionDungeon {

    public static String ID = PokemonRegions.makeID(Kanto.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public Kanto() {
        super(NAME, ID, "images/ui/event/panel.png", false, 3, 12, 10);
        this.setMainMusic(PokemonRegions.makeMusicPath("Littleroot.ogg"));
        this.addTempMusic("Zinnia", PokemonRegions.makeMusicPath("Zinnia.ogg"));
        this.onEnterEvent(ProfessorOak.class);
    }

    public Kanto(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public Kanto(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
        cardUpgradedChance = 0.0F;
    }

    @Override
    public String getBodyText() {
        return TEXT[2];
    }

    @Override
    public String getOptionText() {
        return TEXT[3];
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
        monsters.add(new MonsterInfo(VulpixEnemy.ID, 2.0F));
        monsters.add(new MonsterInfo(RhyhornEnemy.ID, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.DIGLETTS_2, 2.0F));
        monsters.add(new MonsterInfo(EncounterIDs.RATS_2, 2.0F));
        MonsterInfo.normalizeWeights(monsters);
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(DugtrioEnemy.ID, 2.0F));
        //monsters.add(new MonsterInfo("Gremlin Gang", 1.0F));
        //monsters.add(new MonsterInfo("Looter", 2.0F));
        monsters.add(new MonsterInfo(MachampEnemy.ID, 2.0F));
        //monsters.add(new MonsterInfo("Lots of Slimes", 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.RHYHORN_AND_DIGLETT, 1.5F));
        monsters.add(new MonsterInfo(EncounterIDs.FOX_AND_RAT, 1.5F));
        monsters.add(new MonsterInfo(ArbokEnemy.ID, 2.0F));
        //monsters.add(new MonsterInfo("3 Louse", 2.0F));
        //monsters.add(new MonsterInfo("2 Fungi Beasts", 2.0F)); // 2 tentacruel maybe
        MonsterInfo.normalizeWeights(monsters);
        this.populateFirstStrongEnemy(monsters, this.generateExclusions());
        this.populateMonsterList(monsters, count, false);
    }

    @Override
    protected void generateElites(int count) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        monsters.add(new MonsterInfo(GolemEnemy.ID, 1.0F));
        monsters.add(new MonsterInfo(CloysterEnemy.ID, 1.0F));
        monsters.add(new MonsterInfo(EncounterIDs.GHOST_SQUAD, 1.0F));
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