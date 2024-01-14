package pokeregions.cards.mewsGameCards;

import basemod.AutoAdd;
import pokeregions.PokemonRegions;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.util.Wiz.adp;

@AutoAdd.Ignore
public class Riches extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Riches.class.getSimpleName());

    private static final int GOLD = 25;
    private static final int UP_GOLD = 25;

    public Riches() {
        super(ID, CardType.SKILL, CardColor.COLORLESS);
        setMagic(GOLD, UP_GOLD);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
        super.upgrade();
        this.name = cardStrings.EXTENDED_DESCRIPTION[0];
        this.initializeTitle();
    }

    @Override
    public void onMatched() {
        adp().gainGold(magicNumber);
    }
}
