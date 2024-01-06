package code.cards;

import code.PokemonRegions;
import code.actions.FreezeCardInHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Frozen extends AbstractEasyCard {

    public static final String ID = PokemonRegions.makeID(Frozen.class.getSimpleName());

    public Frozen() {
        super(ID, 1, CardType.STATUS, CardRarity.COMMON, CardTarget.NONE, CardColor.COLORLESS);
        exhaust = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        addToBot(new FreezeCardInHandAction(1));
    }

    @Override
    public void upgrade() {
    }
}
