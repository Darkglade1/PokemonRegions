package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;

import static pokeregions.PokemonRegions.makeID;

public class ShuffleDiscardPileBackAction extends AbstractGameAction {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private final float startingDuration;
    private final boolean anyNumber;

    public ShuffleDiscardPileBackAction(int numCards, boolean anyNumber) {
        this.amount = numCards;
        this.actionType = ActionType.CARD_MANIPULATION;
        this.startingDuration = Settings.ACTION_DUR_FAST;
        this.duration = this.startingDuration;
        this.anyNumber = anyNumber;
    }

    public void update() {
        if (this.duration == this.startingDuration) {
            if (AbstractDungeon.player.discardPile.isEmpty()) {
                this.isDone = true;
                return;
            }
            CardGroup tmpGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
            for (int i = 0; i < AbstractDungeon.player.discardPile.size(); i++) {
                tmpGroup.addToTop(AbstractDungeon.player.discardPile.group
                        .get(AbstractDungeon.player.discardPile.size() - i - 1));
            }
            if (!this.anyNumber) {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, TEXT[0], false, false, this.anyNumber, false);
            } else {
                AbstractDungeon.gridSelectScreen.open(tmpGroup, this.amount, this.anyNumber, TEXT[0]);
            }

        } else if (!AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            for (AbstractCard c : AbstractDungeon.gridSelectScreen.selectedCards) {
                AbstractDungeon.player.discardPile.moveToDeck(c, true);
            }
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        tickDuration();
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ShuffleDiscardPileBackAction"));
        TEXT = uiStrings.TEXT;
    }
}
