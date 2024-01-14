package code.events.act1;

import code.cards.mewsGameCards.*;
import code.cards.pokemonAllyCards.Mew;
import code.util.PokemonReward;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.*;

import static code.PokemonRegions.makeEventPath;
import static code.PokemonRegions.makeID;

public class MewsGame extends AbstractImageEvent {
    public static final String ID = makeID(MewsGame.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String title = eventStrings.NAME;
    private AbstractCard chosenCard;
    private AbstractCard hoveredCard;
    private boolean cardFlipped = false;
    private boolean gameDone = false;
    private boolean cleanUpCalled = false;
    private int attemptCount = 6;
    private final CardGroup cards;
    private float waitTimer;
    private int cardsMatched;
    private static final int MATCH_THRESHOLD = 2;
    private CUR_SCREEN screen;
    private final List<String> matchedCards;
    AbstractCard mewCard = new Mew();

    public MewsGame() {
        super(title, DESCRIPTIONS[0], makeEventPath("MewsGame.png"));
        this.noCardsInRewards = true;
        this.cards = new CardGroup(CardGroupType.UNSPECIFIED);
        this.waitTimer = 0.0F;
        this.cardsMatched = 0;
        this.screen = MewsGame.CUR_SCREEN.INTRO;
        this.cards.group = this.initializeCards();
        Collections.shuffle(this.cards.group, new Random(AbstractDungeon.miscRng.randomLong()));
        this.imageEventText.setDialogOption(OPTIONS[0], mewCard);
        this.imageEventText.setDialogOption(OPTIONS[1]);
        this.matchedCards = new ArrayList<>();
        if (AbstractDungeon.ascensionLevel >= 15) {
            attemptCount--;
        }
    }

    private ArrayList<AbstractCard> initializeCards() {
        ArrayList<AbstractCard> retVal = new ArrayList<>();
        ArrayList<AbstractCard> retVal2 = new ArrayList<>();

        AbstractCard minorRiches = new Riches();
        AbstractCard minorHealth = new Health();
        AbstractCard majorBenefit;
        if (AbstractDungeon.eventRng.randomBoolean()) {
            majorBenefit = new Riches();
            majorBenefit.upgrade();
        } else {
            majorBenefit = new Health();
            majorBenefit.upgrade();
        }
        retVal.add(minorRiches);
        retVal.add(minorHealth);
        retVal.add(majorBenefit);

        AbstractCard minorTheft = new Theft();
        AbstractCard majorTheft = new Theft();
        majorTheft.upgrade();

        retVal.add(minorTheft);
        retVal.add(majorTheft);

        AbstractCard prank = new Prank();
        retVal.add(prank);

        for (AbstractCard c : retVal) {
            retVal2.add(c.makeStatEquivalentCopy());
        }
        retVal.addAll(retVal2);

        for (AbstractCard c : retVal) {
            c.current_x = (float)Settings.WIDTH / 2.0F;
            c.target_x = c.current_x;
            c.current_y = -300.0F * Settings.scale;
        }

        return retVal;
    }

    public void update() {
        super.update();
        this.cards.update();
        if (this.screen == MewsGame.CUR_SCREEN.PLAY) {
            this.updateMatchGameLogic();
        } else if (this.screen == MewsGame.CUR_SCREEN.CLEAN_UP) {
            if (!this.cleanUpCalled) {
                this.cleanUpCalled = true;
                this.cleanUpCards();
            }

            if (this.waitTimer > 0.0F) {
                this.waitTimer -= Gdx.graphics.getDeltaTime();
                if (this.waitTimer < 0.0F) {
                    this.waitTimer = 0.0F;
                    this.screen = MewsGame.CUR_SCREEN.COMPLETE;
                    GenericEventDialog.show();
                    if (cardsMatched >= MATCH_THRESHOLD) {
                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                        AbstractDungeon.getCurrRoom().rewards.add(new PokemonReward(mewCard.cardID));
                        AbstractDungeon.combatRewardScreen.open();
                    } else {
                        this.imageEventText.updateBodyText(DESCRIPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        this.imageEventText.setDialogOption(OPTIONS[3]);
                    }
                }
            }
        }

        if (!GenericEventDialog.waitForInput) {
            this.buttonEffect(GenericEventDialog.getSelectedOption());
        }

    }

    private void cleanUpCards() {
        AbstractCard c;
        for(Iterator var1 = this.cards.group.iterator(); var1.hasNext(); c.target_y = -300.0F * Settings.scale) {
            c = (AbstractCard)var1.next();
            c.targetDrawScale = 0.5F;
            c.target_x = (float)Settings.WIDTH / 2.0F;
        }
    }

    private void updateMatchGameLogic() {
        if (this.waitTimer == 0.0F) {
            this.hoveredCard = null;
            Iterator var1 = this.cards.group.iterator();

            while(true) {
                while(var1.hasNext()) {
                    AbstractCard c = (AbstractCard)var1.next();
                    c.hb.update();
                    if (this.hoveredCard == null && c.hb.hovered) {
                        c.drawScale = 0.7F;
                        c.targetDrawScale = 0.7F;
                        this.hoveredCard = c;
                        if (InputHelper.justClickedLeft) {
                            InputHelper.justClickedLeft = false;
                            this.hoveredCard.isFlipped = false;
                            if (!this.cardFlipped) {
                                this.cardFlipped = true;
                                this.chosenCard = this.hoveredCard;
                            } else {
                                this.cardFlipped = false;
                                if (this.chosenCard.cardID.equals(this.hoveredCard.cardID) && this.chosenCard.upgraded == this.hoveredCard.upgraded) {
                                    this.waitTimer = 1.0F;
                                    this.chosenCard.targetDrawScale = 0.7F;
                                    this.chosenCard.target_x = (float)Settings.WIDTH / 2.0F;
                                    this.chosenCard.target_y = (float)Settings.HEIGHT / 2.0F;
                                    this.hoveredCard.targetDrawScale = 0.7F;
                                    this.hoveredCard.target_x = (float)Settings.WIDTH / 2.0F;
                                    this.hoveredCard.target_y = (float)Settings.HEIGHT / 2.0F;
                                } else {
                                    this.waitTimer = 1.25F;
                                    this.chosenCard.targetDrawScale = 1.0F;
                                    this.hoveredCard.targetDrawScale = 1.0F;
                                }
                            }
                        }
                    } else if (c != this.chosenCard) {
                        c.targetDrawScale = 0.5F;
                    }
                }

                return;
            }
        } else {
            this.waitTimer -= Gdx.graphics.getDeltaTime();
            if (this.waitTimer < 0.0F && !this.gameDone) {
                this.waitTimer = 0.0F;
                if (this.chosenCard.cardID.equals(this.hoveredCard.cardID) && this.chosenCard.upgraded == this.hoveredCard.upgraded) {
                    ++this.cardsMatched;
                    this.cards.group.remove(this.chosenCard);
                    this.cards.group.remove(this.hoveredCard);
                    this.matchedCards.add(this.chosenCard.cardID);
                    if (chosenCard instanceof AbstractMatchedCard) {
                        ((AbstractMatchedCard) chosenCard).onMatched();
                    }
                    this.chosenCard = null;
                    this.hoveredCard = null;
                } else {
                    this.chosenCard.targetDrawScale = 0.5F;
                    this.hoveredCard.targetDrawScale = 0.5F;
                    this.chosenCard = null;
                    this.hoveredCard = null;
                }

                --this.attemptCount;
                if (this.attemptCount == 0) {
                    this.gameDone = true;
                    this.waitTimer = 1.0F;
                }
            } else if (this.gameDone) {
                this.screen = MewsGame.CUR_SCREEN.CLEAN_UP;
            }
        }

    }

    protected void buttonEffect(int buttonPressed) {
        switch (this.screen) {
            case INTRO:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[2]);
                        this.imageEventText.clearRemainingOptions();
                        this.screen = MewsGame.CUR_SCREEN.RULE_EXPLANATION;
                        return;
                    case 1:
                        this.imageEventText.updateBodyText(DESCRIPTIONS[4]);
                        this.imageEventText.updateDialogOption(0, OPTIONS[3]);
                        this.imageEventText.clearRemainingOptions();
                        this.screen = MewsGame.CUR_SCREEN.COMPLETE;
                        return;
                    default:
                        return;
                }
            case RULE_EXPLANATION:
                switch (buttonPressed) {
                    case 0:
                        this.imageEventText.removeDialogOption(0);
                        GenericEventDialog.hide();
                        this.screen = MewsGame.CUR_SCREEN.PLAY;
                        this.placeCards();
                        return;
                    default:
                        return;
                }
            case COMPLETE:
                this.openMap();
        }

    }

