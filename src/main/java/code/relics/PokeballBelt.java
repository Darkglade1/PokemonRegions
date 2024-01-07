package code.relics;

import basemod.BaseMod;
import code.actions.HealPokemonCampfireOption;
import code.ui.PokemonTeamButton;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.CoffeeDripper;
import com.megacrit.cardcrawl.ui.campfire.AbstractCampfireOption;

import java.util.ArrayList;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;

public class PokeballBelt extends AbstractEasyRelic {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());
    public static final int STARTING_POKEBALLS = 6;

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        this.counter = STARTING_POKEBALLS;
        fixDescription();
    }

    @Override
    public void onEquip() {
        PokemonTeamButton pokemonTeam = new PokemonTeamButton();
        BaseMod.addTopPanelItem(pokemonTeam);
    }

    @Override
    public void fixDescription() {
        description = getUpdatedDescription();
        this.tips.clear();
        this.tips.add(new PowerTip(this.name, this.description));
        this.initializeTips();
    }

    @Override
    public void addCampfireOption(ArrayList<AbstractCampfireOption> options) {
        options.add(new HealPokemonCampfireOption());
    }

    @Override
    public boolean canUseCampfireOption(AbstractCampfireOption option) {
        if (option instanceof HealPokemonCampfireOption && adp().hasRelic(CoffeeDripper.ID)) {
            ((HealPokemonCampfireOption)option).updateUsability(false);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
    }
}
