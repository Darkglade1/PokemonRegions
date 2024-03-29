package pokeregions.cards.mewsGameCards;

import basemod.AutoAdd;
import pokeregions.PokemonRegions;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.util.Wiz.adp;

@AutoAdd.Ignore
public class Riches extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Riches.class.getSimpleName());

    private static final int GOLD = 40;

    public Riches() {
        super(ID, CardType.SKILL, CardColor.COLORLESS);
        setMagic(GOLD);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onMatched() {
        adp().gainGold(magicNumber);
    }
}
