package pokeregions.cards.mewsGameCards;

import basemod.AutoAdd;
import pokeregions.PokemonRegions;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.util.Wiz.adp;

@AutoAdd.Ignore
public class Theft extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Theft.class.getSimpleName());

    private static final int GOLD_LOSS = 30;

    public Theft() {
        super(ID, CardType.CURSE, CardColor.CURSE);
        setMagic(GOLD_LOSS);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onMatched() {
        adp().loseGold(magicNumber);
    }
}