    private void placeCards() {
        for(int i = 0; i < this.cards.size(); ++i) {
            ((AbstractCard)this.cards.group.get(i)).target_x = (float)(i % 4) * 210.0F * Settings.xScale + 640.0F * Settings.xScale;
            ((AbstractCard)this.cards.group.get(i)).target_y = (float)(i % 3) * -230.0F * Settings.yScale + 750.0F * Settings.yScale;
            ((AbstractCard)this.cards.group.get(i)).targetDrawScale = 0.5F;
            ((AbstractCard)this.cards.group.get(i)).isFlipped = true;
        }

    }

    public void render(SpriteBatch sb) {
        if (this.chosenCard != null) {
            this.chosenCard.render(sb);
        }

        if (this.hoveredCard != null) {
            this.hoveredCard.render(sb);
        }

        if (this.screen == MewsGame.CUR_SCREEN.PLAY) {
            this.cards.render(sb);
            FontHelper.renderSmartText(sb, FontHelper.panelNameFont, OPTIONS[4] + this.attemptCount, 780.0F * Settings.scale, 100.0F * Settings.scale, 2000.0F * Settings.scale, 0.0F, Color.WHITE);
            FontHelper.renderSmartText(sb, FontHelper.panelNameFont, OPTIONS[5] + this.cardsMatched + "/" + MATCH_THRESHOLD, 780.0F * Settings.scale, 70.0F * Settings.scale, 2000.0F * Settings.scale, 0.0F, Color.WHITE);
        }

    }

    public void renderAboveTopPanel(SpriteBatch sb) {
    }

    private enum CUR_SCREEN {
        INTRO,
        RULE_EXPLANATION,
        PLAY,
        COMPLETE,
        CLEAN_UP;

        CUR_SCREEN() {
        }
    }
}
