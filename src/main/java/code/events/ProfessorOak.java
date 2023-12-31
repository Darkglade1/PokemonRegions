package code.events;

import basemod.animations.AbstractAnimation;
import code.BetterSpriterAnimation;
import code.PokemonRegions;
import code.cards.pokemonAllyCards.Bulbasaur;
import code.cards.pokemonAllyCards.Charmander;
import code.relics.PokeballBelt;
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

import static code.PokemonRegions.makeEventPath;

public class ProfessorOak extends AbstractEvent {

    public static final String ID = PokemonRegions.makeID(ProfessorOak.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final float DIALOG_X = 1200.0F * Settings.xScale;
    private static final float DIALOG_Y = AbstractDungeon.floorY + 60.0F * Settings.yScale;
    private final AbstractAnimation oak;

    private int screenNum = 0;
    private boolean pickStarter = false;

    public ProfessorOak() {
        this.body = "";
        this.roomEventText.clear();
        this.roomEventText.addDialogOption(OPTIONS[0]);
        this.talk(DESCRIPTIONS[0]);
        this.hasDialog = true;
        this.hasFocus = true;
        oak = new BetterSpriterAnimation(makeEventPath("Oak/Oak.scml"));
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
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
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
                        group.addToTop(new Bulbasaur());
                        group.addToTop(new Charmander());
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
    }

    @Override
    public void render(SpriteBatch sb) {
        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        oak.renderSprite(sb, 1334.0F * Settings.xScale, AbstractDungeon.floorY - 10.0F * Settings.yScale);
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
