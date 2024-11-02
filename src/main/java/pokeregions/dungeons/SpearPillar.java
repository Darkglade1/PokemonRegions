package pokeregions.dungeons;

import actlikeit.dungeons.CustomDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapGenerator;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import pokeregions.PokemonRegions;
import pokeregions.monsters.act4.DialgaEnemy;
import pokeregions.monsters.act4.PalkiaEnemy;
import pokeregions.relics.PokeballBelt;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.adp;

public class SpearPillar extends AbstractPokemonRegionDungeon {

    public static String ID = PokemonRegions.makeID(SpearPillar.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    public static final String[] TEXT = uiStrings.TEXT;
    public static final String NAME = TEXT[0];

    public SpearPillar() {
        super(NAME, ID, "images/ui/event/panel.png", false, 2, 12, 10);
        this.addTempMusic("LavenderTown", PokemonRegions.makeMusicPath("LavenderTown.ogg"));
        this.addTempMusic("Giratina", PokemonRegions.makeMusicPath("Giratina.ogg"));
    }

    public SpearPillar(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd, p, emptyList);
    }

    public SpearPillar(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
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
    public boolean canSpawn() {
        return adp().hasRelic(PokeballBelt.ID);
    }

    @Override
    public void Ending() {
        CardCrawlGame.music.fadeOutBGM();
        MapRoomNode node = new MapRoomNode(3, 4);
        node.room = new PokemonVictoryRoom();
        AbstractDungeon.nextRoom = node;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.nextRoomTransitionStart();
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
    protected void makeMap() {
        MonsterRoom dialgaRoom = new MonsterRoomCreator(makeMonsterPath("Dialga/DialgaMap.png"), makeMonsterPath("Dialga/DialgaMapOutline.png"), DialgaEnemy.ID).get();
        MonsterRoom palkiaRoom = new MonsterRoomCreator(makeMonsterPath("Palkia/PalkiaMap.png"), makeMonsterPath("Palkia/PalkiaMapOutline.png"), PalkiaEnemy.ID).get();

        map = new ArrayList<>();

        int index = 0;
        map.add(singleNodeArea(new ShopRoom(), index++));
        map.add(singleNodeArea(dialgaRoom, index++));
        map.add(singleNodeArea(new RestRoom(), index++));
        map.add(singleNodeArea(palkiaRoom, index++));
        map.add(singleNodeArea(new RestRoom(), index++));
        map.add(singleNodeArea(new MonsterRoomBoss(), index++));
        map.add(singleNodeArea(new TrueVictoryRoom(), index++, false));

        logger.info("Generated the following dungeon map:");
        logger.info(MapGenerator.toString(map, true));

        firstRoomChosen = false;
        fadeIn();
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(new MapEdge(src.x, src.y, src.offsetX, src.offsetY, dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }

    private ArrayList<MapRoomNode> populate(ArrayList<MonsterRoomCreator> possibilities, int index) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            MapRoomNode mrn = new MapRoomNode(i, index);
            if (i % 2 == 1) {
                mrn.room = possibilities.get(0).get();
                possibilities.remove(0);
            }
            result.add(mrn);
        }

        if (index > 0) {
            ArrayList<MapRoomNode> mapcontent = map.get(index - 1);
            for (int i = 0; i < mapcontent.size(); i++) {
                if (mapcontent.get(i).room != null) {
                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).room != null) {
                            if (Math.abs(i - j) < 4) {
                                this.connectNode(mapcontent.get(i), result.get(j));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    protected void generateMonsters() {
        monsterList = new ArrayList();
        monsterList.add("Shield and Spear");
        monsterList.add("Shield and Spear");
        monsterList.add("Shield and Spear");
        eliteMonsterList = new ArrayList();
        eliteMonsterList.add("Shield and Spear");
        eliteMonsterList.add("Shield and Spear");
        eliteMonsterList.add("Shield and Spear");
    }

    @Override
    protected void generateWeakEnemies(int count) {

    }

    @Override
    protected void generateStrongEnemies(int count) {

    }

    @Override
    protected void generateElites(int count) {

    }

    @Override
    protected ArrayList<String> generateExclusions() {
        return new ArrayList<>();
    }

    protected ArrayList<MapRoomNode> tripleNodeArea(AbstractRoom roomOne, AbstractRoom roomTwo, AbstractRoom roomThree, int index) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        MapRoomNode mrn;
        result.add(new MapRoomNode(0, index));
        mrn = new MapRoomNode(1, index);
        mrn.room = roomOne;
        result.add(mrn);
        result.add(new MapRoomNode(2, index));
        mrn = new MapRoomNode(3, index);
        mrn.room = roomTwo;
        result.add(mrn);
        result.add(new MapRoomNode(4, index));
        mrn = new MapRoomNode(5, index);
        mrn.room = roomThree;
        result.add(mrn);
        result.add(new MapRoomNode(6, index));
        linkNonMonsterAreas(result);
        return result;
    }

    private ArrayList<MapRoomNode> doubleNodeArea(AbstractRoom roomOne, AbstractRoom roomTwo, int index) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        MapRoomNode mrn;
        result.add(new MapRoomNode(0, index));
        result.add(new MapRoomNode(1, index));
        mrn = new MapRoomNode(2, index);
        mrn.room = roomOne;
        result.add(mrn);
        result.add(new MapRoomNode(3, index));
        mrn = new MapRoomNode(4, index);
        mrn.room = roomTwo;
        result.add(mrn);
        result.add(new MapRoomNode(5, index));
        result.add(new MapRoomNode(6, index));

        linkNonMonsterAreas(result);

        return result;
    }

    private ArrayList<MapRoomNode> singleNodeArea(AbstractRoom room, int index) {
        return singleNodeArea(room, index, true);
    }

    private ArrayList<MapRoomNode> singleNodeArea(AbstractRoom room, int index, boolean connected) {
        ArrayList<MapRoomNode> result = new ArrayList<>();
        MapRoomNode mrn;
        result.add(new MapRoomNode(0, index));
        result.add(new MapRoomNode(1, index));
        result.add(new MapRoomNode(2, index));
        mrn = new MapRoomNode(3, index);
        mrn.room = room;
        result.add(mrn);
        result.add(new MapRoomNode(4, index));
        result.add(new MapRoomNode(5, index));
        result.add(new MapRoomNode(6, index));

        if (connected) {
            linkNonMonsterAreas(result);
        }

        return result;
    }

    private void linkNonMonsterAreas(ArrayList<MapRoomNode> result) {
        if (!map.isEmpty()) {
            ArrayList<MapRoomNode> mapcontent = map.get(map.size() - 1);
            for (int i = 0; i < mapcontent.size(); i++) {
                if (mapcontent.get(i).room != null) {
                    for (int j = 0; j < result.size(); j++) {
                        if (result.get(j).room != null) {
                            this.connectNode(mapcontent.get(i), result.get(j));
                        }
                    }
                }
            }
        }
    }

    static class MonsterRoomCreator {
        String image, outline, encounterID;

        public MonsterRoomCreator(String image, String outline, String encounterID) {
            this.image = image;
            this.outline = outline;
            this.encounterID = encounterID;
        }

        public MonsterRoom get() { return new SpecificMonsterRoom(encounterID, image, outline); }
    }
}