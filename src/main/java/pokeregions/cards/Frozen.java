package pokeregions.cards;

import com.megacrit.cardcrawl.actions.common.ReduceCostAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.atb;

public class Frozen extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(Frozen.class.getSimpleName());

    public Frozen() {
        super(ID, 2, CardType.STATUS, CardRarity.COMMON, CardTarget.NONE, CardColor.COLORLESS);
        exhaust = true;
        setMagic(1);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void triggerWhenDrawn() {
        atb(new ReduceCostAction(this.uuid, this.magicNumber));
    }
}
