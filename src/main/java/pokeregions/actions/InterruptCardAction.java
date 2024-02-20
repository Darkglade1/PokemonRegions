package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pokeregions.powers.SuspendedInTime;
import pokeregions.util.Wiz;

public class InterruptCardAction extends AbstractGameAction {
    private final AbstractCard card;
    private static final float DUR = 0.15F;

    public InterruptCardAction(AbstractCard card) {
        this.card = card;
        this.duration = this.startDuration = DUR;
        this.actionType = ActionType.CARD_MANIPULATION;
    }
    @Override
    public void update() {
        SuspendedInTime power = SuspendedInTime.getSuspendPower();
        if (duration == DUR && power != null) {
            Wiz.adp().hand.moveToDiscardPile(card);
            power.renderQueue.removeCard(card);
            SuspendedInTime.SuspendedCardFields.suspendedField.set(card, false);
            SuspendedInTime.SuspendedCardFields.interruptedField.set(card, false);
        }
        tickDuration();
        if (isDone && power != null) {
            power.playNextCard();
        }
    }
}