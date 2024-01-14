package pokeregions.actions;

import pokeregions.cards.AbstractAllyPokemonCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;

public class UpdateStaminaOnCardAction extends AbstractGameAction {
    AbstractAllyPokemonCard allyCard;
    private final int staminaChange;

    public UpdateStaminaOnCardAction(AbstractAllyPokemonCard allyCard, int staminaChange) {
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
        this.allyCard = allyCard;
        this.staminaChange = staminaChange;
    }

    public void update() {
        int newStamina = allyCard.currentStamina + staminaChange;
        allyCard.updateStamina(newStamina);
        this.isDone = true;
    }
}


