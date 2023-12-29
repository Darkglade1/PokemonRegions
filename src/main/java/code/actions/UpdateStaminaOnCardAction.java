package code.actions;

import code.cards.AbstractAllyPokemonCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;

public class UpdateStaminaOnCardAction extends AbstractGameAction {
    AbstractAllyPokemonCard allyCard;
    private int newStamina;

    public UpdateStaminaOnCardAction(AbstractAllyPokemonCard allyCard, int newStamina) {
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
        this.allyCard = allyCard;
        this.newStamina = newStamina;
    }

    public void update() {
        if (newStamina < 0) {
            newStamina = 0;
        }
        if (newStamina > allyCard.maxStamina) {
            newStamina = allyCard.maxStamina;
        }
        allyCard.updateStamina(newStamina);
        allyCard.initializeDescriptionFromMoves();

        this.isDone = true;
    }
}


