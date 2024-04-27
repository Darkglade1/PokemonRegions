package pokeregions.relics;

import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.RestRoom;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;
import pokeregions.PokemonRegions;
import pokeregions.actions.HatchEggOption;

import java.util.ArrayList;

public class PokemonEgg extends AbstractEasyRelic {

    public static final String ID = PokemonRegions.makeID(PokemonEgg.class.getSimpleName());
    private final int threshold = 2;

    public PokemonEgg() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        setCounter(0);
    }

    @Override
    public void justEnteredRoom(AbstractRoom room) {
        if (room instanceof RestRoom) {
            if (this.counter < threshold) {
                this.flash();
                setCounter(this.counter + 1);
                fixDescription();
            }
        }
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        if (this.counter >= threshold) {
            options.add(new HatchEggOption());
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + threshold + DESCRIPTIONS[1]  + this.counter + "/" + threshold;
    }

    @Override
    public void setCounter(int counter) {
        super.setCounter(counter);
        fixDescription();
    }
}
