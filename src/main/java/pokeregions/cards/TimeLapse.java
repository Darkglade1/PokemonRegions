package pokeregions.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;

public class TimeLapse extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(TimeLapse.class.getSimpleName());

    public TimeLapse() {
        super(ID, 0, CardType.STATUS, CardRarity.COMMON, CardTarget.NONE, CardColor.COLORLESS);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {

    }

    @Override
    public void upgrade() {
    }
}
