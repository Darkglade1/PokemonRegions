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
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

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
        if (pokemonTeam.size() + 1 > PokemonTeamButton.MAX_TEAM_SIZE) {
            pokemonTeam.addToTop(card);
            ArrayList<TopPanelItem> topPanelItems = ReflectionHacks.getPrivate(TopPanelHelper.topPanelGroup, TopPanelGroup.class, "topPanelItems");
            for (TopPanelItem item : topPanelItems) {
                if (item instanceof PokemonTeamButton) {
                    ((PokemonTeamButton) item).releaseExcessPokemon();
                }
            }
        } else {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        }
        return true;
    }
}
