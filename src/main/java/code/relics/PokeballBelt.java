package code.relics;

import basemod.BaseMod;
import code.ui.PokemonTeamButton;

import static code.PokemonRegions.makeID;

public class PokeballBelt extends AbstractEasyRelic {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        PokemonTeamButton pokemonTeam = new PokemonTeamButton();
        BaseMod.addTopPanelItem(pokemonTeam);
    }
}
