package pokeregions.relics;

import pokeregions.actions.UpdateStaminaOnCardAction;
import pokeregions.monsters.AbstractPokemonAlly;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;

import static pokeregions.PokemonRegions.makeID;
import static pokeregions.util.Wiz.adp;
import static pokeregions.util.Wiz.atb;

public class RainbowBadge extends AbstractEasyRelic implements OnPokemonSwitchRelic {
    public static final String ID = makeID(RainbowBadge.class.getSimpleName());

    private static final int STAMINA = 1;

    public RainbowBadge() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onPokemonSwitch(AbstractPokemonAlly pokemonAlly) {
        if (pokemonAlly.allyCard.currentStamina < pokemonAlly.allyCard.maxStamina && this.counter > 0) {
            this.counter = 0;
            atb(new UpdateStaminaOnCardAction(pokemonAlly, STAMINA));
            atb(new HealAction(pokemonAlly, pokemonAlly, STAMINA));
            atb(new RelicAboveCreatureAction(adp(), this));
        }
    }

    @Override
    public void atBattleStart() {
        this.counter = 1;
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + STAMINA + DESCRIPTIONS[1];
    }
}
