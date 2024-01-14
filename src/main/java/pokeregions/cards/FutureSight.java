package pokeregions.cards;

import basemod.AutoAdd;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pokeregions.PokemonRegions;

import static pokeregions.util.Wiz.*;

@AutoAdd.Ignore
public class FutureSight extends AbstractEasyCard {
    public static final String ID = PokemonRegions.makeID(FutureSight.class.getSimpleName());
    private final int turns;
    private final AbstractCreature owner;

    public FutureSight(int damage, int turns, AbstractCreature owner) {
        super(ID, -2, CardType.SKILL, CardRarity.SPECIAL, CardTarget.NONE, CardColor.COLORLESS);
        setMagic(damage);
        this.turns = turns;
        this.owner = owner;
        if (turns == 1) {
            this.rawDescription = DESCRIPTIONS[0];
            initializeDescription();
        } else if (turns == 2) {
            this.rawDescription = DESCRIPTIONS[1];
            initializeDescription();
        }
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
    }

    @Override
    public void upgrade() {
    }

    @Override
    public void onChoseThisOption(){
        if (turns == 0) {
            att(new DamageAction(adp(), new DamageInfo(owner, magicNumber, DamageInfo.DamageType.THORNS), AbstractGameAction.AttackEffect.FIRE));
        } else if (turns > 0) {
            applyToTargetTop(owner, owner, new pokeregions.powers.FutureSight(owner, magicNumber, turns));
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new FutureSight(magicNumber, turns, owner);
    }
}
