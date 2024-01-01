package code.util;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static code.PokemonRegions.makeUIPath;

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
        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(card, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
        return true;
    }
}
