package code;

import actlikeit.RazIntent.CustomIntent;
import basemod.*;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import code.CustomIntent.MassAttackIntent;
import code.cards.AbstractEasyCard;
import code.cards.cardvars.AbstractEasyDynamicVariable;
import code.dungeons.EncounterIDs;
import code.dungeons.Kanto;
import code.monsters.act1.enemies.*;
import code.monsters.act1.enemies.birds.ArticunoEnemy;
import code.monsters.act1.enemies.birds.MoltresEnemy;
import code.monsters.act1.enemies.birds.ZapdosEnemy;
import code.relics.AbstractEasyRelic;
import code.relics.PokeballBelt;
import code.ui.PokemonTeamButton;
import code.util.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.events.exordium.*;
import com.megacrit.cardcrawl.events.shrines.AccursedBlacksmith;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static code.util.Wiz.adp;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class PokemonRegions implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        AddAudioSubscriber,
        PostInitializeSubscriber,
        StartGameSubscriber,
        PostBattleSubscriber {

    public static final String modID = "pokeRegions";

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    public static SpireConfig pokemonRegionConfig;
    private static Logger logger = LogManager.getLogger(PokemonRegions.class.getName());

    public static class Enums {
        @SpireEnum(name = "Pokedex")
        public static AbstractCard.CardColor Pokedex;
        @SpireEnum(name = "Pokedex")
        @SuppressWarnings("unused")
        public static CardLibrary.LibraryType LIBRARY_COLOR;
    }

    public static Color color = CardHelper.getColor(255, 30, 30);
    private static final String ATTACK_S_ART = makeImagePath("512/attack.png");
    private static final String SKILL_S_ART = makeImagePath("512/skill.png");
    private static final String POWER_S_ART = makeImagePath("512/power.png");
    private static final String CARD_ENERGY_S = makeImagePath("512/energy.png");
    private static final String TEXT_ENERGY = makeImagePath("512/text_energy.png");
    private static final String ATTACK_L_ART = makeImagePath("1024/attack.png");
    private static final String SKILL_L_ART = makeImagePath("1024/skill.png");
    private static final String POWER_L_ART = makeImagePath("1024/power.png");
    private static final String CARD_ENERGY_L = makeImagePath("1024/energy.png");

    // Stuff for detailed intents
    public static Map<AbstractMonster, ArrayList<Details>> intents = new HashMap<>();

    public static final String WEAK = makeUIPath("Weak.png");
    public static Texture WEAK_TEXTURE;

    public static final String FRAIL = makeUIPath("Frail.png");
    public static Texture FRAIL_TEXTURE;

    public static final String VULNERABLE = makeUIPath("Vulnerable.png");
    public static Texture VULNERABLE_TEXTURE;

    public static final String STRENGTH = makeUIPath("Strength.png");
    public static Texture STRENGTH_TEXTURE;

    public static final String PLATED_ARMOR = makeUIPath("PlatedArmor.png");
    public static Texture PLATED_ARMOR_TEXTURE;

    public static final String METALLICIZE = makeUIPath("Metal.png");
    public static Texture METALLICIZE_TEXTURE;

    public static final String BURN_DEBUFF = makePowerPath("Burn32.png");
    public static Texture BURN_DEBUFF_TEXTURE;

    public static final String CONSTRICTED = makeUIPath("Constricted.png");
    public static Texture CONSTRICTED_TEXTURE;

    public static final String INTANGIBLE = makeUIPath("Intangible.png");
    public static Texture INTANGIBLE_TEXTURE;

    public static final String DRAW_DOWN = makeUIPath("DrawDown.png");
    public static Texture DRAW_DOWN_TEXTURE;

    public static final String HEAL = makeUIPath("Heal.png");
    public static Texture HEAL_TEXTURE;

    public static final String BLOCK = makeUIPath("Block.png");
    public static Texture BLOCK_TEXTURE;

    public static final String DRAW_PILE = makeUIPath("DrawPile.png");
    public static Texture DRAW_PILE_TEXTURE;

    public static final String DISCARD_PILE = makeUIPath("DiscardPile.png");
    public static Texture DISCARD_PILE_TEXTURE;

    public static final String BURN = makeUIPath("Burn.png");
    public static Texture BURN_TEXTURE;

    public static final String DAZED = makeUIPath("Dazed.png");
    public static Texture DAZED_TEXTURE;

    public static final String SLIMED = makeUIPath("Slimed.png");
    public static Texture SLIMED_TEXTURE;

    public static final String VOID = makeUIPath("Void.png");
    public static Texture VOID_TEXTURE;

    public static final String WOUND = makeUIPath("Wound.png");
    public static Texture WOUND_TEXTURE;

    public static final String FROZEN = makeUIPath("Frozen.png");
    public static Texture FROZEN_TEXTURE;

    public static final String PAIN = makeUIPath("Pain.png");
    public static Texture PAIN_TEXTURE;

    public static Settings.GameLanguage[] SupportedLanguages = {
            Settings.GameLanguage.ENG,
    };

    private String getLangString() {
        for (Settings.GameLanguage lang : SupportedLanguages) {
            if (lang.equals(Settings.language)) {
                return Settings.language.name().toLowerCase();
            }
        }
        return "eng";
    }

    public PokemonRegions() {
        BaseMod.subscribe(this);

        BaseMod.addColor(Enums.Pokedex, color, color, color,
                color, color, color, color,
                ATTACK_S_ART, SKILL_S_ART, POWER_S_ART, CARD_ENERGY_S,
                ATTACK_L_ART, SKILL_L_ART, POWER_L_ART,
                CARD_ENERGY_L, TEXT_ENERGY);

        Properties pokemonRegionDefaults = new Properties();
        pokemonRegionDefaults.setProperty("Pokemon Combat Tutorial Seen", "FALSE");
        try {
            pokemonRegionConfig = new SpireConfig("Pokemon Regions", "PokemonRegionsMod", pokemonRegionDefaults);
        } catch (IOException e) {
            logger.error("PokemonRegionMod SpireConfig initialization failed:");
            e.printStackTrace();
        }
    }

    public static String makePath(String resourcePath) {
        return modID + "Resources/" + resourcePath;
    }

    public static String makeImagePath(String resourcePath) {
        return modID + "Resources/images/" + resourcePath;
    }

    public static String makeRelicPath(String resourcePath) {
        return modID + "Resources/images/relics/" + resourcePath;
    }

    public static String makePowerPath(String resourcePath) {
        return modID + "Resources/images/powers/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return modID + "Resources/images/ui/" + resourcePath;
    }

    public static String makeMonsterPath(String resourcePath) {
        return modID + "Resources/images/monsters/" + resourcePath;
    }

    public static String makeEventPath(String resourcePath) {
        return modID + "Resources/images/events/" + resourcePath;
    }

    public static String makeMusicPath(String resourcePath) {
        return modID + "Resources/audio/music/" + resourcePath;
    }

    public static String makeCardPath(String resourcePath) {
        return modID + "Resources/images/cards/" + resourcePath;
    }

    public static void initialize() {
        PokemonRegions thismod = new PokemonRegions();
    }

    @Override
    public void receivePostInitialize() {
        WEAK_TEXTURE = TexLoader.getTexture(WEAK);
        FRAIL_TEXTURE = TexLoader.getTexture(FRAIL);
        VULNERABLE_TEXTURE = TexLoader.getTexture(VULNERABLE);
        STRENGTH_TEXTURE = TexLoader.getTexture(STRENGTH);
        PLATED_ARMOR_TEXTURE = TexLoader.getTexture(PLATED_ARMOR);
        METALLICIZE_TEXTURE = TexLoader.getTexture(METALLICIZE);
        BURN_DEBUFF_TEXTURE = TexLoader.getTexture(BURN_DEBUFF);
        CONSTRICTED_TEXTURE = TexLoader.getTexture(CONSTRICTED);
        INTANGIBLE_TEXTURE = TexLoader.getTexture(INTANGIBLE);
        DRAW_DOWN_TEXTURE = TexLoader.getTexture(DRAW_DOWN);

        HEAL_TEXTURE = TexLoader.getTexture(HEAL);
        BLOCK_TEXTURE = TexLoader.getTexture(BLOCK);

        DRAW_PILE_TEXTURE = TexLoader.getTexture(DRAW_PILE);
        DISCARD_PILE_TEXTURE = TexLoader.getTexture(DISCARD_PILE);

        BURN_TEXTURE = TexLoader.getTexture(BURN);
        DAZED_TEXTURE = TexLoader.getTexture(DAZED);
        SLIMED_TEXTURE = TexLoader.getTexture(SLIMED);
        VOID_TEXTURE = TexLoader.getTexture(VOID);
        WOUND_TEXTURE = TexLoader.getTexture(WOUND);
        FROZEN_TEXTURE = TexLoader.getTexture(FROZEN);
        PAIN_TEXTURE = TexLoader.getTexture(PAIN);

        CustomIntent.add(new MassAttackIntent());
        BaseMod.addSaveField(PokemonTeamButton.ID, new PokemonTeamButton());
        BaseMod.registerCustomReward(
                PokemonRewardEnum.POKEMON_REWARD,
                (rewardSave) -> { // this handles what to do when this type is loaded.
                    return new PokemonReward(rewardSave.id);
                },
                (customReward) -> { // this handles what to do when this type is saved.
                    return new RewardSave(customReward.type.toString(), ((PokemonReward)customReward).card.cardID, 0, 0);
                });

        Kanto kanto = new Kanto();
        kanto.addAct(Exordium.ID);

        //Bosses
        kanto.addBoss(DragoniteEnemy.ID, (BaseMod.GetMonster) DragoniteEnemy::new, makeMonsterPath("Dragonite/DragoniteMap.png"), makeMonsterPath("Dragonite/DragoniteMapOutline.png"));
        kanto.addBoss(EncounterIDs.LEGENDARY_BIRDS, "The Legendary Birds", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new ArticunoEnemy(-450.0F, 0.0F),
                        new MoltresEnemy(-150.0F, 150.0F),
                        new ZapdosEnemy(150.0F, 150.0F)
                }), makeMonsterPath("Moltres/BirdMap.png"), makeMonsterPath("Moltres/BirdMapOutline.png"));

        //Elites
        BaseMod.addMonster(CloysterEnemy.ID, (BaseMod.GetMonster) CloysterEnemy::new);
        BaseMod.addMonster(EncounterIDs.GHOST_SQUAD, "Ghost Squad", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new GastlyEnemy(-450.0F, 100.0F),
                        new HaunterEnemy(-150.0F, 100.0F),
                        new GengarEnemy(150.0F, 100.0F)
                }));
        BaseMod.addMonster(GolemEnemy.ID, (BaseMod.GetMonster) GolemEnemy::new);

        // Easy encounters
        BaseMod.addMonster(EncounterIDs.DIGLETTS_2, "2 Digletts", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new DiglettEnemy(-200.0F, 0.0F, true),
                        new DiglettEnemy(50.0F, 0.0F, false),
                }));
        BaseMod.addMonster(VulpixEnemy.ID, (BaseMod.GetMonster) VulpixEnemy::new);
        BaseMod.addMonster(EncounterIDs.RATS_2, "2 Rats", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new RattataEnemy(-200.0F, 0.0F),
                        new RattataEnemy(50.0F, 0.0F),
                }));
        BaseMod.addMonster(RhyhornEnemy.ID, (BaseMod.GetMonster) RhyhornEnemy::new);

        // Normal encounters
        BaseMod.addMonster(DugtrioEnemy.ID, (BaseMod.GetMonster) DugtrioEnemy::new);
        BaseMod.addMonster(ArbokEnemy.ID, (BaseMod.GetMonster) ArbokEnemy::new);
        BaseMod.addMonster(MachampEnemy.ID, (BaseMod.GetMonster) MachampEnemy::new);
        BaseMod.addMonster(EncounterIDs.FOX_AND_RAT, "Fox and Rat", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new RattataEnemy(-200.0F, 0.0F),
                        new VulpixEnemy(50.0F, 0.0F),
                }));
        BaseMod.addMonster(EncounterIDs.RHYHORN_AND_DIGLETT, "Rhyhorn and Diglett", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new DiglettEnemy(-200.0F, 0.0F, true),
                        new RhyhornEnemy(50.0F, 0.0F),
                }));

        // Events
        BaseMod.addEvent(Sssserpent.ID, Sssserpent.class, Kanto.ID);
        BaseMod.addEvent(LivingWall.ID, LivingWall.class, Kanto.ID);
        BaseMod.addEvent(BigFish.ID, BigFish.class, Kanto.ID);
        BaseMod.addEvent(ScrapOoze.ID, ScrapOoze.class, Kanto.ID);
        BaseMod.addEvent(ShiningLight.ID, ShiningLight.class, Kanto.ID);
        BaseMod.addEvent(GoldenIdolEvent.ID, GoldenIdolEvent.class, Kanto.ID);
        BaseMod.addEvent(GoopPuddle.ID, GoopPuddle.class, Kanto.ID);
        BaseMod.addEvent(GoldenWing.ID, GoldenWing.class, Kanto.ID);
    }

    @Override
    public void receiveEditRelics() {
        new AutoAdd(modID)
                .packageFilter(AbstractEasyRelic.class)
                .any(AbstractEasyRelic.class, (info, relic) -> {
                    if (relic.color == null) {
                        BaseMod.addRelic(relic, RelicType.SHARED);
                    } else {
                        BaseMod.addRelicToCustomPool(relic, relic.color);
                    }
                    if (!info.seen) {
                        UnlockTracker.markRelicAsSeen(relic.relicId);
                    }
                });
    }

    @Override
    public void receiveEditCards() {
        new AutoAdd(modID)
            .packageFilter(AbstractEasyDynamicVariable.class)
            .any(DynamicVariable.class, (info, var) -> 
                BaseMod.addDynamicVariable(var));
        new AutoAdd(modID)
                .packageFilter(AbstractEasyCard.class)
                .setDefaultSeen(false)
                .cards();
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(CardStrings.class, modID + "Resources/localization/" + getLangString() + "/Cardstrings.json");
        BaseMod.loadCustomStringsFile(RelicStrings.class, modID + "Resources/localization/" + getLangString() + "/Relicstrings.json");
        BaseMod.loadCustomStringsFile(PowerStrings.class, modID + "Resources/localization/" + getLangString() + "/Powerstrings.json");
        BaseMod.loadCustomStringsFile(UIStrings.class, modID + "Resources/localization/" + getLangString() + "/UIstrings.json");
        BaseMod.loadCustomStringsFile(EventStrings.class, modID + "Resources/localization/" + getLangString() + "/Eventstrings.json");
        BaseMod.loadCustomStringsFile(MonsterStrings.class, modID + "Resources/localization/" + getLangString() + "/Monsterstrings.json");
        BaseMod.loadCustomStringsFile(PotionStrings.class, modID + "Resources/localization/" + getLangString() + "/Potionstrings.json");
        BaseMod.loadCustomStringsFile(TutorialStrings.class, modID + "Resources/localization/" + getLangString() + "/Tutorialstrings.json");
    }

    @Override
    public void receiveAddAudio() {
        for (ProAudio a : ProAudio.values())
            BaseMod.addAudio(makeID(a.name()), makePath("audio/" + a.name().toLowerCase() + ".ogg"));
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(modID + "Resources/localization/" + getLangString() + "/Keywordstrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(modID.toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    @Override
    public void receiveStartGame() {
        if (!adp().hasRelic(PokeballBelt.ID)) {
            ArrayList<TopPanelItem> itemsToRemove = new ArrayList<>();
            ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
            for (TopPanelItem item : topPanelItems) {
                if (item instanceof PokemonTeamButton) {
                    itemsToRemove.add(item);
                }
            }
            for (TopPanelItem item : itemsToRemove) {
                BaseMod.removeTopPanelItem(item);
            }
        }
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        intents.clear();
    }
}
