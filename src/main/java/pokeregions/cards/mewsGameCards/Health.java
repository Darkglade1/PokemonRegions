package pokeregions.cards.mewsGameCards;

import basemod.AutoAdd;
import pokeregions.PokemonRegions;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import static pokeregions.util.Wiz.adp;

@AutoAdd.Ignore
public class Health extends AbstractMatchedCard {

    public static final String ID = PokemonRegions.makeID(Health.class.getSimpleName());

    private static final int HEAL = 9;

    public Health() {
        super(ID, CardType.SKILL, CardColor.COLORLESS);
        setMagic(HEAL);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void onMatched() {
        adp().heal(magicNumber, true);
    }
}
