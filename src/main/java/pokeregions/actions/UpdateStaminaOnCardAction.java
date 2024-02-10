package pokeregions.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import pokeregions.monsters.AbstractPokemonAlly;

public class UpdateStaminaOnCardAction extends AbstractGameAction {
    AbstractPokemonAlly pokemon;
    private final int staminaChange;

    public UpdateStaminaOnCardAction(AbstractPokemonAlly pokemon, int staminaChange) {
        this.actionType = ActionType.DAMAGE;
        this.duration = Settings.ACTION_DUR_FAST;
        this.pokemon = pokemon;
        this.staminaChange = staminaChange;
    }

    @Override
    public void update() {
        if (pokemon.noStaminaCostForTurn && staminaChange < 0) {
            pokemon.noStaminaCostForTurn = false;
        } else {
            int newStamina = pokemon.allyCard.currentStamina + staminaChange;
            pokemon.allyCard.updateStamina(newStamina);
        }
        this.isDone = true;
    }
}


