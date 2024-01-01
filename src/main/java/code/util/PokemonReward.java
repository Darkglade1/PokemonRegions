package code.util;

import basemod.ReflectionHacks;
import basemod.TopPanelGroup;
import basemod.TopPanelItem;
import basemod.abstracts.CustomReward;
import basemod.patches.com.megacrit.cardcrawl.helpers.TopPanel.TopPanelHelper;
import code.patches.PlayerSpireFields;
import code.ui.PokemonTeamButton;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import java.util.ArrayList;

import static code.PokemonRegions.makeUIPath;
import static code.util.Wiz.adp;

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
        if (pokemonTeam.size() > PokemonTeamButton.MAX_TEAM_SIZE) {
            ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
            for (TopPanelItem item : topPanelItems) {
                if (item instanceof PokemonTeamButton) {
                    ((PokemonTeamButton) item).releaseExcessPokemon();
                }
            }
        }
        return true;
    }
}
