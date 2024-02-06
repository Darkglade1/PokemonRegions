package pokeregions.events;

import basemod.animations.AbstractAnimation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.InfiniteSpeechBubble;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.scene.LevelTransitionTextOverlayEffect;
import pokeregions.BetterSpriterAnimation;
import pokeregions.PokemonRegions;
import pokeregions.cards.AbstractAllyPokemonCard;
import pokeregions.cards.pokemonAllyCards.act3.Blastoise;
import pokeregions.cards.pokemonAllyCards.act3.Charizard;
import pokeregions.cards.pokemonAllyCards.act3.Venusaur;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.relics.PokeballBelt;
import pokeregions.util.Tags;

import static pokeregions.PokemonRegions.makeEventPath;
import static pokeregions.PokemonRegions.makeMonsterPath;
import static pokeregions.util.Wiz.adp;

public class ProfessorBirch extends AbstractEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorBirch.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final float DIALOG_X = 1300.0F * Settings.xScale;
    private static final float DIALOG_Y = AbstractDungeon.floorY + 110.0F * Settings.yScale;
    private final AbstractAnimation birch;
    private final AbstractAnimation starter1;
    private final AbstractAnimation starter2;
    private final AbstractAnimation starter3;

    private int screenNum = 0;
    private boolean pickStarter = false;
    private boolean hasStarter = false;
    private int goldBonus;
    private int maxHPBonus;

    public ProfessorBirch() {
        for (AbstractCard card : PlayerSpireFields.pokemonTeam.get(adp()).group) {
            if (card.hasTag(Tags.STARTER_POKEMON) && card instanceof AbstractAllyPokemonCard) {
                hasStarter = true;
                break;
            }
        }
        this.body = "";
        this.roomEventText.clear();
        this.hasDialog = true;
        this.hasFocus = true;
        this.roomEventText.addDialogOption(OPTIONS[0]);
        birch = new BetterSpriterAnimation(makeEventPath("Birch/Birch.scml"));
        starter1 = new BetterSpriterAnimation(makeMonsterPath("Venusaur/Venusaur.scml"));
        starter2 = new BetterSpriterAnimation(makeMonsterPath("Blastoise/Blastoise.scml"));
        starter3 = new BetterSpriterAnimation(makeMonsterPath("Charizard/Charizard.scml"));
        if (!hasStarter) {
            this.talk(DESCRIPTIONS[0]);
        } else {
            this.talk(DESCRIPTIONS[4]);
        }
        AbstractDungeon.topLevelEffects.add(new LevelTransitionTextOverlayEffect(AbstractDungeon.name, AbstractDungeon.levelNum, true));
    }

    @Override
    public void update() {
        super.update();
        if (!RoomEventDialog.waitForInput) {
            this.buttonEffect(this.roomEventText.getSelectedOption());
        }
        if (this.pickStarter && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            AbstractCard c = (AbstractDungeon.gridSelectScreen.selectedCards.get(0)).makeStatEquivalentCopy();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(c, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
            AbstractDungeon.gridSelectScreen.selectedCards.clear();

            this.roomEventText.updateDialogOption(0, OPTIONS[3]);
            this.dismissBubble();
            this.talk(DESCRIPTIONS[3]);
            screenNum = 3;
            this.pickStarter = false;
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if (!hasStarter) {
            switch (buttonPressed) {
                case 0:
                    switch (screenNum) {
                        case 0:
                            this.roomEventText.updateDialogOption(0, OPTIONS[0] + FontHelper.colorString(OPTIONS[1], "g"));
                            this.dismissBubble();
                            this.talk(DESCRIPTIONS[1]);
                            screenNum = 1;
                            break;
                        case 1:
                            this.roomEventText.updateDialogOption(0, OPTIONS[0] + FontHelper.colorString(OPTIONS[2], "g"));
                            this.dismissBubble();
                            this.talk(DESCRIPTIONS[2]);
                            screenNum = 2;
                            AbstractDungeon.getCurrRoom().spawnRelicAndObtain((float)(Settings.WIDTH / 2), (float)(Settings.HEIGHT / 2), new PokeballBelt());
                            break;
                        case 2:
                            this.pickStarter = true;
                            CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                            group.addToTop(new Venusaur());
                            group.addToTop(new Blastoise());
                            group.addToTop(new Charizard());
                            for (AbstractCard c : group.group) {
                                UnlockTracker.markCardAsSeen(c.cardID);
                            }
                            AbstractDungeon.gridSelectScreen.open(group, 1, OPTIONS[2], false);
                            break;
                        case 3:
                            this.openMap();
                    }
                    break;
                default:
                    this.openMap();
            }
        } else {
            switch (screenNum) {
                case 0:
                    switch (buttonPressed) {
                        case 0:
                            this.dismissBubble();
                            int numCaught = PlayerSpireFields.totalPokemonCaught.get(adp());
                            calcBonuses(numCaught);
                            String msg = DESCRIPTIONS[5] + numCaught + DESCRIPTIONS[6];
                            if (numCaught >= 7) {
                                msg += DESCRIPTIONS[7];
                            } else if (numCaught >= 4) {
                                msg += DESCRIPTIONS[8];
                            } else {
                                msg += DESCRIPTIONS[9];
                            }
                            this.talk(msg);
                            screenNum = 1;
                            break;
                    }
                    break;
                case 1:
                    switch (buttonPressed) {
                        case 0:
                            this.roomEventText.updateDialogOption(0, OPTIONS[0] + FontHelper.colorString(OPTIONS[1], "g"));
                            this.dismissBubble();
                            this.talk(DESCRIPTIONS[10]);
                            screenNum = 2;
                            break;
                    }
                    break;
                case 2:
                    switch (buttonPressed) {
                        case 0:
                            if (AbstractDungeon.player.hasRelic(PokeballBelt.ID)) {
                                PokeballBelt belt = (PokeballBelt) adp().getRelic(PokeballBelt.ID);
                                belt.increment(6);
                            }
                            this.roomEventText.updateDialogOption(0, OPTIONS[4] + FontHelper.colorString(OPTIONS[5] + maxHPBonus + OPTIONS[6], "g"));
                            this.roomEventText.addDialogOption(OPTIONS[7] + FontHelper.colorString(OPTIONS[8] + goldBonus + OPTIONS[9], "g"));
                            this.dismissBubble();
                            this.talk(DESCRIPTIONS[11]);
                            screenNum = 3;
                            break;
                    }
                    break;
                case 3:
                    this.roomEventText.updateDialogOption(0, OPTIONS[3]);
                    this.roomEventText.clearRemainingOptions();
                    this.dismissBubble();
                    this.talk(DESCRIPTIONS[3]);
                    screenNum = 4;
                    switch (buttonPressed) {
                        case 0:
                            adp().increaseMaxHp(maxHPBonus, true);
                            break;
                        case 1:
                            adp().gainGold(goldBonus);
                            break;
                    }
                    break;
                default:
                    this.openMap();
            }
        }
    }

    private void calcBonuses(int numCaught) {
        for (int i = 1; i <= numCaught; i++) {
            boolean getBonus = false;
            if (i <= 5) {
                getBonus = true;
            } else if (i % 2 == 1) {
                getBonus = true;
            }
            if (getBonus) {
                goldBonus += 15;
                maxHPBonus += 1;
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        birch.renderSprite(sb, 1334.0F * Settings.xScale, AbstractDungeon.floorY - 10.0F * Settings.yScale);
        if (!hasStarter) {
            starter1.renderSprite(sb, 1184.0F * Settings.xScale, AbstractDungeon.floorY - 10.0F * Settings.yScale);
            starter2.renderSprite(sb, 1084.0F * Settings.xScale, AbstractDungeon.floorY - 10.0F * Settings.yScale);
            starter3.renderSprite(sb, 984.0F * Settings.xScale, AbstractDungeon.floorY - 10.0F * Settings.yScale);
        }
    }

    private void talk(String msg) {
        AbstractDungeon.effectList.add(new InfiniteSpeechBubble(DIALOG_X, DIALOG_Y, msg));
    }

    private void dismissBubble() {
        for (AbstractGameEffect e : AbstractDungeon.effectList) {
            if (e instanceof InfiniteSpeechBubble) {
                ((InfiniteSpeechBubble) e).dismiss();
            }
        }
    }

}
