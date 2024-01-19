package pokeregions.cards.mewsGameCards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;

@AutoAdd.Ignore
public class Knowledge extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Knowledge.class.getSimpleName());

    public Knowledge() {
        super(ID, CardType.SKILL, CardColor.COLORLESS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onMatched() {
        AbstractDungeon.getCurrRoom().addCardToRewards();
    }
}
