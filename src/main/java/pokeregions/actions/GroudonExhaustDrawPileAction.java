package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import pokeregions.powers.Burn;
import pokeregions.util.Wiz;

import static pokeregions.PokemonRegions.makeID;

public class GroudonExhaustDrawPileAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private final float startingDuration;
    private final boolean anyNumber;

    public GroudonExhaustDrawPileAction(int numCards, boolean anyNumber, AbstractCreature target, AbstractCreature source) {
        this.amount = numCards;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
        this.anyNumber = anyNumber;
        this.target = target;
        this.source = source;
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            if (AbstractDungeon.player.drawPile.isEmpty()) {
                this.isDone = true;
                return;
            }
            CardGroup tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (int i = 0; i < AbstractDungeon.player.drawPile.size(); i++) {
                tmpGroup.addToTop(AbstractDungeon.player.drawPile.group
                        .get(AbstractDungeon.player.drawPile.size() - i - 1));
            }

            if (!this.anyNumber) {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, TEXT[0], false, false, this.anyNumber, false);
            } else {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, this.anyNumber, TEXT[0]);
            }

        } else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            int combinedCosts = 0;
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.player.drawPile.moveToExhaustPile(c);
                if (c.costForTurn > 0) {
                    combinedCosts += c.costForTurn;
                }
            }
            if (combinedCosts > 0) {
                Wiz.applyToTarget(target, source, new Burn(target, combinedCosts));
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        tickDuration();
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ExhaustDrawPile"));
        TEXT = uiStrings.TEXT;
    }
}
