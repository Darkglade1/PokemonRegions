package code.relics;

import basemod.BaseMod;
import code.ui.PokemonTeamButton;
import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;

import static code.PokemonRegions.makeID;

public class PokeballBelt extends AbstractEasyRelic implements ClickableRelic {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());
    public static final int STARTING_POKEBALLS = 6;

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
        this.counter = STARTING_POKEBALLS;
        fixDescription();
    }

    @Override
    public void onRightClick()
    {

    }

    @Override
    public void onEquip() {
        PokemonTeamButton pokemonTeam = new PokemonTeamButton();
        BaseMod.addTopPanelItem(pokemonTeam);
    }

    @Override
    public String getUpdatedDescription() {
        return DESCRIPTIONS[0] + this.counter + DESCRIPTIONS[1];
    }
}
