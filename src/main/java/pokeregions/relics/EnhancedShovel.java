package pokeregions.relics;

import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import pokeregions.actions.EnhancedDigOption;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;

public class EnhancedShovel extends AbstractEasyRelic {
    public static final String ID = makeID(EnhancedShovel.class.getSimpleName());

    public static final int USES = 2;

    public EnhancedShovel() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        this.counter = USES;
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        if (this.counter > 0) {
            options.add(new EnhancedDigOption());
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + USES + DESCRIPTIONS[1];
    }
}
