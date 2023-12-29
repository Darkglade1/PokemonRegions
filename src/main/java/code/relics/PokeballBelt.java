package code.relics;

import basemod.BaseMod;
import code.patches.PlayerSpireFields;
import code.ui.PokemonTeamButton;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;

import static code.PokemonRegions.makeID;
import static code.util.Wiz.adp;

public class PokeballBelt extends AbstractEasyRelic {
    public static final String ID = makeID(PokeballBelt.class.getSimpleName());

    public PokeballBelt() {
        super(ID, RelicTier.SPECIAL, LandingSound.FLAT);
    }

    @Override
    public void onEquip() {
        PokemonTeamButton pokemonTeam = new PokemonTeamButton();
        BaseMod.addTopPanelItem(pokemonTeam);

        AbstractCard card = new code.cards.pokemonAllyCards.Charmander();
        CardGroup team = PlayerSpireFields.pokemonTeam.get(adp());
        team.addToBottom(card);
    }

    @Override
    public void atBattleStart() {
//        Charmander ally = new Charmander(-700.0f, 0.0f);
//        atb(new SpawnMonsterAction(ally, false));
//        atb(new UsePreBattleActionAction(ally));
    }
}
