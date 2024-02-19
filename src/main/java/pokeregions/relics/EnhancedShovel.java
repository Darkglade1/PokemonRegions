package pokeregions.relics;

import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import pokeregions.actions.EnhancedDigOption;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeID;

public class EnhancedShovel extends AbstractEasyRelic {
    public static final String ID = makeID(EnhancedShovel.class.getSimpleName());

    public EnhancedShovel() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        options.add(new EnhancedDigOption());
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0];
    }
}
