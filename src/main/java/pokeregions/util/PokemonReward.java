package pokeregions.util;

import basemod.ReflectionHacks;
import basemod.TopPanelGroup;
import basemod.TopPanelItem;
import basemod.abstracts.CustomReward;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import pokeregions.patches.PlayerSpireFields;
import pokeregions.ui.PokemonTeamButton;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

import static pokeregions.PokemonRegions.makeUIPath;
import static pokeregions.util.Wiz.adp;

public class PokemonReward extends CustomReward {
    private static final Texture ICON = TexLoader.getTexture(makeUIPath("SwitchPokemonButton.png"));

    public AbstractCard card;

    public PokemonReward(String cardID) {
        super(ICON, "Pokemon", PokemonRewardEnum.POKEMON_REWARD);
        card = CardLibrary.getCopy(cardID);
        this.text = card.name;
    }

    @Override
    public boolean claimReward() {
        CardGroup pokemonTeam = PlayerSpireFields.pokemonTeam.get(adp());
        pokemonTeam.addToTop(card);
        UnlockTracker.markCardAsSeen(card.cardID);
        PlayerSpireFields.totalPokemonCaught.set(adp(), PlayerSpireFields.totalPokemonCaught.get(adp()) + 1);
        if (pokemonTeam.size() > PokemonTeamButton.MAX_TEAM_SIZE) {
            PokemonTeamButton.releaseExcessPokemon();
        }
        return true;
    }
}
