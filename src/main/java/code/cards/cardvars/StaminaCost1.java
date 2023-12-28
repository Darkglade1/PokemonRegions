package code.cards.cardvars;

import code.cards.AbstractEasyCard;
import com.megacrit.cardcrawl.cards.AbstractCard;

import static code.PokemonRegions.makeID;

public class StaminaCost1 extends AbstractEasyDynamicVariable {

    @Override
    public String key() {
        return makeID("stamina1");
    }

    @Override
    public boolean isModified(AbstractCard card) {
        return false;
    }

    @Override
    public int value(AbstractCard card) {
        if (card instanceof AbstractEasyCard) {
            return ((AbstractEasyCard) card).staminaCost1;
        }
        return -1;
    }

    @Override
    public int baseValue(AbstractCard card) {
        if (card instanceof AbstractEasyCard) {
            return ((AbstractEasyCard) card).staminaCost1;
        }
        return -1;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        return false;
    }
}