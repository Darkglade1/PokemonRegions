package pokeregions;

import actlikeit.RazIntent.CustomIntent;
import basemod.*;
import basemod.abstracts.DynamicVariable;
import basemod.eventUtil.AddEventParams;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.events.city.*;
import com.megacrit.cardcrawl.events.shrines.FaceTrader;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rewards.RewardSave;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pokeregions.CustomIntent.MassAttackIntent;
import pokeregions.cards.AbstractEasyCard;
import pokeregions.cards.cardvars.AbstractEasyDynamicVariable;
import pokeregions.dungeons.*;
import pokeregions.events.act1.*;
import pokeregions.events.act2.BlackthornGym;
import pokeregions.events.act2.OlivineLighthouse;
import pokeregions.events.act3.*;
import pokeregions.monsters.act1.enemies.*;
import pokeregions.monsters.act1.enemies.birds.ArticunoEnemy;
import pokeregions.monsters.act1.enemies.birds.MoltresEnemy;
import pokeregions.monsters.act1.enemies.birds.ZapdosEnemy;
import pokeregions.monsters.act2.enemies.*;
import pokeregions.monsters.act3.enemies.*;
import pokeregions.monsters.act3.enemies.rayquaza.FlygonR;
import pokeregions.monsters.act3.enemies.rayquaza.RayquazaEnemy;
import pokeregions.monsters.act3.enemies.rayquaza.SalamenceR;
import pokeregions.monsters.act4.DialgaEnemy;
import pokeregions.monsters.act4.GiratinaEnemy;
import pokeregions.monsters.act4.PalkiaEnemy;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.relics.AbstractEasyRelic;
import pokeregions.relics.PokeballBelt;
import pokeregions.ui.PokemonTeamButton;
import pokeregions.ui.PokemonTeamViewScreen;
import pokeregions.util.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static pokeregions.util.Wiz.adp;

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
        PostBattleSubscriber,
        PostUpdateSubscriber {

    public static final String modID = "pokeRegions";

    public static String makeID(String idText) {
        return modID + ":" + idText;
    }

    public static SpireConfig pokemonRegionConfig;

    public static final String DISABLE_POKEMON_OUTSIDE_CONFIG = "disablePokemonOutsideConfig";
    public static boolean disablePokemonOutsideConfig = false;

    public static final String DISABLE_DETAILED_INTENTS_CONFIG = "disableDetailedIntentsConfig";
    public static boolean disableDetailedIntentsConfig = false;
    private static Logger logger = LogManager.getLogger(PokemonRegions.class.getName());

    public static boolean releasingPokemon;

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

    public static final String DEXTERITY = makeUIPath("Dexterity.png");
    public static Texture DEXTERITY_TEXTURE;

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

    public static final String THORNS = makeUIPath("Thorns.png");
    public static Texture THORNS_TEXTURE;

    public static final String REGEN = makeUIPath("Regen.png");
    public static Texture REGEN_TEXTURE;

    public static final String NASTY_PLOT = makeUIPath("Nasty.png");
    public static Texture NASTY_PLOT_TEXTURE;

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
            Settings.GameLanguage.ZHS
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
        pokemonRegionDefaults.setProperty("Pokemon Catch Tutorial Seen", "FALSE");
        try {
            pokemonRegionConfig = new SpireConfig("Pokemon Regions", "PokemonRegionsMod", pokemonRegionDefaults);
        } catch (IOException e) {
            logger.error("PokemonRegionMod SpireConfig initialization failed:");
            e.printStackTrace();
        }
        loadConfig();
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

    public static String makeVfxPath(String resourcePath) {
        return modID + "Resources/images/vfx/" + resourcePath;
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
        DEXTERITY_TEXTURE = TexLoader.getTexture(DEXTERITY);
        PLATED_ARMOR_TEXTURE = TexLoader.getTexture(PLATED_ARMOR);
        METALLICIZE_TEXTURE = TexLoader.getTexture(METALLICIZE);
        BURN_DEBUFF_TEXTURE = TexLoader.getTexture(BURN_DEBUFF);
        CONSTRICTED_TEXTURE = TexLoader.getTexture(CONSTRICTED);
        INTANGIBLE_TEXTURE = TexLoader.getTexture(INTANGIBLE);
        DRAW_DOWN_TEXTURE = TexLoader.getTexture(DRAW_DOWN);
        THORNS_TEXTURE = TexLoader.getTexture(THORNS);
        REGEN_TEXTURE = TexLoader.getTexture(REGEN);
        NASTY_PLOT_TEXTURE = TexLoader.getTexture(NASTY_PLOT);

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

        // Load the Mod Badge
        Texture badgeTexture = TexLoader.getTexture(makeUIPath("Badge.png"));

        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModMenu"));
        String[] modMenuText = uiStrings.TEXT;
        BaseMod.registerModBadge(badgeTexture, modMenuText[0], modMenuText[1], modMenuText[2], settingsPanel);

        // Create the on/off button:
        ModLabeledToggleButton disablePokemonOutside = new ModLabeledToggleButton(modMenuText[3],
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                disablePokemonOutsideConfig,
                settingsPanel,
                (label) -> {},
                (button) -> {
                    disablePokemonOutsideConfig = button.enabled;
                    saveData();
                });
        settingsPanel.addUIElement(disablePokemonOutside);

        ModLabeledToggleButton disableDetailedIntents = new ModLabeledToggleButton(modMenuText[4],
                350.0f, 600.0f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                disableDetailedIntentsConfig,
                settingsPanel,
                (label) -> {},
                (button) -> {
                    disableDetailedIntentsConfig = button.enabled;
                    saveData();
                });
        settingsPanel.addUIElement(disableDetailedIntents);

        CustomIntent.add(new MassAttackIntent());
        BaseMod.addSaveField(PokemonTeamButton.ID, new PokemonTeamButton());
        BaseMod.addSaveField(makeID("pokemonCaught"), new PlayerSpireFields());
        BaseMod.registerCustomReward(
                PokemonRewardEnum.POKEMON_REWARD,
                (rewardSave) -> { // this handles what to do when this type is loaded.
                    return new PokemonReward(rewardSave.id);
                },
                (customReward) -> { // this handles what to do when this type is saved.
                    return new RewardSave(customReward.type.toString(), ((PokemonReward)customReward).card.cardID, 0, 0);
                });
        BaseMod.addCustomScreen(new PokemonTeamViewScreen());

        // Act 1
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
        kanto.addBoss(MewtwoEnemy.ID, (BaseMod.GetMonster) MewtwoEnemy::new, makeMonsterPath("Mewtwo/MewtwoMap.png"), makeMonsterPath("Mewtwo/MewtwoMapOutline.png"));

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
        BaseMod.addMonster(EncounterIDs.BUG_SWARM, "Bug Swarm", () -> new MonsterGroup(generateBugSwarmGroup()));
        BaseMod.addMonster(EncounterIDs.OMASTAR_2, "2 Omastars", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new OmastarEnemy(-200.0F, 0.0F),
                        new OmastarEnemy(50.0F, 0.0F),
                }));
        BaseMod.addMonster(AlakazamEnemy.ID, (BaseMod.GetMonster) AlakazamEnemy::new);

        // Event encounter
        BaseMod.addMonster(EncounterIDs.TEAM_ROCKET, "Team Rocket", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new VictreebelEnemy(-200.0F, 0.0F, false),
                        new ArbokEnemy(50.0F, 0.0F, false),
                }));

        // Events
        BaseMod.addEvent(CeladonGym.ID, CeladonGym.class, Kanto.ID);
        BaseMod.addEvent(new AddEventParams.Builder(PokemonCenter.ID, PokemonCenter.class)
                .bonusCondition(PokemonCenter::canSpawn)
                .dungeonID(Kanto.ID)
                .create());
        BaseMod.addEvent(FuchsiaGym.ID, FuchsiaGym.class, Kanto.ID);
        BaseMod.addEvent(BerryBush.ID, BerryBush.class, Kanto.ID);
        BaseMod.addEvent(SaffronGym.ID, SaffronGym.class, Kanto.ID);
        BaseMod.addEvent(new AddEventParams.Builder(MagikarpSalesman.ID, MagikarpSalesman.class)
                .bonusCondition(MagikarpSalesman::canSpawn)
                .dungeonID(Kanto.ID)
                .create());
        BaseMod.addEvent(new AddEventParams.Builder(TradeOffer.ID, TradeOffer.class)
                .bonusCondition(TradeOffer::canSpawn)
                .dungeonID(Kanto.ID)
                .create());
        BaseMod.addEvent(MewsGame.ID, MewsGame.class, Kanto.ID);
        BaseMod.addEvent(VermilionGym.ID, VermilionGym.class, Kanto.ID);
        BaseMod.addEvent(new AddEventParams.Builder(TeamRocket.ID, TeamRocket.class)
                .bonusCondition(TeamRocket::canSpawn)
                .dungeonID(Kanto.ID)
                .create());
        BaseMod.addEvent(new AddEventParams.Builder(CinnabarGym.ID, CinnabarGym.class)
                .bonusCondition(CinnabarGym::canSpawn)
                .dungeonID(Kanto.ID)
                .create());
        BaseMod.addEvent(Yellow.ID, Yellow.class, Kanto.ID);

        // Act 2
        Johto johto = new Johto();
        johto.addAct(TheCity.ID);

        // Bosses
        johto.addBoss(HoOhEnemy.ID, (BaseMod.GetMonster) HoOhEnemy::new, makeMonsterPath("HoOh/HoOhMap.png"), makeMonsterPath("HoOh/HoOhMapOutline.png"));
        johto.addBoss(LugiaEnemy.ID, (BaseMod.GetMonster) LugiaEnemy::new, makeMonsterPath("Lugia/LugiaMap.png"), makeMonsterPath("Lugia/LugiaMapOutline.png"));

        // Elites
        BaseMod.addMonster(ScizorEnemy.ID, (BaseMod.GetMonster) ScizorEnemy::new);
        BaseMod.addMonster(SteelixEnemy.ID, (BaseMod.GetMonster) SteelixEnemy::new);
        BaseMod.addMonster(EncounterIDs.TYRANITAR_GROUP, "Tyranitar Group", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new TyranitarEnemy(-450.0F, 0.0F),
                        new PupitarEnemy(-150.0F, 0.0F, false),
                        new PupitarEnemy(150.0F, 0.0F, true)
                }));

        // Normal encounters
        BaseMod.addMonster(EncounterIDs.SLUGMA_2, "2 Slugmas", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new SlugmaEnemy(-200.0F, 0.0F),
                        new SlugmaEnemy(50.0F, 0.0F),
                }));
        BaseMod.addMonster(EncounterIDs.MAGCARGO_AND_SLUGMA, "Magcargo and Slugma", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new SlugmaEnemy(-200.0F, 0.0F),
                        new MagcargoEnemy(50.0F, 0.0F),
                }));
        BaseMod.addMonster(QuagsireEnemy.ID, (BaseMod.GetMonster) QuagsireEnemy::new);
        BaseMod.addMonster(AzumarillEnemy.ID, (BaseMod.GetMonster) AzumarillEnemy::new);
        BaseMod.addMonster(EncounterIDs.AZUMARILL_AND_MANTINE, "Azumarill and Mantine", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new AzumarillEnemy(-200.0F, 0.0F),
                        new MantineEnemy(50.0F, 100.0F),
                }));
        BaseMod.addMonster(KingdraEnemy.ID, (BaseMod.GetMonster) KingdraEnemy::new);
        BaseMod.addMonster(EncounterIDs.KINGDRA_AND_LANTURN, "Kingdra and Lanturn", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new KingdraEnemy(-200.0F, 0.0F),
                        new LanturnEnemy(50.0F, 0.0F),
                }));
        BaseMod.addMonster(SkarmoryEnemy.ID, (BaseMod.GetMonster) SkarmoryEnemy::new);

        //Events
        BaseMod.addEvent(new AddEventParams.Builder(OlivineLighthouse.ID, OlivineLighthouse.class)
                .bonusCondition(OlivineLighthouse::canSpawn)
                .dungeonID(Johto.ID)
                .create());
        BaseMod.addEvent(BlackthornGym.ID, BlackthornGym.class, Johto.ID);
        BaseMod.addEvent(Beggar.ID, Beggar.class, Johto.ID);
        BaseMod.addEvent(Addict.ID, Addict.class, Johto.ID);
        BaseMod.addEvent(TheJoust.ID, TheJoust.class, Johto.ID);
        BaseMod.addEvent(Nest.ID, Nest.class, Johto.ID);
        BaseMod.addEvent(ForgottenAltar.ID, ForgottenAltar.class, Johto.ID);

        // Act 3
        Hoenn hoenn = new Hoenn();
        hoenn.addAct(TheBeyond.ID);

        // Bosses
        hoenn.addBoss(KyogreEnemy.ID, (BaseMod.GetMonster) KyogreEnemy::new, makeMonsterPath("Kyogre/KyogreMap.png"), makeMonsterPath("Kyogre/KyogreMapOutline.png"));
        hoenn.addBoss(GroudonEnemy.ID, (BaseMod.GetMonster) GroudonEnemy::new, makeMonsterPath("Groudon/GroudonMap.png"), makeMonsterPath("Groudon/GroudonMapOutline.png"));
        hoenn.addBoss(RayquazaEnemy.ID, "Rayquaza, Dragon Lord", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new SalamenceR(-450.0F, 0.0F),
                        new RayquazaEnemy(-150.0F, 150.0F),
                        new FlygonR(150.0F, 150.0F)
                }), makeMonsterPath("Rayquaza/RayquazaMap.png"), makeMonsterPath("Rayquaza/RayquazaMapOutline.png"));

        // Elites
        BaseMod.addMonster(DeoxysEnemy.ID, (BaseMod.GetMonster) DeoxysEnemy::new);
        BaseMod.addMonster(SalamenceEnemy.ID, (BaseMod.GetMonster) SalamenceEnemy::new);
        BaseMod.addMonster(EncounterIDs.LEGENDARY_GIANTS, "Legendary Giants", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new RegisteelEnemy(-450.0F, 0.0F),
                        new RegiceEnemy(-150.0F, 0.0F),
                        new RegirockEnemy(150.0F, 0.0F)
                }));

        // Normal encounters
        BaseMod.addMonster(SlakingEnemy.ID, (BaseMod.GetMonster) SlakingEnemy::new);
        BaseMod.addMonster(BreloomEnemy.ID, (BaseMod.GetMonster) BreloomEnemy::new);
        BaseMod.addMonster(EncounterIDs.ARONS_3, "3 Arons", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new AronEnemy(-450.0F, 0.0F),
                        new AronEnemy(-150.0F, 0.0F),
                        new AronEnemy(150.0F, 0.0F)
                }));
        BaseMod.addMonster(EncounterIDs.AGGRON_AND_ARONS, "Aggron and Arons", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new AronEnemy(-450.0F, 0.0F),
                        new AronEnemy(-150.0F, 0.0F),
                        new AggronEnemy(150.0F, 0.0F)
                }));
        BaseMod.addMonster(TropiusEnemy.ID, (BaseMod.GetMonster) TropiusEnemy::new);
        BaseMod.addMonster(EncounterIDs.TRAPINCHES_3, "3 Trapinches", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new TrapinchEnemy(-450.0F, 0.0F, false),
                        new TrapinchEnemy(-150.0F, 0.0F, false),
                        new TrapinchEnemy(150.0F, 0.0F, false)
                }));
        BaseMod.addMonster(EncounterIDs.FLYGON_AND_TRAPINCHES, "Flygon and Trapinches", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new TrapinchEnemy(-450.0F, 0.0F, true),
                        new TrapinchEnemy(-150.0F, 0.0F, true),
                        new FlygonEnemy(150.0F, 100.0F)
                }));
        BaseMod.addMonster(GardevoirEnemy.ID, (BaseMod.GetMonster) GardevoirEnemy::new);
        BaseMod.addMonster(MetagrossEnemy.ID, (BaseMod.GetMonster) MetagrossEnemy::new);
        BaseMod.addMonster(EncounterIDs.SOLROCK_AND_LUNATONE, "Solrock and Lunatone", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new SolrockEnemy(-200.0F, 0.0F),
                        new LunatoneEnemy(50.0F, 0.0F),
                }));

        // Event encounter
        BaseMod.addMonster(EncounterIDs.ANNIE_AND_OAKLEY, "Annie and Oakley", () -> new MonsterGroup(
                new AbstractMonster[]{
                        new AriadosOakley(-200.0F, 0.0F),
                        new EspeonAnnie(50.0F, 0.0F),
                }));

        // Events
        BaseMod.addEvent(PokemonTrainerSchool.ID, PokemonTrainerSchool.class, Hoenn.ID);
        BaseMod.addEvent(WishUponAStar.ID, WishUponAStar.class, Hoenn.ID);
        BaseMod.addEvent(new AddEventParams.Builder(BumpInTheRoad.ID, BumpInTheRoad.class)
                .bonusCondition(BumpInTheRoad::canSpawn)
                .dungeonID(Hoenn.ID)
                .create());
        BaseMod.addEvent(FeatherCarnival.ID, FeatherCarnival.class, Hoenn.ID);
        BaseMod.addEvent(LavaridgeGym.ID, LavaridgeGym.class, Hoenn.ID);
        BaseMod.addEvent(WeatherInstitute.ID, WeatherInstitute.class, Hoenn.ID);
        BaseMod.addEvent(Mossdeep.ID, Mossdeep.class, Hoenn.ID);
        BaseMod.addEvent(Altomare.ID, Altomare.class, Hoenn.ID);
        BaseMod.addEvent(new AddEventParams.Builder(Pokemart.ID, Pokemart.class)
                .bonusCondition(Pokemart::canSpawn)
                .dungeonID(Hoenn.ID)
                .create());
        BaseMod.addEvent(new AddEventParams.Builder(AncientRuins.ID, AncientRuins.class)
                .bonusCondition(AncientRuins::canSpawn)
                .dungeonID(Hoenn.ID)
                .create());

        // Act 4
        SpearPillar spearPillar = new SpearPillar();
        spearPillar.addAct(TheEnding.ID);
        BaseMod.addMonster(DialgaEnemy.ID, (BaseMod.GetMonster) DialgaEnemy::new);
        BaseMod.addMonster(PalkiaEnemy.ID, (BaseMod.GetMonster) PalkiaEnemy::new);
        spearPillar.addBoss(GiratinaEnemy.ID, (BaseMod.GetMonster) GiratinaEnemy::new, makeMonsterPath("Giratina/GiratinaMap.png"), makeMonsterPath("Giratina/GiratinaMapOutline.png"));
    }

    private AbstractMonster[] generateBugSwarmGroup() {
        int groupSize = 5;
        float[] groupPositionsSize = {-450.0F, -300.0F, -150.0F, 0.0F, 150.0F};
        AbstractMonster[] monsters = new AbstractMonster[groupSize];
        monsters[0] = new CaterpieEnemy(groupPositionsSize[0], 0.0F);
        monsters[1] = new WeedleEnemy(groupPositionsSize[1], 0.0F);
        for (int i = 2; i < groupSize; i++) {
            if (AbstractDungeon.monsterRng.randomBoolean()) {
                monsters[i] = new CaterpieEnemy(groupPositionsSize[i], 0.0F);
            } else {
                monsters[i] = new WeedleEnemy(groupPositionsSize[i], 0.0F);
            }
        }
        return monsters;
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
        BaseMod.loadCustomStringsFile(ScoreBonusStrings.class, modID + "Resources/localization/" + getLangString() + "/Scorestrings.json");
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
        releasingPokemon = false;
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

    public static float time = 0f;
    @Override
    public void receivePostUpdate() {
        time += Gdx.graphics.getRawDeltaTime();
        // Need to slap this here instead of in PokemonTeamButton's update so it still works if the button is not on screen and being updated.
        if (releasingPokemon && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            releasingPokemon = false;
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            AbstractDungeon.effectList.add(new PurgeCardEffect(c));
            PlayerSpireFields.pokemonTeam.get(adp()).removeCard(c);
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        }
    }

    public static void saveData() {
        try {
            pokemonRegionConfig.setBool(DISABLE_POKEMON_OUTSIDE_CONFIG, disablePokemonOutsideConfig);
            pokemonRegionConfig.setBool(DISABLE_DETAILED_INTENTS_CONFIG, disableDetailedIntentsConfig);
            pokemonRegionConfig.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig(){
        disablePokemonOutsideConfig = pokemonRegionConfig.getBool(DISABLE_POKEMON_OUTSIDE_CONFIG);
        disableDetailedIntentsConfig = pokemonRegionConfig.getBool(DISABLE_DETAILED_INTENTS_CONFIG);
    }
}
